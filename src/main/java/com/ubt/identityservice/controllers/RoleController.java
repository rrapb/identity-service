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
import com.ubt.identityservice.entities.dtos.RoleDTO;
import com.ubt.identityservice.services.RoleService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/all")
    public ResponseEntity roles(){
        return ResponseEntity.ok(roleService.prepareRoleDTOList(roleService.getAll()));
    }

    @GetMapping("/enabled")
    public ResponseEntity enabledRoles() {
        return ResponseEntity.ok(roleService.prepareRoleDTOList(roleService.getAllEnabled()));
    }

    @GetMapping("/disabled")
    public ResponseEntity disabledRoles() {
        return ResponseEntity.ok(roleService.prepareRoleDTOList(roleService.getAllDisabled()));
    }

    @GetMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.prepareRoleDTO(roleService.getById(id).getId()));
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addRole(@RequestBody RoleDTO roleDTO){
        try {
            boolean created = roleService.save(roleDTO);
            if(created) {
                return ResponseEntity.ok(roleDTO);
            }
        }
        catch (DatabaseException ex) {
            log.error("something went wrong while adding new role! ex: {}", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.IM_USED).build();
    }


    @GetMapping(value = "/edit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editRole(@PathVariable Long id) {
        RoleDTO roleDTO = roleService.prepareRoleDTO(id);
        if (roleDTO != null) {
            return ResponseEntity.ok(roleDTO);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editRole(@RequestBody RoleDTO roleDTO){
        try {
            boolean updated = roleService.update(roleDTO);
            if(updated) {
                return ResponseEntity.ok(roleDTO);
            }
        }
        catch (DatabaseException ex) {
            log.error("something went wrong while updating existing role! ex: {}", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.IM_USED).build();
    }

    @GetMapping("/disable/{id}")
    public ResponseEntity disableRole(@PathVariable Long id){
        boolean disabled = roleService.disable(id);
        if(disabled) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/enable/{id}")
    public ResponseEntity enableRole(@PathVariable Long id){
        boolean enabled = roleService.enable(id);
        if(enabled) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

}