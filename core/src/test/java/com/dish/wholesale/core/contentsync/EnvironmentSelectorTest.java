package com.dish.wholesale.core.contentsync;

import com.adobe.acs.commons.mcp.form.FormField;
import com.dish.wholesale.core.contentsync.service.impl.EnvironmentConfigurationFactory;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.scripting.SlingScriptHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EnvironmentSelectorTest {
    @Spy
    EnvironmentSelector testSubject;

    @Mock
    SlingScriptHelper mockHelper;

    @Mock
    EnvironmentConfigurationFactory mockConfig;

    List<EnvironmentConfigurationFactory> mockConfigs;
    EnvironmentConfigurationFactory[] mockConfigsArray;

    @BeforeEach
    public void setup() {
        FormField mockField = mock(FormField.class);
        testSubject.setup("name", null, mockField, mockHelper);
    }

    @Test
    public void getOptions() {
        when(mockConfig.getEnvName()).thenReturn("mock config");

        mockConfigs = Collections.singletonList(mockConfig);
        mockConfigsArray = new EnvironmentConfigurationFactory[] {
                mockConfig
        };
        doReturn(mockConfigsArray).when(testSubject).getEnvironmentConfigs(any());

        testSubject.getOptions();

        verify(testSubject).getEnvironmentConfigs(mockHelper);
        verify(testSubject).getOptionsFromConfigs(mockConfigs);
    }

    @Test
    public void getEnvironmentConfigs() {

        mockConfigs = Collections.singletonList(mockConfig);
        mockConfigsArray = new EnvironmentConfigurationFactory[] {
                mockConfig
        };
        testSubject.getEnvironmentConfigs(mockHelper);

        verify(mockHelper).getServices(eq(EnvironmentConfigurationFactory.class), isNull(String.class));
    }

    @Test
    public void getOptionsFromConfigs_empty() {


        mockConfigs = Collections.singletonList(mockConfig);
        mockConfigsArray = new EnvironmentConfigurationFactory[] {
                mockConfig
        };
        try {
            testSubject.getOptionsFromConfigs(Collections.emptyList());
        } catch (IllegalArgumentException e) {
            assertEquals("No environment configurations exist", e.getMessage());
            return;
        }

        fail("The exception was not thrown.");
    }

    @Test
    public void getOptionsFromConfigs_notEmpty() {
        when(mockConfig.getEnvName()).thenReturn("mock config");

        mockConfigs = Collections.singletonList(mockConfig);
        mockConfigsArray = new EnvironmentConfigurationFactory[] {
                mockConfig
        };
        System.out.println(mockConfigs);
        Map<String, String> options = testSubject.getOptionsFromConfigs(mockConfigs);

        assertEquals(1, options.size());
        assertEquals("mock config", options.get("mock config"));
    }
}

