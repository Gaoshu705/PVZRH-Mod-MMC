package top.ehre.mod.security.authentication;

import top.ehre.mod.system.menu.domain.entity.MenuEntity;
import top.ehre.mod.system.role.domain.entity.RoleEntity;
import top.ehre.mod.system.user.domain.entity.UserEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
public class UserInfo {
    private UserEntity user;
    private Set<RoleEntity> roles;
    private Set<MenuEntity> menus;

    public UserInfo(UserEntity user, Set<RoleEntity> roles, Set<MenuEntity> menus) {
        this.user = user;
        this.roles = roles;
        this.menus = menus;
    }

    public Collection<? extends GrantedAuthority> generateAuthorities() {
        Set<MenuGrantedAuthority> authorities = new HashSet<>();
        Set<MenuEntity> menus = this.getMenus();
        menus.forEach(menu -> {
                    if (menu.getCode() != null)
                        authorities.add(new MenuGrantedAuthority(menu.getCode()));
                }
        );
        return authorities;
    }

    public String getPassword() {
        if (user == null) return "";
        return user.getPassword();
    }

    public void erasePassword() {
        if (user == null) return;
        user.setPassword(null);
    }
}
