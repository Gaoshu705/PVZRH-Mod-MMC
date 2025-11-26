package top.ehre.mod.system.user.mapper;

import top.ehre.mod.system.user.domain.dto.UserPageDTO;
import top.ehre.mod.system.user.domain.entity.UserEntity;
import top.ehre.mod.system.user.domain.vo.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户表 Mapper 接口
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Mapper
@Component
public interface UserMapper extends BaseMapper<UserEntity> {



    @SelectProvider(type = UserSqlProvider.class, method = "queryPage")
    List<UserVO> queryPage(Page page, @Param("pageDTO") UserPageDTO pageDTO);

    class UserSqlProvider {
        public String queryPage(final Page page, final UserPageDTO pageDTO) {
            return new SQL() {{
                SELECT("user_id,username,nickname,password,gender,avatar,create_time,update_time,create_user,update_user");
                FROM("t_user");
                if (pageDTO != null) {
                    if (pageDTO.getUsername() != null && !pageDTO.getUsername().isBlank()) {
                        WHERE("INSTR(username, '" + pageDTO.getUsername() + "')");
                    }
                    if (pageDTO.getNickname() != null && !pageDTO.getNickname().isBlank()) {
                        WHERE("INSTR(nickname, '" + pageDTO.getNickname() + "')");
                    }
                }
            }}.toString();
        }
    }

}
