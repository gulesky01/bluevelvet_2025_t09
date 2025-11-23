package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.RegisterRequest;
import com.musicstore.bluevelvet.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
class LoginController {
    @GetMapping("/login")
    String login() {
        return "login";
    }
}