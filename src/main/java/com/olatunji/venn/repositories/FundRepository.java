package com.olatunji.venn.repositories;

import com.olatunji.venn.domain.entities.Fund;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long> {
  Optional<Fund> findByLoadIdAndCustomerId(String loadId, String customerId);
}
