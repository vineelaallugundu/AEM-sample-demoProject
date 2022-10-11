package com.dish.wholesale.core.contentsync.service.impl;

import com.dish.wholesale.core.contentsync.ContentSyncConstants;
import com.dish.wholesale.core.contentsync.service.ContentSyncPackageBuildService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.api.WorkspaceFilter;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.packaging.*;
import org.apache.jackrabbit.vault.util.DefaultProgressListener;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Creates and assembles content packages. Uses a {@link UUID} for a unique name and {@link Set Set<String>} of paths.
 */


@Component(service = ContentSyncPackageBuildService.class, immediate = true)
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ContentSyncPackageBuildServiceImpl implements ContentSyncPackageBuildService {
	
	private static final Logger log = LoggerFactory.getLogger(ContentSyncPackageBuildServiceImpl.class);
	
    @Reference
    Packaging packaging;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public void buildPackage(@NotNull Set<String> paths, @NotNull UUID uuid)
            throws LoginException, PackageException, RepositoryException, IOException {
        log.info("build package");
        try (ResourceResolver rr = resourceResolverFactory.getServiceResourceResolver(
                ContentSyncConstants.SERVICE_RESOLVER_PARAMS_WRITER)) {
            Session session = rr.adaptTo(Session.class);
            Objects.requireNonNull(session, "This should never happen");
            JcrPackageManager pkgManager = packaging.getPackageManager(session);

            buildPackageInternal(paths, uuid, pkgManager);

            rr.commit();
        }
    }

    /**
     * Create and assemble the package.
     *
     * @param paths the set of paths
     * @param uuid  the uuid name of the package
     * @throws IOException         if there is an IO problem problem creating or building the package
     * @throws RepositoryException if there is a problem accessing the repository
     * @throws PackageException    if there is a problem assembling the package
     */
    void buildPackageInternal(
            @NotNull Set<String> paths, @NotNull UUID uuid, @NotNull JcrPackageManager pkgManager)
            throws IOException, RepositoryException, PackageException {
        log.info("build package internal");
        if (log.isTraceEnabled()) {
            log.trace("Trying to build package named {} with filter paths {}", uuid, paths);
        } else {
            log.info("Trying to build package named {}", uuid);
        }

        if (packageExists(uuid, pkgManager)) {
            log.info("Package with name {} already exists", uuid);
            throw new IllegalArgumentException("Package with uuid " + uuid + " already exists");
        }

        createPackage(paths, uuid, pkgManager);
        assemblePackage(uuid, pkgManager);
    }

    /**
     * Get whether or not the package with the name {@code uuid} and group
     * {@link ContentSyncConstants#CONTENT_SYNC_PACKAGE_GROUP}.
     *
     * @param uuid the uuid name of the package
     * @throws RepositoryException if there is a problem accessing the repository.
     */
    boolean packageExists(@NotNull UUID uuid, @NotNull JcrPackageManager pkgManager) throws RepositoryException {
        PackageId pkgId = getId(uuid);
        try (JcrPackage pkg = pkgManager.open(pkgId)) {
            return pkg != null;
        }
    }

    /**
     * Create the package. Does only creates and saves the definition, does not assemble the package.
     *
     * @param paths the set of paths
     * @param uuid  the uuid name of the package
     * @throws IOException         if there is an IO problem problem creating or building the package
     * @throws RepositoryException if there is a problem accessing the repository
     */
    void createPackage(@NotNull Set<String> paths, @NotNull UUID uuid, @NotNull JcrPackageManager pkgManager)
            throws IOException, RepositoryException {
        log.info("create package");
        if (log.isTraceEnabled()) {
            log.trace("Creating package named {} with paths {}", uuid, paths);
        } else {
            log.info("Creating package named {} with {} paths", uuid, paths.size());
        }

        try (JcrPackage pkg = pkgManager.create(ContentSyncConstants.CONTENT_SYNC_PACKAGE_GROUP, uuid.toString())) {
            JcrPackageDefinition pkgDefinition = pkg.getDefinition();
            Objects.requireNonNull(pkgDefinition);

            WorkspaceFilter wsFilter = createWorkspaceFilter(paths);
            pkgDefinition.setFilter(wsFilter, true);
        }
    }

    /**
     * Assemble the package.
     *
     * @param uuid the uuid name of the package
     * @throws IOException         if there is an IO problem problem creating or building the package
     * @throws RepositoryException if there is a problem accessing the repository
     * @throws PackageException    if there is a problem assembling the package
     */
    void assemblePackage(@NotNull UUID uuid, @NotNull JcrPackageManager pkgManager)
            throws RepositoryException, PackageException, IOException {
        log.info("Assembling package with name {}", uuid);

        try (JcrPackage pkg = pkgManager.open(getId(uuid))) {
            Objects.requireNonNull(pkg);

            StringWriter sw = new StringWriter();
            pkgManager.assemble(pkg, new DefaultProgressListener(new PrintWriter(sw)));

            if (log.isTraceEnabled()) {
                log.trace("Package with name {} assembled. Details:%n{}", uuid, sw);
            } else {
                log.info("Package with name {} assembled", uuid);
            }
        }
    }

    /**
     * Gets a {@link PackageId} for a given {@link UUID}.
     *
     * @param uuid the uuid name of the package
     * @return the {@link PackageId} that associates with the uuid
     */
    PackageId getId(@NotNull UUID uuid) {
        return new PackageId(ContentSyncConstants.CONTENT_SYNC_PACKAGE_GROUP, uuid.toString(), (String) null);
    }

    /**
     * Gets a {@link WorkspaceFilter} for the given {@link Set} of paths
     *
     * @param paths the set of paths
     * @return a WorkspaceFilter that contains all of the paths
     */
    WorkspaceFilter createWorkspaceFilter(@NotNull Set<String> paths) {
        DefaultWorkspaceFilter wsFilter = new DefaultWorkspaceFilter();

        paths.stream()
                .map(PathFilterSet::new)
                .forEach(wsFilter::add);

        return wsFilter;
    }
}
