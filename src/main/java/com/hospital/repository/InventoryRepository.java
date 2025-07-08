package com.hospital.repository;

import com.hospital.entity.InventoryItem;
import com.hospital.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    // Recherche par nom d'article
    List<InventoryItem> findByItemNameContainingIgnoreCase(String itemName);

    // Recherche par code d'article
    Optional<InventoryItem> findByItemCode(String itemCode);

    // Articles par catégorie
    List<InventoryItem> findByCategory(String category);

    // Articles actifs
    List<InventoryItem> findByIsActive(Boolean isActive);

    // Recherche dans l'inventaire
    @Query("SELECT ii FROM InventoryItem ii WHERE " +
           "LOWER(ii.itemName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ii.itemCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ii.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ii.manufacturer) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<InventoryItem> searchInventoryItems(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Stock faible
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.currentStock <= ii.minimumStockLevel")
    List<InventoryItem> findLowStockItems();

    // Articles sans stock
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.currentStock = 0")
    List<InventoryItem> findOutOfStockItems();

    // Articles par fabricant
    List<InventoryItem> findByManufacturer(String manufacturer);

    // Articles par fournisseur
    List<InventoryItem> findBySupplier(String supplier);

    // Compter articles par catégorie
    @Query("SELECT ii.category, COUNT(ii) FROM InventoryItem ii GROUP BY ii.category")
    List<Object[]> countItemsByCategory();

    // Valeur totale du stock
    @Query("SELECT SUM(ii.unitPrice * ii.currentStock) FROM InventoryItem ii WHERE ii.isActive = true")
    Double getTotalInventoryValue();

    // Articles les plus utilisés
    @Query("SELECT ii, SUM(sm.quantity) as totalUsed FROM InventoryItem ii " +
           "LEFT JOIN StockMovement sm ON sm.inventoryItem = ii " +
           "WHERE sm.movementType = 'OUT' " +
           "GROUP BY ii ORDER BY totalUsed DESC")
    List<Object[]> findMostUsedItems(Pageable pageable);

    // Articles critiques (stock très faible)
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.currentStock <= (ii.minimumStockLevel * 0.5)")
    List<InventoryItem> findCriticalStockItems();

    // Articles par prix
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.unitPrice BETWEEN :minPrice AND :maxPrice")
    List<InventoryItem> findItemsByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}

@Repository
interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // Mouvements par article
    List<StockMovement> findByInventoryItemOrderByMovementDateDesc(InventoryItem inventoryItem);

    // Mouvements par type
    List<StockMovement> findByMovementType(String movementType);

    // Mouvements d'aujourd'hui
    @Query("SELECT sm FROM StockMovement sm WHERE DATE(sm.movementDate) = CURRENT_DATE")
    List<StockMovement> findTodayMovements();

    // Mouvements par période
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate")
    List<StockMovement> findMovementsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);

    // Entrées de stock
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementType = 'IN'")
    List<StockMovement> findStockEntries();

    // Sorties de stock
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementType = 'OUT'")
    List<StockMovement> findStockExits();

    // Statistiques des mouvements
    @Query("SELECT sm.movementType, COUNT(sm), SUM(sm.quantity) FROM StockMovement sm GROUP BY sm.movementType")
    List<Object[]> getMovementStatistics();

    // Mouvements par utilisateur
    List<StockMovement> findByUserId(Long userId);

    // Recherche dans les mouvements
    @Query("SELECT sm FROM StockMovement sm WHERE " +
           "LOWER(sm.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(sm.reference) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<StockMovement> searchMovements(@Param("searchTerm") String searchTerm, Pageable pageable);
}