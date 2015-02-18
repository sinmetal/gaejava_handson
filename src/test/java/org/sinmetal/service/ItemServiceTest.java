package org.sinmetal.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.*;

import org.junit.*;
import org.sinmetal.*;
import org.sinmetal.controller.*;
import org.sinmetal.controller.ItemController.PostForm;
import org.sinmetal.meta.*;
import org.sinmetal.model.*;
import org.slim3.datastore.*;

import com.google.appengine.api.datastore.*;

public class ItemServiceTest extends AbstructAppEngineTestCase {

	/**
	 * {@link Key} 生成テスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateKey() throws Exception {
		final String NAME = "hogekey";

		Key key = ItemService.createKey(NAME);
		assertThat(key.getName(), is(NAME));
		assertThat(key.getKind(), is(ItemMeta.get().getKind()));
	}

	/**
	 * put test
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPut() throws Exception {
		final String EMAIL = "user@example.com";
		final String TITLE = "sample title";
		final String CONTENT = "sample content";

		Key key = ItemService.createKey("hogekey");
		Item item = new Item();
		item.setKey(key);
		item.setEmail(EMAIL);
		item.setTitle(TITLE);
		item.setContent(CONTENT);
		ItemService.put(item);

		Item stored = Datastore.getOrNull(ItemMeta.get(), key);
		assertThat(stored, notNullValue());
		assertThat(stored.getEmail(), is(EMAIL));
		assertThat(stored.getTitle(), is(TITLE));
		assertThat(stored.getContent(), is(CONTENT));
		assertThat(stored.getCreatedAt(), notNullValue());
		assertThat(stored.getUpdatedAt(), notNullValue());
	}

	/**
	 * Keyが重複した場合、上書きされる
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPutForKeyRepetition() throws Exception {
		Key key = ItemService.createKey("hogekey");

		{
			Item item = new Item();
			item.setKey(key);
			ItemService.put(item);
		}
		int before = tester.count(Item.class);

		{
			Item item = new Item();
			item.setKey(key);
			ItemService.put(item);
		}
		int after = tester.count(Item.class);

		assertThat(before, is(after));
	}

	/**
	 * 新規作成 テスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreate() throws Exception {
		final String EMAIl = "user@example.com";

		ItemController.PostForm form = new PostForm();
		form.title = "sample title";
		form.content = "sample content";

		Item item = ItemService.create(EMAIl, form);

		Item stored = Datastore.getOrNull(ItemMeta.get(), item.getKey());
		assertThat(stored, notNullValue());
		assertThat(stored.getEmail(), is(EMAIl));
		assertThat(stored.getTitle(), is(form.title));
		assertThat(stored.getContent(), is(form.content));
		assertThat(stored.getCreatedAt(), notNullValue());
		assertThat(stored.getUpdatedAt(), notNullValue());
	}

	/**
	 * Query時に対象データが無い場合、空のListを返すことをテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testQuerySortUpdatedAtDescForEmpty() throws Exception {
		List<Item> stored = ItemService.querySortUpdatedAtDesc();
		assertThat(stored, notNullValue());
		assertThat(stored.size(), is(0));
	}

	/**
	 * Queryが実行できることを確認するテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testQuerySortUpdatedAtDesc() throws Exception {
		final int SIZE = 10;
		Map<Key, Item> testData = new HashMap<>();
		{
			for (int i = 0; i < SIZE; i++) {
				PostForm form = new PostForm();
				form.title = "sample title" + i;
				form.content = "sample content" + i;
				Item item = ItemService.create("user" + i + "@example.com",
						form);
				testData.put(item.getKey(), item);
			}
		}

		List<Item> stored = ItemService.querySortUpdatedAtDesc();
		assertThat(stored, notNullValue());
		assertThat(stored.size(), is(SIZE));

		for (Item item : stored) {
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
	 * 対象の {@link Key} の {@link Item} が無い場合、nullになることをテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetOrNullForEmpty() throws Exception {
		Key key = ItemService.createKey("hoge");
		Item stored = ItemService.getOrNull(key);
		assertThat(stored, nullValue());
	}

	/**
	 * 対象の {@link Key} の {@link Item} が無い場合、nullになることをテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetOrNull() throws Exception {
		Key key;
		{
			PostForm form = new PostForm();
			key = ItemService.create("user@example.com", form).getKey();
		}

		Item stored = ItemService.getOrNull(key);
		assertThat(stored, notNullValue());
	}
}
