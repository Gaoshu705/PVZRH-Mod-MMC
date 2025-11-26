package top.ehre.mod.security.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class MenuGrantedAuthority implements GrantedAuthority {
    private String menuCode;


    public MenuGrantedAuthority() {
    }

    public MenuGrantedAuthority(String menuCode) {
        Assert.hasText(menuCode, "A granted authority textual representation is required");
        this.menuCode = menuCode;
    }

    @Override
    public String getAuthority() {
        return this.menuCode;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof MenuGrantedAuthority) {
            MenuGrantedAuthority mga = (MenuGrantedAuthority) obj;
            return this.menuCode.equals(mga.getAuthority());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.menuCode.hashCode();
    }

    @Override
    public String toString() {
        return this.menuCode;
    }
}
