package com.linghong.fkdp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 用来返回验证结果
 *
 * @author luck_nhb
 */
@ApiModel(value = "返回参数说明")
public class Response implements Serializable {
    @ApiModelProperty(value = "成功标识；true：成功；false:失败")
    private Boolean success;     //是否请求成功
    @ApiModelProperty(value = "返回状态码；200:成功 等信息")
    private Integer code;        //状态码
    @ApiModelProperty(value = "数据信息")
    private Object data;       //数据
    @ApiModelProperty(value = "描述信息")
    private String msg;          //提示信息

    public Response() {
    }

    public Response(Boolean success, Integer code, Object data, String msg) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public void set(Boolean success, Integer code, Object data, String msg) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
