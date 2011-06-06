package org.nutz.lang;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.castor.CastorTest;
import org.nutz.lang.meta.AllMeta;
import org.nutz.lang.random.AllRandom;
import org.nutz.lang.segment.CharSegmentTest;
import org.nutz.lang.segment.SegmentsTest;
import org.nutz.lang.stream.StringOutputStreamTest;
import org.nutz.lang.util.AllUtil;

@RunWith(Suite.class)
@Suite.SuiteClasses({	MirrorTest.class,
						LangTest.class,
						FilesTest.class,
						MathsTest.class,
						AllRandom.class,
						CharSegmentTest.class,
						SegmentsTest.class,
						CastorTest.class,
						AllUtil.class,
						NumsTest.class,
						StringsTest.class,
						StringOutputStreamTest.class,
						AllMeta.class})
public class AllLang {}
