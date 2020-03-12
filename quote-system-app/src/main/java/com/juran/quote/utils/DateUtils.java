package com.juran.quote.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class DateUtils {

	/**比较时间大小 true：当前时间大  false：当前时间小*/
	public static Boolean compareTime(Date endTime){
	        try {
	            Date dt1 = new Date();//当前时间跟活动结束时间比较
	            if (dt1.getTime() > endTime.getTime()) {
	                return false;
	            } else if (dt1.getTime() < endTime.getTime()) {
	                return true;
	            }
	        } catch (Exception exception) {
	            exception.printStackTrace();
	        }
	        return false;
	}
	/***计算倒计时时分秒*/
	public static String calcAutoTime(Date overDate)
    {
        StringBuilder sb=new StringBuilder();
        Date nowDate=new Date();
        if(overDate.compareTo(nowDate)>0)
        {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long diff=overDate.getTime() - nowDate.getTime();
            long hour=diff / nd*24+diff% nd / nh;
            long minite=diff % nd % nh / nm;
            long second=diff % nd % nh % nm / 1000;
            sb.append(hour>=10?hour:"0"+hour);
            sb.append(":");
            sb.append(minite>=10?minite:"0"+minite);
            sb.append(":");
            sb.append(second>=10?second:"0"+second);
        }
        return  sb.toString();
    }
	/**date  转 string**/
	public static String dateToString(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(date);  
		return str;
	}

	/**
	 * 此函数非原创，从网上搜索而来，timeZoneOffset原为int类型，为班加罗尔调整成float类型
	 * timeZoneOffset表示时区，如中国一般使用东八区，因此timeZoneOffset就是8
	 * @param timeZoneOffset
	 * @return
	 */
	public static String getFormatedDateString(float timeZoneOffset){
		if (timeZoneOffset > 13 || timeZoneOffset < -12) {
			timeZoneOffset = 0;
		}

		int newTime=(int)(timeZoneOffset * 60 * 60 * 1000);
		TimeZone timeZone;
		String[] ids = TimeZone.getAvailableIDs(newTime);
		if (ids.length == 0) {
			timeZone = TimeZone.getDefault();
		} else {
			timeZone = new SimpleTimeZone(newTime, ids[0]);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(timeZone);
		return sdf.format(new Date());
	}
	/**
	 * 获取当天最后一秒钟时间
	 */
	public static String getLastSecond(String date){
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		DateFormat format2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d2;
		Date resultDate = null;
		try {
			d2 = format.parse(date);
			int dayMis=1000*60*60*24;//一天的毫秒-1
			long curMillisecond=d2.getTime();//当天的毫秒  
			long resultMis=curMillisecond+(dayMis-1);
			resultDate=new Date(resultMis);  
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		
		return format2.format(resultDate);
	}
	
}
