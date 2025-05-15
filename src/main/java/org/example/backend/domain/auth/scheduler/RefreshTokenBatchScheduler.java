package org.example.backend.domain.auth.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenBatchScheduler {

    private final Job deleteExpiredRefreshTokensJob;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시
    public void runJob() {
        try {
            log.info("🚀 [Batch] 만료된 RefreshToken 삭제 작업 시작");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("runDate", new Date())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(deleteExpiredRefreshTokensJob, jobParameters);
            log.info("✅ [Batch] 작업 상태: {}", execution.getStatus());

        } catch (Exception e) {
            log.error("❌ [Batch] 작업 실패", e);
        }
    }
}
