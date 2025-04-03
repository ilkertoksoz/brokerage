package com.tr.ing.brokerage.service;

import com.tr.ing.brokerage.dto.AssetDTO;
import com.tr.ing.brokerage.dto.OrderDTO;

import java.math.BigDecimal;

public interface AssetService {


    void processOrder(OrderDTO orderDTO);

    void cancelOrder(OrderDTO orderDTO);

    AssetDTO getTryBalance(Long customerId);

    void validateAssetBalance(Long customerId, String assetName, BigDecimal amount);

    void updateAssetBalance(Long customerId, String assetName, BigDecimal newBalance);
}
