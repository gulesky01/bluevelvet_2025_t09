package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.RegisterRequest;
import com.musicstore.bluevelvet.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import jakarta.validation.Valid; // Importante para validação do DTO
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Para lidar com erros de validação
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Log4j2
@Controller
@RequiredArgsConstructor // Para injetar UserService
public class RegisterController {

    private final com.musicstore.bluevelvet.domain.service.UserService userService; // Injetar o Serviço de Usuário

    // Método para exibir o formulário (Já existente, atualizado para adicionar o objeto)
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        log.info("Rendering register page");
        // Adiciona um objeto RegisterRequest vazio para o Thymeleaf preencher
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    // NOVO: Método para processar o envio do formulário
    @PostMapping("/register")
    public String registerNewUser(@ModelAttribute("registerRequest") @Valid RegisterRequest request,
                                  BindingResult result, // <--- 1. Captura os erros
                                  Model model) {

        // 1. Verificar Erros de Validação (Ex: senha < 8 caracteres, campo vazio)
        if (result.hasErrors()) {
            log.warn("Validation errors during user registration: {}", result.getAllErrors());
            // 2. Se houver erros, retorna para a mesma página ('register.html')
            //    O Thymeleaf usa 'result' para exibir os erros nos <span> com th:errors
            return "register";
        }

        try {
            // ... (Chamar o Serviço e Salvar)
            userService.registerNewUser(request);
            log.info("User registered successfully: {}", request.getEmail());

            // 3. Redirecionar para a página de login após o sucesso
            return "redirect:/login";

        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage());
            // 4. Se houver erro do Service (ex: email já existe), adiciona a mensagem de erro
            model.addAttribute("errorMessage", "Erro ao registrar usuário. O email pode já estar em uso.");
            return "register";
        }
    }
}
