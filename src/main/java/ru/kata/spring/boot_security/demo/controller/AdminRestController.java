package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exeption_tregulov.NoSuchUserException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> admin() {
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        try {
            User user = userService.getUserById((long) id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchUserException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("users/roles")
    public  ResponseEntity<List<Role>> allRoles() {
        return new ResponseEntity<>(roleService.findAll(), HttpStatus.OK);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<HttpStatus> newUser(@RequestBody User user) {
        userService.saveUser(userService.createUser(user, user.getRoles()));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<HttpStatus> update(@RequestBody User user) {
        userService.updateUser(user.getId(), userService.updateUser(user, user.getRoles(), user.getId()));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/authUser")
    public ResponseEntity<User> authenticationUser() {
        User user = userService.getInfo();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}