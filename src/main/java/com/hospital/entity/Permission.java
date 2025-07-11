package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Permission extends BaseEntity {

    @Column(name = "name", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Le nom de la permission ne peut pas être vide")
    private String name;

    @Column(name = "display_name", nullable = false, length = 150)
    @NotBlank(message = "Le nom d'affichage ne peut pas être vide")
    private String displayName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "module", nullable = false, length = 50)
    @NotBlank(message = "Le module ne peut pas être vide")
    private String module;

    @Column(name = "action", nullable = false, length = 50)
    @NotBlank(message = "L'action ne peut pas être vide")
    private String action;

    @Column(name = "is_system_permission")
    private Boolean isSystemPermission = false;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}