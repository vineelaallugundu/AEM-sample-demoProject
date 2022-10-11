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
class BannerModelTest {

	private BannerModel bannerModel;

	private Resource resource;

	@BeforeEach
	public void setup(AemContext context) throws BaseException {

		Class<BannerModel> modelClass = BannerModel.class;
		context.load().json("/banner/test-content.json", "/component");
		context.addModelsForClasses(modelClass);

		resource = context.currentResource("/component/banner");
		bannerModel = resource.adaptTo(modelClass);
	}

	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "getBannerOne", "getSrcOne", "getBannerTextOne", "getBannerHeaderOne",
				"getButtonTextOne" };

		UtilTest.testLoadAndGetters(methods, bannerModel, resource);

	}

}
