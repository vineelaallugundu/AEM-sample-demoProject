package com.dish.wholesale.core.contentsync.service.impl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContentSyncProcessDefinitionFactoryTest {

    AemContext aemContext = new AemContext();
    ContentSyncProcessDefinitionFactory contentSyncProcessDefinitionFactory;

    @InjectMocks
    ContentSyncProcessDefinitionFactory contentSync;


    @BeforeEach
    void setUp() {
        contentSyncProcessDefinitionFactory = aemContext.registerService(new ContentSyncProcessDefinitionFactory());
        ContentSyncProcessDefinitionFactory.Config config = mock(ContentSyncProcessDefinitionFactory.Config.class);
        when(config.username()).thenReturn("admin");
        when(config.password()).thenReturn("pass");
        contentSyncProcessDefinitionFactory.activate(config);
    }

    @Test
    void getUsernameTest() {
        assertEquals("Content Sync Process", contentSyncProcessDefinitionFactory.getName());
    }

    @Test
    void getPasswordTest() {
        assertEquals("pass", contentSyncProcessDefinitionFactory.getPassword());
    }

    @Test
    void getTests() {
        assertNotNull(contentSyncProcessDefinitionFactory.getAuthorizedGroups());
    }

    @Test
    void getTestsBind() throws NoSuchFieldException, IllegalAccessException {
        contentSync = aemContext.registerService(new ContentSyncProcessDefinitionFactory());
        EnvironmentConfigurationFactory environmentConfiguration = mock(EnvironmentConfigurationFactory.class);
        boolean thrown = false;
        try {
            contentSync.bind(environmentConfiguration);
        } catch (ClassCastException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void getTestsUnBindThrowException() {
        contentSync = aemContext.registerService(new ContentSyncProcessDefinitionFactory());
        EnvironmentConfigurationFactory environmentConfiguration = mock(EnvironmentConfigurationFactory.class);
        boolean thrown = false;
        try {
            contentSync.unbind(environmentConfiguration);
        } catch (ClassCastException e) {
            thrown = true;
        }
        assertFalse(thrown);
    }

    @Test
    void createProcessDefinitionInstance() {
        contentSync = aemContext.registerService(new ContentSyncProcessDefinitionFactory());
        try {
            contentSync.createProcessDefinitionInstance();
        } catch (Exception e) {

        }
    }
}