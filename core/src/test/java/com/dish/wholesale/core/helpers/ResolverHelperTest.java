package com.dish.wholesale.core.helpers;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;


import org.mockito.junit.jupiter.MockitoExtension;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ResolverHelperTest {

    @Mock
    ResourceResolverFactory resourceResolverFactory;
    @Mock
    ResourceResolver resourceResolver;

    String WHOLESALE_SERVICE_USER = "wholesaleserviceuser";
    String WHOLESALE_SERVICE_USER_DUMMY = "wholesaledummy";

    @BeforeEach
    void setUp() throws LoginException {

        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, WHOLESALE_SERVICE_USER);

    }

    @Test
    void newResolverTest() throws LoginException {
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, WHOLESALE_SERVICE_USER);
        Mockito.lenient().when(resourceResolverFactory.getResourceResolver(params)).thenReturn(resourceResolver);
        assertNotNull(resourceResolver);
    }

    @Test()
    void newResolverTestNotNull() {
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, WHOLESALE_SERVICE_USER_DUMMY);
        Mockito.lenient().when(ResolverHelper.newResolver(resourceResolverFactory)).thenReturn(resourceResolver);
        assertNotNull((resourceResolver));
    }
    @Test()
    void newResolverExceptionTest() {
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, WHOLESALE_SERVICE_USER_DUMMY);
        Mockito.lenient().when(ResolverHelper.newResolver(resourceResolverFactory)).thenReturn(resourceResolver);
        assertNotNull((resourceResolver));
    }

}
