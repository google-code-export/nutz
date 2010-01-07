package org.nutz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.aop.AllAop;
import org.nutz.ioc.AllIocWithoutDB;
import org.nutz.json.AllJson;
import org.nutz.lang.AllLang;
import org.nutz.mvc.AllMvc;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AllLang.class, AllJson.class, AllMvc.class, AllIocWithoutDB.class,
		AllAop.class })
public class AllWithoutDB {}