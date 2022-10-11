package com.dish.wholesale.core.contentsync.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * A service to provide details for server-to-server configuration so that APIs on one can be called by another.
 */
@ObjectClassDefinition(name = "Environment Configuration")
public @interface EnvironmentConfiguration {
    /**
     * The name of this config from the config data.
     * @return the name
     */
    @AttributeDefinition(
            name = "envName",
            description = "The name of this environment. This must be unique."
    )
    String getEnvName() default "local";

    /**
     * The url of this config from the config data.
     * @return the url
     */
    @AttributeDefinition(
            name = "envUrl",
            description = "The url of this config from the config data."
    )
    String getEnvUrl() default "http://localhost:4502";

    /**
     * The run modes that this config should not apply from the config data. This was done to simplify the
     * configuration of multiple environments.
     * @return the excluded run mode
     */
    @AttributeDefinition(
            name = "excludeRunMode",
            description = "The run modes that this config should not apply from the config data."
    )
    String getExcludeRunMode() default "local";

    /**
     * Whether or not this config should be included in a list of configs. The simplest implementation of this would be
     * to check {@code serverRunModes.contains(getExcludedRunMode())}
     * @return whether this is excluded
     */
    @AttributeDefinition(
            name = "isIncluded",
            description = "The run modes that needs to be included.",
            type = AttributeType.BOOLEAN
    )
    boolean isIncluded() default true;

    int getServiceRanking();
}
