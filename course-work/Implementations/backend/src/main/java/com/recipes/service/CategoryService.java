package com.recipes.service;

import com.recipes.dto.*;
import com.recipes.entity.Category;
import com.recipes.exception.ResourceNotFoundException;
import com.recipes.repository.CategoryRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public Page<CategoryResponse> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(this::toResponse);
    }

    public CategoryResponse findById(Long id) {
        return toResponse(repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id)));
    }

    public CategoryResponse create(CategoryRequest req) {
        Category c = new Category();
        c.setName(req.name());
        c.setDescription(req.description());
        c.setImageUrl(req.imageUrl());
        return toResponse(repo.save(c));
    }

    public CategoryResponse update(Long id, CategoryRequest req) {
        Category c = repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        c.setName(req.name());
        c.setDescription(req.description());
        c.setImageUrl(req.imageUrl());
        return toResponse(repo.save(c));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Category not found: " + id);
        repo.deleteById(id);
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription(),
            c.getImageUrl(), c.getCreatedAt(), c.getUpdatedAt());
    }
}
