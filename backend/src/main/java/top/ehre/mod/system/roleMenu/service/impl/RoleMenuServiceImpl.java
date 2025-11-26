package top.ehre.mod.system.roleMenu.service.impl;

import top.ehre.mod.exception.BusinessException;
import top.ehre.mod.system.menu.domain.vo.MenuVO;
import top.ehre.mod.system.menu.mapper.MenuMapper;
import top.ehre.mod.system.role.domain.entity.RoleEntity;
import top.ehre.mod.system.role.mapper.RoleMapper;
import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuAddDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuPageDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuUpdateDTO;
import top.ehre.mod.system.roleMenu.domain.dto.RoleTreeUpdateDTO;
import top.ehre.mod.system.roleMenu.domain.entity.RoleMenuEntity;
import top.ehre.mod.system.roleMenu.domain.vo.RoleMenuTreeVO;
import top.ehre.mod.system.roleMenu.domain.vo.RoleMenuVO;
import top.ehre.mod.system.roleMenu.mapper.RoleMenuMapper;
import top.ehre.mod.system.roleMenu.service.RoleMenuService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色菜单关系表 服务实现类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenuEntity> implements RoleMenuService {

    @Resource
    RoleMenuMapper roleMenuMapper;


    @Resource
    MenuMapper menuMapper;

    @Resource
    RoleMapper roleMapper;

    public PageResult<RoleMenuVO> page(RoleMenuPageDTO roleMenuPageDTO) {
        Page<?> page = PageUtil.convert2PageQuery(roleMenuPageDTO);
        List<RoleMenuVO> list = roleMenuMapper.queryPage(page, roleMenuPageDTO);
        PageResult<RoleMenuVO> pageResult = PageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean add(RoleMenuAddDTO roleMenuAddDTO) {
        RoleMenuEntity roleMenu = new RoleMenuEntity();
        BeanUtils.copyProperties(roleMenuAddDTO, roleMenu);
        boolean saved = save(roleMenu);
        if (!saved) {
            throw new BusinessException("添加失败" );
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean delete(String id) {
        RoleMenuEntity exists = getById(id);
        if (exists == null) throw new BusinessException("不存在该对象" );
        boolean removed = removeById(id);
        if (!removed) throw new BusinessException("删除失败" );
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean batchDelete(List<String> ids) {
        boolean removed = removeBatchByIds(ids);
        if (!removed) throw new BusinessException("删除失败" );
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean update(RoleMenuUpdateDTO roleMenuUpdateDTO) {
        RoleMenuEntity roleMenu = new RoleMenuEntity();
        BeanUtils.copyProperties(roleMenuUpdateDTO, roleMenu);
        if (roleMenu.getId() == null) throw new BusinessException("主键不能为空" );
        else {
            RoleMenuEntity exists = getById(roleMenu.getId());
            if (exists == null) throw new BusinessException("不存在该对象" );
        }
        boolean updated = updateById(roleMenu);
        if (!updated) {
            throw new BusinessException("更新失败" );
        }
        return true;
    }

    public RoleMenuVO get(String id) {
        RoleMenuEntity roleMenu = getById(id);
        if (roleMenu == null) {
            throw new BusinessException("不存在该对象" );
        }
        RoleMenuVO roleMenuVO = new RoleMenuVO();
        BeanUtils.copyProperties(roleMenu, roleMenuVO);
        return roleMenuVO;
    }


    public RoleMenuTreeVO getSelectedMenu(String id) {
        RoleMenuTreeVO roleMenuTreeVO = new RoleMenuTreeVO();
        List<String> menuIds = roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenuEntity>()
                        .eq(RoleMenuEntity::getRoleId, id))
                .stream().map(RoleMenuEntity::getMenuId).toList();
        roleMenuTreeVO.setSelectedMenuIds(menuIds);

        List<MenuVO> menuVOList = menuMapper.selectList(null)
                .stream().map(menuEntity -> {
                    MenuVO menuVO = new MenuVO();
                    BeanUtils.copyProperties(menuEntity, menuVO);
                    return menuVO;
                }).toList();
        Map<String, List<MenuVO>> parentMap = menuVOList.stream().collect(Collectors.groupingBy(MenuVO::getParentId, Collectors.toList()));
        List<RoleMenuTreeVO.MenuTreeItem> menuTreeList = this.buildMenuTree(parentMap, "0" );
        roleMenuTreeVO.setMenuTree(menuTreeList);
        roleMenuTreeVO.setRoleId(id);
        return roleMenuTreeVO;
    }


    @Transactional(rollbackFor = Throwable.class)
    public boolean updateRoleTree(RoleTreeUpdateDTO updateDTO) {
        String roleId = updateDTO.getRoleId();
        RoleEntity roleEntity = roleMapper.selectById(roleId);
        if (roleEntity == null) throw new BusinessException("不存在该角色" );
        int delete = roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenuEntity>()
                .eq(RoleMenuEntity::getRoleId, roleId));
        List<RoleMenuEntity> list = updateDTO.getSelectedMenuIds().stream().map(id -> {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setMenuId(id);
            roleMenuEntity.setRoleId(roleId);
            return roleMenuEntity;
        }).toList();
        this.saveBatch(list);
        return false;
    }

    private List<RoleMenuTreeVO.MenuTreeItem> buildMenuTree(Map<String, List<MenuVO>> parentMap, String parentId) {
        List<RoleMenuTreeVO.MenuTreeItem> res = parentMap.getOrDefault(parentId, Collections.emptyList())
                .stream().map(menuVO -> {
                    RoleMenuTreeVO.MenuTreeItem menuTreeItem = new RoleMenuTreeVO.MenuTreeItem();
                    BeanUtils.copyProperties(menuVO, menuTreeItem);
                    return menuTreeItem;
                }).toList();
        res.forEach(e -> e.setChildren(this.buildMenuTree(parentMap, e.getMenuId())));
        return res;
    }
}
