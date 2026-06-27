package cn.cyc.ai.cog.runtime.harness.repository;

import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessReportQuery;
import cn.cyc.ai.cog.runtime.harness.entity.HarnessReportEntity;
import cn.cyc.ai.cog.runtime.harness.entity.HarnessStepReportEntity;
import cn.cyc.ai.cog.runtime.harness.service.IHarnessReportService;
import cn.cyc.ai.cog.runtime.harness.service.IHarnessStepReportService;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessReportRepository;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Harness 报告 MyBatis Plus 持久化仓储实现。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentHarnessReportRepository implements HarnessReportRepository {

    private static final Logger log = LoggerFactory.getLogger(PersistentHarnessReportRepository.class);

    private final IHarnessReportService reportService;
    private final IHarnessStepReportService stepReportService;
    private final ObjectMapper objectMapper;

    public PersistentHarnessReportRepository(IHarnessReportService reportService,
                                              IHarnessStepReportService stepReportService,
                                              ObjectMapper objectMapper) {
        this.reportService = reportService;
        this.stepReportService = stepReportService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(HarnessReport report) {
        HarnessReportEntity entity = toEntity(report);
        reportService.save(entity);
        if (report.steps() != null) {
            for (HarnessReport.HarnessStepReport step : report.steps()) {
                HarnessStepReportEntity stepEntity = toStepEntity(report.harnessId(), step);
                stepReportService.save(stepEntity);
            }
        }
    }

    @Override
    public Optional<HarnessReport> findById(String harnessId) {
        HarnessReportEntity entity = reportService.getByHarnessId(harnessId);
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public Optional<HarnessReport> findLatest() {
        HarnessReportEntity entity = reportService.getLatest();
        return entity != null ? Optional.of(toDomain(entity)) : Optional.empty();
    }

    @Override
    public List<HarnessReport> findAll() {
        return reportService.lambdaQuery()
                .orderByDesc(HarnessReportEntity::getStartTime)
                .list()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<HarnessReport> findPage(Page<HarnessReport> page) {
        return findPage(page, new HarnessReportQuery(null, null, null));
    }

    @Override
    public Page<HarnessReport> findPage(Page<HarnessReport> page, HarnessReportQuery query) {
        Page<HarnessReportEntity> entityPage = reportService.lambdaQuery()
                .eq(query != null && query.status() != null && !query.status().isBlank(),
                        HarnessReportEntity::getStatus, query == null ? null : query.status())
                .ge(query != null && query.startFrom() != null,
                        HarnessReportEntity::getStartTime, query == null ? null : query.startFrom())
                .le(query != null && query.startTo() != null,
                        HarnessReportEntity::getStartTime, query == null ? null : query.startTo())
                .orderByDesc(HarnessReportEntity::getStartTime)
                .page(new Page<>(page.getCurrent(), page.getSize()));

        List<HarnessReport> records = entityPage.getRecords().stream()
                .map(this::toDomain)
                .toList();

        Page<HarnessReport> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(records);
        return result;
    }

    private HarnessReportEntity toEntity(HarnessReport report) {
        HarnessReportEntity entity = new HarnessReportEntity();
        entity.setHarnessId(report.harnessId());
        entity.setTraceId(report.traceId());
        entity.setStatus(report.status());
        entity.setStartTime(report.startTime());
        entity.setEndTime(report.endTime());
        entity.setTotalDurationMs(report.totalDurationMs());
        try {
            entity.setScenarioJson(objectMapper.writeValueAsString(report.scenario()));
            entity.setSummaryJson(objectMapper.writeValueAsString(report.summary()));
        } catch (JsonProcessingException e) {
            log.warn("JSON serialization failed", e);
        }
        return entity;
    }

    private HarnessStepReportEntity toStepEntity(String harnessId, HarnessReport.HarnessStepReport step) {
        HarnessStepReportEntity entity = new HarnessStepReportEntity();
        entity.setHarnessId(harnessId);
        entity.setSequence(step.sequence());
        entity.setStepCode(step.stepCode());
        entity.setStepName(step.stepName());
        entity.setStatus(step.status());
        entity.setDurationMs(step.durationMs());
        entity.setMessage(step.message());
        try {
            entity.setDetailsJson(objectMapper.writeValueAsString(step.details()));
        } catch (JsonProcessingException e) {
            log.warn("JSON serialization failed", e);
        }
        return entity;
    }

    private HarnessReport toDomain(HarnessReportEntity entity) {
        HarnessReport.HarnessScenarioSummary scenario = parseScenario(entity.getScenarioJson());
        HarnessReport.HarnessSummary summary = parseSummary(entity.getSummaryJson());
        List<HarnessReport.HarnessStepReport> steps = loadSteps(entity.getHarnessId());
        return new HarnessReport(
                entity.getHarnessId(),
                entity.getTraceId(),
                entity.getStatus(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getTotalDurationMs() != null ? entity.getTotalDurationMs() : 0,
                scenario,
                steps,
                summary
        );
    }

    private List<HarnessReport.HarnessStepReport> loadSteps(String harnessId) {
        return stepReportService.lambdaQuery()
                .eq(HarnessStepReportEntity::getHarnessId, harnessId)
                .orderByAsc(HarnessStepReportEntity::getSequence)
                .list()
                .stream()
                .map(this::toStepDomain)
                .toList();
    }

    private HarnessReport.HarnessStepReport toStepDomain(HarnessStepReportEntity entity) {
        return new HarnessReport.HarnessStepReport(
                entity.getSequence() != null ? entity.getSequence() : 0,
                entity.getStepCode(),
                entity.getStepName(),
                null,
                entity.getStatus(),
                entity.getDurationMs() != null ? entity.getDurationMs() : 0,
                entity.getMessage(),
                parseDetails(entity.getDetailsJson())
        );
    }

    private HarnessReport.HarnessScenarioSummary parseScenario(String scenarioJson) {
        if (scenarioJson == null || scenarioJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(scenarioJson, HarnessReport.HarnessScenarioSummary.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse harness scenario JSON", e);
            return null;
        }
    }

    private HarnessReport.HarnessSummary parseSummary(String summaryJson) {
        if (summaryJson == null || summaryJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(summaryJson, HarnessReport.HarnessSummary.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse harness summary JSON", e);
            return null;
        }
    }

    private Map<String, Object> parseDetails(String detailsJson) {
        if (detailsJson == null || detailsJson.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(detailsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse harness step details JSON", e);
            return Map.of();
        }
    }
}
