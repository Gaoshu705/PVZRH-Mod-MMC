package top.ehre.mod.system.roleMenu.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
public class RoleMenuTreeVO {
    private String roleId;
    private List<MenuTreeItem> menuTree;
    private List<String> selectedMenuIds;

    @Data
    public static class MenuTreeItem {
        private String menuId;
        private String menuName;
        private String parentId;
        private Integer type;
        private List<MenuTreeItem> children;
    }
}
