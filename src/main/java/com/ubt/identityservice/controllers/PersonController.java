package com.ubt.identityservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ubt.identityservice.configurations.exceptions.DatabaseException;
import com.ubt.identityservice.entities.PersonImage;
import com.ubt.identityservice.entities.dtos.PersonDTO;
import com.ubt.identityservice.services.PersonImageService;
import com.ubt.identityservice.services.PersonService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonService personService;
    @Autowired
    private PersonImageService personImageService;

    @GetMapping("/all")
    public ResponseEntity persons(){
        return ResponseEntity.ok(personService.preparePersonDTOList(personService.getAll()));
    }

    @GetMapping("/enabled")
    public ResponseEntity enabledPersons() {
        return ResponseEntity.ok(personService.preparePersonDTOList(personService.getAllEnabled()));
    }

    @GetMapping("/enabledWithoutUsers")
    public ResponseEntity enabledWithoutUsers() {
        return ResponseEntity.ok(personService.preparePersonDTOList(personService.getAllEnabledWithoutUsersAssigned()));
    }

    @GetMapping("/enabledWithoutUsersForEdit/{id}")
    public ResponseEntity enabledWithoutUsersForEdit(@PathVariable Long id) {
        return ResponseEntity.ok(personService.preparePersonDTOList(personService.getAllEnabledWithoutUsersAssignedForEdit(id)));
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.preparePersonDTO(personService.getById(id).getId()));
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addPerson(@ModelAttribute PersonDTO personDTO, @RequestParam("image") MultipartFile image){
        try {
            boolean created = personService.save(personDTO, image);
            if(created) {
                return ResponseEntity.ok(personDTO);
            }
        }
        catch (DatabaseException ex) {
            log.error("something went wrong while adding new person! ex: {}", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.IM_USED).build();
    }

    @GetMapping(value = "/edit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editPerson(@PathVariable Long id) {
        PersonDTO personDTO = personService.preparePersonDTO(id);
        if (personDTO != null) {
            return ResponseEntity.ok(personDTO);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editPerson(@ModelAttribute PersonDTO personDTO, @RequestParam("image") MultipartFile image){
        try {
            boolean updated = personService.update(personDTO, image);
            if(updated) {
                return ResponseEntity.ok(personDTO);
            }
        }
        catch (DatabaseException ex) {
            log.error("something went wrong while updating existing person! ex: {}", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.IM_USED).build();
    }

    @GetMapping("/disable/{id}")
    public ResponseEntity disablePerson(@PathVariable Long id){
        boolean disabled = personService.disable(id);
        if(disabled) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/enable/{id}")
    public ResponseEntity enablePerson(@PathVariable Long id){
        boolean enabled = personService.enable(id);
        if(enabled) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        PersonImage personImage = personImageService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + personImage.getName() + "\"")
                .body(personImage.getData());
    }
}