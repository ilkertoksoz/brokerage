package com.tr.ing.brokerage.helper;

import com.tr.ing.brokerage.dto.OrderDTO;
import com.tr.ing.brokerage.entity.Asset;
import com.tr.ing.brokerage.enums.Side;
import com.tr.ing.brokerage.exception.AssetNotFoundException;
import com.tr.ing.brokerage.exception.InsufficientAssetException;
import com.tr.ing.brokerage.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.tr.ing.brokerage.constant.Assets.TRY_ASSETS;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetValidationHelper {

    private final AssetRepository assetRepository;

    // ========== PUBLIC API METHODS ==========

    /**
     * Processes order based on side (BUY/SELL)
     */
    public void processOrder(OrderDTO orderDTO) {
        log.debug("Processing {} order [ID: {}]", orderDTO.getSide(), orderDTO.getId());

        if (orderDTO.getSide() == Side.BUY) {
            processBuyOrder(orderDTO);
        } else {
            processSellOrder(orderDTO);
        }

        log.info("Processed {} order [ID: {}]", orderDTO.getSide(), orderDTO.getId());
    }

    /**
     * Restores assets when order is cancelled
     */
    public void restoreOrderAssets(OrderDTO orderDTO) {
        log.debug("Restoring assets for {} order [ID: {}]",
                orderDTO.getSide(), orderDTO.getId());

        if (orderDTO.getSide() == Side.BUY) {
            BigDecimal amountToRestore = orderDTO.getPrice().multiply(orderDTO.getSize());
            updateTryBalance(
                    orderDTO.getCustomerId(),
                    getTryAssetBalance(orderDTO.getCustomerId()).add(amountToRestore)
            );
        } else {
            updateAssetBalance(
                    orderDTO.getCustomerId(),
                    orderDTO.getAssetName(),
                    getAssetBalance(orderDTO.getCustomerId(), orderDTO.getAssetName())
                            .add(orderDTO.getSize())
            );
        }

        log.debug("Assets restored for order [ID: {}]", orderDTO.getId());
    }

    /**
     * Validates if customer has sufficient TRY balance
     */
    public void validateTryBalance(Long customerId, BigDecimal requiredAmount) {
        BigDecimal tryBalance = getTryAssetBalance(customerId);
        if (tryBalance.compareTo(requiredAmount) < 0) {
            log.warn("Insufficient TRY balance for customer [ID: {}]. Required: {}, Available: {}",
                    customerId, requiredAmount, tryBalance);
            throw new InsufficientAssetException("Insufficient TRY balance");
        }
        log.debug("Validated TRY balance for customer [ID: {}]", customerId);
    }

    /**
     * Validates if customer has sufficient asset balance
     */
    public void validateAssetBalance(Long customerId, String assetName, BigDecimal requiredAmount) {
        BigDecimal assetBalance = getAssetBalance(customerId, assetName);
        if (assetBalance.compareTo(requiredAmount) < 0) {
            log.warn("Insufficient {} balance for customer [ID: {}]. Required: {}, Available: {}",
                    assetName, customerId, requiredAmount, assetBalance);
            throw new InsufficientAssetException("Insufficient " + assetName + " balance");
        }
        log.debug("Validated {} balance for customer [ID: {}]", assetName, customerId);
    }

    /**
     * Gets current TRY balance for customer
     */
    public BigDecimal getTryAssetBalance(Long customerId) {
        BigDecimal balance = getTryAssetEntity(customerId).getUsableSize();
        log.debug("Retrieved TRY balance [{}] for customer [ID: {}]", balance, customerId);
        return balance;
    }

    /**
     * Gets current asset balance for customer
     */
    public BigDecimal getAssetBalance(Long customerId, String assetName) {
        BigDecimal balance = getAssetEntity(customerId, assetName).getUsableSize();
        log.debug("Retrieved {} balance [{}] for customer [ID: {}]", assetName, balance, customerId);
        return balance;
    }

    public void updateAssetBalance(Long customerId, String assetName, BigDecimal newBalance) {
        Asset asset = getAssetEntity(customerId, assetName);
        asset.setUsableSize(newBalance);
        assetRepository.save(asset);
        log.debug("Updated {} balance to {} for customer [ID: {}]",
                assetName, newBalance, customerId);
    }

    public Asset getTryAssetEntity(Long customerId) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, TRY_ASSETS)
                .orElseThrow(() -> {
                    log.error("TRY asset not found for customer [ID: {}]", customerId);
                    return new AssetNotFoundException("TRY asset not found");
                });
    }

    // ========== PRIVATE IMPLEMENTATION METHODS ==========

    private void processBuyOrder(OrderDTO orderDTO) {
        BigDecimal requiredAmount = calculateOrderAmount(orderDTO);
        validateTryBalance(orderDTO.getCustomerId(), requiredAmount);
        updateTryBalance(
                orderDTO.getCustomerId(),
                getTryAssetBalance(orderDTO.getCustomerId()).subtract(requiredAmount)
        );
    }

    private void processSellOrder(OrderDTO orderDTO) {
        validateAssetBalance(
                orderDTO.getCustomerId(),
                orderDTO.getAssetName(),
                orderDTO.getSize()
        );
        updateAssetBalance(
                orderDTO.getCustomerId(),
                orderDTO.getAssetName(),
                getAssetBalance(orderDTO.getCustomerId(), orderDTO.getAssetName())
                        .subtract(orderDTO.getSize())
        );
    }

    private void restoreBuyOrderAssets(OrderDTO orderDTO) {
        BigDecimal amountToRestore = calculateOrderAmount(orderDTO);
        updateTryBalance(
                orderDTO.getCustomerId(),
                getTryAssetBalance(orderDTO.getCustomerId()).add(amountToRestore)
        );
    }

    private void restoreSellOrderAssets(OrderDTO orderDTO) {
        updateAssetBalance(
                orderDTO.getCustomerId(),
                orderDTO.getAssetName(),
                getAssetBalance(orderDTO.getCustomerId(), orderDTO.getAssetName())
                        .add(orderDTO.getSize())
        );
    }

    private void updateTryBalance(Long customerId, BigDecimal newBalance) {
        Asset tryAsset = getTryAssetEntity(customerId);
        tryAsset.setUsableSize(newBalance);
        assetRepository.save(tryAsset);
        log.debug("Updated TRY balance to {} for customer [ID: {}]", newBalance, customerId);
    }


    private BigDecimal calculateOrderAmount(OrderDTO orderDTO) {
        return orderDTO.getPrice().multiply(orderDTO.getSize());
    }


    private Asset getAssetEntity(Long customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> {
                    log.error("{} asset not found for customer [ID: {}]", assetName, customerId);
                    return new AssetNotFoundException(assetName + " asset not found");
                });
    }
}