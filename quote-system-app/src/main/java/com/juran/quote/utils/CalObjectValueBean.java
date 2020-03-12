/** 
 *  IBM Confidential 
 *  OCO Source Materials 
 *  IBM DemandTec Promotion Optimization,IBM DemandTec Promotion Planning 
 *  (c) Copyright IBM Corp 2013 
 *  The source code for this program is not published or otherwise divested of its trade secrets, 
 *  irrespective of what has been deposited with the U.S. Copyright Office. 
 **/
package com.juran.quote.utils;

import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * @author chenpeng
 */
@Data
public class CalObjectValueBean{

	public Object object;
	public Map<String,Double> resultMap = new HashMap<String,Double>();

	public CalObjectValueBean(Object object)
	{
		this.object = object;
	}

	public Double getValue(String fieldName) throws Exception{
		// TODO Auto-generated method stub
		if(resultMap.containsKey(fieldName))
			return resultMap.get(fieldName);
		else
			return new Double(BeanUtils.getProperty(object,fieldName));
	}

	public void setValue(String fieldName, Double result)throws Exception {
		// TODO Auto-generated method stub
		BeanUtils.setProperty(object, fieldName, result);
		resultMap.put(fieldName, result);
	}
}
