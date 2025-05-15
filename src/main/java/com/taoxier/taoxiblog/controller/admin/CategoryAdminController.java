package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.entity.Category;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.CategoryService;
import com.taoxier.taoxiblog.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ：分类后台控制层
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class CategoryAdminController {
    @Autowired
    BlogService blogService;
    @Autowired
    CategoryService categoryService;

    /**
    * @Description 获取博客分类列表
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/categories")
    public ResultVO categories(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "id desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryService.getCategoryList());
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 添加新分类
     * @param category
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("添加分类")
    @PostMapping("/category")
//    @ApiOperation(value = "添加分类")
    public ResultVO saveCategory(@RequestBody Category category) {
        return getResult(category, "save");
    }

    /**
    * @Description 修改分类名称
     * @param category
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("修改分类")
    @PutMapping("/category")
    public ResultVO updateCategory(@RequestBody Category category) {
        return getResult(category, "update");
    }

    /**
    * @Description 按id删除分类
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("删除分类")
    @DeleteMapping("/category")
    public ResultVO delete(@RequestParam Long id) {
        //删除存在博客关联的分类后，该博客的查询会出异常
        int num = blogService.countBlogByCategoryId(id);
        if (num != 0) {
            return ResultVO.error("已有博客与此分类关联，不可删除");
        }
        categoryService.deleteCategoryById(id);
        return ResultVO.ok("删除成功");
    }

    /**
    * @Description 执行分类添加或更新操作：校验参数是否合法，分类是否已存在
     * @param category
     * @param type
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    private ResultVO getResult(Category category, String type) {
        if (StringUtils.isEmpty(category.getName())) {
            return ResultVO.error("分类名称不能为空");
        }
        //查询分类是否已存在
        Category category1 = categoryService.getCategoryByName(category.getName());
        //如果 category1.getId().equals(category.getId()) == true 就是更新分类
        if (category1 != null && !category1.getId().equals(category.getId())) {
            return ResultVO.error("该分类已存在");
        }
        if ("save".equals(type)) {
            categoryService.saveCategory(category);
            return ResultVO.ok("分类添加成功");
        } else {
            categoryService.updateCategory(category);
            return ResultVO.ok("分类更新成功");
        }
    }
}
