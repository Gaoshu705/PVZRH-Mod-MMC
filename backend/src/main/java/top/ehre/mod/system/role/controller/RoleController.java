package top.ehre.mod.system.role.controller;

import top.ehre.mod.system.role.domain.dto.RoleAddDTO;
import top.ehre.mod.system.role.domain.dto.RolePageDTO;
import top.ehre.mod.system.role.domain.dto.RoleUpdateDTO;
import top.ehre.mod.system.role.domain.vo.RoleVO;
import top.ehre.mod.system.role.service.RoleService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 角色表 前端控制器
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private RoleService roleService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:role:get')")
    public Result page(@RequestBody RolePageDTO rolePageDTO) {
        PageResult<RoleVO> page = roleService.page(rolePageDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result add(@RequestBody RoleAddDTO roleAddDTO) {
        boolean added = roleService.add(roleAddDTO);
        return Result.info(added, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:del')")
    public Result delete(@PathVariable("id") String id) {
        boolean deleted = roleService.delete(id);
        return Result.info(deleted, null);
    }

    @PostMapping("/batchDelete")
    @PreAuthorize("hasAuthority('system:role:del')")
    public Result batchDelete(@RequestBody List<String> ids) {
        boolean deleted = roleService.batchDelete(ids);
        return Result.info(deleted, null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:role:upd')")
    public Result update(@RequestBody RoleUpdateDTO roleUpdateDTO) {
        boolean updated = roleService.update(roleUpdateDTO);
        return Result.info(updated, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:get')")
    public Result get(@PathVariable("id") String id) {
        RoleVO roleVO = roleService.get(id);
        return Result.success(roleVO);
    }

}
