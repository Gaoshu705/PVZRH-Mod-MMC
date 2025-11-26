package top.ehre.mod.system.userRole.controller;

import top.ehre.mod.system.userRole.domain.dto.UserRoleAddDTO;
import top.ehre.mod.system.userRole.domain.dto.UserRolePageDTO;
import top.ehre.mod.system.userRole.domain.dto.UserRoleUpdateDTO;
import top.ehre.mod.system.userRole.domain.vo.UserRoleVO;
import top.ehre.mod.system.userRole.service.UserRoleService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户角色关系表 前端控制器
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@RestController
@RequestMapping("/user-role")
public class UserRoleController {
    @Resource
    private UserRoleService userRoleService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:userRole:get')")
    public Result page(@RequestBody UserRolePageDTO userRolePageDTO) {
        PageResult<UserRoleVO> page = userRoleService.page(userRolePageDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:userRole:add')")
    public Result add(@RequestBody UserRoleAddDTO userRoleAddDTO) {
        boolean added = userRoleService.add(userRoleAddDTO);
        return Result.info(added, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:userRole:del')")
    public Result delete(@PathVariable("id") String id) {
        boolean deleted = userRoleService.delete(id);
        return Result.info(deleted, null);
    }

    @PostMapping("/batchDelete")
    @PreAuthorize("hasAuthority('system:userRole:del')")
    public Result batchDelete(@RequestBody List<String> ids) {
        boolean deleted = userRoleService.batchDelete(ids);
        return Result.info(deleted, null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:userRole:upd')")
    public Result update(@RequestBody UserRoleUpdateDTO userRoleUpdateDTO) {
        boolean updated = userRoleService.update(userRoleUpdateDTO);
        return Result.info(updated, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:userRole:get')")
    public Result get(@PathVariable("id") String id) {
        UserRoleVO userRoleVO = userRoleService.get(id);
        return Result.success(userRoleVO);
    }

}
