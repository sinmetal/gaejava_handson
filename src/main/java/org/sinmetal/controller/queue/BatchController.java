package org.sinmetal.controller.queue;

import java.util.*;
import java.util.logging.*;

import org.sinmetal.meta.*;
import org.sinmetal.model.*;
import org.sinmetal.service.*;
import org.slim3.controller.*;
import org.slim3.datastore.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.*;
import com.google.appengine.api.taskqueue.Queue;

public class BatchController extends SimpleController {

	static final Logger logger = Logger.getLogger(BatchController.class
			.getSimpleName());

	static final String PATH = "/queue/batch";

	public static void call(Transaction tx, Key key) {
		Queue queue = QueueFactory.getQueue("batch");

		// Item削除時に、非同期でSearchAPI Index削除を投げているため、少し待つ
		TaskOptions options = TaskOptions.Builder.withUrl(PATH)
				.param("key", Datastore.keyToString(key)).countdownMillis(1200);
		queue.add(options);
	}

	@Override
	protected Navigation run() throws Exception {
		Enumeration<?> headernames = request.getHeaderNames();
		while (headernames.hasMoreElements()) {
			String headerName = (String) headernames.nextElement();
			logger.info(String.format("%s : %s", headerName,
					request.getHeader(headerName)));
		}

		String keyStr = request.getParameter("key");
		Key key = Datastore.stringToKey(keyStr);
		Item item = ItemService.getOrNull(key);
		logger.info(ItemMeta.get().modelToJson(item));

		return null;
	}

}
