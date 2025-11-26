package top.ehre.mod.system.role.domain.dto;

import top.ehre.mod.util.PageParam;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色表分页查询DTO
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
@Accessors(chain = true)
public class RolePageDTO extends PageParam {
    /**
     * 角色名称
     */
    private String roleName;
}
