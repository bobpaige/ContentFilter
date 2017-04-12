package com.bobman.contentfilter;

import java.io.IOException;
import java.net.MalformedURLException;

import lombok.Getter;

/**
 * Base class for content filter classes
 *
 */
public abstract class ContentFilter {
	private @Getter String source;

	public ContentFilter(String s) {
		this.source = s;
	}

	public abstract String filter() throws MalformedURLException, IOException;
}
