package com.tr.ing.brokerage.service.impl;

import com.tr.ing.brokerage.dto.AssetDTO;
import com.tr.ing.brokerage.dto.OrderDTO;
import com.tr.ing.brokerage.exception.InsufficientAssetException;
import com.tr.ing.brokerage.helper.AssetValidationHelper;
import com.tr.ing.brokerage.service.AssetService;
import com.tr.ing.brokerage.util.BaseModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetValidationHelper assetValidationHelper;
    private final BaseModelMapper modelMapper;

    @Override
    public void processOrder(OrderDTO orderDTO) {
        try {
            log.debug("Processing order [ID: {}, Type: {}, Customer: {}]",
                    orderDTO.getId(),
                    orderDTO.getSide(),
                    orderDTO.getCustomerId());

            assetValidationHelper.processOrder(orderDTO);

            log.info("Completed processing order [ID: {}, Type: {}, Asset: {}, Amount: {}]",
                    orderDTO.getId(),
                    orderDTO.getSide(),
                    orderDTO.getAssetName(),
                    orderDTO.getPrice().multiply(orderDTO.getSize()));
        } catch (Exception e) {
            log.error("Failed to process order [ID: {}]: {}", orderDTO.getId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public void cancelOrder(OrderDTO orderDTO) {
        try {
            log.debug("Cancelling order [ID: {}, Type: {}, Customer: {}]",
                    orderDTO.getId(),
                    orderDTO.getSide(),
                    orderDTO.getCustomerId());

            assetValidationHelper.restoreOrderAssets(orderDTO);

            log.info("Successfully cancelled order [ID: {}, Type: {}, Asset: {}]",
                    orderDTO.getId(),
                    orderDTO.getSide(),
                    orderDTO.getAssetName());
        } catch (Exception e) {
            log.error("Failed to cancel order [ID: {}]: {}", orderDTO.getId(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AssetDTO getTryBalance(Long customerId) {
        try {
            log.debug("Fetching TRY balance for customer [ID: {}]", customerId);
            AssetDTO balance = modelMapper.convertToDto(
                    assetValidationHelper.getTryAssetEntity(customerId), AssetDTO.class);
            log.debug("Retrieved TRY balance for customer [ID: {}]: {}",
                    customerId, balance.getUsableSize());
            return balance;
        } catch (Exception e) {
            log.error("Failed to get TRY balance for customer [ID: {}]: {}",
                    customerId, e.getMessage());
            throw e;
        }
    }

    @Override
    public void validateAssetBalance(Long customerId, String assetName, BigDecimal amount) {
        try {
            log.debug("Validating {} balance for customer [ID: {}], Amount: {}",
                    assetName, customerId, amount);
            assetValidationHelper.validateAssetBalance(customerId, assetName, amount);
            log.debug("Validation successful for {} [Customer: {}, Amount: {}]",
                    assetName, customerId, amount);
        } catch (InsufficientAssetException e) {
            log.warn("Validation failed for {} [Customer: {}, Amount: {}]: {}",
                    assetName, customerId, amount, e.getMessage());
            throw e;
        }
    }

    @Override
    public void updateAssetBalance(Long customerId, String assetName, BigDecimal newBalance) {
        try {
            log.debug("Updating {} balance for customer [ID: {}] to {}",
                    assetName, customerId, newBalance);
            assetValidationHelper.updateAssetBalance(customerId, assetName, newBalance);
            log.info("Updated {} balance for customer [ID: {}] to {}",
                    assetName, customerId, newBalance);
        } catch (Exception e) {
            log.error("Failed to update {} balance for customer [ID: {}]: {}",
                    assetName, customerId, e.getMessage());
            throw e;
        }
    }
}

