package top.ehre.mod.system.role.service;

import top.ehre.mod.system.role.domain.dto.RoleAddDTO;
import top.ehre.mod.system.role.domain.dto.RolePageDTO;
import top.ehre.mod.system.role.domain.dto.RoleUpdateDTO;
import top.ehre.mod.system.role.domain.entity.RoleEntity;
import top.ehre.mod.system.role.domain.vo.RoleVO;
import top.ehre.mod.util.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 角色表 服务类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public interface RoleService extends IService<RoleEntity> {

    PageResult<RoleVO> page(RolePageDTO rolePageDTO);

    boolean add(RoleAddDTO roleAddDTO);

    boolean delete(String id);

    boolean batchDelete(List<String> ids);

    boolean update(RoleUpdateDTO roleUpdateDTO);

    RoleVO get(String id);
}
