package top.ehre.mod.system.userRole.mapper;

import top.ehre.mod.system.role.domain.entity.RoleEntity;
import top.ehre.mod.system.userRole.domain.dto.UserRolePageDTO;
import top.ehre.mod.system.userRole.domain.entity.UserRoleEntity;
import top.ehre.mod.system.userRole.domain.vo.UserRoleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户角色关系表 Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Mapper
@Component
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    @Select("select r.role_id, r.role_name " +
            "FROM t_user_role ur " +
            "JOIN t_role r ON ur.role_id = r.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<RoleEntity> findRoleByUserId(@Param("userId") String userId);

    @SelectProvider(type = UserRoleSqlProvider.class, method = "queryPage")
    List<UserRoleVO> queryPage(Page page, @Param("pageDTO") UserRolePageDTO pageDTO);

    class UserRoleSqlProvider {
        public String queryPage(final Page page, final UserRolePageDTO pageDTO) {
            return new SQL() {{
                SELECT("id,user_id,role_id,create_time,update_time,create_user,update_user");
                FROM("t_user_role");
                if (pageDTO != null) {
//                    WHERE("");
                }
            }}.toString();
        }
    }

}
