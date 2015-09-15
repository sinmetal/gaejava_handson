package org.sinmetal.controller;

import java.util.logging.*;

import javax.servlet.http.*;

import org.slim3.controller.*;

import com.google.appengine.api.memcache.*;
import com.google.appengine.api.memcache.MemcacheService.IdentifiableValue;

/**
 * Memcacheを利用して適当にアクセス数をカウントするController
 * 
 * @author sinmetal
 *
 */
public class TempCouterController extends AbstructController {

	static final Logger logger = Logger.getLogger(TempCouterController.class
			.getSimpleName());

	public static final String PATH = "/tempCouter";

	final String MEMCACHE_KEY = "temp-counter-key";

	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	@Override
	protected Navigation run() throws Exception {
		Integer counter = 0;
		if (isDelete()) {
			clear();
		} else {
			counter = increment();
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(APPLICATION_JSON);
		response.setCharacterEncoding(UTF8);
		response.getWriter().write(String.format("{\"count\": %d}", counter));
		response.flushBuffer();
		return null;
	}

	/**
	 * Counterをインクリメントする
	 * 
	 * @return インクリメントした結果の値
	 */
	Integer increment() {
		if (memcache.contains(MEMCACHE_KEY) == false) {
			memcache.put(MEMCACHE_KEY, 1);
			return 1;
		}

		while (true) {
			IdentifiableValue value = memcache.getIdentifiable(MEMCACHE_KEY);
			Integer counter = (Integer) value.getValue();
			counter++;
			boolean result = memcache.putIfUntouched(MEMCACHE_KEY, value,
					counter);
			if (result) {
				return counter;
			} else {
				logger.warning("miss memcache putIfUntouched. counter = "
						+ counter);
			}
		}
	}

	/**
	 * Counterをクリアする
	 */
	void clear() {
		boolean exists = memcache.delete(MEMCACHE_KEY);
		logger.info("clear memcache. result = " + exists);
	}
}
