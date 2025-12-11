package ru.kata.spring.boot_security.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Optional;
@Repository
//JpaRepository - save(), findById(), findAll(), delete(), и т.д
public interface RoleRepository extends JpaRepository<Role, Long> { //JpaRepository предоставляет методы CRUD, чтоб мы их не переписывали по новой
    Optional<Role> findByName(String name);         //только этот метод дописываем, так как его нет в JpaRepository
}