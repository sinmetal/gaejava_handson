package org.sinmetal.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.servlet.http.*;

import org.junit.*;
import org.sinmetal.*;
import org.sinmetal.meta.*;
import org.sinmetal.model.*;
import org.slim3.datastore.*;

/**
 * {@link ItemController} のテスト
 * 
 * @author sinmetal
 *
 */
public class ItemControllerTest extends AbstructControllerTestCase {

	/**
	 * 新規 {@link Item} 作成し、Datastoreに登録されることを確認するテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreatedItem() throws Exception {
		tester.request.setMethod("POST");
		tester.request.setInputStream(TestUtil
				.getResource("/json/item/post_ok.json"));
		tester.start(ItemController.PATH);
		assertThat(tester.response.getStatus(),
				is(HttpServletResponse.SC_CREATED));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));

		Item responseItem = ItemMeta.get().jsonToModel(
				tester.response.getOutputAsString());

		Item stored = Datastore
				.getOrNull(ItemMeta.get(), responseItem.getKey());
		assertThat(stored, notNullValue());
		assertThat(stored.getTitle(), is("sample title"));
		assertThat(stored.getContent(), is("sample content"));
		assertThat(stored.getCreatedAt(), notNullValue());
		assertThat(stored.getUpdatedAt(), notNullValue());
	}

	/**
	 * 新規 {@link Item} 登録時にTitleが無い場合、Errorになることを確認するテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreatedItemNoTitle() throws Exception {
		tester.request.setMethod("POST");
		tester.request.setInputStream(TestUtil
				.getResource("/json/item/post_ng.json"));
		tester.start(ItemController.PATH);
		assertThat(tester.response.getStatus(),
				is(HttpServletResponse.SC_BAD_REQUEST));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));
		assertThat(tester.response.getOutputAsString(),
				is("[\"title is required.\"]"));
	}
}
