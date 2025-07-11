package com.hospital.service;

import com.hospital.entity.InventoryItem;
import com.hospital.entity.StockMovement;
import com.hospital.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Créer un nouvel article d'inventaire
     */
    public InventoryItem createInventoryItem(InventoryItem item) {
        log.info("Création d'un nouvel article d'inventaire: {}", item.getName());
        
        // Générer un code article unique
        if (item.getItemCode() == null || item.getItemCode().isEmpty()) {
            item.setItemCode(generateItemCode());
        }
        
        // Valider l'unicité du code article
        validateItemCode(item.getItemCode());
        
        item.setCreationDate(LocalDateTime.now());
        item.setIsActive(true);
        
        InventoryItem savedItem = inventoryRepository.save(item);
        log.info("Article d'inventaire créé avec succès avec l'ID: {} et code: {}", 
                savedItem.getId(), savedItem.getItemCode());
        
        return savedItem;
    }

    /**
     * Mettre à jour un article d'inventaire existant
     */
    public InventoryItem updateInventoryItem(Long id, InventoryItem item) {
        log.info("Mise à jour de l'article d'inventaire avec l'ID: {}", id);
        
        InventoryItem existingItem = getInventoryItemById(id);
        
        existingItem.setName(item.getName());
        existingItem.setDescription(item.getDescription());
        existingItem.setCategory(item.getCategory());
        existingItem.setSubcategory(item.getSubcategory());
        existingItem.setUnit(item.getUnit());
        existingItem.setUnitPrice(item.getUnitPrice());
        existingItem.setSupplier(item.getSupplier());
        existingItem.setManufacturer(item.getManufacturer());
        existingItem.setBrand(item.getBrand());
        existingItem.setModel(item.getModel());
        existingItem.setReorderLevel(item.getReorderLevel());
        existingItem.setMaxStockLevel(item.getMaxStockLevel());
        existingItem.setStorageLocation(item.getStorageLocation());
        existingItem.setStorageConditions(item.getStorageConditions());
        existingItem.setIsPerishable(item.getIsPerishable());
        existingItem.setShelfLife(item.getShelfLife());
        existingItem.setIsControlled(item.getIsControlled());
        existingItem.setIsSterile(item.getIsSterile());
        existingItem.setUsageInstructions(item.getUsageInstructions());
        existingItem.setSafetyNotes(item.getSafetyNotes());
        existingItem.setBarcode(item.getBarcode());
        existingItem.setTags(item.getTags());
        existingItem.setNotes(item.getNotes());
        existingItem.setIsActive(item.getIsActive());
        
        return inventoryRepository.save(existingItem);
    }

    /**
     * Obtenir un article d'inventaire par ID
     */
    @Transactional(readOnly = true)
    public InventoryItem getInventoryItemById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article d'inventaire non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir un article d'inventaire par code
     */
    @Transactional(readOnly = true)
    public InventoryItem getInventoryItemByCode(String itemCode) {
        return inventoryRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Article d'inventaire non trouvé avec le code: " + itemCode));
    }

    /**
     * Obtenir tous les articles d'inventaire avec pagination
     */
    @Transactional(readOnly = true)
    public Page<InventoryItem> getAllInventoryItems(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    /**
     * Rechercher des articles d'inventaire
     */
    @Transactional(readOnly = true)
    public Page<InventoryItem> searchInventoryItems(String searchTerm, Pageable pageable) {
        return inventoryRepository.searchItems(searchTerm, pageable);
    }

    /**
     * Obtenir les articles actifs
     */
    @Transactional(readOnly = true)
    public List<InventoryItem> getActiveItems() {
        return inventoryRepository.findActiveItems();
    }

    /**
     * Obtenir les articles par catégorie
     */
    @Transactional(readOnly = true)
    public List<InventoryItem> getItemsByCategory(InventoryItem.ItemCategory category) {
        return inventoryRepository.findByCategory(category);
    }

    /**
     * Obtenir les articles par fournisseur
     */
    @Transactional(readOnly = true)
    public List<InventoryItem> getItemsBySupplier(String supplier) {
        return inventoryRepository.findBySupplier(supplier);
    }

    /**
     * Obtenir les articles avec stock bas
     */
    @Transactional(readOnly = true)
    public List<InventoryItem> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    /**
     * Obtenir les articles en rupture de stock
     */
    @Transactional(readOnly = true)
    public List<InventoryItem> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems();
    }

    /**
     * Obtenir les articles expirant bientôt
     */
    @Transactional(readOnly = true)
    public List<InventoryItem> getExpiringSoonItems(LocalDate date) {
        return inventoryRepository.findExpiringSoonItems(date);
    }

    /**
     * Obtenir les articles expirés
     */
    @Transactional(readOnly = true)
    public List<InventoryItem> getExpiredItems() {
        return inventoryRepository.findExpiredItems();
    }

    /**
     * Ajouter du stock
     */
    public void addStock(Long itemId, Integer quantity, String reason, String reference) {
        log.info("Ajout de stock pour l'article ID: {} - Quantité: {}", itemId, quantity);
        
        InventoryItem item = getInventoryItemById(itemId);
        Integer currentStock = item.getCurrentStock() != null ? item.getCurrentStock() : 0;
        item.setCurrentStock(currentStock + quantity);
        item.setLastStockUpdate(LocalDateTime.now());
        
        inventoryRepository.save(item);
        
        // Enregistrer le mouvement de stock
        recordStockMovement(item, StockMovement.MovementType.IN, quantity, reason, reference);
        
        log.info("Stock mis à jour pour l'article {}: {} -> {}", 
                item.getName(), currentStock, item.getCurrentStock());
    }

    /**
     * Retirer du stock
     */
    public void removeStock(Long itemId, Integer quantity, String reason, String reference) {
        log.info("Retrait de stock pour l'article ID: {} - Quantité: {}", itemId, quantity);
        
        InventoryItem item = getInventoryItemById(itemId);
        Integer currentStock = item.getCurrentStock() != null ? item.getCurrentStock() : 0;
        
        if (currentStock < quantity) {
            throw new RuntimeException("Stock insuffisant pour l'article " + item.getName() + 
                    ". Stock actuel: " + currentStock + ", Quantité demandée: " + quantity);
        }
        
        item.setCurrentStock(currentStock - quantity);
        item.setLastStockUpdate(LocalDateTime.now());
        
        inventoryRepository.save(item);
        
        // Enregistrer le mouvement de stock
        recordStockMovement(item, StockMovement.MovementType.OUT, quantity, reason, reference);
        
        // Vérifier si le stock est faible
        if (item.getCurrentStock() <= item.getReorderLevel()) {
            log.warn("Stock faible pour l'article {}: {} unités restantes (seuil: {})", 
                    item.getName(), item.getCurrentStock(), item.getReorderLevel());
        }
        
        log.info("Stock mis à jour pour l'article {}: {} -> {}", 
                item.getName(), currentStock, item.getCurrentStock());
    }

    /**
     * Ajuster le stock (inventaire physique)
     */
    public void adjustStock(Long itemId, Integer newQuantity, String reason) {
        log.info("Ajustement de stock pour l'article ID: {} vers {}", itemId, newQuantity);
        
        InventoryItem item = getInventoryItemById(itemId);
        Integer currentStock = item.getCurrentStock() != null ? item.getCurrentStock() : 0;
        Integer difference = newQuantity - currentStock;
        
        item.setCurrentStock(newQuantity);
        item.setLastStockUpdate(LocalDateTime.now());
        
        inventoryRepository.save(item);
        
        // Enregistrer le mouvement d'ajustement
        if (difference != 0) {
            StockMovement.MovementType movementType = difference > 0 ? 
                    StockMovement.MovementType.ADJUSTMENT_IN : StockMovement.MovementType.ADJUSTMENT_OUT;
            recordStockMovement(item, movementType, Math.abs(difference), reason, "INVENTORY_ADJUSTMENT");
        }
        
        log.info("Stock ajusté pour l'article {}: {} -> {}", 
                item.getName(), currentStock, newQuantity);
    }

    /**
     * Mettre à jour la date d'expiration d'un lot
     */
    public void updateExpiryDate(Long itemId, LocalDate expiryDate) {
        log.info("Mise à jour de la date d'expiration pour l'article ID: {}", itemId);
        
        InventoryItem item = getInventoryItemById(itemId);
        item.setExpiryDate(expiryDate);
        
        inventoryRepository.save(item);
    }

    /**
     * Désactiver un article d'inventaire
     */
    public void deactivateItem(Long id) {
        log.info("Désactivation de l'article d'inventaire avec l'ID: {}", id);
        InventoryItem item = getInventoryItemById(id);
        item.setIsActive(false);
        inventoryRepository.save(item);
    }

    /**
     * Réactiver un article d'inventaire
     */
    public void reactivateItem(Long id) {
        log.info("Réactivation de l'article d'inventaire avec l'ID: {}", id);
        InventoryItem item = getInventoryItemById(id);
        item.setIsActive(true);
        inventoryRepository.save(item);
    }

    /**
     * Supprimer un article d'inventaire
     */
    public void deleteInventoryItem(Long id) {
        log.info("Suppression de l'article d'inventaire avec l'ID: {}", id);
        InventoryItem item = getInventoryItemById(id);
        inventoryRepository.delete(item);
    }

    /**
     * Compter les articles actifs
     */
    @Transactional(readOnly = true)
    public long countActiveItems() {
        return inventoryRepository.countActiveItems();
    }

    /**
     * Calculer la valeur totale du stock
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalStockValue() {
        return inventoryRepository.calculateTotalStockValue();
    }

    /**
     * Obtenir les statistiques par catégorie
     */
    @Transactional(readOnly = true)
    public List<Object[]> getInventoryStatsByCategory() {
        return inventoryRepository.getItemCountByCategory();
    }

    /**
     * Générer un code article unique
     */
    private String generateItemCode() {
        String prefix = "INV";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp.substring(timestamp.length() - 8) + suffix;
    }

    /**
     * Valider l'unicité du code article
     */
    private void validateItemCode(String itemCode) {
        if (inventoryRepository.findByItemCode(itemCode).isPresent()) {
            throw new RuntimeException("Un article avec ce code existe déjà: " + itemCode);
        }
    }

    /**
     * Enregistrer un mouvement de stock
     */
    private void recordStockMovement(InventoryItem item, StockMovement.MovementType type, 
                                   Integer quantity, String reason, String reference) {
        // Cette méthode pourrait enregistrer les mouvements dans une table séparée
        // Pour l'instant, nous loggons simplement l'information
        log.info("Mouvement de stock enregistré - Article: {}, Type: {}, Quantité: {}, Raison: {}", 
                item.getName(), type, quantity, reason);
    }
}