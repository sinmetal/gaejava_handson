package org.sinmetal.service;

import java.util.*;

import org.sinmetal.controller.ItemController.*;
import org.sinmetal.meta.*;
import org.sinmetal.model.*;
import org.slim3.datastore.*;
import org.slim3.util.StringUtil;

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

	/**
	 * 指定した {@link Key} の {@link Item} を更新する
	 * 
	 * @param key
	 *            {@link Item} {@link Key}
	 * @param form
	 *            RequestData
	 * @return 更新した {@link Item}
	 */
	public static Item update(Key key, PutForm form) {
		Transaction tx = Datastore.beginTransaction();
		try {
			Item item = Datastore.get(tx, meta, key, form.version);
			item.setTitle(form.title);
			item.setContent(form.content);
			Datastore.put(item);
			tx.commit();

			return item;
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
		}
	}

	/**
	 * 指定した {@link Key} の {@link Item} を削除する
	 * 
	 * @param key
	 *            {@link Item} {@link Key}
	 * @param form
	 *            RequestData
	 */
	public static void delete(Key key, DeleteForm form) {
		Transaction tx = Datastore.beginTransaction();
		try {
			Datastore.get(tx, meta, key, form.version);
			Datastore.delete(key);
			tx.commit();
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
		}
	}

	/**
	 * {@link Item} を更新日時の降順で全件取得する
	 * 
	 * Index遅延を考慮した用心深い実装になっている。
	 * 
	 * @return 更新日時の降順の {@link Item} 全件
	 */
	public static List<Item> querySortUpdatedAtDesc() {
		return querySortUpdatedAtDesc(null);
	}

	/**
	 * {@link Item} を更新日時の降順で全件取得する
	 * 
	 * Index遅延を考慮した用心深い実装になっている。
	 * 
	 * @param email
	 *            フィルタリング条件に使う作成者のemail
	 * @return 更新日時の降順の {@link Item} 全件
	 */
	public static List<Item> querySortUpdatedAtDesc(String email) {
		ModelQuery<Item> query = Datastore.query(meta);
		if (StringUtil.isEmpty(email) == false) {
			query = query.filter(meta.email.equal(email));
		}
		List<Key> keys = query.sort(meta.updatedAt.desc).asKeyList();

		Map<Key, Item> map = Datastore.getAsMap(meta, keys);

		List<Item> results = new ArrayList<>();
		for (Key key : keys) {
			if (map.containsKey(key)) {
				results.add(map.get(key));
			}
		}
		return results;
	}

	/**
	 * 指定した {@link Key} の {@link Item} を取得する。
	 * 
	 * @param key
	 *            {@link Item} {@link Key}
	 * @return {@link Item} or null
	 */
	public static Item getOrNull(Key key) {
		return Datastore.getOrNull(meta, key);
	}
}
