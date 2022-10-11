package com.dish.wholesale.core.models;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.crx.BaseException;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class LoginModelTest {

	private final AemContext aemContext = new AemContext();

	private LoginModel loginModel;

	private Resource resource;

	@BeforeEach
	void setUp() {
		aemContext.addModelsForClasses(LoginModel.class);
		aemContext.load().json("/login/test-content.json", "/component");
		resource = aemContext.currentResource("/component/login");
		loginModel = resource.adaptTo(LoginModel.class);
	}

	@Test
	void simpleLoadAndGettersTest() throws BaseException {

		String[] methods = new String[] { "getUsername", "getPassword", "getButtontext" };

		UtilTest.testLoadAndGetters(methods, loginModel, resource);

	}
}
