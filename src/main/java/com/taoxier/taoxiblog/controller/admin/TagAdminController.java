package com.taoxier.taoxiblog.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.entity.Tag;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.TagService;
import com.taoxier.taoxiblog.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ：标签后台管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class TagAdminController {
    @Autowired
    BlogService blogService;
    @Autowired
    TagService tagService;

    /**
    * @Description 获取标签列表
     * @param pageNum
     * @param pageSize
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/tags")
    public ResultVO tags(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "id desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PageInfo<Tag> pageInfo = new PageInfo<>(tagService.getTagList());
        return ResultVO.ok("请求成功", pageInfo);
    }

    /**
    * @Description 添加新标签
     * @param tag
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("添加标签")
    @PostMapping("/tag")
    public ResultVO saveTag(@RequestBody Tag tag) {
        return getResult(tag, "save");
    }

    /**
    * @Description 修改标签
     * @param tag
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("修改标签")
    @PutMapping("/tag")
    public ResultVO updateTag(@RequestBody Tag tag) {
        return getResult(tag, "update");
    }

    /**
    * @Description 按id删除标签
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("删除标签")
    @DeleteMapping("/tag")
    public ResultVO delete(@RequestParam Long id) {
        //删除存在博客关联的标签后，该博客的查询会出异常
        int num = blogService.countBlogByTagId(id);
        if (num != 0) {
            return ResultVO.error("已有博客与此标签关联，不可删除");
        }
        tagService.deleteTagById(id);
        return ResultVO.ok("删除成功");
    }

    /**
     * @Description 执行标签添加或更新操作：校验参数是否合法，标签是否已存在
     * @param tag
     * @param type
     * @Author: taoxier
     * @Date: 2025/5/8
     * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
     */
    private ResultVO getResult(Tag tag, String type) {
        if (StringUtils.isEmpty(tag.getName())) {
            return ResultVO.error("参数不能为空");
        }
        //查询标签是否已存在
        Tag tag1 = tagService.getTagByName(tag.getName());
        //如果 tag1.getId().equals(tag.getId()) == true 就是更新标签
        if (tag1 != null && !tag1.getId().equals(tag.getId())) {
            return ResultVO.error("该标签已存在");
        }
        if ("save".equals(type)) {
            tagService.saveTag(tag);
            return ResultVO.ok("添加成功");
        } else {
            tagService.updateTag(tag);
            return ResultVO.ok("更新成功");
        }
    }
}
