package com.zzh.mvc;

import java.io.File;

import javax.servlet.ServletContext;

import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.meta.Val;

public class ServerFileMaker implements ValueMaker {

	private ServletContext context;

	public ServerFileMaker(ServletContext context) {
		this.context = context;
	}

	@Override
	public String forType() {
		return Val.file;
	}

	@Override
	public Object make(Val val) {
		String path = context.getRealPath(val.getValue().toString());
		return new File(path);
	}
}
