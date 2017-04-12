package com.bobman.contentfilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class ContentFilterTest {
	@Test
	public void testHTMLFilter() throws IOException {
		URL url = new File("src/test/resources/hackers.html").toURI().toURL();

		HTMLContentFilter filter = new HTMLContentFilter(url, "body.p");

		String content = filter.filter();

		System.out.println(content);
	}
}
