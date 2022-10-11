package com.dish.wholesale.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.crx.BaseException;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The Class HeaderModelTest.
 */
@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class HeaderModelTest {

	/** The aem context. */
	private final AemContext aemContext = new AemContext();

	/** The header model. */
	private HeaderModel headerModel;

	/** The resource. */
	private Resource resource;

	/** The res resolver. */
	@Mock
	private ResourceResolver resResolver;

	/**
	 * Sets the up.
	 */
	@BeforeEach
	void setUp() {
		aemContext.addModelsForClasses(AccordionModel.class);
		aemContext.load().json("/header/test-content.json", "/component");
		resource = aemContext.currentResource("/component/header");
		headerModel = aemContext.request().adaptTo(HeaderModel.class);

	}

	/**
	 * Gets the detailsfrom multifield.
	 *
	 * @return the detailsfrom multifield
	 */
	@Test
	void getDetailsfromMultifield() {
		assertEquals("Homepage", headerModel.getData().get("headerItems").get(0).getPageLabel());
		assertEquals("/content/wholesale/en/us/home", headerModel.getData().get("secondaryHeaderItems").get(0).getPageUrl());
	}

	/**
	 * Gets the detailsfrom multifield with null.
	 *
	 * @return the detailsfrom multifield with null
	 */
	@Test
	void getDetailsfromMultifieldWithNull() {
		aemContext.currentResource("/component/header-empty");
		headerModel = aemContext.request().adaptTo(HeaderModel.class);
		assertNotNull(headerModel.getData());
	}

	/**
	 * Simple load and getters test.
	 *
	 * @throws BaseException the base exception
	 */
	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "populateMultiFieldItems", "getPropertyValue", "getData" };
		UtilTest.testLoadAndGetters(methods, headerModel, resource);

	}
}
