package com.dish.wholesale.core.contentsync.service.impl;

import com.dish.wholesale.core.contentsync.service.EnvironmentConfiguration;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EnvironmentConfigurationFactoryTest {

    @Mock
    EnvironmentConfiguration environmentConfiguration;

    @Test
    public void activate() {
        SlingSettingsService mockSlingSettingsService = mock(SlingSettingsService.class);
        EnvironmentConfigurationFactory testSubject =
                new EnvironmentConfigurationFactory(mockSlingSettingsService);

        Map<String, Object> mockProps = new HashMap<>();
        when(environmentConfiguration.getEnvName()).thenReturn("name");
        testSubject.activate(environmentConfiguration);
        testSubject.annotationType();
        mockProps.put("envName", "name");
        mockProps.put("envUrl", "url");
        mockProps.put("envToken", "token");
        mockProps.put("excludeRunMode", "run mode");
        testSubject.setEnvUrl("url");
        testSubject.setEnvName("name");
        testSubject.setIncluded(true);
        testSubject.setExcludeRunMode("run mode");
        testSubject.setServiceRanking(1);
        assertEquals("name", testSubject.getEnvName());
        assertEquals("url", testSubject.getEnvUrl());
        assertEquals("run mode", testSubject.getExcludeRunMode());
    }

    @Test
    public void isIncluded_runModeNotExcluded() {
        SlingSettingsService mockSlingSettingsService = mock(SlingSettingsService.class);
        EnvironmentConfigurationFactory testSubject =
                new EnvironmentConfigurationFactory(mockSlingSettingsService);

        Map<String, Object> mockProps = new HashMap<>();

        mockProps.put("excludeRunMode", "run mode");

        assertFalse(testSubject.isIncluded());
    }
}
