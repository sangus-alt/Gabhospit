package com.hospital.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Role extends BaseEntity {

    @Column(name = "name", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le nom du rôle ne peut pas être vide")
    private String name;

    @Column(name = "display_name", nullable = false, length = 100)
    @NotBlank(message = "Le nom d'affichage ne peut pas être vide")
    private String displayName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_system_role")
    private Boolean isSystemRole = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}