package top.ehre.mod.mods.controller;

import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.ehre.mod.mods.domain.dto.ModsPageDTO;
import top.ehre.mod.mods.domain.vo.ModsVO;
import top.ehre.mod.mods.service.ModsService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;

import java.util.List;

/**
 * 公共接口
 * @author 19411
 * @date 2025/11/25
 */

@RestController
@RequestMapping("/public")
public class PublicController {
    @Resource
    private ModsService modsService;

    @GetMapping("/mod")
    public Result page() {
        return Result.success(modsService.getList());
    }

    @GetMapping("/mod/{id}")
    public Result get(@PathVariable("id") String id) {
        ModsVO modsVO = modsService.get(id);
        return Result.success(modsVO);
    }
}
