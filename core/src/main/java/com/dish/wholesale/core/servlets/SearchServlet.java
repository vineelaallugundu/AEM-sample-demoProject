package com.dish.wholesale.core.servlets;


import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.FulltextPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.*;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.dish.wholesale.core.beans.PageList;
import com.dish.wholesale.core.models.SearchModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.scripting.core.ScriptHelper;
import org.apache.sling.xss.XSSAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.StreamSupport;

import static com.adobe.cq.wcm.core.components.models.ExperienceFragment.PN_FRAGMENT_VARIATION_PATH;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * Search servlet.
 */
@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.selectors=" + SearchServlet.DEFAULT_SELECTOR,
                "sling.servlet.resourceTypes=cq/Page",
                "sling.servlet.extensions=json",
                "sling.servlet.methods=GET"
        }
)
public class SearchServlet extends SlingSafeMethodsServlet {

    /**
     * Selector to trigger the search servlet.
     */
    protected static final String DEFAULT_SELECTOR = "dishsearchresults";

    /**
     * Name of the query parameter containing the user query.
     */
    protected static final String PARAM_FULLTEXT = "fulltext";


    /**
     * Input Text maximum length
     */
    private static final int MAX_LIMIT = 20;

    /**
     * Site Root Path for Dish
     */
    protected static final String SITE_ROOT_DISH = "/content/wholesale";

    /**
     * Name of the query parameter indicating the search result offset.
     */
    protected static final String PARAM_RESULTS_OFFSET = "resultsOffset";

    /**
     * Name of the template structure node.
     */
    private static final String NN_STRUCTURE = "structure";

    /**
     * Query builder service.
     */
    @Reference
    private transient QueryBuilder queryBuilder;

    /**
     * Language manager service.
     */
    @Reference
    private transient LanguageManager languageManager;

    /**
     * Relationship manager service.
     */
    @Reference
    private transient LiveRelationshipManager relationshipManager;

    /**
     * Model factory service.
     */
    @Reference
    private transient ModelFactory modelFactory;

    @Reference
    private transient XSSAPI xssApi;

    /**
     * Bundle context.
     */
    private transient BundleContext bundleContext;

    private static final String[] checkArray = {"SELECT *", "SELECT COUNT", "COUNT(", "CHAR(", "CONCAT(", "ROW(", "RAND(", "GROUP BY", "ORDER BY"};

    /**
     * Activate the service.
     *
     * @param bundleContext The bundle context.
     */
    @Activate
    protected void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response)
            throws IOException {
        Page currentPage = Optional.ofNullable(request.getResourceResolver().adaptTo(PageManager.class))
                .map(pm -> pm.getContainingPage(request.getResource()))
                .orElse(null);
        if (currentPage != null) {
            SlingBindings bindings = new SlingBindings();
            bindings.setSling(new ScriptHelper(bundleContext, null, request, response));
            request.setAttribute(SlingBindings.class.getName(), bindings);

            Search searchComponent = getSearchComponent(request, currentPage);
            try {
                List<ListItem> results = getResults(request, searchComponent, currentPage.getPageManager());
                response.setContentType("application/json");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                new ObjectMapper().writeValue(response.getWriter(), results);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Gets the search component for the given request.
     *
     * @param request The search request.
     * @param currentPage The current page.
     * @return The search component.
     */
    @NotNull
    private Search getSearchComponent(@NotNull final SlingHttpServletRequest request, @NotNull final Page currentPage) {
        String suffix = request.getRequestPathInfo().getSuffix();
        String relativeContentResourcePath = Optional.ofNullable(suffix)
                .filter(path -> StringUtils.startsWith(path, "/"))
                .map(path -> StringUtils.substring(path, 1))
                .orElse(suffix);

        return Optional.ofNullable(relativeContentResourcePath)
                .filter(StringUtils::isNotEmpty)
                .map(rcrp -> getSearchComponentResourceFromPage(request.getResource(), rcrp)
                        .orElse(getSearchComponentResourceFromTemplate(currentPage, rcrp)
                                .orElse(null)))
                .map(resource -> modelFactory.getModelFromWrappedRequest(request, resource, Search.class))
                .orElseGet(DefaultSearch::new);
    }

    /**
     * Gets the search component resource from the page. Looks inside experience fragments in the page too.
     *
     * @param pageResource The page resource.
     * @param relativeContentResourcePath The relative path of the search component resource.
     * @return The search component resource.
     */
    private Optional<Resource> getSearchComponentResourceFromPage(@NotNull final Resource pageResource, final String relativeContentResourcePath) {
        return Optional.ofNullable(Optional.ofNullable(pageResource.getChild(relativeContentResourcePath))
                .orElse(getSearchComponentResourceFromFragments(pageResource.getChild(NameConstants.NN_CONTENT), relativeContentResourcePath)
                        .orElse(null)));
    }

    /**
     * Gets the search component resource from the page's template. Looks inside experience fragments in the template too.
     *
     * @param currentPage The current page, whose template will be used.
     * @param relativeContentResourcePath The relative path of the search component resource.
     * @return The search component resource.
     */
    private Optional<Resource> getSearchComponentResourceFromTemplate(@NotNull final Page currentPage, final String relativeContentResourcePath) {
        return Optional.ofNullable(currentPage.getTemplate())
                .map(Template::getPath)
                .map(currentPage.getContentResource().getResourceResolver()::getResource)
                .map(templateResource -> Optional.ofNullable(templateResource.getChild(NN_STRUCTURE + "/" + relativeContentResourcePath))
                        .orElse(getSearchComponentResourceFromFragments(templateResource, relativeContentResourcePath)
                                .orElse(null)));
    }

    /**
     * Gets the search component resource from experience fragments under the resource. Walks down the descendants tree.
     *
     * @param resource The resource where experience fragments with search component would be looked up.
     * @param relativeContentResourcePath The relative path of the search component resource.
     * @return The search component resource.
     */
    private Optional<Resource> getSearchComponentResourceFromFragments(Resource resource, String relativeContentResourcePath) {
        return Optional.ofNullable(resource)
                .map(res -> getSearchComponentResourceFromFragment(res, relativeContentResourcePath)
                        .orElse(StreamSupport.stream(res.getChildren().spliterator(), false)
                                .map(child -> getSearchComponentResourceFromFragments(child, relativeContentResourcePath).orElse(null))
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(null)));
    }

    /**
     * Gets the search component resource from a candidate experience fragment component resource.
     *
     * @param candidate The candidate experience fragment component resource.
     * @param relativeContentResourcePath The relative path of the search component resource.
     * @return The search component resource.
     */
    private Optional<Resource> getSearchComponentResourceFromFragment(Resource candidate, String relativeContentResourcePath) {
        return Optional.ofNullable(candidate)
                .map(Resource::getValueMap)
                .map(properties -> properties.get(PN_FRAGMENT_VARIATION_PATH, String.class))
                .map(path -> candidate.getResourceResolver().getResource(path + "/" + relativeContentResourcePath));
    }

    /**
     * Gets the search results.
     *
     * @param request The search request.
     * @param searchComponent The search component.
     * @param pageManager A PageManager.
     * @return List of search results.
     */
    @NotNull
    private List<ListItem> getResults(@NotNull final SlingHttpServletRequest request,
                                      @NotNull final Search searchComponent,
                                      @NotNull final PageManager pageManager) {

        List<ListItem> results = new ArrayList<>();
        String fulltext = xssApi.encodeForHTML(request.getParameter(PARAM_FULLTEXT));
        if (fulltext == null || fulltext.length() < searchComponent.getSearchTermMinimumLength() || fulltext.length() > MAX_LIMIT || Arrays.stream(checkArray).anyMatch(fulltext.toUpperCase() :: contains)) {
            return results;
        }
        long resultsOffset = Optional.ofNullable(request.getParameter(PARAM_RESULTS_OFFSET)).map(Long::parseLong).orElse(0L);
        Map<String, String> predicatesMap = new HashMap<>();
        predicatesMap.put(FulltextPredicateEvaluator.FULLTEXT, fulltext);
        predicatesMap.put(PathPredicateEvaluator.PATH, searchComponent.getSearchRootPagePath());
        PredicateGroup predicates = PredicateConverter.createPredicates(predicatesMap);
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();
        Query query = queryBuilder.createQuery(predicates, resourceResolver.adaptTo(Session.class));
        if (searchComponent.getResultsSize() != 0) {
            query.setHitsPerPage(searchComponent.getResultsSize());
        }
        if (resultsOffset != 0) {
            query.setStart(resultsOffset);
        }
        SearchResult searchResult = query.getResult();

        // Query builder has a leaking resource resolver, so the following work around is required.
        ResourceResolver leakingResourceResolver = null;
        try {
            Iterator<Resource> resourceIterator = searchResult.getResources();
            List<ListItem> temporaryDataResults = new ArrayList<>();
            while (resourceIterator.hasNext()) {
                Resource resource = resourceIterator.next();

                // Get a reference to QB's leaking resource resolver
                if (leakingResourceResolver == null) {
                    leakingResourceResolver = resource.getResourceResolver();
                }

                Optional.of(resource)
                        .map(res -> resourceResolver.getResource(res.getPath()))
                        .map(pageManager::getContainingPage)
                        .filter(page -> !page.isHideInNav())
                        .map(page -> new PageList(page, searchComponent.getId()))
                        .ifPresent(temporaryDataResults::add);
            }
            results = temporaryDataResults.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(ListItem::getPath))), ArrayList::new));
        } finally {
            if (leakingResourceResolver != null) {
                leakingResourceResolver.close();
            }
        }
        return results;
    }

    /**
     * A fall-back implementation of the Search model.
     */
    private final class DefaultSearch implements Search {

        @Override
        @Nullable
        public String getId() {
            return null;
        }

        @Override
        public int getResultsSize() {
            return SearchModel.PROP_RESULTS_SIZE_DEFAULT;
        }

        @Override
        public int getSearchTermMinimumLength() {
            return SearchModel.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT;
        }

        @NotNull
        @Override
        public String getSearchRootPagePath() {
            return SITE_ROOT_DISH;
        }

    }
}
