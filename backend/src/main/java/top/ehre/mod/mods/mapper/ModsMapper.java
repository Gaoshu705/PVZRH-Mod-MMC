package top.ehre.mod.mods.mapper;

import top.ehre.mod.mods.domain.entity.ModsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.ehre.mod.mods.domain.dto.ModsPageDTO;
import top.ehre.mod.mods.domain.vo.ModsVO;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:33
 */
@Mapper
@Component
public interface ModsMapper extends BaseMapper<ModsEntity> {
    @SelectProvider(type = ModsSqlProvider.class, method = "queryPage")
    List<ModsVO> queryPage(Page page, @Param("pageDTO") ModsPageDTO pageDTO);
    int addOtherAuthor(@Param("id") String modId, @Param("authorId") String authorId);

    List<String> getOtherAuthors(@Param("id") String modId);

    int deleteOtherAuthor(@Param("id") String modId);

    class ModsSqlProvider {
        public String queryPage(final Page page, final ModsPageDTO pageDTO) {
            return new SQL() {{
                SELECT("id,mod_name,english_name,author_id,mod_description,video_url,game_name,supported_versions,framework_name,show_direct_url,download_direct_url,download_cloud_url,version,file_size,download_count,view_count,is_approved,is_featured,is_visible,created_at,updated_at");
                FROM("mods");
                if (pageDTO != null && pageDTO.getAuthorId() != null) {
                    WHERE("author_id = #{pageDTO.authorId}");
                }
            }}.toString();
        }
    }
    

}
