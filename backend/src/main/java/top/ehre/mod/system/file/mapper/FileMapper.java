package top.ehre.mod.system.file.mapper;

import top.ehre.mod.system.file.domain.dto.FilePageDTO;
import top.ehre.mod.system.file.domain.entity.FileEntity;
import top.ehre.mod.system.file.domain.vo.FileVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件表 Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Mapper
@Component
public interface FileMapper extends BaseMapper<FileEntity> {



    @SelectProvider(type = FileSqlProvider.class, method = "queryPage")
    List<FileVO> queryPage(Page page, @Param("pageDTO") FilePageDTO pageDTO);

    class FileSqlProvider {
        public String queryPage(final Page page, final FilePageDTO pageDTO) {
            return new SQL() {{
                SELECT("file_id,folder_type,file_name,file_size,file_type,create_time,update_time,create_user,update_user");
                FROM("t_file");
                if (pageDTO != null) {
                    if (pageDTO.getFileName() != null && !pageDTO.getFileName().isBlank()) {
                        WHERE("INSTR(file_name, '" + pageDTO.getFileName() + "')");
                    }
                }
            }}.toString();
        }
    }

}
