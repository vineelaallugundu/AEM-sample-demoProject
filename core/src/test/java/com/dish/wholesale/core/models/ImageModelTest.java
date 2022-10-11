package com.dish.wholesale.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class ImageModelTest {

    private final AemContext aemContext = new AemContext();

    private Resource resource;

    private ImageModel imageModel;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(ImageModel.class);
        aemContext.load().json("/image/test-image.json", "/component");
    }

    @Test
    void testGetters(){
        resource = aemContext.currentResource("/component/imageWithPixels");
        imageModel = resource.adaptTo(ImageModel.class);
        String[] methods = new String[] { "getFileReference", "getAltText", "getAlignment", "getWidth", "getImageLink"};
        UtilTest.testLoadAndGetters(methods, imageModel, resource);
    }

    @Test
    void testForWidthInPixels() {
        resource = aemContext.currentResource("/component/imageWithPixels");
        imageModel = resource.adaptTo(ImageModel.class);
        resource = aemContext.currentResource("/component/imageWithPixelsAndNull");
        imageModel = resource.adaptTo(ImageModel.class);
    }

    @Test
    void testForWidthInPercentage() {
        resource = aemContext.currentResource("/component/imageWithPercentage");
        imageModel = resource.adaptTo(ImageModel.class);
        resource = aemContext.currentResource("/component/imageWithPercentageAndNull");
        imageModel = resource.adaptTo(ImageModel.class);
    }

    @Test
    void testForNoWidth() {
        resource = aemContext.currentResource("/component/imageWithNoWidth");
        imageModel = resource.adaptTo(ImageModel.class);
    }

}
