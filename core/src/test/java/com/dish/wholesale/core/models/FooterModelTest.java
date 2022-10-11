package com.dish.wholesale.core.models;

import com.day.crx.BaseException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class FooterModelTest {

	private final AemContext aemContext = new AemContext();

	private FooterModel footerModel;

	private Resource resource;

	@Mock
	private ResourceResolver resResolver;

	@BeforeEach
	void setUp() {
		aemContext.addModelsForClasses(FooterModel.class);
		aemContext.load().json("/footer/test-content.json", "/component");
		resource = aemContext.currentResource("/component/footer");
		footerModel = aemContext.request().adaptTo(FooterModel.class);

	}

	@Test
	void getDetailsfromMultifield() {
		assertEquals(3, footerModel.getData().size());
		assertEquals("Test", footerModel.getData().get(0).getPageLabel());
		assertEquals("Test", footerModel.getData().get(0).getPageUrl());
		resource = aemContext.currentResource("/component/footer-items-empty");
		footerModel = aemContext.request().adaptTo(FooterModel.class);
		resource = aemContext.currentResource("/component/footer-items-null");
		footerModel = aemContext.request().adaptTo(FooterModel.class);

	}

	@Test
	void getDetailsfromMultifieldWithNull() {
		aemContext.currentResource("/component/footer-empty");
		footerModel = aemContext.request().adaptTo(FooterModel.class);
		assertNull(footerModel.getData());
	}

	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "populateMultiFieldItems", "getPropertyValue", "getData" };
		UtilTest.testLoadAndGetters(methods, footerModel, resource);

	}

}
