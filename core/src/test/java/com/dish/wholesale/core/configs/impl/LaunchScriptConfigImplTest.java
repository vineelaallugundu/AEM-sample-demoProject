package com.dish.wholesale.core.configs.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class LaunchScriptConfigImplTest {

	AemContext aemContext = new AemContext();
	LaunchScriptConfigImpl launchScriptConfigTest;

	private String launchScriptUrl = "//assets.adobedtm.com/f4211b096882/326008712d36/launch-fe99f8cf72c5-development.min.js";

	@BeforeEach
	void setUp() {
		launchScriptConfigTest = aemContext.registerService(new LaunchScriptConfigImpl());
		LaunchScriptConfigImpl.Config config = mock(LaunchScriptConfigImpl.Config.class);
		when(config.launchScript()).thenReturn(launchScriptUrl);
		launchScriptConfigTest.activate(config);
	}

	@Test
	void getLaunchScript() {
		assertEquals(launchScriptUrl, launchScriptConfigTest.getLaunchScript());
	}

}
