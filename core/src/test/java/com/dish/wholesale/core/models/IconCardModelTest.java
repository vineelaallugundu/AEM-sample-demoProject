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

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class IconCardModelTest {
	/** The Constant RESOURCE_CONTENT. */
	private static final String RESOURCE_CONTENT = "/iconCard/test-content.json";

	/** The Constant TEST_CONTENT_ROOT. */
	private static final String TEST_CONTENT_ROOT = "/component";

	/** The Constant RESOURCE. */
	private static final String RESOURCE = TEST_CONTENT_ROOT + "/icon_card";

	/** The languageSelector model. */
	private IconCardModel iconCardModel;

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

		Class<IconCardModel> modelClass = IconCardModel.class;

		// load the resources for each object
		context.load().json(RESOURCE_CONTENT, TEST_CONTENT_ROOT);
		context.addModelsForClasses(modelClass);

		resource = context.currentResource(RESOURCE);
		iconCardModel = resource.adaptTo(modelClass);
	}

	/**
	 * Test model, resource and all getters of the Image model
	 * 
	 * @throws BaseException
	 */
	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "getIconCardTitle", "getIconCardImageAltText", "getIconCardDescription",
				"getIconCardImage" };

		UtilTest.testLoadAndGetters(methods, iconCardModel, resource);

	}
}
