package com.dish.wholesale.core.models;

import com.day.crx.BaseException;
import com.dish.wholesale.core.configs.HcaptchaConfig;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class SalesforceLeadModelTest {
	/** The Constant RESOURCE_CONTENT. */
	private static final String RESOURCE_CONTENT = "/salesLeadForm/test-content.json";

	/** The Constant TEST_CONTENT_ROOT. */
	private static final String TEST_CONTENT_ROOT = "/component";

	/** The Constant RESOURCE. */
	private static final String RESOURCE = TEST_CONTENT_ROOT + "/sales_lead_form";

	/** The languageSelector model. */
	private SalesforceLeadModel salesforceLeadModel;

	/** The resource. */
	private Resource resource;

	@Mock
	private ResourceResolver resResolver;

	@Mock
	private HcaptchaConfig hcaptchaConfig;

	/**
	 * Method loads the context, resource and language selector model.
	 *
	 * @throws BaseException the base exception
	 */
	@BeforeEach
	public void setup(AemContext context) throws BaseException {
		Class<SalesforceLeadModel> modelClass = SalesforceLeadModel.class;
		// load the resources for each object
		context.load().json(RESOURCE_CONTENT, TEST_CONTENT_ROOT);
		context.addModelsForClasses(modelClass);
		HcaptchaConfig hcaptchaConfig = mock(HcaptchaConfig.class);
		Mockito.lenient().when(hcaptchaConfig.getHcaptchaSiteKey()).thenReturn("Value");
		context.registerService(HcaptchaConfig.class, hcaptchaConfig);
		resource = context.currentResource(RESOURCE);
		salesforceLeadModel = resource.adaptTo(modelClass);
	}

	/**
	 * Test model, resource and all getters of the Image model
	 * 
	 * @throws BaseException
	 */
	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "getSalesforceUrl", "getSalesforceOid", "getFirstNameLabel",
				"getLastNameLabel", "getEmail", "getCompany", "getCity", "getCountry", "getState", "getStartDateID",
				"getIntendedUse", "getIntendedLength", "getEstimatedRadius", "getDesiredSpectrum",
				"getProductInterestID", "getDescription", "getStartDate", "getSalesforceLeadSource", "getProductInterest",
				"getTitle", "getPhone", "getZip", "getStreet", "getHcaptchaSiteKey"};

		UtilTest.testLoadAndGetters(methods, salesforceLeadModel, resource);

	}
}
