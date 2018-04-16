package com.socket.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间公共类
 * 
 * @author ouArea
 * 
 */
public class TimeUtil {
	private static SimpleDateFormat simpleDateFormat = null;

	/**
	 * 获取时间格式类
	 * 
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat() {
		if (null == simpleDateFormat) {
			return new SimpleDateFormat("yyyyMMddHHmmss", Locale.SIMPLIFIED_CHINESE);
		} else {
			return simpleDateFormat;
		}
	}

	/**
	 * 将时间按格式转化为数据库保存的字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDataBaseString(Date date) {
		return getSimpleDateFormat().format(date);
	}
}
