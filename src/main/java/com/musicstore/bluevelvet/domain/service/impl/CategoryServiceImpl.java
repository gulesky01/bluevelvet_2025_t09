package com.musicstore.bluevelvet.domain.service.impl;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.exception.CategoryNotFoundException;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import com.musicstore.bluevelvet.infrastructure.repository.CategoryRepository;
import com.musicstore.bluevelvet.domain.service.MassStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final MassStorageService massStorage;

    //HACK: este valor mágico deveria ser um parâmetro em um arquivo de configuração
    // ou adquirido de um serviço de domínios
    // (para permitir redirecionamento em tempo real e CDN)
    private String getBasePictureUrl(){
        return "http://localhost:32791/pics/";
    }

    private final CategoryRepository repository;

    private CategoryResponse entityToResponse(Category entity){
        CategoryResponse response = new CategoryResponse();
        response.setName(entity.getName());
        response.setId(entity.getId());
        response.setParentId(entity.getParent_id());
        response.setPictureUrl(getBasePictureUrl() + entity.getPicture_uuid() + ".webp");
        return response;
    }
    
    
    public CategoryResponse findById(Long id){
        Optional<Category> fetched = repository.findById(id);
        if (fetched.isEmpty())
            throw new CategoryNotFoundException("Categoria não existe.");
        Category existing=fetched.get();
        CategoryResponse response = new CategoryResponse();
        response.setName(existing.getName());
        response.setId(existing.getId());
        response.setParentId(existing.getParent_id());
        response.setPictureUrl(getBasePictureUrl() + existing.getPicture_uuid() + ".webp");
        return response;
    }

    public Page<CategoryResponse> findAll(Pageable pageable){
        return null;
    }

    public void deleteById(Long id){

    }

    public CategoryResponse createCategory(CategoryRequest request){
        return null;
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request){
        return null;
    }

    public CategoryResponse setPictureOfCategory(Long id, MultipartFile file) throws Exception {

        Optional<Category> fetched = repository.findById(id);
        if (fetched.isEmpty())
            throw new CategoryNotFoundException("Categoria não existe.");
        Category existing=fetched.get();
        String generatedFileName = UUID.randomUUID().toString();

        //SUGESTÃO: VALIDAR SE O ARQUIVO ENVIADO REALMENTE É UM WEBP
        // (use alguma biblioteca de codec de imagens/fotos)

        //Save randomly-named file to picture mass storage
        byte[] fileData = file.getBytes();
        massStorage.addPicture(fileData, generatedFileName);

        //save picture identifier sequence as field on an entity at repository
        existing.setPicture_uuid(generatedFileName);
        repository.save(existing);

        CategoryResponse response = entityToResponse(existing);
        response.setPictureUrl(getBasePictureUrl() + generatedFileName + ".webp");
        return response;
        
    }


}
