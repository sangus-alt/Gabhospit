package com.hospital.repository;

import com.hospital.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Recherche par nom
    Optional<Department> findByName(String name);

    // Recherche par code
    Optional<Department> findByCode(String code);

    // Recherche par nom ou description
    @Query("SELECT d FROM Department d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Department> searchDepartments(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Départements actifs
    @Query("SELECT d FROM Department d WHERE d.isActive = true")
    List<Department> findActiveDepartments();

    // Départements avec lits disponibles
    @Query("SELECT d FROM Department d WHERE EXISTS (SELECT b FROM Bed b WHERE b.department = d AND b.isAvailable = true)")
    List<Department> findDepartmentsWithAvailableBeds();

    // Compter les lits par département
    @Query("SELECT d.name, COUNT(b) FROM Department d LEFT JOIN d.beds b GROUP BY d.name")
    List<Object[]> countBedsByDepartment();

    // Statistiques d'occupation
    @Query("SELECT d.name, " +
           "COUNT(b) as totalBeds, " +
           "SUM(CASE WHEN b.isOccupied = true THEN 1 ELSE 0 END) as occupiedBeds " +
           "FROM Department d LEFT JOIN d.beds b " +
           "GROUP BY d.name")
    List<Object[]> getDepartmentOccupancyStats();
}