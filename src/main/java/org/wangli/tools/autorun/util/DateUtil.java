package org.wangli.tools.autorun.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/**
	 * 将日期从绝对毫秒转换成字符串格式
	 * 
	 * @param time_ms
	 * @return
	 */
	public static final String transferMs2String(long time_ms) {
		String time_string = "";
		Date date = new Date(time_ms);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		time_string = sdf.format(date);
		return time_string;
	}

}
