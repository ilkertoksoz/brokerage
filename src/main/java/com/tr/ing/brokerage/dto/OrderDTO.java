package com.tr.ing.brokerage.dto;

import com.tr.ing.brokerage.enums.Side;
import com.tr.ing.brokerage.enums.Status;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;

    private Long customerId;

    private String assetName;

    private Side side;

    private BigDecimal size;

    private BigDecimal price;

    private Status status;

    private LocalDateTime createDate;
}
