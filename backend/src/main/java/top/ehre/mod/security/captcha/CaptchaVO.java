package top.ehre.mod.security.captcha;

import lombok.Data;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
public class CaptchaVO {

    /**
     * 验证码图片ID
     */
    private String captchaOwner;

    /**
     * 验证码Base64图片
     */
    private String captchaBase64Image;
}
