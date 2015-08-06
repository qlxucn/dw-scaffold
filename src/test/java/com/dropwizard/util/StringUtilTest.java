package com.dropwizard.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by benson on 9/29/14.
 */
public class StringUtilTest {
	@Test
	public void testFormatCurrency(){
		assertEquals("1009.12", StringUtil.formatCurrency(1009.12f));
		assertEquals("10001", StringUtil.formatCurrency(10001f));
		assertEquals("1000.1", StringUtil.formatCurrency(1000.1f));
		assertNull(StringUtil.formatCurrency(null));

		assertEquals("1009.13", StringUtil.formatCurrency(1009.129f));

		assertEquals("1009.12", StringUtil.formatCurrency(1009.125f));
		assertEquals("1009.13", StringUtil.formatCurrency(1009.126f));
	}

	@Test
	public void testParseCurrency(){
		Float num = StringUtil.parseCurrency("1009.12");
		assertNotNull(num);
		assertEquals("1009.12", num.toString());

		num = StringUtil.parseCurrency("1009.126");
		assertNotNull(num);
		assertEquals("1009.126", num.toString());
	}
}
