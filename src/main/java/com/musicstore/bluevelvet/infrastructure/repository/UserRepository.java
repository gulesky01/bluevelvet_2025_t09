package com.musicstore.bluevelvet.infrastructure.repository;

import com.musicstore.bluevelvet.infrastructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Método mágico que o Spring cria sozinho para checar se o email existe
    boolean existsByEmail(String email);

    // Método para buscar usuário por email (usado no login)
    User findByEmail(String email);
}