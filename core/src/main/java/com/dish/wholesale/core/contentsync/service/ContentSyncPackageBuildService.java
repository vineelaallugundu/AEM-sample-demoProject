package com.dish.wholesale.core.contentsync.service;

import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.sling.api.resource.LoginException;
import org.jetbrains.annotations.NotNull;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Creates and assembles content packages. Uses a {@link UUID} for a unique name and {@link Set Set<String>} of paths.
 */
public interface ContentSyncPackageBuildService {
    /**
     * Builds a package with the given set of paths as filters and the UUID as the name. If the package cannot be built
     * (e.g. the package name is already) a proper exception will be thrown.
     * <p>
     *
     * @param paths the set of paths
     * @param uuid  the uuid name of the package
     * @throws LoginException      if there is a problem getting a resource resolver
     * @throws IOException         if there is an IO problem problem creating or building the package
     * @throws RepositoryException if there is a problem accessing the repository
     * @throws PackageException    if there is a problem assembling the package
     */
    void buildPackage(@NotNull Set<String> paths, @NotNull UUID uuid)
            throws LoginException, IOException, RepositoryException, PackageException;
}
