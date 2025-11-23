package com.musicstore.bluevelvet.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Importações para validação de campos (boas práticas)
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data // Gera getters, setters, toString, hashCode e equals
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class RegisterRequest {

    // Campo 'Name'
    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    // Campo 'Email'
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    // Campo 'Password' - Requisito da US-1603: pelo menos 8 caracteres
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
    private String password;

    // Campo 'Role'
    @NotBlank(message = "A função (Role) é obrigatória.")
    private String role;


}