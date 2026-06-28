package cn.cyc.ai.cog.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("admin-it")
class AdminOrgIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateOrganizationFor2B() throws Exception {
        jdbcTemplate.update("DELETE FROM qz_iam_user WHERE id = 2");
        jdbcTemplate.update("""
                INSERT INTO qz_iam_user (id, tenant_id, username, password_hash, nickname, status, user_type, primary_account_id)
                VALUES (2, 1, 'org-owner', '$2a$10$zmoVc2gWRhqfNkCfkVtic.Prw/fxhL3ViXdVz0pcwjPQDxWU4G0Oi', '组织负责人', 'ENABLED', 'CUSTOMER', NULL)
                """);

        mockMvc.perform(post("/api/admin/orgs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orgName": "IT 测试企业",
                                  "segment": "2B",
                                  "ownerUserId": 2,
                                  "seatLimit": 20,
                                  "contactName": "张三",
                                  "contactPhone": "13800000000"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.orgName", is("IT 测试企业")))
                .andExpect(jsonPath("$.data.seatLimit", is(20)));

        Integer orgCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM qz_acct_org WHERE org_name = 'IT 测试企业'", Integer.class);
        org.junit.jupiter.api.Assertions.assertTrue(orgCount != null && orgCount >= 1);
    }

    @Test
    void shouldManageDepartmentsAndMembers() throws Exception {
        jdbcTemplate.update("DELETE FROM qz_acct_org_member WHERE org_id IN (SELECT id FROM qz_acct_org WHERE org_name = '部门测试企业')");
        jdbcTemplate.update("DELETE FROM qz_acct_org_department WHERE org_id IN (SELECT id FROM qz_acct_org WHERE org_name = '部门测试企业')");
        jdbcTemplate.update("DELETE FROM qz_acct_org WHERE org_name = '部门测试企业'");
        jdbcTemplate.update("DELETE FROM qz_iam_user WHERE id IN (2, 3)");
        jdbcTemplate.update("""
                INSERT INTO qz_iam_user (id, tenant_id, username, password_hash, nickname, status, user_type, primary_account_id)
                VALUES (3, 1, 'dept-owner', '$2a$10$zmoVc2gWRhqfNkCfkVtic.Prw/fxhL3ViXdVz0pcwjPQDxWU4G0Oi', '部门负责人', 'ENABLED', 'CUSTOMER', NULL)
                """);
        jdbcTemplate.update("""
                INSERT INTO qz_iam_user (id, tenant_id, username, password_hash, nickname, status, user_type, primary_account_id)
                VALUES (2, 1, 'dept-member', '$2a$10$zmoVc2gWRhqfNkCfkVtic.Prw/fxhL3ViXdVz0pcwjPQDxWU4G0Oi', '部门成员', 'ENABLED', 'CUSTOMER', NULL)
                """);

        mockMvc.perform(post("/api/admin/orgs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orgName": "部门测试企业",
                                  "segment": "2B",
                                  "ownerUserId": 3,
                                  "seatLimit": 10,
                                  "contactName": "李四",
                                  "contactPhone": "13900000000"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        Long orgId = jdbcTemplate.queryForObject(
                "SELECT id FROM qz_acct_org WHERE org_name = '部门测试企业' LIMIT 1", Long.class);

        mockMvc.perform(post("/api/admin/orgs/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orgId": %d, "deptName": "研发部", "parentId": 0, "sortNo": 1}
                                """.formatted(orgId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.deptName", is("研发部")));

        Long deptId = jdbcTemplate.queryForObject(
                "SELECT id FROM qz_acct_org_department WHERE org_id = ? AND dept_name = '研发部'", Long.class, orgId);

        mockMvc.perform(get("/api/admin/orgs/" + orgId + "/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].deptName", is("研发部")));

        mockMvc.perform(post("/api/admin/orgs/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orgId": %d, "userId": 2, "deptId": %d, "orgRole": "MEMBER"}
                                """.formatted(orgId, deptId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.userId", is("2")));

        mockMvc.perform(get("/api/admin/orgs/" + orgId + "/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.userId == 2)]").exists());

        Long memberId = jdbcTemplate.queryForObject(
                "SELECT id FROM qz_acct_org_member WHERE org_id = ? AND user_id = 2", Long.class, orgId);

        mockMvc.perform(delete("/api/admin/orgs/" + orgId + "/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(get("/api/admin/orgs/" + orgId + "/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.userId == 2)]").doesNotExist());
    }
}
