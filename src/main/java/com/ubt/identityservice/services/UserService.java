package com.ubt.identityservice.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ubt.identityservice.configurations.exceptions.DatabaseException;
import com.ubt.identityservice.entities.EmailNotification;
import com.ubt.identityservice.entities.Person;
import com.ubt.identityservice.entities.Role;
import com.ubt.identityservice.entities.User;
import com.ubt.identityservice.entities.UserRegistrationEmailModel;
import com.ubt.identityservice.entities.dtos.UserDTO;
import com.ubt.identityservice.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User loadUserByUsername(String email) {

        User user = userRepository.findByEmailAndEnabled(email, true);

        if (user != null)
            return user;

        return null;
    }

    public User loadUserByEmailAndPassword(String email, String password) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = userRepository.findByEmail(email);

        if (user != null) {
            boolean isPasswordMatch = passwordEncoder.matches(password, user.getPassword());
            return isPasswordMatch ? user : null;
        }
        return null;
    }

    public boolean updatePassword(String email, String password) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(password);
        User user = userRepository.findByEmail(email);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        return true;
    }

    public boolean activateUser(String email) {

        User user = userRepository.findByEmail(email);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    public List<User> getAllEnabled(){
        return userRepository.findAllByEnabled(true);
    }

    public List<User> getAllDisabled(){
        return userRepository.findAllByEnabled(false);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean save(UserDTO userDTO) throws DatabaseException {

        String randomPassword = RandomStringUtils.randomAlphanumeric(8);

        String encodedPassword = Base64.getEncoder().encodeToString(randomPassword.getBytes());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(randomPassword);

        Person person = personService.getById(userDTO.getPersonId());
        Role role = roleService.getById(userDTO.getRoleId());
        User user = User.builder().username(userDTO.getUsername()).email(userDTO.getEmail()).person(person).role(role)
                .password(encryptedPassword).enabled(false).build();
        if(StringUtils.isNotBlank(user.getUsername()) && StringUtils.isNotBlank(user.getEmail())
                && user.getPerson() != null && user.getRole() != null) {
            try {
                userRepository.save(user);

                final UserRegistrationEmailModel userRegistrationEmailModel = UserRegistrationEmailModel
                        .builder()
                        .firstName(user.getPerson().getFirstName())
                        .lastName(user.getPerson().getLastName())
                        .url("http://localhost:8080/welcome/"+encodedPassword)
                        .build();

                final EmailNotification emailNotification = EmailNotification
                        .builder()
                        .sender(senderEmail)
                        .receiver(user.getEmail())
                        .subject("Register")
                        .content(userRegistrationEmailModel)
                        .build();

                emailService.sendEmail(emailNotification);

                return true;
            }catch (Exception e) {
                throw new DatabaseException("duplicate");
            }
        }
        else {
            return false;
        }
    }

    public boolean update(UserDTO userDTO) throws DatabaseException {

        Person person = personService.getById(userDTO.getPersonId());
        Role role = roleService.getById(userDTO.getRoleId());
        User user = getById(userDTO.getId());

        if(StringUtils.isNotBlank(userDTO.getUsername()) && StringUtils.isNotBlank(userDTO.getEmail())
                &&  person != null && role != null){
            try {
                user.setUsername(userDTO.getUsername());
                user.setEmail(userDTO.getEmail());
                user.setPerson(person);
                user.setRole(role);
                userRepository.save(user);
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

        User user = getById(id);
        if(userRepository.existsById(user.getId()) && user.isEnabled()){
            user.setEnabled(false);
            userRepository.save(user);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean enable(Long id) {

        User user = getById(id);
        if(userRepository.existsById(user.getId()) && !user.isEnabled()){
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
        else {
            return false;
        }
    }

    public UserDTO prepareUserDTO(final Long id) {

        User user = getById(id);

        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getPerson().getFirstName())
                .lastName(user.getPerson().getLastName())
                .roleName(user.getRole().getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .image(user.getPerson().getImage() != null ? user.getPerson().getImage().getId() : null)
                .personId(user.getPerson().getId())
                .roleId(user.getRole().getId())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .tokenExpired(user.isTokenExpired())
                .enabled(user.isEnabled())
                .build();
    }

    public List<UserDTO> prepareUserDTOList(List<User> users) {
        List<UserDTO> userDTOS = new ArrayList<>();
        for(User user : users) {
            userDTOS.add(prepareUserDTO(user.getId()));
        }
        return userDTOS;
    }
}