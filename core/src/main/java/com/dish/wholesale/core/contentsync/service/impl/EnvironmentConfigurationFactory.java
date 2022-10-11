package com.dish.wholesale.core.contentsync.service.impl;

import com.dish.wholesale.core.contentsync.service.EnvironmentConfiguration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * A factory implementation for {@link EnvironmentConfiguration}s. This allows env configs to be created much like
 * loggers are configured.
 */

@Component(service = EnvironmentConfigurationFactory.class, immediate = true)
@Designate(ocd = EnvironmentConfiguration.class, factory = true)
@Slf4j
public class EnvironmentConfigurationFactory implements EnvironmentConfiguration {
 
	private static final Logger log = LoggerFactory.getLogger(EnvironmentConfigurationFactory.class);

    @Getter @Setter
    private String envName;

    @Getter @Setter
    private String envUrl;

    @Getter @Setter
    private String excludeRunMode;

    @Reference
    @ToString.Exclude
    private SlingSettingsService slingSettingsService;

    @Getter @Setter
    private boolean isIncluded;

    @Getter @Setter
    private int serviceRanking;

    /**
     * Create an instance. Used by the OSGi framework.
     */
    @SuppressWarnings("unused")
    public EnvironmentConfigurationFactory() {
        // Nothing to do
    }

    /**
     * Create an instance. Used by unit tests to simplify mocking.
     *
     * @param slingSettingsService the {@link SlingSettingsService}
     */
    EnvironmentConfigurationFactory(
            SlingSettingsService slingSettingsService) {
        this.slingSettingsService = slingSettingsService;
    }

    /**
     * Processes configurations and sets the fields. Called by the OSGi framework.
     *
     * @param properties the {@link Map} of properties representing the configuration of this service instance.
     */
    @Activate
    public void activate(EnvironmentConfiguration properties) {
        this.envName = properties.getEnvName();
        this.envUrl = properties.getEnvUrl();
        this.excludeRunMode = properties.getExcludeRunMode();
        this.isIncluded = !slingSettingsService.getRunModes().contains(this.excludeRunMode);
        this.serviceRanking = properties.getServiceRanking();

        log.info("env name is:" + this.envName);
        log.info("envurl is:" + this.envUrl);
        log.info("exclude runmode is:" + this.excludeRunMode);

        log.debug("Config registered with values {}", this);
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
