package top.ehre.mod.announcement.service.impl;

import top.ehre.mod.announcement.domain.entity.AnnouncementEntity;
import top.ehre.mod.announcement.mapper.AnnouncementMapper;
import top.ehre.mod.announcement.service.AnnouncementService;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import top.ehre.mod.announcement.domain.vo.AnnouncementVO;
import top.ehre.mod.announcement.domain.dto.AnnouncementPageDTO;
import top.ehre.mod.announcement.domain.dto.AnnouncementAddDTO;
import top.ehre.mod.announcement.domain.dto.AnnouncementUpdateDTO;
import top.ehre.mod.util.PageResult;
import top.ehre.mod.util.PageUtil;
import top.ehre.mod.exception.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告信息表 服务实现类
 *
 * @author LibrhHp_0928
 * @since 2026-06-21
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, AnnouncementEntity> implements AnnouncementService {

    @Resource
    AnnouncementMapper announcementMapper;


    public PageResult<AnnouncementVO> page(AnnouncementPageDTO announcementPageDTO) {
        Page<?> page = PageUtil.convert2PageQuery(announcementPageDTO);
        List<AnnouncementVO> list = announcementMapper.queryPage(page, announcementPageDTO);
        PageResult<AnnouncementVO> pageResult = PageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean add(AnnouncementAddDTO announcementAddDTO) {
        AnnouncementEntity announcement = new AnnouncementEntity();
        BeanUtils.copyProperties(announcementAddDTO, announcement);
        boolean saved = save(announcement);
        if (!saved) {
            throw new BusinessException("添加失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean delete(String id) {
        AnnouncementEntity exists = getById(id);
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
    public boolean update(AnnouncementUpdateDTO announcementUpdateDTO) {
        AnnouncementEntity announcement = new AnnouncementEntity();
        BeanUtils.copyProperties(announcementUpdateDTO, announcement);
        if (announcement.getId() == null) throw new BusinessException("主键不能为空");
        else {
            AnnouncementEntity exists = getById(announcement.getId());
            if (exists == null) throw new BusinessException("不存在该对象");
        }
        boolean updated = updateById(announcement);
        if (!updated) {
            throw new BusinessException("更新失败");
        }
        return true;
    }

    public AnnouncementVO get(String id) {
        AnnouncementEntity announcement = getById(id);
        if (announcement == null) {
            throw new BusinessException("不存在该对象");
        }
        AnnouncementVO announcementVO = new AnnouncementVO();
        BeanUtils.copyProperties(announcement, announcementVO);
        return announcementVO;
    }

    @Override
    public List<AnnouncementVO> getPublishedAnnouncements() {
        List<AnnouncementEntity> list = announcementMapper.getPublishedAnnouncements();
        return list.stream().map(entity -> {
            AnnouncementVO vo = new AnnouncementVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
