package org.sinmetal.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.servlet.http.*;

import org.junit.*;
import org.sinmetal.meta.*;
import org.sinmetal.model.*;

/**
 * {@link IndexController} のテスト
 * 
 * @author sinmetal
 *
 */
public class IndexControllerTest extends AbstructControllerTestCase {

	/**
	 * GetのResponseを確認する
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGet() throws Exception {
		tester.start("/");
		assertThat(tester.response.getStatus(),
				is(equalTo(HttpServletResponse.SC_OK)));
		Item item = ItemMeta.get().jsonToModel(
				tester.response.getOutputAsString());
		assertThat(item.getKey(), notNullValue());
		assertThat(item.getEmail(), notNullValue());
		assertThat(item.getTitle(), notNullValue());
		assertThat(item.getContent(), notNullValue());
		assertThat(item.getCreatedAt(), notNullValue());
		assertThat(item.getUpdatedAt(), notNullValue());
	}
}
