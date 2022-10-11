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
class JumpNavModelTest {

	private final AemContext aemContext = new AemContext();

	private JumpNavModel jumpNavModel;

	private Resource resource;

	@Mock
	private ResourceResolver resResolver;

	@BeforeEach
	void setUp() {
		aemContext.addModelsForClasses(AccordionModel.class);
		aemContext.load().json("/jump-nav/test-content.json", "/component");
		resource = aemContext.currentResource("/component/jump-nav");
		jumpNavModel = aemContext.request().adaptTo(JumpNavModel.class);

	}

	@Test
	void getDetailsfromMultifield() {
		assertEquals(3, jumpNavModel.getData().size());
		assertEquals("Test", jumpNavModel.getData().get(0).getLabel());
		assertEquals("test", jumpNavModel.getData().get(0).getId());
	}

	@Test
	void getDetailsfromMultifieldWithNull() {
		aemContext.currentResource("/component/jumpnav-empty");
		jumpNavModel = aemContext.request().adaptTo(JumpNavModel.class);
		assertNotNull(jumpNavModel.getData());
	}

	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "populateMultiFieldItems", "getPropertyValue", "getData" };
		UtilTest.testLoadAndGetters(methods, jumpNavModel, resource);

	}
}
