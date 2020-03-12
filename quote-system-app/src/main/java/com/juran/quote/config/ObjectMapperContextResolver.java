package com.juran.quote.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
    @Autowired
    private CustomObjectMapper objectMapper;
    public ObjectMapperContextResolver() {
        super();
    }
    @Override
    public ObjectMapper getContext(Class<?> type) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	return objectMapper;
    }
}
