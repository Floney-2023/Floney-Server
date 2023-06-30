package com.floney.floney;

import com.floney.floney.book.dto.CarryOverInfo;
import com.floney.floney.book.util.CategoryFactory;
import com.floney.floney.book.dto.constant.CategoryEnum;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookLineCategory;
import com.floney.floney.book.entity.CarryOver;
import com.floney.floney.book.repository.BookLineCategoryRepository;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.constant.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
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

    private final CategoryFactory categoryCreator;
    private final BookLineCategoryRepository bookLineCategoryRepository;
    private final BookLineRepository bookLineRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final int CHUNK_SIZE = 10;

    @Bean
    public Job jpaPagingItemReaderJob() {
        return jobBuilderFactory.get("jpaPagingItemReaderJobs")
            .start(jpaPagingItemReaderStep())
            .build();
    }

    @Bean
    public Step jpaPagingItemReaderStep() {
        return stepBuilderFactory.get("carryOverStep")
            .<CarryOver, CarryOverInfo>chunk(CHUNK_SIZE)
            .reader(jpaPagingItemReader())
            .processor(jpaProcessor())
            .writer(jpaPagingItemWriter())
            .build();
    }

    @Bean
    public JpaPagingItemReader<CarryOver> jpaPagingItemReader() {
        HashMap<String, Object> paramValues = new HashMap<>();
        paramValues.put("active", Status.ACTIVE);
        return new JpaPagingItemReaderBuilder<CarryOver>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
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

    @Bean
    public ItemProcessor<CarryOver, CarryOverInfo> jpaProcessor() {
        return CarryOver::calculateValue;
    }

    private ItemWriter<CarryOverInfo> jpaPagingItemWriter() {

        return CarryOverInfos -> {
            for (CarryOverInfo carryOverInfo : CarryOverInfos) {
                BookLine line = bookLineRepository.save(carryOverInfo.getBookLine());

                BookLineCategory lineCategory = categoryCreator.create(carryOverInfo);
                bookLineCategoryRepository.save(lineCategory);
                line.add(CategoryEnum.ASSET, lineCategory);

                bookLineRepository.save(line);
            }
        };
    }
}
