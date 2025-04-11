package com.tr.ing.brokerage.controller;

import com.tr.ing.brokerage.api.AssetAPI;
import com.tr.ing.brokerage.dto.AssetDTO;
import com.tr.ing.brokerage.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static com.tr.ing.brokerage.constant.AppConstants.API_BASE_PATH;

@Slf4j
@RestController
@RequestMapping(API_BASE_PATH + "/customers/{customerId}/assets")
@RequiredArgsConstructor
public class AssetController implements AssetAPI {

    private final AssetService assetService;

    @Override
    @PostMapping("/initialize-try")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> initializeTryAsset(@PathVariable Long customerId) {
        log.debug("Initializing TRY asset for customer ID: {}", customerId);
        assetService.createInitialTryAsset(customerId);
        log.info("Successfully initialized TRY asset for customer ID: {}", customerId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssetDTO> createAsset(@PathVariable Long customerId,
                                                @RequestBody AssetDTO assetDTO) {
        log.debug("Creating new asset for customer ID: {}", customerId);
        assetDTO.setCustomerId(customerId);
        AssetDTO createdAsset = assetService.createAsset(assetDTO);
        log.info("Successfully created asset {} for customer ID: {}", createdAsset.getAssetName(), customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAsset);
    }

    @Override
    @GetMapping("/try-balance")
    @PreAuthorize("(hasRole('USER') and #customerId == authentication.principal.id) or hasRole('ADMIN')")
    public ResponseEntity<AssetDTO> getTryBalance(@PathVariable Long customerId) {
        log.debug("Fetching TRY balance for customer ID: {}", customerId);
        AssetDTO balance = assetService.getTryBalance(customerId);
        log.debug("Retrieved TRY balance: {} for customer ID: {}", balance.getUsableSize(), customerId);
        return ResponseEntity.ok(balance);
    }

    @Override
    @PostMapping("/{assetName}/validate-balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> validateAssetBalance(
            @PathVariable Long customerId,
            @PathVariable String assetName,
            @RequestParam BigDecimal amount) {
        log.debug("Validating {} balance for customer ID: {}, amount: {}", assetName, customerId, amount);
        assetService.validateAssetBalance(customerId, assetName, amount);
        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping("/{assetName}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateAssetBalance(
            @PathVariable Long customerId,
            @PathVariable String assetName,
            @RequestParam BigDecimal newBalance) {
        log.debug("Updating {} balance to {} for customer ID: {}", assetName, newBalance, customerId);
        assetService.updateAssetBalance(customerId, assetName, newBalance);
        return ResponseEntity.ok().build();
    }
}