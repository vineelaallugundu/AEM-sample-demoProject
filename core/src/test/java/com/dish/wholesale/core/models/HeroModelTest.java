package com.dish.wholesale.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.crx.BaseException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class HeroModelTest {
	/** The Constant RESOURCE_CONTENT. */
	private static final String RESOURCE_CONTENT = "/hero/test-content.json";

	/** The Constant TEST_CONTENT_ROOT. */
	private static final String TEST_CONTENT_ROOT = "/component";

	/** The Constant RESOURCE. */
	private static final String RESOURCE = TEST_CONTENT_ROOT + "/hero";

	/** The languageSelector model. */
	private HeroModel heroModel;

	/** The resource. */
	private Resource resource;

	@Mock
	private ResourceResolver resResolver;

	/**
	 * Method loads the context, resource and language selector model.
	 *
	 * @throws BaseException the base exception
	 */
	@BeforeEach
	public void setup(AemContext context) throws BaseException {

		Class<HeroModel> modelClass = HeroModel.class;
		heroModel = new HeroModel();
		heroModel.setAlt("Alt text");
		heroModel.setImage("/cotnent/dam/test.jpeg");
		heroModel.setHeroTitle("Hero title test");
		heroModel.setHeroHeader("Header");

		// load the resources for each object
		context.load().json(RESOURCE_CONTENT, TEST_CONTENT_ROOT);
		context.addModelsForClasses(modelClass);

		resource = context.currentResource(RESOURCE);
		heroModel = resource.adaptTo(modelClass);
	}

	/**
	 * Test model, resource and all getters of the Image model
	 * 
	 * @throws BaseException
	 */
	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "getAlt", "getHeroTitle", "getHeroHeader", "getImage" };

		UtilTest.testLoadAndGetters(methods, heroModel, resource);

	}
}
