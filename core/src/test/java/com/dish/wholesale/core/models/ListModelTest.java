package com.dish.wholesale.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class ListModelTest {

    private final AemContext aemContext = new AemContext();

    private ListModel listModel;

    private Resource resource;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(ListModel.class);
        aemContext.load().json("/list/list.json", "/component");
    }

    @Test
    void getDetailsfromMultifield() {
        resource = aemContext.currentResource("/component/list");
        listModel= aemContext.request().adaptTo(ListModel.class);
        assertEquals("/tmp", listModel.getData().get(0).getListButtonLink());
        assertEquals("yes", listModel.getData().get(0).getListButtonText());
        assertEquals("Invo", listModel.getData().get(0).getListTitle());
    }


    @Test
    void getDetailsfromMultifieldWithNull() {
        resource = aemContext.currentResource("/component/list-empty");
        listModel = aemContext.request().adaptTo(ListModel.class);
        assertNull(listModel.getData());
    }

    @Test
    void getDetailsfromMultifieldWithNullList() {
        resource = aemContext.currentResource("/component/list-null");
        listModel = aemContext.request().adaptTo(ListModel.class);
        assertNull(listModel.getData());
    }

    @Test
    void getDetailsfromMultifieldWithNullChildItems() {
        resource = aemContext.currentResource("/component/list-null-items");
        listModel = aemContext.request().adaptTo(ListModel.class);
        assertNull(listModel.getData());
    }

    @Test
    void simpleLoadAndGettersTest() {
        resource = aemContext.currentResource("/component/list");
        listModel= aemContext.request().adaptTo(ListModel.class);
        String[] methods = new String[] { "populateMultiFieldItems", "getPropertyValue", "getData" };
        UtilTest.testLoadAndGetters(methods, listModel, resource);
    }

}