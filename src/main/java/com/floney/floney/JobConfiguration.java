package com.floney.floney;

import com.floney.floney.book.dto.CarryOverInfo;
import com.floney.floney.book.entity.CarryOver;
import com.floney.floney.book.repository.BookLineCategoryRepository;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {
    private final BookLineCategoryRepository bookLineCategoryRepository;
    private final BookRepository bookRepository;
    private final BookLineRepository bookLineRepository;
    private final CategoryRepository categoryRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;

    @Bean
    public Job jpaPagingItemReaderJob() {
        return jobBuilderFactory.get("jpaPagingItemReaderJobs")
            .start(jpaPagingItemReaderStep())
            .build();
    }

    @Bean
    public Step jpaPagingItemReaderStep() {
        return stepBuilderFactory.get("carrssyOver")
            .<CarryOver, CarryOverInfo>chunk(chunkSize)
            .reader(jpaPagingItemReader())
            .build();
    }

    @Bean
    public JpaPagingItemReader<CarryOver> jpaPagingItemReader() {
        HashMap<String, Object> paramValues = new HashMap<>();
        paramValues.put("active", Status.ACTIVE);
        return new JpaPagingItemReaderBuilder<CarryOver>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("select new com.floney.floney.book.entity.CarryOver(book , sum(case when blc.name = '수입' then bl.money else 0 end), " +
                "sum(case when blc.name = '지출' then bl.money else 0 end))" +
                "from BookLine bl " +
                "inner join bl.book book " +
                "inner join bl.bookLineCategories blc " +
                "where book.carryOver = true " +
                "and bl.status = : active " +
                "group by book.bookKey")
            .parameterValues(paramValues)
            .build();
    }

}
