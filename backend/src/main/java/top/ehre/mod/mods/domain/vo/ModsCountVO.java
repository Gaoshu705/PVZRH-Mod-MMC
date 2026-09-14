package top.ehre.mod.mods.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 模组下载量 / 浏览量
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Mods统计对象", description = "下载量与浏览量")
public class ModsCountVO {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("下载次数")
    private String downloadCount;

    @ApiModelProperty("查看次数")
    private String viewCount;
}
