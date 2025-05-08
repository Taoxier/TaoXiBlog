package com.taoxier.taoxiblog.controller.admin;

import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.model.entity.SiteSetting;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description ：站点设置后台管理
 * @Author taoxier
 * @Date 2025/5/8
 */
@RestController
@RequestMapping("/admin")
public class SiteSettingAdminController {
    @Autowired
    SiteSettingService siteSettingService;

    /**
    * @Description 获取所有站点配置信息
     * @param
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/siteSettings")
    public ResultVO siteSettings() {
        Map<String, List<SiteSetting>> typeMap = siteSettingService.getList();
        return ResultVO.ok("请求成功", typeMap);
    }

    /**
    * @Description 修改、删除(部分配置可为空，但不可删除)、添加(只能添加一部分) 站点配置
     * @param map 包含所有站点信息更新后的数据 map => {settings=[更新后的所有配置List], deleteIds=[要删除的配置id List]}
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @OperationLogger("更新站点配置信息")
    @PostMapping("/siteSettings")
    public ResultVO updateAll(@RequestBody Map<String, Object> map) {
        List<LinkedHashMap> siteSettings = (List<LinkedHashMap>) map.get("settings");
        List<Integer> deleteIds = (List<Integer>) map.get("deleteIds");
        siteSettingService.updateSiteSetting(siteSettings, deleteIds);
        return ResultVO.ok("更新成功");
    }

    /**
    * @Description  查询网页标题后缀
     * @param
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @GetMapping("/webTitleSuffix")
    public ResultVO getWebTitleSuffix() {
        return ResultVO.ok("请求成功", siteSettingService.getWebTitleSuffix());
    }
}
