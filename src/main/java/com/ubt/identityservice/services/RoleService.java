package com.ubt.identityservice.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubt.identityservice.configurations.exceptions.DatabaseException;
import com.ubt.identityservice.entities.Permission;
import com.ubt.identityservice.entities.Role;
import com.ubt.identityservice.entities.dtos.RoleDTO;
import com.ubt.identityservice.repositories.PermissionRepository;
import com.ubt.identityservice.repositories.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Role> getAll(){
        return roleRepository.findAll();
    }

    public List<Role> getAllEnabled(){
        return roleRepository.findAllByEnabled(true);
    }

    public Role getById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public boolean save(RoleDTO roleDTO) throws DatabaseException {

        Role role = Role.builder().name(roleDTO.getName()).permissions(preparePermissions(roleDTO)).enabled(true).build();
        if(role.getName() != null && !role.getName().trim().isEmpty()){
            try {
                roleRepository.save(role);
                return true;
            }catch (Exception e) {
                throw new DatabaseException("duplicate");
            }
        }
        else {
            return false;
        }
    }

    public boolean update(RoleDTO roleDTO) throws DatabaseException {

        Role role = getById(roleDTO.getId());
        Role tempRole = Role.builder().id(roleDTO.getId()).name(roleDTO.getName())
                .permissions(preparePermissions(roleDTO)).enabled(role.isEnabled()).build();

        if(tempRole.getName() != null && !tempRole.getName().trim().isEmpty() && roleRepository.existsById(tempRole.getId())){
            try {
                roleRepository.save(tempRole);
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

        Role role = getById(id);
        if(roleRepository.existsById(role.getId()) && role.isEnabled()){
            role.setEnabled(false);
            roleRepository.save(role);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean enable(Long id) {

        Role role = getById(id);
        if(roleRepository.existsById(role.getId()) && !role.isEnabled()){
            role.setEnabled(true);
            roleRepository.save(role);
            return true;
        }
        else {
            return false;
        }
    }

    public RoleDTO prepareRoleDTO(final Long id) {
        Role role = getById(id);

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setEnabled(role.isEnabled());
        for(Permission permission: role.getPermissions()){
            if (permission.getName().equals("READ_ROLE"))
                roleDTO.setViewRoles(true);
            else if (permission.getName().equals("WRITE_ROLE"))
                roleDTO.setAddRoles(true);
            else if (permission.getName().equals("READ_USER"))
                roleDTO.setViewUsers(true);
            else if (permission.getName().equals("WRITE_USER"))
                roleDTO.setAddUsers(true);
            else if (permission.getName().equals("READ_PERSON"))
                roleDTO.setViewPersons(true);
            else if (permission.getName().equals("WRITE_PERSON"))
                roleDTO.setAddPersons(true);
            else if (permission.getName().equals("READ_CATEGORY"))
                roleDTO.setViewCategories(true);
            else if (permission.getName().equals("WRITE_CATEGORY"))
                roleDTO.setAddCategories(true);
            else if (permission.getName().equals("READ_TOOL"))
                roleDTO.setViewTools(true);
            else if (permission.getName().equals("WRITE_TOOL"))
                roleDTO.setAddTools(true);
            else if (permission.getName().equals("READ_PLAN_PROGRAM"))
                roleDTO.setViewPlanPrograms(true);
            else if (permission.getName().equals("WRITE_PLAN_PROGRAM"))
                roleDTO.setAddPlanPrograms(true);
        }

        return roleDTO;
    }

    public List<RoleDTO> prepareRoleDTOList(final List<Role> roles) {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        for(Role role : roles) {
            roleDTOS.add(prepareRoleDTO(role.getId()));
        }
        return roleDTOS;
    }

    private ArrayList<Permission> preparePermissions(final RoleDTO roleDTO) {

        ArrayList<Permission> permissions = new ArrayList<>();

        if(roleDTO.isViewRoles())
            permissions.add(permissionRepository.findByName("READ_ROLE"));
        if (roleDTO.isAddRoles())
            permissions.add(permissionRepository.findByName("WRITE_ROLE"));
        if (roleDTO.isViewUsers())
            permissions.add(permissionRepository.findByName("READ_USER"));
        if (roleDTO.isAddUsers())
            permissions.add(permissionRepository.findByName("WRITE_USER"));
        if (roleDTO.isViewPersons())
            permissions.add(permissionRepository.findByName("READ_PERSON"));
        if (roleDTO.isAddPersons())
            permissions.add(permissionRepository.findByName("WRITE_PERSON"));
        if (roleDTO.isViewCategories())
            permissions.add(permissionRepository.findByName("READ_CATEGORY"));
        if (roleDTO.isAddCategories())
            permissions.add(permissionRepository.findByName("WRITE_CATEGORY"));
        if (roleDTO.isViewTools())
            permissions.add(permissionRepository.findByName("READ_TOOL"));
        if (roleDTO.isAddTools())
            permissions.add(permissionRepository.findByName("WRITE_TOOL"));
        if (roleDTO.isViewPlanPrograms())
            permissions.add(permissionRepository.findByName("READ_PLAN_PROGRAM"));
        if (roleDTO.isAddPlanPrograms())
            permissions.add(permissionRepository.findByName("WRITE_PLAN_PROGRAM"));

        return permissions;
    }
}