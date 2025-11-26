package top.ehre.mod.security.authentication;

import top.ehre.mod.system.menu.domain.entity.MenuEntity;
import top.ehre.mod.system.menu.mapper.MenuMapper;
import top.ehre.mod.system.role.domain.entity.RoleEntity;
import top.ehre.mod.system.user.domain.entity.UserEntity;
import top.ehre.mod.system.user.mapper.UserMapper;
import top.ehre.mod.system.userRole.mapper.UserRoleMapper;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Component
public class UsernamePasswordProvider implements AuthenticationProvider {


    @Resource
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private MenuMapper menuMapper;

    @Resource
    RedisTemplate redisTemplate;


    public UsernamePasswordProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal() == null ? "" : (String) authentication.getPrincipal();
        Object userCacheJsonString = redisTemplate.opsForValue().get("login:cache:" + username);
        UserInfo userInfo = null;
        boolean cacheWasUsed = true;
        if (userCacheJsonString != null) {
            userInfo = JSON.parseObject((String) userCacheJsonString, UserInfo.class);
        }
        if (userInfo == null) {
            cacheWasUsed = false;
            userInfo = retrieveUser(username);
        }
        try {
            this.additionalAuthenticationChecks(userInfo, (UsernamePasswordToken) authentication);
        } catch (AuthenticationException ex) {
            if (!cacheWasUsed) throw ex;
            cacheWasUsed = false;
            userInfo = this.retrieveUser(username);
            this.additionalAuthenticationChecks(userInfo, (UsernamePasswordToken) authentication);
        }
        if (!cacheWasUsed) {
            redisTemplate.opsForValue().set("login:cache:" + username, JSON.toJSONString(userInfo), 5, TimeUnit.MINUTES);
        }
        return this.createSuccessAuthentication(username, authentication, userInfo);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordToken.class.isAssignableFrom(authentication);
    }

    public final UserInfo retrieveUser(String username) throws AuthenticationException {
        UserEntity loadedUser = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
        if (loadedUser == null) throw new BadCredentialsException("用户不存在");
        List<RoleEntity> roles = userRoleMapper.findRoleByUserId(loadedUser.getUserId());

        SortedSet<MenuEntity> menus = new TreeSet<>(Comparator
                .comparing(MenuEntity::getPriority)
                .thenComparing(MenuEntity::getMenuName));
        for (RoleEntity role : roles) {
            List<MenuEntity> menuEntityList = menuMapper.findMenuByRoleIds(
                    Collections.singletonList(role.getRoleId()));
            menus.addAll(menuEntityList);
        }
        UserInfo userInfo = new UserInfo(loadedUser, new HashSet<>(roles), menus);
        return userInfo;
    }

    protected void additionalAuthenticationChecks(UserInfo userInfo, UsernamePasswordToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("未输入密码");
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            if (!passwordEncoder.matches(presentedPassword, userInfo.getPassword())) {
                throw new BadCredentialsException("账号名或密码错误");
            }
        }
    }

    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserInfo userInfo) {
        UsernamePasswordToken result = new UsernamePasswordToken(principal, authentication.getCredentials(), userInfo.generateAuthorities());

        userInfo.erasePassword();
        result.setDetails(userInfo);
        return result;
    }
}
