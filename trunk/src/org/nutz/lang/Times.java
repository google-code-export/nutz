package org.nutz.lang;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 一些时间相关的帮助函数
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Times {

	/**
	 * 将一个秒数（天中），转换成一个如下格式的数组:
	 * 
	 * <pre>
	 * [0-23][0-59[-059]
	 * </pre>
	 * 
	 * @param sec
	 *            秒数
	 * @return 时分秒的数组
	 */
	public static int[] T(int sec) {
		int[] re = new int[3];
		re[0] = Math.min(23, sec / 3600);
		re[1] = Math.min(59, (sec - (re[0] * 3600)) / 60);
		re[2] = Math.min(59, sec - (re[0] * 3600) - (re[1] * 60));
		return re;
	}

	/**
	 * @return 服务器当前时间
	 */
	public static Date now() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 根据字符串得到时间
	 * 
	 * <pre>
	 * 如果你输入了格式为 "yyyy-MM-dd HH:mm:ss"
	 *    那么会匹配到秒
	 *    
	 * 如果你输入格式为 "yyyy-MM-dd"
	 *    相当于你输入了 "yyyy-MM-dd 00:00:00"
	 * </pre>
	 * 
	 * @param ds
	 *            时间字符串
	 * @return 时间
	 */
	public static Date D(String ds) {
		try {
			if (ds.length() < 12)
				return DF_DATE.parse(ds);
			return DF_DATE_TIME.parse(ds);
		}
		catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据毫秒数得到时间
	 * 
	 * @param ms
	 *            时间的毫秒数
	 * @return 时间
	 */
	public static Date D(long ms) {
		return new Date(ms);
	}

	/**
	 * 根据字符串得到时间
	 * 
	 * <pre>
	 * 如果你输入了格式为 "yyyy-MM-dd HH:mm:ss"
	 *    那么会匹配到秒
	 *    
	 * 如果你输入格式为 "yyyy-MM-dd"
	 *    相当于你输入了 "yyyy-MM-dd 00:00:00"
	 * </pre>
	 * 
	 * @param ds
	 *            时间字符串
	 * @return 时间
	 */
	public static Calendar C(String ds) {
		return C(D(ds));
	}

	/**
	 * 根据日期对象得到时间
	 * 
	 * @param d
	 *            时间对象
	 * @return 时间
	 */
	public static Calendar C(Date d) {
		return C(d.getTime());
	}

	/**
	 * 根据毫秒数得到时间
	 * 
	 * @param ms
	 *            时间的毫秒数
	 * @return 时间
	 */
	public static Calendar C(long ms) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ms);
		return c;
	}

	/**
	 * 根据时间得到字符串
	 * 
	 * @param d
	 *            日期时间对象
	 * @return 时间字符串 , 格式为 y-M-d H:m:s.S
	 */
	public static String sDTms(Date d) {
		return DF_DATE_TIME_MS.format(d);
	}

	/**
	 * 根据时间得到字符串
	 * 
	 * @param d
	 *            日期时间对象
	 * @return 时间字符串 , 格式为 yyyy-MM-dd HH:mm:ss
	 */
	public static String sDT(Date d) {
		return DF_DATE_TIME.format(d);
	}

	/**
	 * 根据时间得到日期字符串
	 * 
	 * @param d
	 *            日期时间对象
	 * @return 时间字符串 , 格式为 yyyy-MM-dd
	 */
	public static String sD(Date d) {
		return DF_DATE.format(d);
	}

	/**
	 * 将一个秒数（天中），转换成一个时间字符串
	 * 
	 * @param sec
	 *            秒数
	 * @return 格式为 'HH:mm:ss' 的字符串
	 */
	public static String sT(int sec) {
		int[] ss = T(sec);
		return Strings.alignRight(ss[0], 2, '0')
				+ ":"
				+ Strings.alignRight(ss[1], 2, '0')
				+ ":"
				+ Strings.alignRight(ss[2], 2, '0');
	}

	/**
	 * 以本周为基础获得某一周的时间范围
	 * 
	 * @param off
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 * 
	 * @see org.nutz.ztask.util.ZTasks#weeks(long, int, int)
	 */
	public static Date[] week(int off) {
		return week(System.currentTimeMillis(), off);
	}

	/**
	 * 以某周为基础获得某一周的时间范围
	 * 
	 * @param base
	 *            基础时间，毫秒
	 * @param off
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 * 
	 * @see org.nutz.ztask.util.ZTasks#weeks(long, int, int)
	 */
	public static Date[] week(long base, int off) {
		return weeks(base, off, off);
	}

	/**
	 * 以本周为基础获得时间范围
	 * 
	 * @param offL
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * @param offR
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 * 
	 * @see org.nutz.ztask.util.ZTasks#weeks(long, int, int)
	 */
	public static Date[] weeks(int offL, int offR) {
		return weeks(System.currentTimeMillis(), offL, offR);
	}

	/**
	 * 按周获得某几周周一 00:00:00 到周六 的时间范围
	 * <p>
	 * 它会根据给定的 offL 和 offR 得到一个时间范围
	 * <p>
	 * 对本函数来说 week(-3,-5) 和 week(-5,-3) 是一个意思
	 * 
	 * @param base
	 *            基础时间，毫秒
	 * @param offL
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * @param offR
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 */
	public static Date[] weeks(long base, int offL, int offR) {
		int from = Math.min(offL, offR);
		int len = Math.abs(offL - offR);
		// 现在
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(base);

		Date[] re = new Date[2];

		// 计算开始
		c.setTimeInMillis(c.getTimeInMillis() + MS_WEEK * from);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		re[0] = c.getTime();

		// 计算结束
		c.setTimeInMillis(c.getTimeInMillis() + MS_WEEK * (len + 1) - 1000);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		re[1] = c.getTime();

		// 返回
		return re;
	}

	private static final DateFormat DF_DATE_TIME_MS = new SimpleDateFormat("y-M-d H:m:s.S");
	private static final DateFormat DF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat DF_DATE = new SimpleDateFormat("yyyy-MM-dd");

	private static final long MS_DAY = 3600 * 24 * 1000;
	private static final long MS_WEEK = MS_DAY * 7;
}
