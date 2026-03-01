package com.olatunji.venn.domain.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "funds")
public class Fund {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "load_id", nullable = false)
  private String loadId;

  @Column(name = "customer_id", nullable = false)
  private String customerId;

  @Column(name = "load_amount", nullable = false)
  private BigDecimal loadAmount;

  @Column(name = "time", nullable = false)
  private LocalDateTime time;

  public UUID getId() {
    return id;
  }

  public String getLoadId() {
    return loadId;
  }

  public void setLoadId(String loadId) {
    this.loadId = loadId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public BigDecimal getLoadAmount() {
    return loadAmount;
  }

  public void setLoadAmount(BigDecimal loadAmount) {
    this.loadAmount = loadAmount;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }
}
