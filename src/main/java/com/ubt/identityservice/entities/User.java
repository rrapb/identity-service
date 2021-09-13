package com.ubt.identityservice.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ubt.identityservice.configurations.audit.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="users")
public class User extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private boolean enabled;

    @Column
    private boolean accountNonExpired;

    @Column
    private boolean accountNonLocked;

    @Column
    private boolean credentialsNonExpired;

    @Column
    private boolean tokenExpired;

    @JsonBackReference(value = "role-user")
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @JsonBackReference(value = "person-user")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id", referencedColumnName = "id", unique = true)
    private Person person;

    public List<String> getPrivileges() {

        List<String> privileges = new ArrayList<>();
        List<Permission> collection = new ArrayList<>(role.getPermissions());
        for (Permission item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }
}

