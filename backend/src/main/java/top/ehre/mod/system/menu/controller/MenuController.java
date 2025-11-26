package top.ehre.mod.system.menu.controller;

import top.ehre.mod.system.menu.domain.dto.MenuAddDTO;
import top.ehre.mod.system.menu.domain.dto.MenuPageDTO;
import top.ehre.mod.system.menu.domain.dto.MenuUpdateDTO;
import top.ehre.mod.system.menu.domain.vo.MenuVO;
import top.ehre.mod.system.menu.service.MenuService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 菜单表 前端控制器
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private MenuService menuService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:menu:get')")
    public Result page(@RequestBody MenuPageDTO menuPageDTO) {
        PageResult<MenuVO> page = menuService.page(menuPageDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result add(@RequestBody MenuAddDTO menuAddDTO) {
        boolean added = menuService.add(menuAddDTO);
        return Result.info(added, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:del')")
    public Result delete(@PathVariable("id") String id) {
        boolean deleted = menuService.delete(id);
        return Result.info(deleted, null);
    }

    @PostMapping("/batchDelete")
    @PreAuthorize("hasAuthority('system:menu:del')")
    public Result batchDelete(@RequestBody List<String> ids) {
        boolean deleted = menuService.batchDelete(ids);
        return Result.info(deleted, null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:menu:upd')")
    public Result update(@RequestBody MenuUpdateDTO menuUpdateDTO) {
        boolean updated = menuService.update(menuUpdateDTO);
        return Result.info(updated, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:get')")
    public Result get(@PathVariable("id") String id) {
        MenuVO menuVO = menuService.get(id);
        return Result.success(menuVO);
    }

}
