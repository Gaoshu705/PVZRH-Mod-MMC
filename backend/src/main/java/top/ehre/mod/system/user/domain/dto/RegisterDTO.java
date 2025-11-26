package top.ehre.mod.system.user.domain.dto;

import lombok.Data;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
public class RegisterDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 重复密码
     */
    private String newPassword;

    /**
     * 性别，0未知，1男，2女
     */
    private Byte gender;


    /**
     * 角色ID
     */
    private Long roleId;
}

