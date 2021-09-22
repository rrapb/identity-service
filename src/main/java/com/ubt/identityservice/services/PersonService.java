package com.ubt.identityservice.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ubt.identityservice.configurations.exceptions.DatabaseException;
import com.ubt.identityservice.entities.Person;
import com.ubt.identityservice.entities.dtos.PersonDTO;
import com.ubt.identityservice.repositories.PersonRepository;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonImageService personImageService;

    public List<Person> getAll(){
        return personRepository.findAll();
    }

    public List<Person> getAllEnabled(){
        return personRepository.findAllByEnabled(true);
    }

    public List<Person> getAllDisabled(){
        return personRepository.findAllByEnabled(false);
    }

    public List<Person> getAllEnabledWithoutUsersAssigned(){
        return personRepository.findAllWithoutUsers();
    }

    public List<Person> getAllEnabledWithoutUsersAssignedForEdit(Long id){
        return personRepository.findAllWithoutUsersForEdit(id);
    }

    public Person getById(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    public boolean save(PersonDTO personDTO, MultipartFile image) throws DatabaseException {
        Person person = Person.builder()
                .firstName(personDTO.getFirstName())
                .lastName(personDTO.getLastName())
                .birthDate(getBirthdateFromString(personDTO.getBirthDateString()))
                .gender(personDTO.getGender())
                .personalId(personDTO.getPersonalId())
                .enabled(true).build();

        if(StringUtils.isNotBlank(person.getFirstName()) && StringUtils.isNotBlank(person.getLastName())
                && StringUtils.isNotBlank(person.getPersonalId()) && person.getBirthDate() != null
                && (person.getGender() == 'M' || person.getGender() == 'F')) {
            try {
                personRepository.save(person);
                if(image != null) {
                    personImageService.save(image, person);
                }
                return true;
            }catch (Exception e) {
                throw new DatabaseException("duplicate");
            }
        }
        else {
            return false;
        }
    }

    public boolean update(PersonDTO personDTO, MultipartFile image) throws DatabaseException {

        Person person = getById(personDTO.getId());
        Person tempPerson = Person.builder()
                .id(person.getId())
                .firstName(personDTO.getFirstName())
                .lastName(personDTO.getLastName())
                .birthDate(getBirthdateFromString(personDTO.getBirthDateString()))
                .gender(personDTO.getGender())
                .personalId(personDTO.getPersonalId())
                .enabled(true).build();

        if(StringUtils.isNotBlank(tempPerson.getFirstName()) && StringUtils.isNotBlank(tempPerson.getLastName())
                && StringUtils.isNotBlank(tempPerson.getPersonalId()) && tempPerson.getBirthDate() != null
                && (tempPerson.getGender() == 'M' || tempPerson.getGender() == 'F') && personRepository.existsById(tempPerson.getId())) {
            try {
                personRepository.save(tempPerson);
                if(image != null) {
                    personImageService.save(image, person);
                }
                return true;
            }catch (Exception e) {
                throw new DatabaseException("duplicate");
            }
        }
        else {
            return false;
        }
    }

    public boolean disable(Long id) {

        Person person = getById(id);
        if(personRepository.existsById(person.getId()) && person.isEnabled()){
            person.setEnabled(false);
            personRepository.save(person);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean enable(Long id) {

        Person person = getById(id);
        if(personRepository.existsById(person.getId()) && !person.isEnabled()){
            person.setEnabled(true);
            personRepository.save(person);
            return true;
        }
        else {
            return false;
        }
    }

    public PersonDTO preparePersonDTO(final Long id) {

        Person person = getById(id);

        return PersonDTO.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .birthDate(person.getBirthDate())
                .gender(person.getGender())
                .personalId(person.getPersonalId())
                .enabled(person.isEnabled())
                .build();
    }

    public List<PersonDTO> preparePersonDTOList(final List<Person> persons) {
        List<PersonDTO> personDTOList = new ArrayList<>();
        for(Person person: persons) {
            personDTOList.add(preparePersonDTO(person.getId()));
        }
        return personDTOList;
    }

    private Date getBirthdateFromString(String date) {

        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
