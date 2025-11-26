package top.ehre.mod.system.userRole.service.impl;

import top.ehre.mod.exception.BusinessException;
import top.ehre.mod.system.userRole.domain.dto.UserRoleAddDTO;
import top.ehre.mod.system.userRole.domain.dto.UserRolePageDTO;
import top.ehre.mod.system.userRole.domain.dto.UserRoleUpdateDTO;
import top.ehre.mod.system.userRole.domain.entity.UserRoleEntity;
import top.ehre.mod.system.userRole.domain.vo.UserRoleVO;
import top.ehre.mod.system.userRole.mapper.UserRoleMapper;
import top.ehre.mod.system.userRole.service.UserRoleService;
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
 * 用户角色关系表 服务实现类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleEntity> implements UserRoleService {

    @Resource
    UserRoleMapper userRoleMapper;



    public PageResult<UserRoleVO> page(UserRolePageDTO userRolePageDTO) {
        Page<?> page = PageUtil.convert2PageQuery(userRolePageDTO);
        List<UserRoleVO> list = userRoleMapper.queryPage(page, userRolePageDTO);
        PageResult<UserRoleVO> pageResult = PageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean add(UserRoleAddDTO userRoleAddDTO) {
        UserRoleEntity userRole = new UserRoleEntity();
        BeanUtils.copyProperties(userRoleAddDTO, userRole);
        boolean saved = save(userRole);
        if (!saved) {
            throw new BusinessException("添加失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean delete(String id) {
        UserRoleEntity exists = getById(id);
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
    public boolean update(UserRoleUpdateDTO userRoleUpdateDTO) {
        UserRoleEntity userRole = new UserRoleEntity();
        BeanUtils.copyProperties(userRoleUpdateDTO, userRole);
        if (userRole.getId() == null) throw new BusinessException("主键不能为空");
        else {
            UserRoleEntity exists = getById(userRole.getId());
            if (exists == null) throw new BusinessException("不存在该对象");
        }
        boolean updated = updateById(userRole);
        if (!updated) {
            throw new BusinessException("更新失败");
        }
        return true;
    }

    public UserRoleVO get(String id) {
        UserRoleEntity userRole = getById(id);
        if (userRole == null) {
            throw new BusinessException("不存在该对象");
        }
        UserRoleVO userRoleVO = new UserRoleVO();
        BeanUtils.copyProperties(userRole, userRoleVO);
        return userRoleVO;
    }
}
