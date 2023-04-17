package com.floney.floney.book.repository;

import com.floney.floney.book.entity.FlowCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlowCategoryRepository extends JpaRepository<FlowCategory,Long> {
    Optional<FlowCategory> findFlowCategoryByFlow(String flow);
}
