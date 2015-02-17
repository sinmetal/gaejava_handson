package org.sinmetal.controller;

import java.util.*;

import javax.servlet.http.*;

import org.codehaus.jackson.map.*;
import org.sinmetal.meta.*;
import org.sinmetal.model.*;
import org.sinmetal.service.*;
import org.slim3.controller.*;
import org.slim3.util.*;

/**
 * {@link Item} に関するAPI
 * 
 * @author sinmetal
 *
 */
public class ItemController extends AbstructController {

	public static final String PATH = "/item";

	@Override
	protected Navigation run() throws Exception {
		if (isPost()) {
			doPost();
			return null;
		} else if (isGet()) {
			list();
			return null;
		} else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}

	void doPost() throws Exception {
		final ObjectMapper om = new ObjectMapper();

		PostForm form;
		try {
			form = om.readValue(request.getInputStream(), PostForm.class);
		} catch (Throwable e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		List<String> errors = form.validate();
		if (errors.isEmpty() == false) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType(APPLICATION_JSON);
			response.setCharacterEncoding(UTF8);
			om.writeValue(response.getWriter(), errors);
			return;
		}

		// FIXME Item作成者のEmailをLoginUserのものに修正する
		Item stored = ItemService.create("user@example.com", form);

		response.setStatus(HttpServletResponse.SC_CREATED);
		response.setContentType(APPLICATION_JSON);
		response.setCharacterEncoding(UTF8);
		response.getWriter().write(ItemMeta.get().modelToJson(stored));
		response.flushBuffer();
	}

	public static class PostForm {

		/** 記事タイトル */
		public String title;

		/** 本文 */
		public String content;

		/**
		 * validate
		 * 
		 * errorが無い時は、空のListを返す
		 * 
		 * @return error message list
		 */
		public List<String> validate() {
			List<String> errors = new ArrayList<>();
			if (StringUtil.isEmpty(title)) {
				errors.add("title is required.");
			}
			return errors;
		}
	}

	void list() throws Exception {
		List<Item> items = ItemService.querySortUpdatedAtDesc();

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(APPLICATION_JSON);
		response.setCharacterEncoding(UTF8);
		response.getWriter().write(ItemMeta.get().modelsToJson(items));
		response.flushBuffer();
	}
}
