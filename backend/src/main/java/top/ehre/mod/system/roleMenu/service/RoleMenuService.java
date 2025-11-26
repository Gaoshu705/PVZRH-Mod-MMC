package top.ehre.mod.system.roleMenu.service;

import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuAddDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuPageDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuUpdateDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleTreeUpdateDTO;
import top.ehre.mod.system.roleMenu.domain.entity.RoleMenuEntity;
import top.ehre.mod.system.roleMenu.domain.vo.RoleMenuTreeVO;
import top.ehre.mod.system.roleMenu.domain.vo.RoleMenuVO;
import top.ehre.mod.util.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


/**
 * 角色菜单关系表 服务类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public interface RoleMenuService extends IService<RoleMenuEntity> {

    PageResult<RoleMenuVO> page(RoleMenuPageDTO roleMenuPageDTO);

    boolean add(RoleMenuAddDTO roleMenuAddDTO);

    boolean delete(String id);

    boolean batchDelete(List<String> ids);

    boolean update(RoleMenuUpdateDTO roleMenuUpdateDTO);

    RoleMenuVO get(String id);

    RoleMenuTreeVO getSelectedMenu(@PathVariable String id);

    boolean updateRoleTree(RoleTreeUpdateDTO updateDTO);
}
