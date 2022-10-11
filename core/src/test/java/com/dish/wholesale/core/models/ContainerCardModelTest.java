package com.dish.wholesale.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class ContainerCardModelTest {

    private final AemContext aemContext = new AemContext();

    private ContainerCardModel containerCardModel;

    private Resource resource;

    @BeforeEach
    void setUp() {

        aemContext.addModelsForClasses(ContainerCardModel.class);
        aemContext.load().json("/container-card/container-card.json", "/component");
        /*
         * resource = aemContext.currentResource("/component/container-card");
         * containerCardModel = resource.adaptTo(ContainerCardModel.class);
         */
    }

    @Test
    void getContainerCardDetailsTest() {
        resource = aemContext.currentResource("/component/container-card");
        containerCardModel = resource.adaptTo(ContainerCardModel.class);
        assertEquals("cover",containerCardModel.getBgSize());
        assertEquals("to bottom right",containerCardModel.getBgGradientDirection());
        assertEquals("#FFDC00",containerCardModel.getContainerBgColorSecondary());
        assertEquals("#7FDBFF",containerCardModel.getContainerBgColorPrimary());
        assertNotNull(containerCardModel.getContainerBgImage());
        assertNotNull(containerCardModel.getBgPosition());
        assertNotNull(containerCardModel.getGuid());
        assertNotNull(containerCardModel.getBackgroundCss());
    }

    @Test
    void getContainerCardDetailsFirstIfTest() {
        resource = aemContext.currentResource("/component/container-card-firstif");
        containerCardModel = resource.adaptTo(ContainerCardModel.class);
        assertNotNull(containerCardModel.getBackgroundCss());
    }

    @Test
    void getContainerCardDetailsSecondIfTest() {
        resource = aemContext.currentResource("/component/container-card-secondif");
        containerCardModel = resource.adaptTo(ContainerCardModel.class);
        assertNotNull(containerCardModel.getBackgroundCss());
    }

    @Test
    void getContainerCardDetailsThirdIfTest() {
        resource = aemContext.currentResource("/component/container-card-thirdif");
        containerCardModel = resource.adaptTo(ContainerCardModel.class);
        assertNotNull(containerCardModel.getBackgroundCss());
    }

    @Test
    void getContainerCardDetailsFourthIfTest() {
        resource = aemContext.currentResource("/component/container-card-fourthif");
        containerCardModel = resource.adaptTo(ContainerCardModel.class);
        assertNotNull(containerCardModel.getBackgroundCss());
    }

    @Test
    void getContainerCardDetailsFifthIfTest() {
        resource = aemContext.currentResource("/component/container-card-fifthif");
        containerCardModel = resource.adaptTo(ContainerCardModel.class);
        assertNotNull(containerCardModel.getBackgroundCss());
    }
}