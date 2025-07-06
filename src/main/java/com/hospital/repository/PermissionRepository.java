package com.hospital.repository;

import com.hospital.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);
    
    List<Permission> findByNameContainingIgnoreCase(String name);
    
    List<Permission> findByDisplayNameContainingIgnoreCase(String displayName);
    
    List<Permission> findByModule(String module);
    
    List<Permission> findByAction(String action);
    
    List<Permission> findByIsSystemPermission(Boolean isSystemPermission);
    
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.name = :roleName")
    List<Permission> findByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.id = :userId")
    List<Permission> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM Permission p WHERE p.isSystemPermission = false")
    long countCustomPermissions();
    
    @Query("SELECT DISTINCT p.module FROM Permission p ORDER BY p.module")
    List<String> findAllModules();
    
    @Query("SELECT DISTINCT p.action FROM Permission p ORDER BY p.action")
    List<String> findAllActions();
}