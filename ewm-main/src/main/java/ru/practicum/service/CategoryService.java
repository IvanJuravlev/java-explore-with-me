package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.DuplicateDataException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = categoryRepository.save(CategoryMapper.CATEGORY_MAPPER.toCategory(categoryDto));
        log.info("Category created with id {}", category.getId());
        return CategoryMapper.CATEGORY_MAPPER.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Category with id %x not found", id));
        });
        if (categoryDto.getName().equals(category.getName())) {
            throw new DuplicateDataException("Same category name");
        }
        category.setName(categoryDto.getName());
        log.info("Category with id {} updated", id);
        return CategoryMapper.CATEGORY_MAPPER.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Category with id %x not found", id));
        });
        categoryRepository.deleteById(id);
        log.info("Category with id {} deleted", id);
    }

    public List<CategoryDto> findAll(Pageable pageable) {
        log.info("send category info");
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper.CATEGORY_MAPPER::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Category with id %x not found", id));
        });
        log.info("send category with id {}", id);
        return CategoryMapper.CATEGORY_MAPPER.toCategoryDto(category);
    }
}
