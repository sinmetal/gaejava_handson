package org.sinmetal.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.servlet.http.*;

import org.junit.*;

/**
 * {@link TempCouterController} のテスト
 * 
 * @author sinmetal
 *
 */
public class TempCouterControllerTest extends AbstructControllerTestCase {

	/**
	 * CountUpのテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCountUp() throws Exception {
		{
			tester.request.setMethod("POST");
			tester.start(TempCouterController.PATH);

			assertThat(tester.response.getStatus(),
					is(HttpServletResponse.SC_OK));
			assertThat(tester.response.getContentType(), is("application/json"));
			assertThat(tester.response.getCharacterEncoding(), is("utf-8"));
			assertThat(tester.response.getOutputAsString(),
					is("{\"count\": 1}"));
		}
	}

	/**
	 * Clearのテスト
	 * 
	 * @throws Exception
	 */
	@Test
	public void testClear() throws Exception {
		tester.request.setMethod("DELETE");
		tester.start(TempCouterController.PATH);

		assertThat(tester.response.getStatus(), is(HttpServletResponse.SC_OK));
		assertThat(tester.response.getContentType(), is("application/json"));
		assertThat(tester.response.getCharacterEncoding(), is("utf-8"));
		assertThat(tester.response.getOutputAsString(), is("{\"count\": 0}"));
	}

}
