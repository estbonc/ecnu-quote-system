package com.ecnu.paper.quotesystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.stereotype.Component;


@Component
public class CustomObjectMapper extends ObjectMapper {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3161048759088835085L;

	public CustomObjectMapper(){
		SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		registerModule(simpleModule);
    }



}

