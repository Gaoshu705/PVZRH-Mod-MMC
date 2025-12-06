package top.ehre.mod.system.user.service;

import top.ehre.mod.security.authentication.UserInfo;
import top.ehre.mod.system.user.domain.dto.RegisterDTO;
import top.ehre.mod.system.user.domain.dto.UserAddDTO;
import top.ehre.mod.system.user.domain.dto.UserPageDTO;
import top.ehre.mod.system.user.domain.dto.UserUpdateDTO;
import top.ehre.mod.system.user.domain.entity.UserEntity;
import top.ehre.mod.system.user.domain.vo.UserVO;
import top.ehre.mod.util.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 用户表 服务类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public interface UserService extends IService<UserEntity> {

    PageResult<UserVO> page(UserPageDTO userPageDTO);

    boolean add(UserAddDTO userAddDTO);

    boolean delete(String id);

    boolean batchDelete(List<String> ids);

    boolean update(UserUpdateDTO userUpdateDTO);

    UserVO get(String id);

    UserInfo getUserInfo(String username);

    UserVO register(RegisterDTO registerDTO);

    List<UserVO> getSameRoleUsers();
}
