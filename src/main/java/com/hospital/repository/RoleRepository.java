package com.hospital.repository;

import com.hospital.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    
    List<Role> findByNameContainingIgnoreCase(String name);
    
    List<Role> findByDisplayNameContainingIgnoreCase(String displayName);
    
    List<Role> findByIsSystemRole(Boolean isSystemRole);
    
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findByPermissionName(@Param("permissionName") String permissionName);
    
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Role r WHERE r.isSystemRole = false")
    long countCustomRoles();
}