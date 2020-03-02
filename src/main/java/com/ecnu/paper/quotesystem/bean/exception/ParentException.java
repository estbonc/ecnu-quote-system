package com.ecnu.paper.quotesystem.bean.exception;

import com.ecnu.paper.quotesystem.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * 
 * @author 王云龙
 * @date 2017年1月18日 下午3:07:02
 * @version 1.0
 * @description 框架级别的exception父类
 *
 */

public abstract class ParentException extends Exception {

	private static final long serialVersionUID = 713616801491210431L;
	
	private ErrorMsgBean errorMsgBean=new ErrorMsgBean();
	
	
	
	public ErrorMsgBean getErrorMsgBean() {
		return errorMsgBean;
	}

	public void setErrorMsgBean(ErrorMsgBean errorMsgBean) {
		this.errorMsgBean = errorMsgBean;
	}

	public int getErrorCode() {
		return errorMsgBean.getErrorCode();
	}

	public void setErrorCode(int errorCode) {
		this.errorMsgBean.setErrorCode(errorCode);
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsgBean.setErrorMsg(errorMsg);
	}

	public String getErrorMsg() {
		return errorMsgBean.getErrorMsg();
	}
	
	public String getErrorId() {
		return errorMsgBean.getErrorId();
	}

	public void setErrorId(String errorId) {
		this.errorMsgBean.setErrorId(errorId);
	}


	public String toResponseJson(){
		if(StringUtils.isBlank(errorMsgBean.getErrorId())){
			errorMsgBean.setErrorId(UUID.randomUUID().toString());
		}
		String responseStr= JsonUtil.toJson(errorMsgBean);
//		String responseStr=String.format("{\"error_id\" : \"%s\", \"error_code\": %s, \"msg\" : \"%s\"}", errorMsgBean.getErrorId(),this.errorMsgBean.getErrorCode(),this.errorMsgBean.getErrorMsg());
		return responseStr;
	}

}
