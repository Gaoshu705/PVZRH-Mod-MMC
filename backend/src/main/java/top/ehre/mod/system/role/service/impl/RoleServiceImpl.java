package top.ehre.mod.system.role.service.impl;

import top.ehre.mod.exception.BusinessException;
import top.ehre.mod.system.role.domain.dto.RoleAddDTO;
import top.ehre.mod.system.role.domain.dto.RolePageDTO;
import top.ehre.mod.system.role.domain.dto.RoleUpdateDTO;
import top.ehre.mod.system.role.domain.entity.RoleEntity;
import top.ehre.mod.system.role.domain.vo.RoleVO;
import top.ehre.mod.system.role.mapper.RoleMapper;
import top.ehre.mod.system.role.service.RoleService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色表 服务实现类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {

    @Resource
    RoleMapper roleMapper;



    public PageResult<RoleVO> page(RolePageDTO rolePageDTO) {
        Page<?> page = PageUtil.convert2PageQuery(rolePageDTO);
        List<RoleVO> list = roleMapper.queryPage(page, rolePageDTO);
        PageResult<RoleVO> pageResult = PageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean add(RoleAddDTO roleAddDTO) {
        RoleEntity role = new RoleEntity();
        BeanUtils.copyProperties(roleAddDTO, role);
        boolean saved = save(role);
        if (!saved) {
            throw new BusinessException("添加失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean delete(String id) {
        RoleEntity exists = getById(id);
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
    public boolean update(RoleUpdateDTO roleUpdateDTO) {
        RoleEntity role = new RoleEntity();
        BeanUtils.copyProperties(roleUpdateDTO, role);
        if (role.getRoleId() == null) throw new BusinessException("主键不能为空");
        else {
            RoleEntity exists = getById(role.getRoleId());
            if (exists == null) throw new BusinessException("不存在该对象");
        }
        boolean updated = updateById(role);
        if (!updated) {
            throw new BusinessException("更新失败");
        }
        return true;
    }

    public RoleVO get(String id) {
        RoleEntity role = getById(id);
        if (role == null) {
            throw new BusinessException("不存在该对象");
        }
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        return roleVO;
    }
}
