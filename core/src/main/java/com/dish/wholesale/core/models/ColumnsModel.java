package com.dish.wholesale.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class for Columns component.
 * This class provides all required methods to support the Columns component.
 *
 * @author spuli
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ColumnsModel {

    private static final int DEFAULT_PARSYS = 1;
    public static final String COL_LG = "col-lg-";
    @ValueMapValue
    @Default(intValues = DEFAULT_PARSYS)
    private int lines;
    private String layoutClass;
    private String[] bootStrapClass = null;
    @Inject @Default(values = "6,6")
    private String twopanelradioselect;

    @Inject @Default(values = "4,4,4")
    private String threepanelradioselect;

    @Inject @Default(values = "3,3,3,3")
    private String fourpanelradioselect;

    @Inject @Default(intValues = 0)
    private int columns;

    @PostConstruct
    public void init() {
        if (columns <= 0) {
            columns = 1;
            layoutClass = "full-width-layout";
            bootStrapClass = new String[]{"col-lg-12 p-0"};
        } else if (columns == 1) {
            layoutClass = "one-column-layout";
            bootStrapClass = new String[]{"col-lg-12 p-0"};
        } else if (columns == 2) {
            layoutClass = "two-column-layout";
            bootStrapClass = new String[]{COL_LG + twopanelradioselect.split(",")[0] + " " + "pr-0", COL_LG + twopanelradioselect.split(",")[1] + " " + "pl-0"};
        } else if (columns == 3) {
            layoutClass = "three-column-layout";
            bootStrapClass = new String[]{COL_LG + threepanelradioselect.split(",")[0] + " " + "pr-0", COL_LG + threepanelradioselect.split(",")[1] + " " + "p-0", COL_LG + threepanelradioselect.split(",")[2] + " " + "pl-0"};
        } else if (columns == 4) {
            layoutClass = "four-column-layout";
            bootStrapClass = new String[]{COL_LG + fourpanelradioselect.split(",")[0] + " " + "pr-0", COL_LG + fourpanelradioselect.split(",")[1] + " " + "p-0", COL_LG + fourpanelradioselect.split(",")[2] + " " + "p-0", COL_LG + fourpanelradioselect.split(",")[3] + " " + "pl-0"};
        }
    }

    public int getLines() { return lines; }
    public String getLayoutClass() { return layoutClass; }
    public int getColumns() { return columns; }

    public Map<String, String> getGrid() {
        Map<String, String> grid = new HashMap<>();
            for (int lineCount = 1; lineCount <= lines; lineCount++) {
                for (int columnCount = 1; columnCount <= columns; columnCount++) {
                    grid.put(String.valueOf(columnCount), bootStrapClass[columnCount - 1]);
                }
            }
        return grid;
    }
}
