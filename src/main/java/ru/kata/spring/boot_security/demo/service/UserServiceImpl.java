package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*Это метод который нам даёт UserDetailsService
      Мы можем передать системе 'username' параметр и он нам выдаст пользователя
         У выданного пользователя через интерфейс UserService мы можем получать его данные
         (Имя = getUsername() /Пароль = getPassword() /ПраваДоступа getAuthorities и доп. boolean-методы о статусе акк-нта*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {             //если не найдёшь такого юзера то выброси исключение!
            throw new UsernameNotFoundException(String.format("User '%s' not found", username)); //вывод "User 'username' not found"
        }

        return new org.springframework.security.core.userdetails.User(      //Берем юзера из базы и переводим его в язык понятный для Spring
                user.getUsername(),                                 //Указываем Спрингу имя пользователя
                user.getPassword(),                                 //Указываем Спрингу пароль пользователя
                mapRolesToAuthorities(user.getRoles())          //Указываем Спрингу Права юзера (из метода ниже!)
        );

    }

    //Метод позволяет из переданного списка 'roles' получить GrantedAuthority
    //Это обязательно, т.к Спринг понимает только так данные о юзере
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles
                .stream()                           //преобразуем коллекцию ролей в стрим
                .map(r -> new SimpleGrantedAuthority(       // Переводим все роли в GrantedAuthorities
                        r.getAuthority()))                       //Берем имя роли в качестве строки
                .collect(Collectors.toList());              //делаем маппинг роли в List<GrantedAuthorities>
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User createUser(User user, Set<Role> roles) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roleSet = new HashSet<>();
        for (Role role : roles) {
            Role newRole = role;
            if (newRole != null) {
                roleSet.add(newRole);
            }
        }
        user.setRoles(roleSet);
        return user;
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userService = (UserDetails) authentication.getPrincipal();
        return (User) authentication.getPrincipal();
    }

    @Override
    @Transactional
    public void updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User  not found"));
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}