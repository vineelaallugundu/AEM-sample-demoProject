package com.dish.wholesale.core.contentsync.servlet;

import com.dish.wholesale.core.contentsync.service.ContentSyncPackageRetrievalService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * The API endpoint to retrieve a content package.
 */
@SlingServletPaths(ContentSyncPackageRetrievalServlet.PATH_PACKAGE_RETRIEVAL_SERVLET)
@Component(immediate = true, service = Servlet.class, property = {
        "sling.servlet.methods=POST" })
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ContentSyncPackageRetrievalServlet extends SlingAllMethodsServlet {
    private static final String SLASH = "/";
    public static final String PARAM_UUID = "uuid";
    public static final String PATH_PACKAGE_RETRIEVAL_SERVLET = SLASH + "bin/wholesale/content-sync/retrieve-package";

    @Reference
    transient ContentSyncPackageRetrievalService retrievalService;

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        UUID uuid = uuid(request);
        log.info("posting");
        try (InputStream pkgStream = retrievalService.findPackage(uuid)) {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/zip");
            copy(pkgStream, response.getOutputStream());
        } catch (LoginException | RepositoryException e) {
            throw new ServletException(e);
        }

        try {
            retrievalService.deletePackage(uuid);
        } catch (LoginException | RepositoryException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Gets the UUID parameter from the request
     *
     * @param request the request
     * @return the uuid
     * @throws IllegalArgumentException if the uuid param is null or not a valid uuid
     */
    UUID uuid(SlingHttpServletRequest request) {
        String uuidString = request.getParameter(PARAM_UUID);
        return Optional.ofNullable(uuidString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Invalid UUID: " + uuidString));
    }

    /**
     * Copies an input stream to an output stream. Mainly used for testing (so it can be mocked).
     *
     * @param input  the input stream
     * @param output the output stream
     * @throws IOException if the copy throws an exception
     */
    void copy(InputStream input, OutputStream output) throws IOException {
        IOUtils.copy(input, output);
    }
}
