package top.ehre.mod.system.roleMenu.mapper;

import top.ehre.mod.system.roleMenu.domain.dto.RoleMenuPageDTO;
import top.ehre.mod.system.roleMenu.domain.entity.RoleMenuEntity;
import top.ehre.mod.system.roleMenu.domain.vo.RoleMenuVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色菜单关系表 Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Mapper
@Component
public interface RoleMenuMapper extends BaseMapper<RoleMenuEntity> {



    @SelectProvider(type = RoleMenuSqlProvider.class, method = "queryPage")
    List<RoleMenuVO> queryPage(Page page, @Param("pageDTO") RoleMenuPageDTO pageDTO);

    class RoleMenuSqlProvider {
        public String queryPage(final Page page, final RoleMenuPageDTO pageDTO) {
            return new SQL() {{
                SELECT("id,role_id,menu_id,create_time,update_time,create_user,update_user");
                FROM("t_role_menu");
                if (pageDTO != null) {
//                    WHERE("");
                }
            }}.toString();
        }
    }

}
