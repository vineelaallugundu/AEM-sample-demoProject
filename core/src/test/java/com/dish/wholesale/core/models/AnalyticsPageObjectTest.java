package com.dish.wholesale.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.crx.BaseException;
import com.dish.wholesale.core.configs.LaunchScriptConfig;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.framework.Assert;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Locale;

/**
 * The Class AnalyticsPageObjectTest.
 */
@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class AnalyticsPageObjectTest {

	/** The breadcrumb model. */
	@InjectMocks
	private AnalyticsPageObject analyticsPageObject;

	/** The current page. */
	@Mock
	private Page currentPage;

	/** The request. */
	@Mock
	private SlingHttpServletRequest request;

	/** The launch script config. */
	@Mock
	private LaunchScriptConfig launchScriptConfig;

	/** The page manager. */
	@Mock
	private PageManager pageManager;

	/** The resource. */
	@Mock
	private Resource resource;

	/** The resource resolver. */
	@Mock
	ResourceResolver resourceResolver;

	/**
	 * Method loads the context, resource and language selector model.
	 *
	 * @param context the new up
	 * @throws BaseException        the base exception
	 * @throws WCMException         the WCM exception
	 * @throws PersistenceException the persistence exception
	 */
	@BeforeEach
	public void setup(AemContext context) throws BaseException, WCMException, PersistenceException {
		Mockito.lenient().when(request.getServerName()).thenReturn("Test server");
		HashMap<String, Object> map = new HashMap<>();
		map.put("test", analyticsPageObject);
		resourceResolver.create(resource, "test", map);
		Mockito.lenient().when(resource.getResourceResolver()).thenReturn(resourceResolver);
		Mockito.lenient().when(resource.getResourceResolver().adaptTo(PageManager.class)).thenReturn(pageManager);
		Mockito.lenient().when(currentPage.getLanguage()).thenReturn(Locale.ENGLISH);
		Mockito.lenient().when(currentPage.getPath()).thenReturn("/content/wholesale/us/en/home");
		Mockito.lenient().when(pageManager.getPage("/content/wholesale/us/en/home")).thenReturn(currentPage);
		Mockito.lenient().when(launchScriptConfig.getLaunchScript()).thenReturn("#");

	}

	/**
	 * Sets the bread crumb items.
	 *
	 * @return the page name
	 * @throws Exception the exception
	 */
	@Test
	void getPageName() throws Exception {
		Assert.assertNotNull(analyticsPageObject.getPageName());
		Mockito.lenient().when(currentPage.getName()).thenReturn("current page");
		Assert.assertNotNull(analyticsPageObject.getPageName());
	}

	/**
	 * Gets the domain.
	 *
	 * @return the domain
	 * @throws Exception the exception
	 */
	@Test
	void getDomain() throws Exception {
		Assert.assertNotNull(analyticsPageObject.getDomain());
	}

	/**
	 * Gets the launch url.
	 *
	 * @return the launch url
	 * @throws Exception the exception
	 */
	@Test
	void getLaunchUrl() throws Exception {
		Assert.assertNotNull(analyticsPageObject.getLaunchUrl());
	}

	/**
	 * Gets the site section.
	 *
	 * @return the site section
	 * @throws Exception the exception
	 */
	@Test
	void getSiteSection() throws Exception {
		Assert.assertNotNull(analyticsPageObject.getSiteSection());
		Mockito.lenient().when(currentPage.getTitle()).thenReturn("Homepage");
		Assert.assertNotNull(analyticsPageObject.getSiteSection());

	}

}
