package top.ehre.mod.system.file.domain.dto;

import top.ehre.mod.util.PageParam;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件表分页查询DTO
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
@Accessors(chain = true)
public class FilePageDTO extends PageParam {
    /**
     * 文件名称
     */
    private String fileName;
}
