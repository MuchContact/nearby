package com.larscheng.www.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author larscheng
 * @since 2019-12-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
  @Accessors(chain = true)
public class UserGeohash implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 名称
     */
      private String name;

      /**
     * 经度
     */
      private Double longitude;

      /**
     * 纬度
     */
      private Double latitude;

      /**
     * 经纬度所计算的geohash码
     */
      private String geoCode;

      /**
     * 创建时间
     */
      private LocalDateTime createTime;


}
