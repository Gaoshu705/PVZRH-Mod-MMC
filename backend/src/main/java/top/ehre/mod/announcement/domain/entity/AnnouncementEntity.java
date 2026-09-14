package top.ehre.mod.announcement.domain.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 公告信息表
 *
 * @author LibrhHp_0928
 * @since 2026-06-21
 */
@Data
@Accessors(chain = true)
@TableName("announcement")
@ApiModel(value = "announcement对象", description = "公告信息表")
public class AnnouncementEntity {

    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty("公告标题")
    private String title;

    @ApiModelProperty("公告内容")
    private String content;

    @ApiModelProperty("是否发布（0:未发布，1:已发布）")
    private Boolean isPublished;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedTime;
}
