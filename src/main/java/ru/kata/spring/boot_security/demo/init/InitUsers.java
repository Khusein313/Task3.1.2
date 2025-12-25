package ru.kata.spring.boot_security.demo.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Optional;

@Component
public class InitUsers implements CommandLineRunner {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    @Autowired
    public InitUsers(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //метод от интерфейса
    @Override
    public void run(String... args) {
        Role admin = createRoleIfNotExists("ROLE_ADMIN");
        Role user = createRoleIfNotExists("ROLE_USER");


        createUserIfNotExists("admin", "admin@mail.ru", "admin", admin);
        createUserIfNotExists("user", "user@gmail.com", "user", user);
        createUserIfNotExists("asd", "asd@mail.ru", "asd", admin);
        createUserIfNotExists("usd", "usd@mail.ru", "usd", user);
    }

    private Role createRoleIfNotExists(String roleName) {
        Optional<Role> existingRole = roleRepository.findByName(roleName);
        if (existingRole.isPresent()) {
            return existingRole.get();
        } else {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        }
    }

    private void createUserIfNotExists(String username, String email, String password, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }
}