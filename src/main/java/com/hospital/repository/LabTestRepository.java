package com.hospital.repository;

import com.hospital.entity.LabTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {

    // Recherche par code de test
    Optional<LabTest> findByTestCode(String testCode);

    // Recherche par nom
    List<LabTest> findByTestNameContainingIgnoreCase(String testName);

    // Tests par catégorie
    List<LabTest> findByCategory(String category);

    // Tests actifs
    List<LabTest> findByIsActive(Boolean isActive);

    // Tests par type d'échantillon
    List<LabTest> findBySampleType(String sampleType);

    // Recherche dans les tests
    @Query("SELECT lt FROM LabTest lt WHERE " +
           "LOWER(lt.testName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(lt.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(lt.testCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(lt.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<LabTest> searchLabTests(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Tests par prix
    @Query("SELECT lt FROM LabTest lt WHERE lt.price BETWEEN :minPrice AND :maxPrice")
    List<LabTest> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Tests par durée d'analyse
    @Query("SELECT lt FROM LabTest lt WHERE lt.processingTimeHours <= :maxHours")
    List<LabTest> findByMaxProcessingTime(@Param("maxHours") Integer maxHours);

    // Compter tests par catégorie
    @Query("SELECT lt.category, COUNT(lt) FROM LabTest lt GROUP BY lt.category")
    List<Object[]> countTestsByCategory();

    // Tests les plus demandés
    @Query("SELECT lt, COUNT(lo) as orderCount FROM LabTest lt " +
           "LEFT JOIN LabOrder lo ON lo.labTest = lt " +
           "GROUP BY lt ORDER BY orderCount DESC")
    List<Object[]> findMostOrderedTests(Pageable pageable);

    // Tests d'urgence disponibles
    @Query("SELECT lt FROM LabTest lt WHERE lt.isUrgentAvailable = true")
    List<LabTest> findUrgentAvailableTests();

    // Tests nécessitant préparation spéciale
    @Query("SELECT lt FROM LabTest lt WHERE lt.preparationInstructions IS NOT NULL AND lt.preparationInstructions != ''")
    List<LabTest> findTestsWithPreparation();
}