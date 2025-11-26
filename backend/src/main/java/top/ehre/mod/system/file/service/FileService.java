package top.ehre.mod.system.file.service;

import top.ehre.mod.system.file.domain.dto.FileAddDTO;
import top.ehre.mod.system.file.domain.dto.FilePageDTO;
import top.ehre.mod.system.file.domain.dto.FileUpdateDTO;
import top.ehre.mod.system.file.domain.entity.FileEntity;
import top.ehre.mod.system.file.domain.vo.FileVO;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 文件表 服务类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public interface FileService extends IService<FileEntity> {

    Result fileUpload(MultipartFile file, Byte folderType);

    PageResult<FileVO> page(FilePageDTO filePageDTO);

    boolean add(FileAddDTO fileAddDTO);

    boolean delete(String id);

    boolean batchDelete(List<String> ids);

    boolean update(FileUpdateDTO fileUpdateDTO);

    FileVO get(String id);
}
