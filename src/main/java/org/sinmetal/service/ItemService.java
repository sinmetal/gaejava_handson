package org.sinmetal.service;

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
}
