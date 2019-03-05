package com.entr.sbdem.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sproles")
public class SpRole {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @Column(name = "sprole_id", nullable = false, unique = true,columnDefinition = "serial")
    private Long spRoleId;

    @NotNull
    @Column(name = "sprole", unique = true)
    private String role;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
    @JsonBackReference
    private Set<SpUser> users = new HashSet<>();

    public SpRole(){}

    public SpRole(@NotNull String role, Set<SpUser> users) {
        this.role = role;
        this.users = users;
    }

    public SpRole(@NotNull String role) {
        this.role = role;
    }

    public Long getSpRoleId() {
        return spRoleId;
    }

    public String getRole() {
        return role;
    }

    public Set<SpUser> getUsers() {
        return users;
    }

    public void setSpRoleId(Long spRoleId) {
        this.spRoleId = spRoleId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsers(Set<SpUser> users) {
        this.users = users;
    }
}
