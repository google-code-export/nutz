package org.nutz.resource.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.resource.NutResource;

/**
 * 针对本地文件系统，递归扫描一组特定的资源（不包括目录）
 * <ul>
 * <li>参数 <b>src</b> : 表示特定资源参考路径，可以是一个文件或者目录或者 jar 中的实体
 * <li>参数 <b>filter</b> : 将被作为一个正则表达式，来匹配资源名（注，不是全路径名，仅仅是名称）
 * </ul>
 * <p>
 * 特别需要说明的是
 * <ul>
 * <li>如果你要寻找的资源在一个 jar 里面，你的参考路径必须为一个具体的实体。
 * <li>默认的，所有的隐藏文件，将被忽略，如果想修改这个设置，请调用 setIgnoreHidden(false)
 * </ul>
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class LocalResourceScan extends AbstractResourceScan {

	private boolean ignoreHidden;

	/**
	 * 是否忽略隐藏文件
	 * 
	 * @param ignoreHidden
	 *            ： true 忽略， false 不忽略
	 * @return 自身
	 */
	public LocalResourceScan setIgnoreHidden(boolean ignoreHidden) {
		this.ignoreHidden = ignoreHidden;
		return this;
	}

	public LocalResourceScan() {
		this(true);
	}

	public LocalResourceScan(boolean ignoreHidden) {
		this.ignoreHidden = ignoreHidden;
	}

	public boolean canWork() {
		return true;
	}

	public List<NutResource> list(String src, String filter) {
		final List<NutResource> list = new LinkedList<NutResource>();
		final Pattern regex = null == filter ? null : Pattern.compile(filter);
		// 查看资源是否存在在磁盘系统中
		File f = new File(Disks.normalize(src));
		if (f.isFile()) {
			list.add(new FileResource(f.getParentFile(), f));
			return list;
		}

		// 如果存在，递归这个目录
		if (f.exists()) {
			scanByFile(list, regex, f.getParent(), f, ignoreHidden);
		}
		// 查看资源是否存在在 CLASSPATH 中
		else {
			// 如果在其中，那么是在一个 JAR 中还是在一个本地目录里
			String path = Disks.absolute(	src,
											getClass().getClassLoader(),
											Encoding.defaultEncoding());
			if (null != path) {
				f = new File(path);
				// 如果是本地目录，递归这个目录
				if (!path.contains(".jar!")) {
					// 首先查找以下， CLASSPATH 从哪里开始
					int pos = path.lastIndexOf(src);
					scanByFile(list, regex, path.substring(0, pos), f, ignoreHidden);
				}
				// 如果在 jar 中，则循环查找这个 jar 的每一个实体
				else {
					int posL = path.indexOf("file:");
					posL = posL < 0 ? 0 : posL + "file:".length();
					int posR = path.indexOf(".jar!") + ".jar!".length();
					int posE = path.replace('\\', '/').lastIndexOf('/');
					String jarPath = path.substring(posL, posR - 1);
					String prefix = path.substring(posR + 1, posE + 1);
					try {
						JarFile jar = new JarFile(jarPath);
						Enumeration<JarEntry> ens = jar.entries();
						while (ens.hasMoreElements()) {
							JarEntry jen = ens.nextElement();
							String name = jen.getName();
							if (name.startsWith(prefix)) {
								int pos = name.replace('\\', '/').lastIndexOf('/');
								String enName = name.substring(pos + 1);
								if (null == regex || regex.matcher(enName).find())
									list.add(new JarEntryResource(jar, jen));
							}
						}
					}
					catch (IOException e) {
						throw Lang.wrapThrow(e);
					}
				}
			}
		}
		// 返回资源列表
		return list;
	}

	private void scanByFile(final List<NutResource> list,
							final Pattern regex,
							final String base,
							File f,
							final boolean ignoreHidden) {
		if (null == f || (ignoreHidden && f.isHidden()))
			return;

		if (!f.isDirectory())
			f = f.getParentFile();

		Disks.visitFile(f, new FileVisitor() {
			public void visit(File file) {
				list.add(new FileResource(base, file));
			}
		}, new FileFilter() {
			public boolean accept(File theFile) {
				if (ignoreHidden && theFile.isHidden())
					return false;
				if (theFile.isDirectory())
					return true;
				return regex == null || regex.matcher(theFile.getName()).find();
			}
		});
	}
}