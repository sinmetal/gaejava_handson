package org.sinmetal.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.*;

import javax.servlet.http.*;

import org.junit.*;
import org.sinmetal.*;
import org.sinmetal.controller.ItemController.PostForm;
import org.sinmetal.meta.*;
import org.sinmetal.model.*;
import org.sinmetal.service.*;
import org.slim3.datastore.*;

import com.google.appengine.api.datastore.*;

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

	/**
	 * 指定した {@link Item} が更新されることを確認
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdatedItem() throws Exception {
		Item testData;
		{
			PostForm form = new PostForm();
			form.title = "sample title";
			form.content = "sample content";
			testData = ItemService.create("user@example.com", form);
		}
		String strKey = Datastore.keyToString(testData.getKey());

		tester.request.setMethod("PUT");
		tester.request.setInputStream(TestUtil
				.getResource("/json/item/put_ok.json"));
		tester.start(ItemController.PATH + "/" + strKey);
		assertThat(tester.response.getStatus(), is(HttpServletResponse.SC_OK));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));

		Item responseItem = ItemMeta.get().jsonToModel(
				tester.response.getOutputAsString());

		Item stored = Datastore
				.getOrNull(ItemMeta.get(), responseItem.getKey());
		assertThat(stored, notNullValue());
		assertThat(stored.getTitle(), is("new title"));
		assertThat(stored.getContent(), is("new content"));
		assertThat(stored.getCreatedAt(), notNullValue());
		assertThat(stored.getUpdatedAt(), notNullValue());
	}

	/**
	 * {@link Item} 更新時にTitleが無い場合、Errorになることを確認するテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdatedItemNoTitle() throws Exception {
		Item testData;
		{
			PostForm form = new PostForm();
			form.title = "sample title";
			form.content = "sample content";
			testData = ItemService.create("user@example.com", form);
		}
		String strKey = Datastore.keyToString(testData.getKey());

		tester.request.setMethod("PUT");
		tester.request.setInputStream(TestUtil
				.getResource("/json/item/put_ng.json"));
		tester.start(ItemController.PATH + "/" + strKey);

		assertThat(tester.response.getStatus(),
				is(HttpServletResponse.SC_BAD_REQUEST));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));
		assertThat(tester.response.getOutputAsString(),
				is("[\"title is required.\"]"));
	}

	/**
	 * {@link Item} が0件の場合に、正常に一覧取得ができることを確認
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetList() throws Exception {
		Map<Key, Item> testData = new HashMap<>();
		{
			for (int i = 0; i < 10; i++) {
				PostForm form = new PostForm();
				form.title = "sample title" + i;
				form.content = "sample content" + i;
				Item item = ItemService.create("user" + i + "@example.com",
						form);
				testData.put(item.getKey(), item);
			}
		}

		tester.request.setMethod("GET");
		tester.start(ItemController.PATH);
		assertThat(tester.response.getStatus(), is(HttpServletResponse.SC_OK));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));
		Item[] results = ItemMeta.get().jsonToModels(
				tester.response.getOutputAsString());
		assertThat(results.length, is(testData.size()));
		for (Item item : results) {
			assertThat(testData.containsKey(item.getKey()), is(true));
			Item src = testData.get(item.getKey());
			assertThat(item.getEmail(), is(src.getEmail()));
			assertThat(item.getTitle(), is(src.getTitle()));
			assertThat(item.getContent(), is(src.getContent()));
			assertThat(item.getCreatedAt(), is(src.getCreatedAt()));
			assertThat(item.getUpdatedAt(), is(src.getUpdatedAt()));
		}
	}

	/**
	 * {@link Item} が0件の場合に、正常に一覧取得ができることを確認
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetListForDataEmpty() throws Exception {
		tester.request.setMethod("GET");
		tester.start(ItemController.PATH);
		assertThat(tester.response.getStatus(), is(HttpServletResponse.SC_OK));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));
		assertThat(tester.response.getOutputAsString(), is("[]"));
	}

	@Test
	public void testGet() throws Exception {
		Item testData;
		{
			PostForm form = new PostForm();
			form.title = "sample title";
			form.content = "sample content";
			testData = ItemService.create("user@example.com", form);
		}
		String strKey = Datastore.keyToString(testData.getKey());

		tester.request.setMethod("GET");
		tester.start(ItemController.PATH + "/" + strKey);

		assertThat(tester.response.getStatus(), is(HttpServletResponse.SC_OK));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));

		Item item = ItemMeta.get().jsonToModel(
				tester.response.getOutputAsString());
		assertThat(item.getKey(), is(testData.getKey()));
		assertThat(item.getEmail(), is(testData.getEmail()));
		assertThat(item.getTitle(), is(testData.getTitle()));
		assertThat(item.getContent(), is(testData.getContent()));
		assertThat(item.getCreatedAt(), is(testData.getCreatedAt()));
		assertThat(item.getUpdatedAt(), is(testData.getUpdatedAt()));
	}

	@Test
	public void testGetForDataEmpty() throws Exception {
		Key key = ItemService.createKey("hoge");
		String strKey = Datastore.keyToString(key);

		tester.request.setMethod("GET");
		tester.start(ItemController.PATH + "/" + strKey);

		assertThat(tester.response.getStatus(),
				is(HttpServletResponse.SC_NOT_FOUND));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));
		assertThat(
				tester.response.getOutputAsString(),
				is("[\"agpVbml0IFRlc3Rzcg4LEgRJdGVtIgRob2dlDA is not found.\"]"));
	}
}
