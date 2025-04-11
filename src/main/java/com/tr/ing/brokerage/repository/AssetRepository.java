package com.tr.ing.brokerage.repository;

import com.tr.ing.brokerage.entity.Asset;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    @EntityGraph(attributePaths = {"customer"})
    List<Asset> findByCustomerId(Long customerId);

    @EntityGraph(attributePaths = {"customer"})
    Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);

    boolean existsByCustomerIdAndAssetName(Long customerId, String assetName);

}
