package com.ubt.identityservice.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name="persons")
public class Person extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private char gender;

    @Column
    private Date birthDate;

    @Column(unique = true, name = "personal_id")
    private String personalId;

    @Column
    private boolean enabled;

    @JsonManagedReference(value = "person-user")
    @OneToOne(mappedBy = "person")
    private User user;

    @JsonManagedReference(value = "person-image")
    @OneToOne(mappedBy = "person")
    private PersonImage image;
}
