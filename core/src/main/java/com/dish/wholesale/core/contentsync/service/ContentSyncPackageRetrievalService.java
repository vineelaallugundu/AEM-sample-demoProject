package com.dish.wholesale.core.contentsync.service;

import org.apache.sling.api.resource.LoginException;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Retrieves and deletes built content sync packages, by UUID.
 */
public interface ContentSyncPackageRetrievalService {
    /**
     * Gets a package as an input stream.
     * NOTE: the returned input stream must be closed by the calling code.
     *
     * @param uuid the package UUID to retrieve
     * @return the package contents, as an {@link InputStream}
     */
    InputStream findPackage(UUID uuid) throws LoginException, RepositoryException, IOException;

    /**
     * Deletes a package.
     *
     * @param uuid the package UUID to delete
     */
    void deletePackage(UUID uuid) throws LoginException, RepositoryException;
}
