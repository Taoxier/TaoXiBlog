package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.entity.Category;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface CategoryService extends IService<Category> {
    List<Category> getCategoryList();

    List<Category> getCategoryNameList();

    @Transactional(rollbackFor = Exception.class)
    void saveCategory(Category category);

    Category getCategoryById(Long id);

    Category getCategoryByName(String name);

    @Transactional(rollbackFor = Exception.class)
    void deleteCategoryById(Long id);

    @Transactional(rollbackFor = Exception.class)
    void updateCategory(Category category);
}
