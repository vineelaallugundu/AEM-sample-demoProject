package com.dish.wholesale.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.crx.BaseException;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class AccordionModelTest {

	private final AemContext aemContext = new AemContext();

	private AccordionModel accordionModel;

	private Resource resource;

	@Mock
	private ResourceResolver resResolver;

	@BeforeEach
	void setUp() {
		aemContext.addModelsForClasses(AccordionModel.class);
		aemContext.load().json("/accordion/test-content.json", "/component");
		resource = aemContext.currentResource("/component/accordion");
		accordionModel = aemContext.request().adaptTo(AccordionModel.class);

	}

	@Test
	void getDetailsfromMultifield() {
		assertEquals(3, accordionModel.getData().size());
		assertEquals("A", accordionModel.getData().get(0).getTitle());
		assertEquals("aaa", accordionModel.getData().get(0).getDescription());
	}

	@Test
	void getDetailsfromMultifieldWithNull() {
		aemContext.currentResource("/component/accordion-empty");
		accordionModel = aemContext.request().adaptTo(AccordionModel.class);
		assertNotNull(accordionModel.getData());
	}

	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "populateMultiFieldItems", "getPropertyValue", "getData" };
		UtilTest.testLoadAndGetters(methods, accordionModel, resource);

	}
}
