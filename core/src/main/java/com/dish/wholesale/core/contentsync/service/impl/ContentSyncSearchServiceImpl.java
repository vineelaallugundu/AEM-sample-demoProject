package com.dish.wholesale.core.contentsync.service.impl;

import com.adobe.acs.commons.mcp.ProcessDefinitionFactory;
import com.day.cq.wcm.api.reference.Reference;
import com.day.cq.wcm.api.reference.ReferenceProvider;
import com.dish.wholesale.core.contentsync.ContentSyncConstants;
import com.dish.wholesale.core.contentsync.service.ContentSyncSearchService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component(service = ContentSyncSearchService.class, immediate = true)
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ContentSyncSearchServiceImpl implements ContentSyncSearchService {
    // These are fully qualified so that the simple name "Reference" can be used in code below.
    @org.osgi.service.component.annotations.Reference
    ResourceResolverFactory resourceResolverFactory;

    @org.osgi.service.component.annotations.Reference(
            target = "(component.name=com.adobe.cq.xf.impl.ExperienceFragmentsReferenceProvider)")
    ReferenceProvider efragReferenceProvider;

    @org.osgi.service.component.annotations.Reference(
            target = "(component.name=com.day.cq.dam.commons.util.impl.AssetReferenceProvider)")
    ReferenceProvider assetReferenceProvider;

    @org.osgi.service.component.annotations.Reference(
            target = "(component.name=com.day.cq.wcm.core.impl.reference.PageTemplateReferenceProvider)")
    ReferenceProvider templateReferenceProvider;

    @org.osgi.service.component.annotations.Reference(
            target = "(component.name=com.day.cq.wcm.core.impl.reference.ContentPolicyReferenceProvider)")
    ReferenceProvider contentPolicyReferenceProvider;

    @Override
    public @NotNull Set<String> findContent(
            @NotNull Collection<String> paths,
            boolean includeAssets,
            boolean includeTemplates,
            boolean includeEfrags) throws LoginException {
    log.info("find content");
        try (ResourceResolver rr = resourceResolverFactory.getServiceResourceResolver(
                ContentSyncConstants.SERVICE_RESOLVER_PARAMS_READER)) {
            Set<String> filteredPaths = paths.stream()
                    // ensures the paths being searched are restricted to what is configured for the service user
                    .map(this::addJcrContentToPage)
                    .filter(path -> rr.getResource(path) != null)
                    .collect(Collectors.toSet());

            return filterPaths(findContentInternal(
                    rr, filteredPaths, new TreeSet<>(filteredPaths), includeAssets, includeTemplates, includeEfrags));
        }
    }

    @NotNull String addJcrContentToPage(String path) {
        if (isPage(path)) {
            return addJcrContentIfNotThere(path);
        }

        return path;
    }

    @NotNull String addJcrContentIfNotThere(@NotNull String path) {
        if (!path.endsWith("/jcr:content")) {
            return path + "/jcr:content";
        }

        return path;
    }

    boolean isPage(@NotNull String path) {
        return path.startsWith("/content/") && !isEfrag(path) && !isAsset(path);
    }

    @NotNull Set<String> filterPaths(Collection<String> unfilteredPaths) {
        return unfilteredPaths.stream()
                .filter(this::isValidPath)
                .collect(Collectors.toSet());
    }

    boolean isValidPath(@NotNull String path) {
        return isEfrag(path) || isAsset(path) || isTemplate(path) || isPolicy(path) || isPage(path);
    }

    @NotNull Set<String> findContentInternal(
            @NotNull ResourceResolver rr,
            @NotNull Collection<String> searchPaths,
            @NotNull Set<String> foundPaths,
            boolean includeAssets,
            boolean includeTemplates,
            boolean includeEfrags) {

        if (searchPaths.isEmpty()) {
            return foundPaths;
        }

        Set<String> newPaths = new TreeSet<>();

        if (includeEfrags) {
            newPaths.addAll(findEfrags(searchPaths, rr));
        }

        if (includeTemplates) {
            Set<String> templates = findTemplates(searchPaths, rr);
            Set<String> policies = findPolicies(templates, rr);

            // Templates come with the /structure at the end, and we want the entire template
            templates = templates.stream()
                    .map(ResourceUtil::getParent)
                    .collect(Collectors.toSet());

            newPaths.addAll(templates);
            newPaths.addAll(policies);
        }

        if (includeAssets) {
            newPaths.addAll(findAssets(searchPaths, rr));
        }

        newPaths.removeAll(foundPaths);
        foundPaths.addAll(newPaths);

        return findContentInternal(rr, newPaths, foundPaths, includeAssets, includeTemplates, includeEfrags);
    }

    @NotNull Stream<String> findReferences(
            @NotNull Collection<String> paths,
            @NotNull ResourceResolver rr,
            @NotNull ReferenceProvider referenceProvider) {
        return paths.stream()
                .map(rr::getResource)
                .filter(Objects::nonNull)
                .map(this::getJcrContent)
                .filter(Objects::nonNull)
                .map(referenceProvider::findReferences)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Reference::getResource)
                .filter(Objects::nonNull)
                .map(Resource::getPath);
    }

    @Nullable Resource getJcrContent(@NotNull Resource resource) {
        if (resource.getName().equals("jcr:content") || resource.getPath().startsWith("/conf")) {
            return resource;
        } else {
            return resource.getChild("jcr:content");
        }
    }

    @NotNull Set<String> findEfrags(@NotNull Collection<String> paths, @NotNull ResourceResolver rr) {
        return findReferences(paths, rr, efragReferenceProvider)
                .filter(this::isEfrag)
                .map(this::addJcrContentIfNotThere)
                .collect(Collectors.toSet());
    }

    boolean isEfrag(@NotNull String path) {
        return path.startsWith("/content/experience-fragments/");
    }

    @NotNull Set<String> findAssets(@NotNull Collection<String> paths, @NotNull ResourceResolver rr) {
        return findReferences(paths, rr, assetReferenceProvider)
                .filter(this::isAsset)
                .collect(Collectors.toSet());
    }

    boolean isAsset(@NotNull String path) {
        return path.startsWith("/content/dam/");
    }

    @NotNull Set<String> findTemplates(@NotNull Collection<String> paths, @NotNull ResourceResolver rr) {
        return findReferences(paths, rr, templateReferenceProvider)
                .filter(this::isTemplate)
                .collect(Collectors.toSet());
    }

    boolean isTemplate(@NotNull String path) {
        return path.startsWith("/conf/") && path.contains("/settings/wcm/templates/");
    }

    @NotNull Set<String> findPolicies(@NotNull Collection<String> paths, @NotNull ResourceResolver rr) {
        return findReferences(paths, rr, contentPolicyReferenceProvider)
                .filter(this::isPolicy)
                .collect(Collectors.toSet());
    }

    boolean isPolicy(@NotNull String path) {
        return path.startsWith("/conf/") && path.contains("/settings/wcm/policies/");
    }
}
