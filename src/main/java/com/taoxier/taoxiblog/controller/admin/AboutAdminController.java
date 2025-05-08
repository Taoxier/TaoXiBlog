package com.taoxier.taoxiblog.controller.admin;

import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.AboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description ：关于我控制层
 * @Author taoxier
 * @Date 2025/5/7
 */
@RestController
@RequestMapping("/admin")
public class AboutAdminController {
    @Autowired
    AboutService aboutService;

    /**
    * @Description 获取关于我页面配置
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/about")
    public ResultVO about(){
        return ResultVO.ok("请求成功",aboutService.getAboutSetting());
    }

    @OperationLogger("修改关于我页面")
    @PutMapping("/about")
    public ResultVO updateAbout(@RequestBody Map<String,String> map){
        aboutService.updateAbout(map);
        return ResultVO.ok("修改成功");
    }
}
