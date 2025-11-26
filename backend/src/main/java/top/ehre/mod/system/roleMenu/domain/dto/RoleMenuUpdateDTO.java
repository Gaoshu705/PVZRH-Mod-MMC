package top.ehre.mod.system.roleMenu.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
 * 角色菜单关系表修改DTO
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
@Accessors(chain = true)
public class RoleMenuUpdateDTO {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 菜单ID
     */
    private String menuId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 更新用户
     */
    private String updateUser;

}
