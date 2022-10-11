package com.dish.wholesale.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.framework.Assert;


@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class NavigationModelTest {

	private final AemContext aemContext = new AemContext();
	
	@InjectMocks
	private NavigationModel navModel;
	
	@Mock
	private Resource resource;

	@Mock
	private ResourceResolver resResolver;
	
	@Mock
	private Page page;

	@BeforeEach
	void setUp() {
		
		aemContext.addModelsForClasses(NavigationModel.class);
		aemContext.load().json("/navigation/test-content.json", "/component");
		resource = aemContext.currentResource("/component/navigation");
		navModel = aemContext.request().adaptTo(NavigationModel.class);
		navModel.getParentPage();
		
	}

	@Test
	void getDetailsfromNavigation() {
		
		Assert.assertEquals("/content/wholesale/us/en/iconpage", navModel.getIconPageUrl());
		Assert.assertEquals("/content/wholesale/us/en/home", navModel.getUrl());
		Assert.assertEquals("/content/wholesale/us/en/login", navModel.getLoginPageUrl());
		Assert.assertEquals("/content/wholesale/us/en/testnode", navModel.getNode());
		Assert.assertEquals("/content/dam/wholesale/icon.jpeg", navModel.getIconPath());
		
	}

}
