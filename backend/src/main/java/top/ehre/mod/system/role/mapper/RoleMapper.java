package top.ehre.mod.system.role.mapper;

import top.ehre.mod.system.role.domain.dto.RolePageDTO;
import top.ehre.mod.system.role.domain.entity.RoleEntity;
import top.ehre.mod.system.role.domain.vo.RoleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色表 Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Mapper
@Component
public interface RoleMapper extends BaseMapper<RoleEntity> {



    @SelectProvider(type = RoleSqlProvider.class, method = "queryPage")
    List<RoleVO> queryPage(Page page, @Param("pageDTO") RolePageDTO pageDTO);

    class RoleSqlProvider {
        public String queryPage(final Page page, final RolePageDTO pageDTO) {
            return new SQL() {{
                SELECT("role_id,role_name,create_time,update_time,create_user,update_user");
                FROM("t_role");
                if (pageDTO != null) {
                    if (pageDTO.getRoleName() != null && !pageDTO.getRoleName().isBlank()) {
                        WHERE("INSTR(role_name, '" + pageDTO.getRoleName() + "')");
                    }
                }
            }}.toString();
        }
    }

}
