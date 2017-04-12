package com.bobman.contentfilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Parses input, assuming HTML, and returns filtered output.
 * 
 * Assumes the whitelist of tags are not nested.
 * 
 * Returns newlines between tag instance, i.e. if requested tags are "p",
 * returns everything between every &lt;p&gt; / &lt;/p&gt; pair, filtered of all
 * other HTML tags, with a newline between them. Effectively, returns paragraphs
 * of text.
 *
 */
public class HTMLContentFilter extends ContentFilter {
	private URL sourceURL;
	private Set<String> tagContentToReturn = new HashSet<>();
	private static final String TAG_START = "<";
	private static final char TAG_START_CHAR = '<';
	private static final String TAG_END = ">";
	private static final char TAG_END_CHAR = '>';
	private static final String TAG_ENDSTART = "</";

	public HTMLContentFilter(URL source, String... tagsToReturn) {
		super(source.toString());
		sourceURL = source;
		for (String t : tagsToReturn) {
			if (t.startsWith(TAG_START)) {
				t = t.substring(1);
			}
			if (t.endsWith(TAG_END)) {
				t = t.substring(0, t.length() - 2);
			}
			tagContentToReturn.add(t);
		}
	}

	@Override
	public String filter() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(sourceURL.openStream()));
		String line = null;
		String contentIn = "";
		while ((line = in.readLine()) != null) {
			contentIn += line;
		}
		StringBuilder contentOut = new StringBuilder();

		for (String t : tagContentToReturn) {
			// if tags are . delimited, apply recursively, i.e. body.p
			// returns
			// all p tags within body.
			String[] nestedTags = t.split("\\.");
			String source = contentIn;
			String partialContentOut = "";
			for (String tag : nestedTags) {
				if (partialContentOut.length() > 0) {
					source = partialContentOut;
					partialContentOut = "";
				}
				int pos = 0;
				while (pos >= 0) {
					String startTag = TAG_START + tag;
					String stopTag = TAG_ENDSTART + tag;
					int startIndex = findFirstIn(pos, source, startTag + " ", startTag + ">");
					if (startIndex > 0) {
						int endIndex = source.indexOf(stopTag, pos + startIndex);
						if (endIndex > 0) {
							partialContentOut += source.substring(startIndex, endIndex);
							partialContentOut += "\n";
							pos = endIndex;
						} else {
							pos = -1;
							break;
						}
					} else {
						pos = -1;
						break;
					}
				}
				contentOut.append(partialContentOut);
			}
		}

		return contentOut.toString();
	}

	/**
	 * Returns the index of the first occurrence of the passed strings in "in"
	 * 
	 * @param in
	 * @param strings
	 * @return
	 */
	private int findFirstIn(int start, String in, String... strings) {
		int min = in.length();
		for (String s : strings) {
			int i = in.indexOf(s, start);
			if (i < min && i >= 0) {
				min = i;
			}
		}
		return min;
	}

}
