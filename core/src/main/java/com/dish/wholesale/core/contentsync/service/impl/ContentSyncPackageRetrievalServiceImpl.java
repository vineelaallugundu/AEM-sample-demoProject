package com.dish.wholesale.core.contentsync.service.impl;

import com.dish.wholesale.core.contentsync.ContentSyncConstants;
import com.dish.wholesale.core.contentsync.service.ContentSyncPackageInstallService;
import com.dish.wholesale.core.contentsync.service.ContentSyncPackageRetrievalService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackageId;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import static com.dish.wholesale.core.contentsync.ContentSyncConstants.CONTENT_SYNC_PACKAGE_GROUP;


@Component(service = ContentSyncPackageRetrievalService.class, immediate = true)
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ContentSyncPackageRetrievalServiceImpl implements ContentSyncPackageRetrievalService {
    @Reference
    Packaging packaging;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public @Nullable InputStream findPackage(@NotNull UUID uuid)
            throws LoginException, RepositoryException, IOException {
        log.info("find package retrival");
        try (ResourceResolver rr = resourceResolverFactory.getServiceResourceResolver(
                ContentSyncConstants.SERVICE_RESOLVER_PARAMS_INSTALLER)) {
            Session session = rr.adaptTo(Session.class);
            Objects.requireNonNull(session, "session should never be null");
            JcrPackageManager pkgManager = packaging.getPackageManager(session);

            try (JcrPackage pkg = pkgManager.open(getId(uuid))) {
                Objects.requireNonNull(pkg, "pkg should never be null");

                Node pkgNode = pkg.getNode();
                Objects.requireNonNull(pkgNode, "pkgNode should never be null");

                Resource packageResource = rr.getResource(pkgNode.getPath());
                Objects.requireNonNull(packageResource, "packageResource should never be null");

                return packageResource.adaptTo(InputStream.class);
            }
        }
    }

    /**
     * !!! Deduplicate this method. It is the same as {@link ContentSyncPackageBuildServiceImpl#getId}
     * <p>
     * Gets a {@link PackageId} for a given {@link UUID}.
     * <p>
     * * @param uuid the uuid name of the package
     *
     * @return the {@link PackageId} that associates with the uuid
     */
    PackageId getId(@NotNull UUID uuid) {
        return new PackageId(CONTENT_SYNC_PACKAGE_GROUP, uuid.toString(), (String) null);
    }

    @Override
    public void deletePackage(@NotNull UUID uuid) throws LoginException, RepositoryException {
        try (ResourceResolver rr = resourceResolverFactory.getServiceResourceResolver(
                ContentSyncConstants.SERVICE_RESOLVER_PARAMS_INSTALLER)) {
            Session session = rr.adaptTo(Session.class);
            Objects.requireNonNull(session, "session should never be null");
            JcrPackageManager pkgManager = packaging.getPackageManager(session);
            try (JcrPackage pkg = pkgManager.open(getId(uuid))) {
                Objects.requireNonNull(pkg, "pkg should never be null");

                Node pkgNode = pkg.getNode();
                Objects.requireNonNull(pkgNode, "pkgNode should never be null");

                pkgNode.remove();
                session.save();
            }
        }
    }
}
