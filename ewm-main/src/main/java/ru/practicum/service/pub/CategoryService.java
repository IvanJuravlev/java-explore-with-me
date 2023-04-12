package ru.practicum.service.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.DuplicateDataException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.zip.DataFormatException;

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
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Category with id %x not found", categoryId));
        });
        if (categoryDto.getName().equals(category.getName())) {
            throw new DuplicateDataException("Same category name");
        }
        category.setName(categoryDto.getName());
        log.info("Category with id {} updated", categoryId);
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
}
