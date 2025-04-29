package org.example.backend.domain.performance.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.entity.QPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PerformanceCustomRepositoryImpl implements PerformanceCustomRepository {
    private final JPAQueryFactory queryFactory;

    private final QPerformance performance = QPerformance.performance;

    //통합 검색
    @Override
    public Page<Performance> searchPerformances(String keyword, String category, String status, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(
                    performance.title.containsIgnoreCase(keyword)
                            .or(performance.description.containsIgnoreCase(keyword)));
        }

        if (category != null && !category.isEmpty()) {
            builder.and(performance.category.stringValue().eq(category));
        }

        if (status != null && !status.isEmpty()) {
            builder.and(performance.performanceStatus.stringValue().eq(status));
        }

        List<Performance> content = queryFactory
                .selectFrom(performance)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(performance.count())
                .from(performance)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

    //키워드로 공연 목록 조회
    @Override
    public Page<Performance> findByKeyword(String keyword, Pageable pageable) {
        List<Performance> content = queryFactory
                .selectFrom(performance)
                .where(
                        performance.title.containsIgnoreCase(keyword)
                                .or(performance.description.containsIgnoreCase(keyword))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(performance.count())
                .from(performance)
                .where(
                        performance.title.containsIgnoreCase(keyword)
                                .or(performance.description.containsIgnoreCase(keyword))
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

    //카테고리로 공연 목록 조회
    @Override
    public Page<Performance> findByCategory(String category, Pageable pageable) {
        List<Performance> content = queryFactory
                .selectFrom(performance)
                .where(performance.category.stringValue().eq(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(performance.count())
                .from(performance)
                .where(performance.category.eq(Enum.valueOf(org.example.backend.domain.performance.entity.PerformanceCategory.class, category)))
                .fetchOne();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

}
