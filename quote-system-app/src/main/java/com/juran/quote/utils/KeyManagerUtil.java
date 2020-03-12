package com.juran.quote.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KeyManagerUtil {

    private String[] chars = new String[]{"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9"};

    public Long get16PlacesId() {
        StringBuilder shortBuilder = new StringBuilder();

        //根据年月和时间生成八位数字
        int time = (int) (System.currentTimeMillis() % 10E7);
        shortBuilder.append(time);

        //利用UUID生成八位数字
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuilder.append(chars[x % 10]);
        }

        return Long.parseLong(shortBuilder.toString());
    }

    public Long getUniqueId() {
        Long uniqueId = get16PlacesId();
        while (true) {
            if (uniqueId != null && !uniqueId.equals(0L)) {
                return uniqueId;
            } else {
                uniqueId = get16PlacesId();
            }
        }
    }
}
