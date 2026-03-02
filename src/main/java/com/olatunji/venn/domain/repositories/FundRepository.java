package com.olatunji.venn.domain.repositories;

import com.olatunji.venn.domain.entities.Fund;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long> {

    Optional<Fund> findByLoadIdAndCustomerId(String loadId, String customerId);

    // Coalesce ensures that the JPA query returns 0 as default if no data matches the query
    @Query(
            "SELECT COALESCE(SUM(f.loadAmount), 0) FROM Fund f WHERE f.customerId = :customerId AND f.time BETWEEN :startTime AND :endTime")
    BigDecimal sumLoadAmountByCustomerIdAndDateRange(
            @Param("customerId") String customerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(f) FROM Fund f WHERE f.customerId = :customerId AND f.time BETWEEN :startTime AND :endTime")
    long countByCustomerIdAndDateRange(
            @Param("customerId") String customerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
