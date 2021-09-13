package com.ubt.identityservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubt.identityservice.configurations.exceptions.DatabaseException;
import com.ubt.identityservice.entities.User;
import com.ubt.identityservice.entities.dtos.CredentialsDTO;
import com.ubt.identityservice.entities.dtos.UserDTO;
import com.ubt.identityservice.services.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity users() {
        return ResponseEntity.ok(userService.prepareUserDTOList(userService.getAll()));
    }

    @GetMapping("/enabled")
    public ResponseEntity enabledUsers() {
        return ResponseEntity.ok(userService.prepareUserDTOList(userService.getAllEnabled()));
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.prepareUserDTO(userService.getById(id).getId()));
    }

    @PostMapping(value = "/loadUserByEmailAndPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loadUserByEmailAndPassword(@RequestBody CredentialsDTO credentialsDTO) {
        User user = userService.
                loadUserByEmailAndPassword(credentialsDTO.getEmail(), credentialsDTO.getPassword());
        if(user != null) {
            return ResponseEntity.ok(userService.prepareUserDTO(user.getId()));
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/loadUserByUsername", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loadUserByUsername(@RequestBody CredentialsDTO credentialsDTO) {
        User user = userService.
                loadUserByUsername(credentialsDTO.getEmail());
        if(user != null) {
            return ResponseEntity.ok(userService.prepareUserDTO(user.getId()));
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addUser(@RequestBody UserDTO userDTO) {
        try {
            boolean created = userService.save(userDTO);
            if(created) {
                return ResponseEntity.ok(userDTO);
            }
        }
        catch (DatabaseException ex) {
            log.error("something went wrong while adding new user! ex: {}", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.IM_USED).build();
    }

    @GetMapping(value = "/edit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editUser(@PathVariable Long id) {
        UserDTO userDTO = userService.prepareUserDTO(id);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editUser(@RequestBody UserDTO userDTO) {
        try {
            boolean updated = userService.update(userDTO);
            if(updated) {
                return ResponseEntity.ok(userDTO);
            }
        }
        catch (DatabaseException ex) {
            log.error("something went wrong while updating existing user! ex: {}", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.IM_USED).build();
    }

    @GetMapping("/disable/{id}")
    public ResponseEntity disableUser(@PathVariable Long id) {
        boolean disabled = userService.disable(id);
        if(disabled) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/enable/{id}")
    public ResponseEntity enableUser(@PathVariable Long id) {
        boolean enabled = userService.enable(id);
        if(enabled) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/privileges/{id}")
    public ResponseEntity getPrivilegesByUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if(user != null) {
            return ResponseEntity.ok(user.getPrivileges());
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity updatePassword(@RequestBody CredentialsDTO credentialsDTO) {
        boolean enabled = userService.updatePassword(credentialsDTO.getEmail(), credentialsDTO.getPassword());
        if(enabled) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping("/activateUser")
    public ResponseEntity activateUser(@RequestBody CredentialsDTO credentialsDTO) {
        boolean enabled = userService.activateUser(credentialsDTO.getEmail());
        if(enabled) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
}