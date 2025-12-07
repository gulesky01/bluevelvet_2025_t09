package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.request.ProductRequest;
import com.musicstore.bluevelvet.api.response.ProductResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.ProductService;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import com.musicstore.bluevelvet.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ThymeleafController {

    private final ProductService service;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    // ==== HOME ====
    @GetMapping("/")
    public String getHomePage() {
        return "index";
    }

    // ==== DASHBOARD ====
    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "name") String sort,
                            @RequestParam(defaultValue = "asc") String dir,
                            @RequestParam(defaultValue = "") String search,
                            Authentication authentication) {

        // Usuário logado
        String username = authentication.getName();
        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        model.addAttribute("username", username);
        model.addAttribute("role", role);

        // Paginação
        Sort.Direction direction = dir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<Category> categories;

        if (search.isEmpty()) {
            categories = categoryRepository.findAll(pageable);
        } else {
            categories = categoryRepository.findByNameContainingIgnoreCase(search, pageable);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        return "dashboard";
    }


    @GetMapping("/categories/reset")
    public String resetCategories() {
        categoryService.resetCategories(); // você implementa isso
        return "redirect:/dashboard";
    }



    // ==== PRODUCTS ====
    @GetMapping("/product/{id}")
    public String getProductView(@PathVariable("id") Long id, Model model) {
        ProductResponse response = service.findById(id);
        model.addAttribute("product", response);
        return "view-product";
    }

    @GetMapping("/product")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new ProductRequest());
        return "create-product";
    }

    @PostMapping("/product")
    public String createProduct(@ModelAttribute ProductRequest request) {
        ProductResponse response = service.createProduct(request);
        return "redirect:/product/%d".formatted(response.getId());
    }

    // ==== CATEGORIES ====

    //LISTAR
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "category-list";
    }

    //FORMULÁRIO NOVA
    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        return "category-form";
    }

    //SALVAR (criar/editar)
    @PostMapping("/categories/save")
    public String saveCategory(
            @ModelAttribute("category") CategoryRequest category,
            Model model
    ) {

        // Verifica duplicidade
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            model.addAttribute("errorMessage", "Categoria já existente.");
            return "category-form";
        }

        // CHAMA O SERVICE CORRETO
        categoryService.createCategory(category);

        return "redirect:/categories";
    }



    // Editar
    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).orElseThrow();
        model.addAttribute("category", category);
        return "category-form";
    }

    // Ativar / Desativar
    @GetMapping("/categories/toggle/{id}")
    public String toggleCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setEnabled(!category.getEnabled());
        categoryRepository.save(category);
        return "redirect:/categories";
    }

    // Deletar
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }




}
