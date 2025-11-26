package top.ehre.mod.system.menu.mapper;

import top.ehre.mod.system.menu.domain.dto.MenuPageDTO;
import top.ehre.mod.system.menu.domain.entity.MenuEntity;
import top.ehre.mod.system.menu.domain.vo.MenuVO;
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
 * 菜单表 Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Mapper
@Component
public interface MenuMapper extends BaseMapper<MenuEntity> {

    @Select("<script> " +
            "SELECT DISTINCT " +
            "    p.menu_id, p.menu_name, p.parent_id,type,code, p.uri, p.component_path, p.icon, p.priority  " +
            "FROM " +
            "    t_menu p " +
            "JOIN " +
            "    t_role_menu rp ON p.menu_id = rp.menu_id " +
            "WHERE " +
            "    rp.role_id IN " +
            "    <foreach item='roleId' collection='roleIds' open='(' separator=',' close=')'>" +
            "        #{roleId} " +
            "    </foreach> " +
            "</script>")
    List<MenuEntity> findMenuByRoleIds(@Param("roleIds") List<String> roleIds);


    @SelectProvider(type = MenuSqlProvider.class, method = "queryPage")
    List<MenuVO> queryPage(Page page, @Param("pageDTO") MenuPageDTO pageDTO);

    class MenuSqlProvider {
        public String queryPage(final Page page, final MenuPageDTO pageDTO) {
            return new SQL() {{
                SELECT("menu_id,menu_name,parent_id,type,code,uri,component_path,icon,priority,create_time,update_time,create_user,update_user");
                FROM("t_menu");
                if (pageDTO != null) {
                    WHERE("type = 1");
                }
            }}.toString();
        }
    }

}
