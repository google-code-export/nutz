package org.nutz.dao.impl;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.dao.SqlManager;
import org.nutz.dao.SqlNotFoundException;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.ComboSql;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.LinkedCharArray;
import org.nutz.log.Logs;

public class FileSqlManager implements SqlManager {

	public FileSqlManager(String... paths) {
		this.paths = paths;
		refresh();
	}

	private String[] paths;
	private Map<String, String> sqlMaps;
	private List<String> keys;

	public boolean contains(String key) {
		return sqlMaps.containsKey(key);
	}

	public void save() throws IOException {
		if (null == paths || paths.length != 1) {
			throw new RuntimeException("This Sql set not come from one single file!");
		}
		saveAs(paths[0]);
	}

	public void saveAs(String path) throws IOException {
		File f = new File(path);
		if (!f.exists())
			Files.createNewFile(f);
		else if (!f.isFile()) {
			throw Lang.makeThrow("Path '%s' should be a file.", f);
		} else {
			Files.deleteFile(f);
			Files.createNewFile(f);
		}
		saveAs(f);
	}

	public void saveAs(File f) throws IOException {
		Writer w = Streams.fileOutw(f);
		for (String key : keys) {
			w.append("/*").append(Strings.dup('-', 60)).append("*/\n");
			String sql = sqlMaps.get(key);
			w.append(format("/*%s*/\n", key));
			w.append(sql).append("\n");
		}
		w.flush();
		w.close();
	}

	public void refresh() {
		try {
			this.buildSQLMaps();
		}
		catch (Exception ex) {
			throw Lang.wrapThrow(ex);
		}

	}

	public String get(String key) {
		String sql = sqlMaps.get(key);
		if (null == sql)
			throw new SqlNotFoundException(key, paths);
		return sql;
	}

	public Sql create(String key) throws SqlNotFoundException {
		return Sqls.create(get(key));
	}

	public ComboSql createCombo(String... keys) {
		ComboSql combo = new ComboSql();
		if (null == keys || keys.length == 0)
			keys = this.keys();
		for (String key : keys) {
			Sql sql = create(key);
			combo.add(sql);
		}
		return combo;
	}

	public int count() {
		return sqlMaps.size();
	}

	public String[] keys() {
		return keys.toArray(new String[keys.size()]);
	}

	private void buildSQLMaps() {
		sqlMaps = new HashMap<String, String>();
		if (null != paths)
			for (String path : paths) {
				if (null == path)
					continue;
				InputStream stream = Files.findFileAsStream(path);
				if (stream != null) {
					Reader reader = null;
					try {
						reader = new InputStreamReader(stream);
						loadSQL(reader);
					}
					catch (IOException e) {
						Logs.getLog(getClass()).warnf("Fail to load sqls from %s",e);
					}
					finally {
						Streams.safeClose(reader);
						Streams.safeClose(stream);
					}
				}
			}
	}

	public void addSql(String key, String value) {
		if (sqlMaps.containsKey(key)) {
			throw Lang.makeThrow("duplicate key '%s'", key);
		}
		key = Strings.trim(key);
		sqlMaps.put(key, value);
		keys.add(key);
	}

	static final Pattern ptn = Pattern.compile("(?<=^\n/[*])(.*)(?=[*]/)");

	static class InnerStack {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		LinkedCharArray list = new LinkedCharArray();
		LinkedCharArray cmts = new LinkedCharArray();
		String key = null;
		boolean inNormalComment;

		void eat(int c) {
			if (inNormalComment) {
				if (cmts.push(c).endsWith("*/")) {
					cmts.clear();
					inNormalComment = false;
				}
			} else if (key != null) {
				if (list.push(c).endsWith("\n/*")) {
					list.popLast(3);
					addOne();
					list.push("\n/*");
				} else if (list.endsWith("/*")) {
					list.popLast(2);
					inNormalComment = true;
				}
			} else {
				if (list.size() < 3) {
					if (!"\n/*".startsWith(list.push(c).toString())) {
						list.clear();
					}
				} else {
					if (list.push(c).endsWith("*/")) {
						Matcher matcher = ptn.matcher(list.popAll());
						if (matcher.find()) {
							key = Strings.trim(matcher.group());
						}
					}
				}
			}
		}

		void addOne() {
			String value = Strings.trim(list.popAll());
			if (!Strings.isBlank(value))
				map.put(key, value);
			key = null;
		}

	}

	static class SqlFileBuilder {
		LinkedHashMap<String, String> map;

		SqlFileBuilder(BufferedReader reader) throws IOException {
			InnerStack stack = new InnerStack();
			int c;
			stack.eat('\n');
			while (-1 != (c = reader.read())) {
				stack.eat(c);
			}
			if (stack.key != null)
				stack.addOne();
			map = stack.map;
			Streams.safeClose(reader);
		}

		Set<String> keys() {
			return map.keySet();
		}

		String get(String key) {
			return map.get(key);
		}
	}

	public void remove(String key) {
		this.keys.remove(key);
		this.sqlMaps.remove(key);
	}

	/**
	 * 执行根据流来加载sql内容的操作
	 * 
	 * @param stream
	 * @throws IOException
	 * @author mawenming at 2010-4-10 上午10:04:17
	 */
	private void loadSQL(Reader stream) throws IOException {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(stream);
			SqlFileBuilder p = new SqlFileBuilder(bufferedReader);

			Iterator<String> it = p.keys().iterator();
			keys = new ArrayList<String>(p.map.size());
			while (it.hasNext()) {
				String key = it.next();
				String value = Strings.trim(p.get(key));
				addSql(key, value);
			}
		}
		finally {
			Streams.safeClose(bufferedReader);
			Streams.safeClose(stream);
		}

	}

}
