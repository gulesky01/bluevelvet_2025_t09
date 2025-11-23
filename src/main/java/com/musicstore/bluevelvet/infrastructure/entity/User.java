package com.musicstore.bluevelvet.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Entidade que representa um usuário na aplicação BlueVelvet Music Store.
 */
@Entity // Marca a classe como uma entidade JPA
@Table(schema = "db", name = "users") // Nome da tabela no banco de dados (é comum usar o plural e evitar "user" por ser palavra reservada em alguns bancos)
@Data // Lombok: Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Lombok: Construtor sem argumentos
@AllArgsConstructor // Lombok: Construtor com todos os argumentos
public class User {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estratégia de geração de ID (auto-incremento)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true) // O email deve ser único
    private String email;

    @Column(nullable = false)
    private String password; // Senha criptografada

    @Column(nullable = false)
    private String role; // Role do usuário (Administrator, Sales Manager, etc.)

    @Column(nullable = false)
    private int enabled;


    // Você pode adicionar mais campos aqui se necessário (ex: createdAt, enabled)
}