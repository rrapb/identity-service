package com.ubt.identityservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubt.identityservice.entities.Person;
import com.ubt.identityservice.entities.PersonImage;

@Repository
public interface PersonImageRepository extends JpaRepository<PersonImage, String> {

    PersonImage findByPerson(Person person);
}
