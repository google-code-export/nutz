package org.nutz.lang.util;

import java.io.File;
import java.io.UnsupportedEncodingException;

import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.nutz.lang.Files;
import org.nutz.lang.Strings;

/**
 * 磁盘操作的帮助函数集合
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Disks {

	/**
	 * 一个 Vistor 模式的目录深层遍历
	 * 
	 * @param f
	 *            要遍历的目录或者文件，如果是目录，深层遍历，否则，只访问一次文件
	 * @param fv
	 *            对文件要进行的操作
	 * @param filter
	 *            遍历目录时，哪些文件应该被忽略
	 * @return 遍历的文件个数
	 */
	public static int visitFile(File f, FileVisitor fv, FilenameFilter filter) {
		int re = 0;
		if (f.isFile()) {
			fv.visit(f);
			re++;
		} else if (f.isDirectory()) {
			File[] fs = null == filter ? f.listFiles() : f.listFiles(filter);
			for (File ff : fs)
				re += visitFile(ff, fv, filter);
		}
		return re;
	}

	/**
	 * 将两个文件对象比较，得出相对路径
	 * 
	 * @param base
	 *            基础文件对象
	 * @param file
	 *            相对文件对象
	 * @return 相对于基础文件对象的相对路径
	 */
	public static String getRelativePath(File base, File file) {
		return getRelativePath(base.getAbsolutePath(), file.getAbsolutePath());
	}

	/**
	 * 将两个路径比较，得出相对路径
	 * 
	 * @param base
	 *            基础路径
	 * @param path
	 *            相对文件路径
	 * @return 相对于基础路径对象的相对路径
	 */
	public static String getRelativePath(String base, String path) {
		String[] bb = Strings.splitIgnoreBlank(getCanonicalPath(base), "[\\\\/]");
		String[] ff = Strings.splitIgnoreBlank(getCanonicalPath(path), "[\\\\/]");
		int len = Math.min(bb.length, ff.length);
		int pos = 0;
		for (; pos < len; pos++)
			if (!bb[pos].equals(ff[pos]))
				break;
		StringBuilder sb = new StringBuilder(Strings.dup("../", bb.length - 1 - pos));
		for (; pos < ff.length; pos++)
			sb.append(ff[pos]).append('/');
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * 整理路径。 将会合并路径中的 ".."
	 * 
	 * @param path
	 *            路径
	 * @return 整理后的路径
	 */
	public static String getCanonicalPath(String path) {
		String[] pa = Strings.splitIgnoreBlank(path, "[\\\\/]");
		LinkedList<String> paths = new LinkedList<String>();
		for (String s : pa) {
			if ("..".equals(s)) {
				if (paths.size() > 0)
					paths.removeLast();
				continue;
			} else {
				paths.add(s);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String s : paths) {
			sb.append("/").append(s);
		}
		return sb.deleteCharAt(0).toString();
	}

	/**
	 * @return 当前账户的主目录全路径
	 */
	public static String home() {
		return System.getProperty("user.home");
	}

	/**
	 * @param path
	 *            相对用户主目录的路径
	 * @return 相对用户主目录的全路径
	 */
	public static String home(String path) {
		return home() + path;
	}

	/**
	 * 获取一个路径的绝对路径
	 * 
	 * @param path
	 *            路径
	 * @return 绝对路径
	 */
	public static String absolute(String path) {
		return absolute(path, Files.class.getClassLoader(), Charset.defaultCharset().name());
	}

	/**
	 * 获取一个路径的绝对路径
	 * 
	 * @param path
	 *            路径
	 * @param klassLoader
	 *            参考 ClassLoader
	 * @param enc
	 *            路径编码方式
	 * @return 绝对路径
	 */
	public static String absolute(String path, ClassLoader klassLoader, String enc) {
		path = normalize(path, enc);
		if (null == path || path.length() == 0)
			return null;

		File f = new File(path);
		if (!f.exists()) {
			URL url = klassLoader.getResource(path);
			if (null == url)
				url = ClassLoader.getSystemResource(path);
			if (null != url)
				return normalize(url.getPath(), "UTF-8");//通过URL获取String,一律使用UTF-8编码进行解码
			return null;
		}
		return path;
	}

	/**
	 * 让路径变成正常路径，将 ~ 替换成用户主目录
	 * 
	 * @param path
	 *            路径
	 * @return 正常化后的路径
	 */
	public static String normalize(String path) {
		return normalize(path, Charset.defaultCharset().name());
	}

	/**
	 * 让路径变成正常路径，将 ~ 替换成用户主目录
	 * 
	 * @param path
	 *            路径
	 * @param enc
	 *            路径编码方式
	 * @return 正常化后的路径
	 */
	public static String normalize(String path, String enc) {
		if (null == path || path.length() == 0)
			return null;
		// if the path start with ~
		if (path.charAt(0) == '~')
			path = Disks.home() + path.substring(1);
		// Encode path
		try {
			return URLDecoder.decode(path, enc);
		}
		catch (UnsupportedEncodingException e) {
			return null;
		}

	}
}