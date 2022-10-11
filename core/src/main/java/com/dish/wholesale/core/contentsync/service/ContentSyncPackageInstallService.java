package com.dish.wholesale.core.contentsync.service;

import com.day.cq.replication.ReplicationException;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.sling.api.resource.LoginException;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * "Uploads" and installs package contents.
 */
public interface ContentSyncPackageInstallService {
    /**
     * "Uplodds" contents of the package in packageInputStream.
     *
     * @param packageInputStream the contents of the package
     * @throws IllegalArgumentException if both install and save are both false
     *                                  or if install is false and replicate is true
     */
    void uploadPackage(InputStream packageInputStream)
            throws LoginException, IOException, RepositoryException, PackageException, ReplicationException;

    /**
     * "Uplodds" and/or installs the contents of the package in packageInputStream.
     *
     * @param uuid      the UUID for the package
     * @param install   whether to install the package
     * @param replicate whether to replicate the package
     * @param save      whether to save the package after install
     * @throws IllegalArgumentException if both install and save are both false
     *                                  or if install is false and replicate is true
     */
    void installPackage(UUID uuid, boolean install, boolean replicate, boolean save)
            throws LoginException, IOException, RepositoryException, PackageException, ReplicationException;
}
