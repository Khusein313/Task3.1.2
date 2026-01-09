package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //https://stackoverflow.com/questions/66919053/how-to-get-user-from-database-for-login-with-spring-boot-and-spring-data-jpa (информация о @Query по ссылке)
    @Query("Select u from User u left join fetch u.roles where u.email=:email")
    Optional<User> findByEmail(String email);
}