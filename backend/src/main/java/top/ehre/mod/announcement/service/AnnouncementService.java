package top.ehre.mod.announcement.service;

import top.ehre.mod.announcement.domain.entity.AnnouncementEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import top.ehre.mod.announcement.domain.vo.AnnouncementVO;
import top.ehre.mod.announcement.domain.dto.AnnouncementPageDTO;
import top.ehre.mod.announcement.domain.dto.AnnouncementAddDTO;
import top.ehre.mod.announcement.domain.dto.AnnouncementUpdateDTO;
import top.ehre.mod.util.PageResult;

import java.util.List;


/**
 * 公告信息表 服务类
 *
 * @author LibrhHp_0928
 * @since 2026-06-21
 */
public interface AnnouncementService extends IService<AnnouncementEntity> {

    PageResult<AnnouncementVO> page(AnnouncementPageDTO announcementPageDTO);

    boolean add(AnnouncementAddDTO announcementAddDTO);

    boolean delete(String id);

    boolean batchDelete(List<String> ids);

    boolean update(AnnouncementUpdateDTO announcementUpdateDTO);

    AnnouncementVO get(String id);

    List<AnnouncementVO> getPublishedAnnouncements();
}
