package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.exception.NotFoundException;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.CategoryMapper;
import com.taoxier.taoxiblog.model.entity.Category;
import com.taoxier.taoxiblog.service.CategoryService;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    TagService tagService;
    @Autowired
    RedisService redisService;

    /**
    * @Description 查找全部分类
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Category>
    */
    @Override
    public List<Category> getCategoryList(){
        return this.list();
    }

    /**
    * @Description 获取分类名list
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Category>
    */
    @Override
    public List<Category> getCategoryNameList() {
        String redisKey = RedisKeyConstants.CATEGORY_NAME_LIST;
        List<Category> categoryListFromRedis = redisService.getListByKey(redisKey);
        if (categoryListFromRedis != null) {
            return categoryListFromRedis;
        }
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.select("category_name").orderByDesc("id");
        List<Category> categoryList = categoryMapper.selectList(wrapper);
        redisService.saveListToValue(redisKey, categoryList);
        return categoryList;
    }

    /**
    * @Description 添加分类
     * @param category
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCategory(Category category) {
        if (!save(category)) {
            throw new PersistenceException("分类添加失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstants.CATEGORY_NAME_LIST);
    }

    /**
    * @Description 根据id查找分类
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: com.taoxier.taoxiblog.model.entity.Category
    */
    @Override
    public Category getCategoryById(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new NotFoundException("分类不存在");
        }
        return category;
    }

    /**
    * @Description 根据分类名查找分类
     * @param name
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: com.taoxier.taoxiblog.model.entity.Category
    */
    @Override
    public Category getCategoryByName(String name) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name", name);
        return categoryMapper.selectOne(wrapper);
    }

    /**
    * @Description 根据id删除分类
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCategoryById(Long id) {
        if (removeById(id) != true) {
            throw new PersistenceException("删除分类失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstants.CATEGORY_NAME_LIST);
    }

    /**
    * @Description 更新分类
     * @param category
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCategory(Category category) {
        if (!updateById(category)) {
            throw new PersistenceException("分类更新失败");
        }
        redisService.deleteCacheByKey(RedisKeyConstants.CATEGORY_NAME_LIST);
        //修改了分类名，可能有首页文章关联了分类，也要更新首页缓存
        redisService.deleteCacheByKey(RedisKeyConstants.HOME_BLOG_INFO_LIST);
    }

}
