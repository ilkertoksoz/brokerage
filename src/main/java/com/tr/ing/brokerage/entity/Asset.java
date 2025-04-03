package com.tr.ing.brokerage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal size;

    @Column(name = "usable_size", precision = 19, scale = 4, nullable = false)
    private BigDecimal usableSize;
}
