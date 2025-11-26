package top.ehre.mod.security.captcha;

import top.ehre.mod.exception.BusinessException;
import top.ehre.mod.util.Result;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@RestController
public class CaptchaController {

    @Resource
    private Producer producer;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/getCaptcha")
    public Result getCaptcha() {
        String text = producer.createText();
        String captchaOwner = UUID.randomUUID().toString().replaceAll("-", "");
        BufferedImage image = producer.createImage(text);
        String redisKey = "captcha:" + captchaOwner;
        redisTemplate.opsForValue().set(redisKey, text, 5, TimeUnit.MINUTES);
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaOwner(captchaOwner);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", os);
            String base64Code = Base64.getEncoder().encodeToString(os.toByteArray());
            captchaVO.setCaptchaBase64Image("data:image/png;base64," + base64Code);
        } catch (IOException e) {
            throw new BusinessException("响应验证码失败！");
        }
        return Result.success(captchaVO);
    }

}

