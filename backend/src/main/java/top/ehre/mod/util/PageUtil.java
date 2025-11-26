package top.ehre.mod.util;

import top.ehre.mod.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class PageUtil {
    public static Page<?> convert2PageQuery(PageParam pageParam) {
        Page<?> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        if (pageParam.getSearchCount() != null) {
            page.setSearchCount(pageParam.getSearchCount());
        }

        List<PageParam.SortItem> sortItemList = pageParam.getSortItemList();
        if (sortItemList == null || sortItemList.isEmpty()) {
            return page;
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        for (PageParam.SortItem sortItem : sortItemList) {

            if (sortItem.getColumn() == null || sortItem.getColumn().isEmpty()) {
                continue;
            }

            if (SqlInjectionUtils.check(sortItem.getColumn())) {
                throw new BusinessException("存在SQL注入风险！");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn(sortItem.getColumn());
            orderItem.setAsc(sortItem.getIsAsc());
            orderItemList.add(orderItem);
        }
        page.setOrders(orderItemList);
        return page;
    }

    public static <E> PageResult<E> convert2PageResult(Page<?> page, List<E> sourceList) {
        PageResult<E> pageResult = new PageResult<>();
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setTotal(page.getTotal());
        pageResult.setPages(page.getPages());
        pageResult.setList(sourceList);
        return pageResult;
    }
}