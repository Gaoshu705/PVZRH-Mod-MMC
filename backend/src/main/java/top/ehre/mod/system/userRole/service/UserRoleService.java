package top.ehre.mod.system.userRole.service;

import top.ehre.mod.system.userRole.domain.dto.UserRoleAddDTO;
import top.ehre.mod.system.userRole.domain.dto.UserRolePageDTO;
import top.ehre.mod.system.userRole.domain.dto.UserRoleUpdateDTO;
import top.ehre.mod.system.userRole.domain.entity.UserRoleEntity;
import top.ehre.mod.system.userRole.domain.vo.UserRoleVO;
import top.ehre.mod.util.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 用户角色关系表 服务类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public interface UserRoleService extends IService<UserRoleEntity> {

    PageResult<UserRoleVO> page(UserRolePageDTO userRolePageDTO);

    boolean add(UserRoleAddDTO userRoleAddDTO);

    boolean delete(String id);

    boolean batchDelete(List<String> ids);

    boolean update(UserRoleUpdateDTO userRoleUpdateDTO);

    UserRoleVO get(String id);
}
