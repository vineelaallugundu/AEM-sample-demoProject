package com.dish.wholesale.core.contentsync.service;

import org.apache.sling.api.resource.LoginException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

/**
 * Finds content, including referenced content, if requested.
 */
public interface ContentSyncSearchService {
    /**
     * Finds the content path and any referenced content, based on the boolean flags provided.
     *
     * @param paths            the list of paths to find
     * @param includeAssets    whether to include referenced assets
     * @param includeTemplates whether to include templates for the pages
     * @param includeEfrags    whether to include referenced efrags
     * @return a set of paths
     */
    @NotNull Set<String> findContent(
            @NotNull Collection<String> paths,
            boolean includeAssets,
            boolean includeTemplates,
            boolean includeEfrags) throws LoginException;
}
