package top.ehre.mod.system.user.controller;

import top.ehre.mod.security.authentication.UserInfo;
import top.ehre.mod.system.user.domain.dto.RegisterDTO;
import top.ehre.mod.system.user.domain.dto.UserAddDTO;
import top.ehre.mod.system.user.domain.dto.UserPageDTO;
import top.ehre.mod.system.user.domain.dto.UserUpdateDTO;
import top.ehre.mod.system.user.domain.vo.UserVO;
import top.ehre.mod.system.user.service.UserService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户表 前端控制器
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:user:get')")
    public Result page(@RequestBody UserPageDTO userPageDTO) {
        PageResult<UserVO> page = userService.page(userPageDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result add(@RequestBody UserAddDTO userAddDTO) {
        boolean added = userService.add(userAddDTO);
        return Result.info(added, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:del')")
    public Result delete(@PathVariable("id") String id) {
        boolean deleted = userService.delete(id);
        return Result.info(deleted, null);
    }

    @PostMapping("/batchDelete")
    @PreAuthorize("hasAuthority('system:user:del')")
    public Result batchDelete(@RequestBody List<String> ids) {
        boolean deleted = userService.batchDelete(ids);
        return Result.info(deleted, null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:user:upd')")
    public Result update(@RequestBody UserUpdateDTO userUpdateDTO) {
        boolean updated = userService.update(userUpdateDTO);
        return Result.info(updated, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:get')")
    public Result get(@PathVariable("id") String id) {
        UserVO userVO = userService.get(id);
        return Result.success(userVO);
    }

    @GetMapping("/getUserInfo")
    public Result getUserInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo userInfo = userService.getUserInfo((String) principal);
        return Result.success(userInfo);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        return Result.success(userService.register(registerDTO));
    }

}
