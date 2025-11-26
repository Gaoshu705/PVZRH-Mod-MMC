package top.ehre.mod.system.roleMenu.controller;

import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuAddDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuPageDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuUpdateDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleTreeUpdateDTO;
import top.ehre.mod.system.roleMenu.domain.vo.RoleMenuVO;
import top.ehre.mod.system.roleMenu.service.RoleMenuService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 角色菜单关系表 前端控制器
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@RestController
@RequestMapping("/role-menu")
public class RoleMenuController {
    @Resource
    private RoleMenuService roleMenuService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:roleMenu:get')")
    public Result page(@RequestBody RoleMenuPageDTO roleMenuPageDTO) {
        PageResult<RoleMenuVO> page = roleMenuService.page(roleMenuPageDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:roleMenu:add')")
    public Result add(@RequestBody RoleMenuAddDTO roleMenuAddDTO) {
        boolean added = roleMenuService.add(roleMenuAddDTO);
        return Result.info(added, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:roleMenu:del')")
    public Result delete(@PathVariable("id") String id) {
        boolean deleted = roleMenuService.delete(id);
        return Result.info(deleted, null);
    }

    @PostMapping("/batchDelete")
    @PreAuthorize("hasAuthority('system:roleMenu:del')")
    public Result batchDelete(@RequestBody List<String> ids) {
        boolean deleted = roleMenuService.batchDelete(ids);
        return Result.info(deleted, null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:roleMenu:upd')")
    public Result update(@RequestBody RoleMenuUpdateDTO roleMenuUpdateDTO) {
        boolean updated = roleMenuService.update(roleMenuUpdateDTO);
        return Result.info(updated, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:roleMenu:get')")
    public Result get(@PathVariable("id") String id) {
        RoleMenuVO roleMenuVO = roleMenuService.get(id);
        return Result.success(roleMenuVO);
    }


    @GetMapping("/selectedMenu/{id}")
    @PreAuthorize("hasAuthority('system:role:get')")
    public Result getSelectedMenu(@PathVariable String id) {
        return Result.success(roleMenuService.getSelectedMenu(id));
    }

    @PutMapping("/update/all")
    @PreAuthorize("hasAuthority('system:role:upd')")
    public Result updateRoleTree(@RequestBody RoleTreeUpdateDTO updateDTO){
        roleMenuService.updateRoleTree(updateDTO);
        return Result.success();
    }


}
