package org.sinmetal.service;

import java.util.*;

import org.sinmetal.controller.ItemController.PostForm;
import org.sinmetal.meta.*;
import org.sinmetal.model.*;
import org.slim3.datastore.*;

import com.google.appengine.api.datastore.*;

/**
 * {@link Item} のユーティリティ
 * 
 * @author sinmetal
 *
 */
public class ItemService {

	static ItemMeta meta = ItemMeta.get();

	private ItemService() {
	};

	/**
	 * {@link Key} 生成
	 * 
	 * @param uuid
	 *            UUID
	 * @return {@link Item} {@link Key}
	 */
	public static Key createKey(String uuid) {
		return Datastore.createKey(meta, uuid);
	}

	/**
	 * {@link Item} 生成
	 * 
	 * @param item
	 */
	public static Item put(Item item) {
		Datastore.put(item);
		return item;
	}

	/**
	 * 新しい {@link Item} を作成する
	 * 
	 * @param email
	 *            作成者のemail
	 * @param form
	 *            RequestData
	 * @return 作成した {@link Item}
	 */
	public static Item create(String email, PostForm form) {
		Key key = createKey(UUID.randomUUID().toString());
		Item item = new Item();
		item.setKey(key);
		item.setEmail(email);
		item.setTitle(form.title);
		item.setContent(form.content);
		Datastore.put(item);
		return item;
	}
}
