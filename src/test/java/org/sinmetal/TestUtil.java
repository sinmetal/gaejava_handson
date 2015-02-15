package org.sinmetal;

import java.io.*;

import javax.servlet.*;

import com.google.common.io.*;

/**
 * UniTestのためのユーティリティ
 * 
 * @author sinmetal
 *
 */
public class TestUtil {

	/**
	 * Test時にtesterに渡すInputStreamを作成する
	 * 
	 * @param content
	 * @return {@link SequenceInputStream}
	 */
	public static ServletInputStream createInputStream(final byte[] content) {
		return new ServletInputStream() {

			ByteArrayInputStream in = new ByteArrayInputStream(content);

			@Override
			public int available() {
				return in.available();
			}

			@Override
			public int read() {
				return in.read();
			}

			@Override
			public int read(byte[] b, int off, int len) {
				return in.read(b, off, len);
			}
		};
	}

	/**
	 * Test時にtesterに渡すInputStreamを作成する
	 * 
	 * @param input
	 * @return {@link SequenceInputStream}
	 * @throws IOException
	 */
	public static ServletInputStream createInputStream(InputStream input)
			throws IOException {
		return createInputStream(ByteStreams.toByteArray(input));
	}

	/**
	 * ResourceからInputStreamを作成する
	 * 
	 * @param filePath
	 * @return {@link ServletInputStream}
	 * @throws IOException
	 */
	public static ServletInputStream getResource(String filePath)
			throws IOException {
		InputStream is = TestUtil.class.getResourceAsStream(filePath);
		return createInputStream(is);
	}
}
