package org.sinmetal.controller;

import org.slim3.controller.router.*;

/**
 * Slim3 AppRouter
 * 
 * https://sites.google.com/site/slim3documentja/documents/slim3-controller/url-
 * mapping
 * 
 * @author sinmetal
 *
 */
public class AppRouter extends RouterImpl {
	public AppRouter() {
		addRouting("/item/{strKey}", "/item?strKey={strKey}");
	}
}
