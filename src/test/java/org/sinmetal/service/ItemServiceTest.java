package org.sinmetal.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.*;
import org.sinmetal.*;
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
}
