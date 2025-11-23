package com.musicstore.bluevelvet.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Marca a classe como uma entidade JPA
@Table(schema = "db", name = "authorities") // Nome da tabela no banco de dados (é comum usar o plural e evitar "user" por ser palavra reservada em alguns bancos)
@Data // Lombok: Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Lombok: Construtor sem argumentos
@AllArgsConstructor // Lombok: Construtor com todos os argumentos
public class Authority {

    @Id
    private long id;

    @Column(nullable=false)
    private String authority;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ref_user")
    private User owner;

}
