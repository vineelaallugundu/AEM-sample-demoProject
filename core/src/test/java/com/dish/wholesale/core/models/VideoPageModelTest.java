package com.dish.wholesale.core.models;

import com.day.cq.dam.api.Asset;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class VideoPageModelTest {

    private final AemContext aemContext = new AemContext();

    @InjectMocks
    VideoPageModel videoPageModel;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    Resource resource;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Asset asset;

    @Test
    void whenInit() {
        assertEquals(videoPageModel.getValue(), null);
    }
}
