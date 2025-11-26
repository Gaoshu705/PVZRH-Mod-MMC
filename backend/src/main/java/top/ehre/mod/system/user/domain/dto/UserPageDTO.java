package top.ehre.mod.system.user.domain.dto;

import top.ehre.mod.util.PageParam;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户表分页查询DTO
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
@Accessors(chain = true)
public class UserPageDTO extends PageParam {
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
}
