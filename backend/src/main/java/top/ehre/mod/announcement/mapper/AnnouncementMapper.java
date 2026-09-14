package top.ehre.mod.announcement.mapper;

import top.ehre.mod.announcement.domain.entity.AnnouncementEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.ehre.mod.announcement.domain.dto.AnnouncementPageDTO;
import top.ehre.mod.announcement.domain.vo.AnnouncementVO;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * 公告信息表 Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2026-06-21
 */
@Mapper
@Component
public interface AnnouncementMapper extends BaseMapper<AnnouncementEntity> {


    @SelectProvider(type = AnnouncementSqlProvider.class, method = "queryPage")
    List<AnnouncementVO> queryPage(Page page, @Param("pageDTO") AnnouncementPageDTO pageDTO);

    class AnnouncementSqlProvider {
        public String queryPage(final Page page, final AnnouncementPageDTO pageDTO) {
            return new SQL() {{
                SELECT("id,title,content,is_published,created_time,updated_time");
                FROM("announcement");
                if (pageDTO != null) {
                }
            }}.toString();
        }
    }

    List<AnnouncementEntity> getPublishedAnnouncements();

}
