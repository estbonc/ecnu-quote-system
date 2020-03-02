package com.ecnu.paper.quotesystem.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 王云龙
 * @date 2017年3月10日 上午9:40:37
 * @version 1.0
 * @description json处理工具类
 *
 */
public class JsonUtil {

	/**
	 * 
	 * @author 王云龙
	 * @date 2017年3月10日 上午9:45:03
	 * @version 1.0
	 * @description 对象转json
	 *
	 * @param t
	 * @return
	 */
	public static <T> String toJson(T t) {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(t);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
		return json;
	}

	// public static void main(String[] args) {
	// String json="{\"name\":\"aaa\",\"sex\":{\"aaa\":\"bbb\"}}";
	// Map<String ,String> nodes=new HashMap<String, String>();
	// nodes.put("bbb", "bbb");
	// System.out.println(addJsonStrNode(json, nodes));
	// }

	/**
	 * 
	 * @author 王云龙
	 * @date 2017年3月21日 下午1:30:41
	 * @version 1.0
	 * @description 根据path将json中的某个path
	 *
	 * @param json
	 *            需要解析的json
	 * @param path
	 *            需要解析json中的某个路径，例如：/user/type
	 * @param clazz
	 *            需要转换的class type
	 * @return
	 */
	public static <T> T toObject(String json, String path, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		T t = null;
		if (StringUtils.isBlank(path)) {
			try {
				t = mapper.readValue(json, clazz);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return t;
		}
		try {
			JsonNode jsonRoot = mapper.readTree(json);
			JsonNode jsonNode = jsonRoot.at(path);
			if (jsonNode.isMissingNode()) {
				return t;
			}
			t = mapper.readValue(jsonNode.toString(), clazz);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 
	 * @author 王云龙
	 * @date 2017年3月10日 上午9:57:16
	 * @version 1.0
	 * @description 将字符串转为对象
	 *
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T toObject(String json, Class<T> clazz) {
		return toObject(json, null, clazz);
	}

	/**
	 * 
	 * @author 王云龙
	 * @date 2017年3月16日 下午5:40:57
	 * @version 1.0
	 * @description 将字符串转为对象List
	 *
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> toObjectList(String json, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
		try {
			List<T> tList = mapper.readValue(json, javaType);
			return tList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @author 宋明旭
	 * @date 2017年6月7日 下午5:06:57
	 * @version 1.0
	 * @description 将字符串转为对象List
	 *
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> toObjectList(String json, String path, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		if (StringUtils.isBlank(path)) {
			JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
			try {
				List<T> tList = mapper.readValue(json, javaType);
				return tList;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				JsonNode jsonRoot = mapper.readTree(json);
				JsonNode jsonNode = jsonRoot.at(path);
				if (jsonNode.isMissingNode()) {
					return null;
				}
				JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
				List<T> tList = mapper.readValue(jsonNode.toString(), javaType);
				return tList;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @author 王云龙
	 * @date 2017年3月27日 上午11:37:51
	 * @version 1.0
	 * @description 向已有json字符串添加新节点
	 *
	 * @param json
	 * @param nodes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String addJsonStrNode(String json, Map<String, Object> nodes) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> jsonMap = null;
		String jsonResult = null;
		try {
			jsonMap = mapper.readValue(json, HashMap.class);
			jsonMap.putAll(nodes);
			jsonResult = mapper.writeValueAsString(jsonMap);
			return jsonResult;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

}
