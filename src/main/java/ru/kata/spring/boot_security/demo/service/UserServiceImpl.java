package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void updateUser(Long id, User user) {
        User oldUser = userRepository.findById(id).orElse(null);
        assert oldUser != null;
        oldUser.setUsername(user.getUsername());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(user.getPassword());
        oldUser.setRoles(user.getRoles());
        userRepository.save(oldUser);
    }

    @Override
    public User getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }


    @Override
    public User createUser(User user, Set<Role> roles) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roleSet = new HashSet<>();
        for (Role role : roles) {
            if (role != null) {
                roleSet.add(role);
            }
        }
        user.setRoles(roleSet);
        return user;
    }

    @Override
    public User updateUser(User user, Set<Role> roles, Long id) {
        User oldUser = getUserById(id);
        String oldPassword = oldUser.getPassword();
        String newPassword = user.getPassword();

        if (newPassword != null && !newPassword.isEmpty() && !passwordEncoder.matches(newPassword, oldPassword)) {

            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            user.setPassword(oldPassword);
        }

        Set<Role> roleSet = new HashSet<>();

        if (roles == null || roles.isEmpty()) {
            roleSet = oldUser.getRoles();
        } else {
            for (Role role : roles) {
                if (role != null) {
                    roleSet.add(role);
                }
            }
        }

        user.setRoles(roleSet);
        return user;
    }

}


/*   Это метод который нам даёт UserDetailsService
     Мы можем передать системе 'username' параметр и он нам выдаст пользователя
        У выданного пользователя через интерфейс UserService мы можем получать его данные
        (Имя = getUsername() /Пароль = getPassword() /ПраваДоступа getAuthorities и доп. boolean-методы о статусе акк-нта
   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findByUsername(username);
       if (user == null) {             //если не найдёшь такого юзера то выброси исключение!
           throw new UsernameNotFoundException(String.format("User '%s' not found", username)); //вывод "User 'username' not found"
       }

       return new org.springframework.security.core.userdetails.User(      //Берем юзера из базы и переводим его в язык понятный для Spring
               user.getUsername(),                                 //Указываем Spring имя пользователя
               user.getPassword(),                                 //Указываем Spring пароль пользователя
               mapRolesToAuthorities(user.getRoles())          //Указываем Spring Права юзера (из метода ниже!)
       );

   }


   //Метод позволяет из переданного списка 'roles' получить GrantedAuthority
   //Это обязательно, т.к Spring понимает только так данные о юзере
   private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
       return roles
               .stream()                           //преобразуем коллекцию ролей в стрим
               .map(r -> new SimpleGrantedAuthority(       // Переводим все роли в GrantedAuthorities
                       r.getAuthority()))                       //Берем имя роли в качестве строки
               .collect(Collectors.toList());              //делаем маппинг роли в List<GrantedAuthorities>
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

 */

