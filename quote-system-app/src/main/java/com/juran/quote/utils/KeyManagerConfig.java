package com.juran.quote.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by songmingxu
 */
@Component
@ConfigurationProperties(prefix = "keyManagerConfig")
public class KeyManagerConfig {

	private String sendUrl;

	public String getSendUrl() {
		return sendUrl;
	}

	public void setSendUrl(String sendUrl) {
		this.sendUrl = sendUrl;
	}
}
