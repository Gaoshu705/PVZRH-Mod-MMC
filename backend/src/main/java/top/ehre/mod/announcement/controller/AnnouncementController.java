package top.ehre.mod.announcement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.ehre.mod.announcement.service.AnnouncementService;

import top.ehre.mod.announcement.domain.vo.AnnouncementVO;
import top.ehre.mod.announcement.domain.dto.AnnouncementPageDTO;
import top.ehre.mod.announcement.domain.dto.AnnouncementAddDTO;
import top.ehre.mod.announcement.domain.dto.AnnouncementUpdateDTO;

import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;

import java.util.List;


/**
 * 公告信息表 前端控制器
 *
 * @author LibrhHp_0928
 * @since 2026-06-21
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    @Resource
    private AnnouncementService announcementService;

    @PostMapping("/page")
    @PreAuthorize("hasAuthority('business:announcement:get')")
    public Result page(@RequestBody AnnouncementPageDTO announcementPageDTO) {
        PageResult<AnnouncementVO> page = announcementService.page(announcementPageDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('business:announcement:add')")
    public Result add(@RequestBody AnnouncementAddDTO announcementAddDTO) {
        boolean added = announcementService.add(announcementAddDTO);
        return Result.info(added, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:announcement:get')")
    public Result delete(@PathVariable("id") String id) {
        boolean deleted = announcementService.delete(id);
        return Result.info(deleted, null);
    }

    @PostMapping("/batchDelete")
    @PreAuthorize("hasAuthority('business:announcement:del')")
    public Result batchDelete(@RequestBody List<String> ids) {
        boolean deleted = announcementService.batchDelete(ids);
        return Result.info(deleted, null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('business:announcement:upd')")
    public Result update(@RequestBody AnnouncementUpdateDTO announcementUpdateDTO) {
        boolean updated = announcementService.update(announcementUpdateDTO);
        return Result.info(updated, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:announcement:get')")
    public Result get(@PathVariable("id") String id) {
        AnnouncementVO announcementVO = announcementService.get(id);
        return Result.success(announcementVO);
    }

}
