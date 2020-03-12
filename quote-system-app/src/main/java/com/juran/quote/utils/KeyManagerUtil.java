package com.juran.quote.utils;

import com.alibaba.fastjson.JSONObject;
import com.juran.core.utils.http.HttpSendUtil;
import com.juran.core.utils.http.bean.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KeyManagerUtil {

    @Autowired
    KeyManagerConfig config;

    private String[] chars = new String[] { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9"};

    /**
     * 通过keyManager获取唯一主键
     *
     * @return
     */
    private Long getKey() {
        String url = config.getSendUrl() + "/key-manager-app/api/v1/keys?keyMAsterId=design&subTable=assetId";
        HttpResponse httpResponse = HttpSendUtil.sendHttpGet(url);
        if (httpResponse.getStatus() == 200) {
            JSONObject keyObj = JSONObject.parseObject(httpResponse.getContent());
            if (keyObj != null) {
                String keyStr = keyObj.getString("key");
                if (StringUtils.isNotEmpty(keyStr)) {
                    return Long.valueOf(keyStr);
                }
            }
        }
        return null;
    }

    public Long get16PlacesId() {
        StringBuffer shortBuffer = new StringBuffer();

        //根据年月和时间生成八位数字
        int time = (int)( System.currentTimeMillis() % 10E7);
        shortBuffer.append(time);

        //利用UUID生成八位数字
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 10]);
        }

        return Long.parseLong(shortBuffer.toString());
    }

    public Long getUniqueId() {
        Long uniqueId = getKey();
        while (true) {
            if (uniqueId != null && !uniqueId.equals(Long.valueOf(0))) {
                return uniqueId;
            } else {
                uniqueId = getKey();
            }
        }
    }
}
