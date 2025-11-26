package top.ehre.mod.system.menu.service;

import top.ehre.mod.system.menu.domain.dto.MenuAddDTO;
import top.ehre.mod.system.menu.domain.dto.MenuPageDTO;
import top.ehre.mod.system.menu.domain.dto.MenuUpdateDTO;
import top.ehre.mod.system.menu.domain.entity.MenuEntity;
import top.ehre.mod.system.menu.domain.vo.MenuVO;
import top.ehre.mod.util.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 菜单表 服务类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public interface MenuService extends IService<MenuEntity> {

    PageResult<MenuVO> page(MenuPageDTO menuPageDTO);

    boolean add(MenuAddDTO menuAddDTO);

    boolean delete(String id);

    boolean batchDelete(List<String> ids);

    boolean update(MenuUpdateDTO menuUpdateDTO);

    MenuVO get(String id);
}
