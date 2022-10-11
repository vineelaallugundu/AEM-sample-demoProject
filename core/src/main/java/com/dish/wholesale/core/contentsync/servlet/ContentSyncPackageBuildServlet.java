package com.dish.wholesale.core.contentsync.servlet;

import com.dish.wholesale.core.contentsync.service.ContentSyncPackageBuildService;
import com.dish.wholesale.core.contentsync.service.ContentSyncSearchService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The API endpoint to build a content package for a given set of parameters.
 * Returns a UUID for future requests
 */
@SlingServletPaths(ContentSyncPackageBuildServlet.PATH_PACKAGE_BUILD_SERVLET)
@Component(immediate = true, service = Servlet.class, property = {
        "sling.servlet.methods=POST" })
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ContentSyncPackageBuildServlet extends SlingAllMethodsServlet {
    private static final String SLASH = "/";
    public static final String PARAM_PATH = "path";
    public static final String PARAM_INCLUDE_ASSETS = "includeAssets";
    public static final String PARAM_INCLUDE_TEMPLATES = "includeTemplates";
    public static final String PARAM_INCLUDE_EFRAGS = "includeEfrags";
    public static final String PATH_PACKAGE_BUILD_SERVLET = SLASH + "bin/wholesale/content-sync/build-package";

    @Reference
    transient ContentSyncPackageBuildService buildService;

    @Reference
    transient ContentSyncSearchService searchService;

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        log.info("do post");
        try {
            Set<String> foundPaths = searchService.findContent(
                    paths(request),
                    includeAssets(request),
                    includeTemplates(request),
                    includeEfragsr(request));

            UUID uuid = uuid(request);

            buildService.buildPackage(foundPaths, uuid);

            response.getWriter().print(uuid.toString());
        } catch (IllegalArgumentException | LoginException | RepositoryException | PackageException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Gets all path parameters from the request.
     *
     * @param request the request
     * @return the path parameters, as a {@link Collection}
     * @throws IllegalArgumentException if the path parameters array is null
     */
    Collection<String> paths(SlingHttpServletRequest request) {
        log.info("paths");
        return Optional.ofNullable(request.getParameterValues(PARAM_PATH))
                .map(Arrays::stream)
                .orElseThrow(() -> new IllegalArgumentException("No paths provided"))
                .collect(Collectors.toSet());
    }

    /**
     * Get the include assets parameter boolean
     *
     * @param request the request
     * @return true if the include assets param value is "true" (ignoring case)
     */
    boolean includeAssets(SlingHttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(PARAM_INCLUDE_ASSETS));
    }

    /**
     * Get the include templates parameter boolean
     *
     * @param request the request
     * @return true if the include templates param value is "true" (ignoring case)
     */
    boolean includeTemplates(SlingHttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(PARAM_INCLUDE_TEMPLATES));
    }

    /**
     * Get the include efrags parameter boolean
     *
     * @param request the request
     * @return true if the include efrags param value is "true" (ignoring case)
     */
    boolean includeEfragsr(SlingHttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(PARAM_INCLUDE_EFRAGS));
    }

    UUID uuid(SlingHttpServletRequest request) {
        String uuidString = request.getParameter("debugginguuid");
        try {
            return Optional.ofNullable(uuidString)
                    .map(UUID::fromString)
                    .orElseGet(UUID::randomUUID);
        } catch (Exception e) {
            return UUID.randomUUID();
        }
    }
}
