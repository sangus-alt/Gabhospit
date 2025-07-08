package com.hospital.repository;

import com.hospital.entity.Bed;
import com.hospital.entity.Department;
import com.hospital.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {

    // Lits par département
    List<Bed> findByDepartment(Department department);

    // Lits par chambre
    List<Bed> findByRoom(Room room);

    // Lits disponibles
    @Query("SELECT b FROM Bed b WHERE b.isAvailable = true AND b.isOccupied = false")
    List<Bed> findAvailableBeds();

    // Lits disponibles par département
    @Query("SELECT b FROM Bed b WHERE b.department = :department AND b.isAvailable = true AND b.isOccupied = false")
    List<Bed> findAvailableBedsByDepartment(@Param("department") Department department);

    // Lits occupés
    @Query("SELECT b FROM Bed b WHERE b.isOccupied = true")
    List<Bed> findOccupiedBeds();

    // Lits par type
    List<Bed> findByBedType(String bedType);

    // Lits par numéro
    Optional<Bed> findByBedNumber(String bedNumber);

    // Recherche dans les lits
    @Query("SELECT b FROM Bed b WHERE " +
           "LOWER(b.bedNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.bedType) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Bed> searchBeds(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Statistiques d'occupation par département
    @Query("SELECT d.name, " +
           "COUNT(b) as totalBeds, " +
           "SUM(CASE WHEN b.isOccupied = true THEN 1 ELSE 0 END) as occupiedBeds, " +
           "SUM(CASE WHEN b.isAvailable = true AND b.isOccupied = false THEN 1 ELSE 0 END) as availableBeds " +
           "FROM Bed b JOIN b.department d GROUP BY d.name")
    List<Object[]> getBedOccupancyStatsByDepartment();

    // Lits nécessitant maintenance
    @Query("SELECT b FROM Bed b WHERE b.isAvailable = false AND b.isOccupied = false")
    List<Bed> findBedsForMaintenance();

    // Compter lits par type
    @Query("SELECT b.bedType, COUNT(b) FROM Bed b GROUP BY b.bedType")
    List<Object[]> countBedsByType();

    // Lits par statut d'entretien
    List<Bed> findByMaintenanceStatus(String maintenanceStatus);

    // Taux d'occupation global
    @Query("SELECT " +
           "COUNT(b) as totalBeds, " +
           "SUM(CASE WHEN b.isOccupied = true THEN 1 ELSE 0 END) as occupiedBeds, " +
           "ROUND((SUM(CASE WHEN b.isOccupied = true THEN 1 ELSE 0 END) * 100.0 / COUNT(b)), 2) as occupancyRate " +
           "FROM Bed b WHERE b.isAvailable = true")
    List<Object[]> getOverallOccupancyRate();

    // Lits VIP disponibles
    @Query("SELECT b FROM Bed b WHERE b.bedType = 'VIP' AND b.isAvailable = true AND b.isOccupied = false")
    List<Bed> findAvailableVIPBeds();

    // Lits en urgence disponibles
    @Query("SELECT b FROM Bed b WHERE b.bedType = 'EMERGENCY' AND b.isAvailable = true AND b.isOccupied = false")
    List<Bed> findAvailableEmergencyBeds();
}