package com.tr.ing.brokerage.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {

    private Long id;

    private String customerId;

    private String name;

    private List<AssetDTO> assets;

    private List<OrderDTO> orders;

    private Boolean isDeleted;

}
