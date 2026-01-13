package ru.kata.spring.boot_security.demo.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitUsers {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public InitUsers(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    User admin = new User("admin", "$2a$12$dXM5XTfXkZKgGVgeJF9Rh.d3SmCdWtXGWOiyEae1QA687MydvZRHK", "admin@mail.ru");
    User user = new User("user", "$2a$12$reYBI5v/tqYCCt4VLywPEeAEf/do/lnzBwXO16W7fFm3o/PwqlusC", "user@mail.ru");
    Role roleAdmin = new Role("ROLE_ADMIN");
    Role roleUser = new Role("ROLE_USER");
    Set<Role> setAdmin = new HashSet<>();
    Set<Role> setUser = new HashSet<>();

    @PostConstruct
    public void getInitializationTable() {
        roleRepository.save(roleAdmin);
        roleRepository.save(roleUser);
        setAdmin.add(roleAdmin);
        setAdmin.add(roleUser);
        admin.setRoles(setAdmin);
        userRepository.save(admin);
        setUser.add(roleUser);
        user.setRoles(setUser);
        userRepository.save(user);
    }
}