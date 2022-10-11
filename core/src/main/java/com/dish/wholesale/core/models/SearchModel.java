package com.dish.wholesale.core.models;

import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Model(adaptables = SlingHttpServletRequest.class, resourceType = {
        SearchModel.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class SearchModel implements Search {

    /**
     * The resource type.
     */
    protected static final String RESOURCE_TYPE = "wholesale/components/structure/header";

    /**
     * Default number of results to show.
     */
    public static final int PROP_RESULTS_SIZE_DEFAULT = 10;

    /**
     * Default minimum search term length.
     */
    public static final int PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT = 3;

    /**
     * The current request.
     */
    @Self
    private SlingHttpServletRequest request;

    /**
     * The current page.
     */
    @ScriptVariable
    private Page currentPage;

    /**
     * The current style.
     */
    @ScriptVariable
    private Style currentStyle;

    /**
     * The relative path between this component and the containing page.
     */
    private String relativePath;

    /**
     * The number of results to return.
     */
    private int resultsSize;

    /**
     * The minimum search term length.
     */
    private int searchTermMinimumLength;

    /**
     * Initialize the model.
     */
    @PostConstruct
    private void initModel() {
        resultsSize = currentStyle.get(PN_RESULTS_SIZE, PROP_RESULTS_SIZE_DEFAULT);
        searchTermMinimumLength = currentStyle.get(PN_SEARCH_TERM_MINIMUM_LENGTH, PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT);
        Resource currentResource = request.getResource();
        this.relativePath = Optional.ofNullable(currentPage.getPageManager().getContainingPage(currentResource))
                .map(Page::getPath)
                .map(path -> StringUtils.substringAfter(currentResource.getPath(), path))
                .orElse(null);
    }

    @Override
    public int getResultsSize() {
        return resultsSize;
    }

    @Override
    public int getSearchTermMinimumLength() {
        return searchTermMinimumLength;
    }

    @NotNull
    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @NotNull
    @Override
    public String getSearchRootPagePath() {
        return "/content/wholesale/us";
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

}
