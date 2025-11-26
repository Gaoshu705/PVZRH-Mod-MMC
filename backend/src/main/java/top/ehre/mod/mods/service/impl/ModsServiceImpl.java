package top.ehre.mod.mods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import top.ehre.mod.mods.domain.entity.ModsEntity;
import top.ehre.mod.mods.mapper.ModsMapper;
import top.ehre.mod.mods.service.ModsService;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import top.ehre.mod.mods.domain.vo.ModsVO;
import top.ehre.mod.mods.domain.dto.ModsPageDTO;
import top.ehre.mod.mods.domain.dto.ModsAddDTO;
import top.ehre.mod.mods.domain.dto.ModsUpdateDTO;
import top.ehre.mod.security.authentication.UserInfo;
import top.ehre.mod.system.user.service.UserService;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.PageUtil;
import top.ehre.mod.exception.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
// ---------------------- 联表导包 -------------------------
// ---------------------------------------------------------

import java.util.List;

/**
 *  服务实现类
 *
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:33
 */
@Service
public class ModsServiceImpl extends ServiceImpl<ModsMapper, ModsEntity> implements ModsService {

    @Resource
    ModsMapper modsMapper;

    @Resource
    UserService userService;


    @Override
    public PageResult<ModsVO> page(ModsPageDTO modsPageDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserInfo userInfo = userService.getUserInfo(authentication.getName());
            if (!"1".equals(userInfo.getUser().getUserId())){
                modsPageDTO.setAuthorId(userInfo.getUser().getUserId());
            }
        }
        Page<?> page = PageUtil.convert2PageQuery(modsPageDTO);
        List<ModsVO> list = modsMapper.queryPage(page, modsPageDTO);
        PageResult<ModsVO> pageResult = PageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    @Override
    public List<ModsVO> getList() {
        // 根据字段is_visible 查询
        QueryWrapper<ModsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_visible", true);
        List<ModsEntity> list = modsMapper.selectList( queryWrapper);
        List<ModsVO> listVO = list.stream().map(mods -> {
            ModsVO modsVO = new ModsVO();
            BeanUtils.copyProperties(mods, modsVO);
            modsVO.setAuthorName(userService.get(mods.getAuthorId()).getUsername());
            return modsVO;
        }).toList();
        return listVO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean add(ModsAddDTO modsAddDTO) {
        ModsEntity mods = new ModsEntity();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserInfo userInfo = userService.getUserInfo(authentication.getName());
            modsAddDTO.setAuthorId(userInfo.getUser().getUserId());
        }
        BeanUtils.copyProperties(modsAddDTO, mods);
        boolean saved = save(mods);
        if (!saved) {
            throw new BusinessException("添加失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean delete(String id) {
        ModsEntity exists = getById(id);
        if (exists == null) throw new BusinessException("不存在该对象");
        boolean removed = removeById(id);
        if (!removed) throw new BusinessException("删除失败");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean batchDelete(List<String> ids) {
        boolean removed = removeBatchByIds(ids);
        if (!removed) throw new BusinessException("删除失败");
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean update(ModsUpdateDTO modsUpdateDTO) {
        // 鉴权 只有是自己的mod才可以编辑，超级管理员则不受影响
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserInfo userInfo = userService.getUserInfo(authentication.getName());
            if (!"1".equals(userInfo.getUser().getUserId())){
                ModsEntity mods = getById(modsUpdateDTO.getId());
                if (!mods.getAuthorId().equals(userInfo.getUser().getUserId())){
                    throw new BusinessException("无权限");
                }
            }
        }
        ModsEntity mods = new ModsEntity();
        BeanUtils.copyProperties(modsUpdateDTO, mods);
        if (mods.getId() == null) throw new BusinessException("主键不能为空");
        else {
            ModsEntity exists = getById(mods.getId());
            if (exists == null) throw new BusinessException("不存在该对象");
        }
        boolean updated = updateById(mods);
        if (!updated) {
            throw new BusinessException("更新失败");
        }
        return true;
    }

    @Override
    public ModsVO get(String id) {
        ModsEntity mods = getById(id);
        if (mods == null) {
            throw new BusinessException("不存在该对象");
        }
        ModsVO modsVO = new ModsVO();
        BeanUtils.copyProperties(mods, modsVO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            modsVO.setAuthorName(userService.get(mods.getAuthorId()).getNickname());
        }
        return modsVO;
    }
}
