package com.musicstore.bluevelvet.domain.service.impl;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.domain.exception.CategoryNotFoundException;
import com.musicstore.bluevelvet.domain.exception.InvalidDataOperationException;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import com.musicstore.bluevelvet.infrastructure.repository.CategoryRepository;
import com.musicstore.bluevelvet.domain.service.MassStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private CategoryResponse responseFromEntity(Category entity){
        CategoryResponse response = new CategoryResponse();
        response.setName(entity.getName());
        response.setId(entity.getId());
        response.setParentId(entity.getParentId());
        if(entity.getPicture_uuid() != null) {
            response.setPictureUrl(getBasePictureUrl() + entity.getPicture_uuid() + ".webp");
        }
        response.setEnabled(entity.getEnabled());
        return response;
    }

    private Category entityFromRequest(CategoryRequest request){
        Category category= new Category();
        //HACK(?): setId() throws nullpointer exception, although parent_id does not...
        if(request.getId() != null)
            category.setId (request.getId());
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setEnabled(request.getEnabled());
        return category;
    }
    
    
    public CategoryResponse findById(Long id){
        Optional<Category> fetched = repository.findById(id);
        if (fetched.isEmpty())
            throw new CategoryNotFoundException("Categoria não existe.");
        Category existing=fetched.get();
        CategoryResponse response = new CategoryResponse();
        response.setName(existing.getName());
        response.setId(existing.getId());
        response.setParentId(existing.getParentId());
        if(existing.getPicture_uuid() != null) {
            response.setPictureUrl(getBasePictureUrl() + existing.getPicture_uuid() + ".webp");
        }
        return response;
    }

    public Page<CategoryResponse> findAll(Pageable pageable){

        return repository.findAll(pageable).map(this::responseFromEntity);

    }

    public Page<CategoryResponse> findByName(String name,Pageable pageable){
        return repository.findByNameLike("%" + name + "%", pageable).map(this::responseFromEntity);
    }

    public void deleteById(Long id){
        Optional<Category> fetched = repository.findById(id);
        if (fetched.isEmpty())
            throw new CategoryNotFoundException("Categoria não existe.");
        repository.delete(fetched.get());
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = entityFromRequest(request);
        if(request.getEnabled() == null){
            request.setEnabled(true);
        }
        category = repository.save(category);
        return responseFromEntity(category);
    }
    public CategoryResponse updateCategory(Long id, CategoryRequest request){

        Optional<Category> fetched = repository.findById(id);
        if (fetched.isEmpty())
            throw new CategoryNotFoundException("Categoria não existe.");
        Category existing=fetched.get();
        if (request.getName() == null){
            throw new InvalidDataOperationException("Campo \"name\" obrigatório.");
        }
        existing.setName(request.getName());
        existing.setParentId(request.getParentId());
        existing = repository.save(existing);
        CategoryResponse response = responseFromEntity(existing);
        return response;

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

        CategoryResponse response = responseFromEntity(existing);
        response.setPictureUrl(getBasePictureUrl() + generatedFileName + ".webp");
        return response;
        
    }

    public Page<CategoryResponse> findByProductId(Long id, Pageable pageable){
        return repository.findByProductId(id, pageable).map(this::responseFromEntity);
    }

    public Page<CategoryResponse> findByParentId(Long id, Pageable pageable){
        return repository.findByParentId(id, pageable).map(this::responseFromEntity);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }



}
