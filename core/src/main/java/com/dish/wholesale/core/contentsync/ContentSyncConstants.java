package com.dish.wholesale.core.contentsync;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// CHECKSTYLE:OFF: HideUtilityClassConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentSyncConstants {
    public static final String CONTENT_SYNC_PACKAGE_GROUP = "content-sync-tool";
    public static final Map<String, Object> SERVICE_RESOLVER_PARAMS_WRITER;
    public static final Map<String, Object> SERVICE_RESOLVER_PARAMS_INSTALLER;
    public static final Map<String, Object> SERVICE_RESOLVER_PARAMS_READER;

    static {
        Map<String, Object> temp = new HashMap<>();
        temp.put(ResourceResolverFactory.SUBSERVICE, "wholesale-content-sync-package-writer");
        SERVICE_RESOLVER_PARAMS_WRITER = Collections.unmodifiableMap(temp);
    }

    static {
        Map<String, Object> temp = new HashMap<>();
        temp.put(ResourceResolverFactory.SUBSERVICE, "wholesale-content-sync-package-installer");
        SERVICE_RESOLVER_PARAMS_INSTALLER = Collections.unmodifiableMap(temp);
    }

    static {
        Map<String, Object> temp = new HashMap<>();
        temp.put(ResourceResolverFactory.SUBSERVICE, "wholesale-content-sync-reader");
        SERVICE_RESOLVER_PARAMS_READER = Collections.unmodifiableMap(temp);
    }
}
// CHECKSTYLE:ON: HideUtilityClassConstructor
