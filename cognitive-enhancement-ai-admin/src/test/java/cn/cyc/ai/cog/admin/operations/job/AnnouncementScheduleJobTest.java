package cn.cyc.ai.cog.admin.operations.job;

import cn.cyc.ai.cog.platform.operations.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnnouncementScheduleJobTest {

    @Mock
    private AnnouncementService announcementService;

    @InjectMocks
    private AnnouncementScheduleJob announcementScheduleJob;

    @Test
    void shouldDelegatePublishDueScheduled() {
        announcementScheduleJob.publishDueAnnouncements();
        verify(announcementService).publishDueScheduled();
    }
}
