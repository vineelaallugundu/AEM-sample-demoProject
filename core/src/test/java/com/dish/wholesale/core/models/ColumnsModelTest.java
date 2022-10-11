package com.dish.wholesale.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.poi.ss.formula.functions.Columns;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class ColumnsModelTest {

    private final AemContext aemContext = new AemContext();

    private Resource resource;

    private ColumnsModel columnsModel;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(ColumnsModel.class);
        aemContext.load().json("/columns/columns.json", "/component");
    }

    @Test
    void testForNoRadioSelect() {
        resource = aemContext.currentResource("/component/noradioselect");
        columnsModel = resource.adaptTo(ColumnsModel.class);
        columnsModel.getGrid();
        columnsModel.getColumns();
        columnsModel.getLayoutClass();
        columnsModel.getLines();
    }

    @Test
    void testForOnePanelRadioSelect() {
        resource = aemContext.currentResource("/component/onepanelradioselect");
        columnsModel = resource.adaptTo(ColumnsModel.class);
    }

    @Test
    void testFortTwoPanelRadioSelect() {
        resource = aemContext.currentResource("/component/twopanelradioselect");
        columnsModel = resource.adaptTo(ColumnsModel.class);
    }
    @Test
    void testFortThreePanelRadioSelect() {
        resource = aemContext.currentResource("/component/threepanelradioselect");
        columnsModel = resource.adaptTo(ColumnsModel.class);
    }
       
    @Test
    void testForFourPanelRadioSelect() {
        resource = aemContext.currentResource("/component/fourpanelradioselect");
        columnsModel = resource.adaptTo(ColumnsModel.class);
    }



}

