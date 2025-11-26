package top.ehre.mod.util;

import lombok.Data;

import java.util.List;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
public class PageParam {

  /**
   * 当前页
   */
  private Long pageNum;

  /**
   * 每页数量
   */
  private Long pageSize;

  /**
   * 是否查询总条数
   */
  protected Boolean searchCount;

  /**
   * 排序字段集合
   */
  private List<SortItem> sortItemList;

  @Data
  public static class SortItem {

      /**
       * 是否正序
       */
      private Boolean isAsc;

      /**
       * 排序字段名
       */
      private String column;
  }
}