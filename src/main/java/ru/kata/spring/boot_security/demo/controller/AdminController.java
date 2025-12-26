package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String allUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("user", userService.getInfo());
        model.addAttribute("allRoles", roleService.findAll());
        return "users";
    }

    @PostMapping("/createUser")
    public String postCreateUser(@ModelAttribute("newUser") User user) {
        Set<Role> roles = user.getRoles();
        userService.saveUser(userService.createUser(user, roles));
        return "redirect:/admin/";
    }

    @GetMapping(value = "/update/{id}")
    public String getUpdate(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        return null;
    }

    @PostMapping(value = "/update")
    public String postUpdate(@ModelAttribute("user") User user, @RequestParam("id") Long id) {
        userService.updateUser(id, user);
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @PostMapping(value = "/delete")
    public String getDelete(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}