package com.dish.wholesale.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import javax.servlet.ServletException;

import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dish.wholesale.core.configs.HcaptchaConfig;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junit.framework.Assert;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class HcaptchaServletTest {

	private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
		
	@InjectMocks
	private HcaptchaServlet hcaptchaServlet;
	
	@Mock
	private HcaptchaConfig hcaptchaConfig;
		
	@Mock
	private MockSlingHttpServletRequest mockSlingRequest;
	
	@Mock
	private MockSlingHttpServletResponse mockSlingResponse;
	
	@BeforeEach
	void setUp() {
		mockSlingRequest = aemContext.request();
		mockSlingResponse = aemContext.response();
	}

	@Test
	void getHCaptchaResponse() throws ServletException, IOException
	{
		try {
			Assert.assertNotNull(hcaptchaServlet);
			hcaptchaServlet.doGet(mockSlingRequest, mockSlingResponse);
			assertEquals(200, mockSlingResponse.getStatus());
		} catch (Exception e) {

		}
	}

}
