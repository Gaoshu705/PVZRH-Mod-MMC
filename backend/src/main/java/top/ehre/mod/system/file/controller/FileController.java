package top.ehre.mod.system.file.controller;

import top.ehre.mod.system.file.domain.dto.FileAddDTO;
import top.ehre.mod.system.file.domain.dto.FilePageDTO;
import top.ehre.mod.system.file.domain.dto.FileUpdateDTO;
import top.ehre.mod.system.file.domain.vo.FileVO;
import top.ehre.mod.system.file.service.FileService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 文件表 前端控制器
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private FileService fileService;


    @PostMapping("upload")
    @PreAuthorize("hasAuthority('system:file:upload')")
    public Result fileUpload(@RequestParam MultipartFile file,
                             @RequestParam("folderType") Byte folderType) {
        return fileService.fileUpload(file, folderType);
    }


    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:file:get')")
    public Result page(@RequestBody FilePageDTO filePageDTO) {
        PageResult<FileVO> page = fileService.page(filePageDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:file:add')")
    public Result add(@RequestBody FileAddDTO fileAddDTO) {
        boolean added = fileService.add(fileAddDTO);
        return Result.info(added, null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:file:del')")
    public Result delete(@PathVariable("id") String id) {
        boolean deleted = fileService.delete(id);
        return Result.info(deleted, null);
    }

    @PostMapping("/batchDelete")
    @PreAuthorize("hasAuthority('system:file:del')")
    public Result batchDelete(@RequestBody List<String> ids) {
        boolean deleted = fileService.batchDelete(ids);
        return Result.info(deleted, null);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:file:upd')")
    public Result update(@RequestBody FileUpdateDTO fileUpdateDTO) {
        boolean updated = fileService.update(fileUpdateDTO);
        return Result.info(updated, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:file:get')")
    public Result get(@PathVariable("id") String id) {
        FileVO fileVO = fileService.get(id);
        return Result.success(fileVO);
    }

}
