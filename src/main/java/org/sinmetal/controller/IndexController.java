package org.sinmetal.controller;

import javax.servlet.http.*;

import org.sinmetal.meta.*;
import org.sinmetal.model.*;
import org.sinmetal.service.*;
import org.slim3.controller.*;

import com.google.appengine.api.datastore.*;

/**
 * Sample {@link Item} 表示
 * 
 * @author sinmetal
 *
 */
public class IndexController extends AbstructController {

	@Override
	protected Navigation run() throws Exception {
		if (isGet()) {
			doGet();
			return null;
		} else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}

	void doGet() throws Exception {
		Key key = ItemService.createKey("sample item");
		Item item = new Item();
		item.setKey(key);
		item.setEmail("user@example.com");
		item.setTitle("sample title");
		item.setContent("sample content");
		Item stored = ItemService.put(item);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(APPLICATION_JSON);
		response.setCharacterEncoding(UTF8);

		response.getWriter().write(ItemMeta.get().modelToJson(stored));
		response.flushBuffer();
	}
}
