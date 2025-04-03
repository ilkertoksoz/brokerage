package com.tr.ing.brokerage.service;

import com.tr.ing.brokerage.dto.AssetDTO;
import com.tr.ing.brokerage.dto.OrderDTO;
import com.tr.ing.brokerage.entity.Asset;
import com.tr.ing.brokerage.enums.Side;
import com.tr.ing.brokerage.exception.InsufficientAssetException;
import com.tr.ing.brokerage.helper.AssetValidationHelper;
import com.tr.ing.brokerage.service.impl.AssetServiceImpl;
import com.tr.ing.brokerage.util.BaseModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private AssetValidationHelper assetValidationHelper;

    @Mock
    private BaseModelMapper modelMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    private OrderDTO orderDTO;
    private AssetDTO assetDTO;
    private Asset asset;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setCustomerId(100L);
        orderDTO.setAssetName("BTC");
        orderDTO.setSide(Side.BUY);
        orderDTO.setSize(BigDecimal.valueOf(1.5));
        orderDTO.setPrice(BigDecimal.valueOf(50000));

        assetDTO = new AssetDTO();
        assetDTO.setAssetName("TRY");
        assetDTO.setUsableSize(BigDecimal.valueOf(100000));

        asset = new Asset();
        asset.setAssetName("TRY");
        asset.setUsableSize(BigDecimal.valueOf(100000));
    }

    @Test
    void processOrder_Success() {
        assertDoesNotThrow(() -> assetService.processOrder(orderDTO));
        verify(assetValidationHelper, times(1)).processOrder(orderDTO);
    }

    @Test
    void processOrder_ThrowsException() {
        doThrow(new RuntimeException("Error")).when(assetValidationHelper).processOrder(orderDTO);

        assertThrows(RuntimeException.class, () -> assetService.processOrder(orderDTO));
        verify(assetValidationHelper, times(1)).processOrder(orderDTO);
    }

    @Test
    void cancelOrder_Success() {
        assertDoesNotThrow(() -> assetService.cancelOrder(orderDTO));
        verify(assetValidationHelper, times(1)).restoreOrderAssets(orderDTO);
    }

    @Test
    void cancelOrder_ThrowsException() {
        doThrow(new RuntimeException("Error")).when(assetValidationHelper).restoreOrderAssets(orderDTO);

        assertThrows(RuntimeException.class, () -> assetService.cancelOrder(orderDTO));
        verify(assetValidationHelper, times(1)).restoreOrderAssets(orderDTO);
    }

    @Test
    void getTryBalance_Success() {
        when(assetValidationHelper.getTryAssetEntity(100L)).thenReturn(asset);
        when(modelMapper.convertToDto(asset, AssetDTO.class)).thenReturn(assetDTO);

        AssetDTO result = assetService.getTryBalance(100L);

        assertNotNull(result);
        assertEquals("TRY", result.getAssetName());
        verify(assetValidationHelper, times(1)).getTryAssetEntity(100L);
        verify(modelMapper, times(1)).convertToDto(asset, AssetDTO.class);
    }

    @Test
    void validateAssetBalance_Success() {
        assertDoesNotThrow(() ->
                assetService.validateAssetBalance(100L, "BTC", BigDecimal.valueOf(1.5)));
        verify(assetValidationHelper, times(1))
                .validateAssetBalance(100L, "BTC", BigDecimal.valueOf(1.5));
    }

    @Test
    void validateAssetBalance_ThrowsInsufficientAssetException() {
        doThrow(new InsufficientAssetException("Insufficient balance"))
                .when(assetValidationHelper)
                .validateAssetBalance(100L, "BTC", BigDecimal.valueOf(1.5));

        assertThrows(InsufficientAssetException.class, () ->
                assetService.validateAssetBalance(100L, "BTC", BigDecimal.valueOf(1.5)));
    }

    @Test
    void updateAssetBalance_Success() {
        assertDoesNotThrow(() ->
                assetService.updateAssetBalance(100L, "BTC", BigDecimal.valueOf(1.5)));
        verify(assetValidationHelper, times(1))
                .updateAssetBalance(100L, "BTC", BigDecimal.valueOf(1.5));
    }

    @Test
    void updateAssetBalance_ThrowsException() {
        doThrow(new RuntimeException("Error"))
                .when(assetValidationHelper)
                .updateAssetBalance(100L, "BTC", BigDecimal.valueOf(1.5));

        assertThrows(RuntimeException.class, () ->
                assetService.updateAssetBalance(100L, "BTC", BigDecimal.valueOf(1.5)));
    }
}