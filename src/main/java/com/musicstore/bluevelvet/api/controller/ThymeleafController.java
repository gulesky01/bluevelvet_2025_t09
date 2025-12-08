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
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @PreAuthorize("hasRole('Administrator') or hasRole('Editor')")
    @GetMapping("/categories")
    public String listCategories(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "") String search
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        model.addAttribute("username", username);
        model.addAttribute("role", role);

        Sort.Direction direction = dir.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        Page<Category> rootCategories;

        if (search.isEmpty()) {
            // Sem busca: apenas categorias raiz
            rootCategories = categoryRepository.findByParentIdIsNull(pageable);

        } else {
            // Com busca: filtrar APENAS categorias raiz manualmente
            Page<Category> filtered = categoryRepository.findByNameContainingIgnoreCase(search, pageable);

            // Manter só os ROOTS
            List<Category> onlyRoots = filtered
                    .stream()
                    .filter(cat -> cat.getParentId() == null)
                    .collect(Collectors.toList());

            rootCategories = new PageImpl<>(
                    onlyRoots,
                    pageable,
                    onlyRoots.size()
            );
        }

        // Buscar todas as subcategorias de cada root
        Map<Long, List<Category>> childrenMap = new HashMap<>();

        rootCategories.forEach(root -> {
            List<Category> children = categoryRepository.findByParentId(root.getId(), Pageable.unpaged()).getContent();
            childrenMap.put(root.getId(), children);
        });

        model.addAttribute("categories", rootCategories);
        model.addAttribute("childrenMap", childrenMap);

        // manter UI
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("search", search);

        return "category-list";
    }



    // FORM NOVA CATEGORIA
    @PreAuthorize("hasRole('Administrator')")
    @GetMapping("/categories/new")
    public String newCategory(Model model) {

        model.addAttribute("category", new CategoryRequest());
        model.addAttribute("parents", categoryRepository.findAll());

        return "category-form";
    }
    //SALVAR (criar/editar)
    @PreAuthorize("hasAnyRole('Administrator','Editor')")
    @PostMapping("/categories/save")
    public String saveCategory(
            @ModelAttribute("category") CategoryRequest request,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model
    ) {

        boolean isEditing = request.getId() != null;

        // -------------- Validar duplicidade (apenas CREATE)
// -------------- Validar duplicidade (apenas CREATE)
        if (!isEditing) {
            boolean exists;

            if (request.getParentId() == null) {
                // Categoria raiz
                exists = categoryRepository.existsByNameIgnoreCase(request.getName());
            } else {
                // Subcategoria
                exists = categoryRepository.existsByNameIgnoreCaseAndParentId(
                        request.getName(),
                        request.getParentId()
                );
            }

            if (exists) {
                model.addAttribute("errorMessage", "Category name already exists.");
                model.addAttribute("parents", categoryRepository.findAll());
                return "category-form";
            }
        }


        try {
            categoryService.saveCategory(request, imageFile);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("parents", categoryRepository.findAll());
            return "category-form";
        }

        return "redirect:/categories";
    }



    // FORM EDITAR (APENAS ADMIN)
    @PreAuthorize("hasAnyRole('Administrator','Editor')")
    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        CategoryRequest request = new CategoryRequest(
                category.getId(),
                category.getName(),
                category.getParentId(),
                category.getEnabled()
        );

        model.addAttribute("category", request);

        // ✅ Ajustado para o nome real do campo
        model.addAttribute("currentImage", category.getPicture_uuid());

        model.addAttribute("parents", categoryRepository.findAll());

        return "category-form";
    }

    // Ativar / Desativar
    @PreAuthorize("hasAnyRole('Administrator','Editor')")
    @GetMapping("/categories/toggle/{id}")
    public String toggleCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setEnabled(!category.getEnabled());
        categoryRepository.save(category);
        return "redirect:/categories";
    }

    // Deletar
    @PreAuthorize("hasAnyRole('Administrator','Editor')")
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }




}
