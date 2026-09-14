package top.ehre.mod.announcement.domain.dto;

import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import top.ehre.mod.util.PageParam;

/**
 * 公告信息表分页查询DTO
 *
 * @author LibrhHp_0928
 * @since 2026-06-21
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Announcement分页查询对象", description = "公告信息表分页查询DTO")
public class AnnouncementPageDTO extends PageParam {
}
