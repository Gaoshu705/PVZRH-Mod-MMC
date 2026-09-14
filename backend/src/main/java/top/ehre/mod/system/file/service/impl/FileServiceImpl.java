package top.ehre.mod.system.file.service.impl;

import top.ehre.mod.exception.BusinessException;
import top.ehre.mod.system.file.domain.dto.FileAddDTO;
import top.ehre.mod.system.file.domain.dto.FilePageDTO;
import top.ehre.mod.system.file.domain.dto.FileUpdateDTO;
import top.ehre.mod.system.file.domain.entity.FileEntity;
import top.ehre.mod.system.file.domain.enumeration.FolderTypeEnum;
import top.ehre.mod.system.file.domain.vo.FileVO;
import top.ehre.mod.system.file.mapper.FileMapper;
import top.ehre.mod.system.file.service.FileService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.PageUtil;
import top.ehre.mod.util.Result;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import top.ehre.mod.util.FileStoragePaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * 文件表 服务实现类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Resource
    FileMapper fileMapper;


    @Value("${file.localPath}")
    public String localPath;

    @Value("${file.network}")
    public String network;

    @Override
    public Result fileUpload(MultipartFile file, Byte folderType) {
        FolderTypeEnum folderTypeEnum = FolderTypeEnum.getByValue(folderType);
        if (folderTypeEnum == null) throw new BusinessException("不存在该文件夹类型");
        if (file == null || file.isEmpty()) throw new BusinessException("上传文件不能为空");
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.extName(originalFilename);
        if (extension == null || extension.isBlank()) {
            extension = "bin";
        }

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(originalFilename)
                .setFileSize((int) file.getSize())
                .setFolderType(folderType)
                .setFileType(extension);
        boolean saved = save(fileEntity);
        if (!saved) throw new BusinessException("添加失败");

        String newFilename = fileEntity.getFileId() + "." + extension;
        Path directory = FileStoragePaths.folder(localPath, folderTypeEnum.getDesc());
        Path dest = directory.resolve(newFilename);
        try {
            Files.createDirectories(directory);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!Files.isRegularFile(dest) || Files.size(dest) <= 0) {
                throw new IOException("文件写入后不存在: " + dest.toAbsolutePath());
            }
            log.info("file uploaded: {}", dest.toAbsolutePath());
        } catch (IOException e) {
            log.error("file upload failed, dest={}", dest.toAbsolutePath(), e);
            try {
                Files.deleteIfExists(dest);
            } catch (IOException ignored) {
            }
            removeById(fileEntity.getFileId());
            return Result.fail("上传失败");
        }
        FileVO fileVO = new FileVO();
        BeanUtils.copyProperties(fileEntity, fileVO);
        fileVO.setFileUrl(network + "/" + folderTypeEnum.getDesc() + "/" + newFilename);
        return Result.success(fileVO);
    }

    public PageResult<FileVO> page(FilePageDTO filePageDTO) {
        Page<?> page = PageUtil.convert2PageQuery(filePageDTO);
        List<FileVO> list = fileMapper.queryPage(page, filePageDTO);
        PageResult<FileVO> pageResult = PageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean add(FileAddDTO fileAddDTO) {
        FileEntity file = new FileEntity();
        BeanUtils.copyProperties(fileAddDTO, file);
        boolean saved = save(file);
        if (!saved) {
            throw new BusinessException("添加失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean delete(String id) {
        FileEntity exists = getById(id);
        if (exists == null) throw new BusinessException("不存在该对象");
        boolean removed = removeById(id);
        if (!removed) throw new BusinessException("删除失败");
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean batchDelete(List<String> ids) {
        boolean removed = removeBatchByIds(ids);
        if (!removed) throw new BusinessException("删除失败");
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean update(FileUpdateDTO fileUpdateDTO) {
        FileEntity file = new FileEntity();
        BeanUtils.copyProperties(fileUpdateDTO, file);
        if (file.getFileId() == null) throw new BusinessException("主键不能为空");
        else {
            FileEntity exists = getById(file.getFileId());
            if (exists == null) throw new BusinessException("不存在该对象");
        }
        boolean updated = updateById(file);
        if (!updated) {
            throw new BusinessException("更新失败");
        }
        return true;
    }

    public FileVO get(String id) {
        FileEntity file = getById(id);
        if (file == null) {
            throw new BusinessException("不存在该对象");
        }
        FileVO fileVO = new FileVO();
        BeanUtils.copyProperties(file, fileVO);
        return fileVO;
    }
}
