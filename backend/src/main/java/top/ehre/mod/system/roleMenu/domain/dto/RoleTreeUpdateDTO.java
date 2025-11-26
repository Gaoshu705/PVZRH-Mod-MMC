package top.ehre.mod.system.roleMenu.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
public class RoleTreeUpdateDTO {
    private String roleId;
    private List<String> selectedMenuIds;
}
