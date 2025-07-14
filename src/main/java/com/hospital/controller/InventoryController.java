package com.hospital.controller;

import com.hospital.entity.InventoryItem;
import com.hospital.entity.StockMovement;
import com.hospital.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventaire", description = "API de gestion de l'inventaire hospitalier")
public class InventoryController {

    private final InventoryService inventoryService;

    // Gestion des articles d'inventaire
    @GetMapping("/items")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les articles d'inventaire")
    public ResponseEntity<Page<InventoryItem>> getAllInventoryItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean lowStock,
            @RequestParam(required = false) Boolean expired,
            Pageable pageable) {
        
        log.info("Récupération des articles d'inventaire avec filtres: name={}, category={}, location={}, lowStock={}, expired={}", 
                name, category, location, lowStock, expired);
        
        Page<InventoryItem> items = inventoryService.findAllInventoryItems(
                name, category, location, lowStock, expired, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un article d'inventaire par ID")
    public ResponseEntity<InventoryItem> getInventoryItemById(@PathVariable Long id) {
        log.info("Récupération de l'article d'inventaire avec ID: {}", id);
        InventoryItem item = inventoryService.findInventoryItemById(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Créer un nouvel article d'inventaire")
    public ResponseEntity<InventoryItem> createInventoryItem(@Valid @RequestBody InventoryItem item) {
        log.info("Création d'un nouvel article d'inventaire: {}", item.getName());
        InventoryItem createdItem = inventoryService.createInventoryItem(item);
        return ResponseEntity.ok(createdItem);
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Modifier un article d'inventaire")
    public ResponseEntity<InventoryItem> updateInventoryItem(@PathVariable Long id, @Valid @RequestBody InventoryItem item) {
        log.info("Modification de l'article d'inventaire ID: {}", id);
        InventoryItem updatedItem = inventoryService.updateInventoryItem(id, item);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Supprimer un article d'inventaire")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long id) {
        log.info("Suppression de l'article d'inventaire ID: {}", id);
        inventoryService.deleteInventoryItem(id);
        return ResponseEntity.ok().build();
    }

    // Gestion des mouvements de stock
    @GetMapping("/movements")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Lister tous les mouvements de stock")
    public ResponseEntity<Page<StockMovement>> getAllStockMovements(
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String movementType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        
        log.info("Récupération des mouvements de stock avec filtres: item={}, type={}, location={}", 
                itemName, movementType, location);
        
        Page<StockMovement> movements = inventoryService.findAllStockMovements(
                itemName, movementType, location, fromDate, toDate, pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/movements/{id}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer un mouvement de stock par ID")
    public ResponseEntity<StockMovement> getStockMovementById(@PathVariable Long id) {
        log.info("Récupération du mouvement de stock avec ID: {}", id);
        StockMovement movement = inventoryService.findStockMovementById(id);
        return ResponseEntity.ok(movement);
    }

    @PostMapping("/movements")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Enregistrer un mouvement de stock")
    public ResponseEntity<StockMovement> recordStockMovement(@Valid @RequestBody StockMovement movement) {
        log.info("Enregistrement d'un mouvement de stock pour l'article ID: {}", 
                movement.getInventoryItem().getId());
        StockMovement recordedMovement = inventoryService.recordStockMovement(movement);
        return ResponseEntity.ok(recordedMovement);
    }

    // Opérations de stock
    @PostMapping("/items/{id}/receive")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Réceptionner du stock")
    public ResponseEntity<InventoryItem> receiveStock(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam BigDecimal unitCost,
            @RequestParam(required = false) String batchNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestParam String receivedBy) {
        
        log.info("Réception de {} unités pour l'article ID: {} par {}", quantity, id, receivedBy);
        InventoryItem updatedItem = inventoryService.receiveStock(id, quantity, unitCost, batchNumber, expiryDate, receivedBy);
        return ResponseEntity.ok(updatedItem);
    }

    @PostMapping("/items/{id}/issue")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('NURSE') or hasRole('ADMIN')")
    @Operation(summary = "Sortir du stock")
    public ResponseEntity<InventoryItem> issueStock(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam String issuedTo,
            @RequestParam String purpose,
            @RequestParam String issuedBy) {
        
        log.info("Sortie de {} unités de l'article ID: {} vers {} par {}", quantity, id, issuedTo, issuedBy);
        InventoryItem updatedItem = inventoryService.issueStock(id, quantity, issuedTo, purpose, issuedBy);
        return ResponseEntity.ok(updatedItem);
    }

    @PostMapping("/items/{id}/transfer")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Transférer du stock")
    public ResponseEntity<InventoryItem> transferStock(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam String fromLocation,
            @RequestParam String toLocation,
            @RequestParam String transferredBy) {
        
        log.info("Transfert de {} unités de l'article ID: {} de {} vers {} par {}", 
                quantity, id, fromLocation, toLocation, transferredBy);
        InventoryItem updatedItem = inventoryService.transferStock(id, quantity, fromLocation, toLocation, transferredBy);
        return ResponseEntity.ok(updatedItem);
    }

    @PostMapping("/items/{id}/adjust")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Ajuster le stock")
    public ResponseEntity<InventoryItem> adjustStock(
            @PathVariable Long id,
            @RequestParam int newQuantity,
            @RequestParam String reason,
            @RequestParam String adjustedBy) {
        
        log.info("Ajustement du stock de l'article ID: {} à {} unités par {} pour raison: {}", 
                id, newQuantity, adjustedBy, reason);
        InventoryItem updatedItem = inventoryService.adjustStock(id, newQuantity, reason, adjustedBy);
        return ResponseEntity.ok(updatedItem);
    }

    // Alertes et notifications
    @GetMapping("/alerts/low-stock")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les articles en rupture de stock")
    public ResponseEntity<List<InventoryItem>> getLowStockItems() {
        log.info("Récupération des articles en rupture de stock");
        List<InventoryItem> lowStockItems = inventoryService.findLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }

    @GetMapping("/alerts/expired")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les articles expirés")
    public ResponseEntity<List<InventoryItem>> getExpiredItems() {
        log.info("Récupération des articles expirés");
        List<InventoryItem> expiredItems = inventoryService.findExpiredItems();
        return ResponseEntity.ok(expiredItems);
    }

    @GetMapping("/alerts/expiring-soon")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les articles qui expirent bientôt")
    public ResponseEntity<List<InventoryItem>> getExpiringSoonItems(@RequestParam(defaultValue = "30") int days) {
        log.info("Récupération des articles expirant dans {} jours", days);
        List<InventoryItem> expiringSoonItems = inventoryService.findExpiringSoonItems(days);
        return ResponseEntity.ok(expiringSoonItems);
    }

    @GetMapping("/alerts/reorder-needed")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les articles nécessitant une commande")
    public ResponseEntity<List<InventoryItem>> getReorderNeededItems() {
        log.info("Récupération des articles nécessitant une commande");
        List<InventoryItem> reorderItems = inventoryService.findReorderNeededItems();
        return ResponseEntity.ok(reorderItems);
    }

    // Inventaire physique
    @PostMapping("/physical-count/start")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Démarrer un inventaire physique")
    public ResponseEntity<Object> startPhysicalCount(
            @RequestParam String location,
            @RequestParam String countedBy) {
        
        log.info("Démarrage d'un inventaire physique pour la location {} par {}", location, countedBy);
        Object physicalCount = inventoryService.startPhysicalCount(location, countedBy);
        return ResponseEntity.ok(physicalCount);
    }

    @PostMapping("/physical-count/{countId}/record")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Enregistrer un comptage physique")
    public ResponseEntity<Object> recordPhysicalCount(
            @PathVariable Long countId,
            @RequestParam Long itemId,
            @RequestParam int countedQuantity) {
        
        log.info("Enregistrement du comptage physique pour l'article ID: {} avec {} unités", itemId, countedQuantity);
        Object countRecord = inventoryService.recordPhysicalCount(countId, itemId, countedQuantity);
        return ResponseEntity.ok(countRecord);
    }

    @PostMapping("/physical-count/{countId}/complete")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Finaliser un inventaire physique")
    public ResponseEntity<Object> completePhysicalCount(@PathVariable Long countId) {
        log.info("Finalisation de l'inventaire physique ID: {}", countId);
        Object completedCount = inventoryService.completePhysicalCount(countId);
        return ResponseEntity.ok(completedCount);
    }

    // Rapports et statistiques
    @GetMapping("/reports/stock-valuation")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Rapport de valorisation du stock")
    public ResponseEntity<byte[]> generateStockValuationReport() {
        log.info("Génération du rapport de valorisation du stock");
        byte[] report = inventoryService.generateStockValuationReport();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "valorisation_stock.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }

    @GetMapping("/reports/movement-history")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Rapport d'historique des mouvements")
    public ResponseEntity<byte[]> generateMovementHistoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String location) {
        
        log.info("Génération du rapport d'historique des mouvements entre {} et {} pour la location: {}", 
                fromDate, toDate, location);
        byte[] report = inventoryService.generateMovementHistoryReport(fromDate, toDate, location);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "historique_mouvements_" + fromDate + "_" + toDate + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(report);
    }

    @GetMapping("/statistics/consumption")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Statistiques de consommation")
    public ResponseEntity<Object> getConsumptionStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String category) {
        
        log.info("Calcul des statistiques de consommation entre {} et {} pour la catégorie: {}", 
                fromDate, toDate, category);
        Object stats = inventoryService.getConsumptionStatistics(fromDate, toDate, category);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics/turnover")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Statistiques de rotation des stocks")
    public ResponseEntity<Object> getTurnoverStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        log.info("Calcul des statistiques de rotation des stocks entre {} et {}", fromDate, toDate);
        Object stats = inventoryService.getTurnoverStatistics(fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    // Gestion des fournisseurs et commandes
    @GetMapping("/suppliers")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Lister les fournisseurs")
    public ResponseEntity<Object> getAllSuppliers() {
        log.info("Récupération de la liste des fournisseurs");
        Object suppliers = inventoryService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @PostMapping("/purchase-orders")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Créer un bon de commande")
    public ResponseEntity<Object> createPurchaseOrder(@RequestBody Object purchaseOrder) {
        log.info("Création d'un bon de commande");
        Object createdOrder = inventoryService.createPurchaseOrder(purchaseOrder);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/purchase-orders")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Lister les bons de commande")
    public ResponseEntity<Object> getAllPurchaseOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String supplier) {
        
        log.info("Récupération des bons de commande avec filtres: status={}, supplier={}", status, supplier);
        Object orders = inventoryService.getAllPurchaseOrders(status, supplier);
        return ResponseEntity.ok(orders);
    }

    // Gestion des emplacements
    @GetMapping("/locations")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Lister les emplacements de stockage")
    public ResponseEntity<Object> getAllLocations() {
        log.info("Récupération de la liste des emplacements de stockage");
        Object locations = inventoryService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/locations/{location}/items")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Récupérer les articles d'un emplacement")
    public ResponseEntity<List<InventoryItem>> getLocationItems(@PathVariable String location) {
        log.info("Récupération des articles de l'emplacement: {}", location);
        List<InventoryItem> items = inventoryService.findItemsByLocation(location);
        return ResponseEntity.ok(items);
    }

    // Codes-barres et QR codes
    @GetMapping("/items/{id}/barcode")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Générer le code-barres d'un article")
    public ResponseEntity<byte[]> generateBarcode(@PathVariable Long id) {
        log.info("Génération du code-barres pour l'article ID: {}", id);
        byte[] barcode = inventoryService.generateBarcode(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("filename", "barcode_" + id + ".png");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(barcode);
    }

    @GetMapping("/items/search-by-barcode")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('PHARMACY_MANAGER') or hasRole('NURSE') or hasRole('ADMIN')")
    @Operation(summary = "Rechercher un article par code-barres")
    public ResponseEntity<InventoryItem> searchByBarcode(@RequestParam String barcode) {
        log.info("Recherche d'un article par code-barres: {}", barcode);
        InventoryItem item = inventoryService.findByBarcode(barcode);
        return ResponseEntity.ok(item);
    }
}