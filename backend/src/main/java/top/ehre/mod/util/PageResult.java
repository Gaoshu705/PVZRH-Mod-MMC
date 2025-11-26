package top.ehre.mod.util;

import top.ehre.mod.config.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Data
public class PageResult<T> {

  /**
   * 当前页
   */
  @JsonSerialize(using = LongJsonSerializer.class)
  private Long pageNum;

  /**
   * 每页的数量
   */
  @JsonSerialize(using = LongJsonSerializer.class)
  private Long pageSize;

  /**
   * 总记录数
   */
  @JsonSerialize(using = LongJsonSerializer.class)
  private Long total;

  /**
   * 总页数
   */
  @JsonSerialize(using = LongJsonSerializer.class)
  private Long pages;

  /**
   * 结果集
   */
  private List<T> list;
}

