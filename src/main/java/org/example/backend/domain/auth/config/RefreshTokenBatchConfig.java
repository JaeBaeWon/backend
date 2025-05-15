package org.example.backend.domain.auth.config;

import org.example.backend.domain.auth.entity.RefreshToken;
import org.example.backend.domain.auth.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class RefreshTokenBatchConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public Job deleteExpiredRefreshTokensJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("deleteExpiredRefreshTokensJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteExpiredRefreshTokensStep", jobRepository)
                .<RefreshToken, RefreshToken>chunk(100, transactionManager)
                .reader(reader())
                .writer(items -> refreshTokenRepository.deleteAllInBatch(List.copyOf(items.getItems())))
                .build();
    }

    @Bean
    public JpaPagingItemReader<RefreshToken> reader() {
        return new JpaPagingItemReaderBuilder<RefreshToken>()
                .name("expiredRefreshTokenReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT rt FROM RefreshToken rt WHERE rt.expiration < :now")
                .parameterValues(Collections.singletonMap("now", LocalDateTime.now()))
                .pageSize(100)
                .build();
    }
}
