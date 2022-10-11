package com.dish.wholesale.core.contentsync.service.impl;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.dish.wholesale.core.contentsync.ContentSyncConstants;
import com.dish.wholesale.core.contentsync.service.ContentSyncPackageInstallService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.NullWriter;
import org.apache.jackrabbit.vault.fs.io.ImportOptions;
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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.*;
import java.util.Objects;
import java.util.UUID;

import static com.dish.wholesale.core.contentsync.ContentSyncConstants.CONTENT_SYNC_PACKAGE_GROUP;

@Component(service = ContentSyncPackageInstallService.class, immediate = true)
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ContentSyncPackageInstallServiceImpl implements ContentSyncPackageInstallService {
	
	private static final Logger log = LoggerFactory.getLogger(ContentSyncPackageInstallServiceImpl.class);
	
    @Reference
    Packaging packaging;

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    Replicator replicator;

    @Override
    public void uploadPackage(InputStream packageInputStream)
            throws LoginException, IOException, RepositoryException, PackageException, ReplicationException {
        log.info("uploda package---");
        try (ResourceResolver rr = resourceResolverFactory.getServiceResourceResolver(
                ContentSyncConstants.SERVICE_RESOLVER_PARAMS_INSTALLER)) {
            Session session = rr.adaptTo(Session.class);
            Objects.requireNonNull(session, "session should never be null");
            JcrPackageManager pkgManager = packaging.getPackageManager(session);

            try (JcrPackage pkg = pkgManager.upload(packageInputStream, true)) {
                JcrPackageDefinition pkgDefinition = pkg.getDefinition();
                Objects.requireNonNull(pkgDefinition, "pkgDefinition should never be null");
                log.debug("Package {} installed", pkgDefinition.getId());
            }
        }
    }

    @Override
    public void installPackage(UUID uuid, boolean install, boolean replicate, boolean save)
            throws LoginException, IOException, RepositoryException, PackageException, ReplicationException {
        log.info("install package");
        if (!install) {
            if (!save) {
                throw new IllegalArgumentException("either install or save must be true");
            }

            if (replicate) {
                throw new IllegalArgumentException("replicate cannot be true when install is false");
            }
        }

        try (ResourceResolver rr = resourceResolverFactory.getServiceResourceResolver(
                ContentSyncConstants.SERVICE_RESOLVER_PARAMS_INSTALLER)) {
            Session session = rr.adaptTo(Session.class);
            Objects.requireNonNull(session, "session should never be null");
            JcrPackageManager pkgManager = packaging.getPackageManager(session);

            try (JcrPackage pkg = pkgManager.open(getId(uuid))) {
                Objects.requireNonNull(pkg, "pkg should never be null");

                ImportOptions options = new ImportOptions();
                options.setAutoSaveThreshold(1024);
                Node pkgNode = pkg.getNode();

                if (install) {
                    try (Writer pkgInstallOutput = newWriter()) {
                        options.setListener(new DefaultProgressListener(new PrintWriter(pkgInstallOutput)));

                        Objects.requireNonNull(pkgNode, "pkgNode should never be null");

                        if (replicate) {
                            replicator.replicate(session, ReplicationActionType.ACTIVATE, pkgNode.getPath());
                        }

                        try {
                            pkg.install(options);
                        } finally {
                            log.trace("Package install details:\n{}", pkgInstallOutput);
                        }
                    }
                }

                if (!save) {
                    pkgNode.remove();
                    session.save();
                }
            }
        }
    }

    Writer newWriter() {
        if (log.isTraceEnabled()) {
            return new StringWriter();
        } else {
            return new NullWriter();
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
}
