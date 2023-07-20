package com.imooc.bilibili.service.excel;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mdp_pct_partner_rela__log")
@JsonNaming(com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanyName {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;
    private String url;
    private String detail;
    private String reason;
}
