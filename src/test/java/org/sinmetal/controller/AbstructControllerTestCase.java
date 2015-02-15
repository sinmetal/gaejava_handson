package org.sinmetal.controller;

import org.slim3.tester.*;

import com.google.appengine.tools.development.testing.*;

/**
 * UnitTest用の設定を入れたControllerTestCase
 * 
 * @author sinmetal
 *
 */
public abstract class AbstructControllerTestCase extends ControllerTestCase {

	LocalServiceTestHelper helper;

	@Override
	public void setUp() throws Exception {
		LocalDatastoreServiceTestConfig dsConfig = new LocalDatastoreServiceTestConfig();
		dsConfig.setApplyAllHighRepJobPolicy();
		dsConfig.setNoStorage(true);
		dsConfig.setNoIndexAutoGen(true);

		LocalSearchServiceTestConfig ssConfig = new LocalSearchServiceTestConfig()
				.setPersistent(false);
		LocalBlobstoreServiceTestConfig blobConfig = new LocalBlobstoreServiceTestConfig();
		LocalFileServiceTestConfig fileConfig = new LocalFileServiceTestConfig();
		helper = new LocalServiceTestHelper(dsConfig, ssConfig, blobConfig,
				fileConfig);

		helper = new LocalServiceTestHelper(dsConfig);
		helper.setUp();
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		helper.tearDown();
	}
}
