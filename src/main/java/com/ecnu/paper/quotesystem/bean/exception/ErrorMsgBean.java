package com.ecnu.paper.quotesystem.bean.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "异常处理信息类")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorMsgBean implements Serializable {
	
	private static final long serialVersionUID = -7447689818750410956L;

	@ApiModelProperty(value = "错误 ID",required=true, example = "UUID",position = 1)
	@JsonProperty("error_id")
	private String errorId;
	
	@ApiModelProperty(value = "错误 code",required=true, example = "100110121",position = 2)
	@JsonProperty("error_code")
	private int errorCode;
	
	@ApiModelProperty(value = "错误信息",required=true, example = "在注册时候用户名不能为空",position = 3)
	@JsonProperty("msg")
	private String errorMsg;

	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
