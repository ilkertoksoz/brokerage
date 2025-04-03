package com.tr.ing.brokerage.api;

import com.tr.ing.brokerage.dto.AssetDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Tag(name = "Asset", description = "API for managing Customer Assets")
@SecurityRequirement(name = "access_token")
public interface AssetAPI {

    @Operation(method = "GET", summary = "Get TRY balance for customer", operationId = "getTryBalance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    ResponseEntity<AssetDTO> getTryBalance(@PathVariable Long customerId);

    @Operation(method = "POST", summary = "Validate asset balance", operationId = "validateAssetBalance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance is sufficient"),
            @ApiResponse(responseCode = "400", description = "Insufficient balance"),
            @ApiResponse(responseCode = "404", description = "Customer or asset not found")
    })
    ResponseEntity<Void> validateAssetBalance(
            @PathVariable Long customerId,
            @PathVariable String assetName,
            @RequestParam BigDecimal amount);

    @Operation(method = "PUT", summary = "Update asset balance", operationId = "updateAssetBalance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Customer or asset not found")
    })
    ResponseEntity<Void> updateAssetBalance(
            @PathVariable Long customerId,
            @PathVariable String assetName,
            @RequestParam BigDecimal newBalance);
}

