package top.ehre.mod.system.menu.service.impl;

import top.ehre.mod.exception.BusinessException;
import top.ehre.mod.system.menu.domain.dto.MenuAddDTO;
import top.ehre.mod.system.menu.domain.dto.MenuPageDTO;
import top.ehre.mod.system.menu.domain.dto.MenuUpdateDTO;
import top.ehre.mod.system.menu.domain.entity.MenuEntity;
import top.ehre.mod.system.menu.domain.vo.MenuVO;
import top.ehre.mod.system.menu.mapper.MenuMapper;
import top.ehre.mod.system.menu.service.MenuService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜单表 服务实现类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {

    @Resource
    MenuMapper menuMapper;

    private void setChildren(List<MenuVO> list, int deep) {
        if (deep == 4) return;
        for (MenuVO each : list) {
            List<MenuVO> children = menuMapper.selectList(new LambdaQueryWrapper<MenuEntity>()
                            .eq(MenuEntity::getParentId, each.getMenuId()))
                    .stream().map(entity -> {
                        MenuVO vo = new MenuVO();
                        BeanUtils.copyProperties(entity, vo);
                        return vo;
                    }).toList();
            if (children.isEmpty()) continue;
            setChildren(children, deep + 1);
            each.setChildren(children);
        }
    }


    public PageResult<MenuVO> page(MenuPageDTO menuPageDTO) {
        Page<?> page = PageUtil.convert2PageQuery(menuPageDTO);
        List<MenuVO> list = menuMapper.queryPage(page, menuPageDTO);

        setChildren(list, 1);

        PageResult<MenuVO> pageResult = PageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean add(MenuAddDTO menuAddDTO) {
        MenuEntity menu = new MenuEntity();
        BeanUtils.copyProperties(menuAddDTO, menu);
        boolean saved = save(menu);
        if (!saved) {
            throw new BusinessException("添加失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean delete(String id) {
        MenuEntity exists = getById(id);
        if (exists == null) throw new BusinessException("不存在该对象");
        boolean removed = removeById(id);
        if (!removed) throw new BusinessException("删除失败");
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean batchDelete(List<String> ids) {
        boolean removed = removeBatchByIds(ids);
        if (!removed) throw new BusinessException("删除失败");
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean update(MenuUpdateDTO menuUpdateDTO) {
        MenuEntity menu = new MenuEntity();
        BeanUtils.copyProperties(menuUpdateDTO, menu);
        if (menu.getMenuId() == null) throw new BusinessException("主键不能为空");
        else {
            MenuEntity exists = getById(menu.getMenuId());
            if (exists == null) throw new BusinessException("不存在该对象");
        }
        boolean updated = updateById(menu);
        if (!updated) {
            throw new BusinessException("更新失败");
        }
        return true;
    }

    public MenuVO get(String id) {
        MenuEntity menu = getById(id);
        if (menu == null) {
            throw new BusinessException("不存在该对象");
        }
        MenuVO menuVO = new MenuVO();
        BeanUtils.copyProperties(menu, menuVO);
        return menuVO;
    }
}
