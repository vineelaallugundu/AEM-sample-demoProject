package com.dish.wholesale.core.contentsync.service.impl;

import com.adobe.acs.commons.mcp.AuthorizedGroupProcessDefinitionFactory;
import com.adobe.acs.commons.mcp.ProcessDefinitionFactory;
import com.dish.wholesale.core.contentsync.service.ContentSyncPackageInstallService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The {@link ProcessDefinitionFactory} that creates {@link ContentSyncProcess} instances.
 */
@Component(service = ProcessDefinitionFactory.class, immediate = true)
@Designate(ocd = ContentSyncProcessDefinitionFactory.Config.class)
@Slf4j
@AllArgsConstructor
public class ContentSyncProcessDefinitionFactory extends AuthorizedGroupProcessDefinitionFactory<ContentSyncProcess> {
    /**
     * The default set of groups allowed to execute this MCP.
     */
	private static final Logger log = LoggerFactory.getLogger(ContentSyncProcessDefinitionFactory.class);
	
    private static final String[] DEFAULT_ALLOWED_GROUPS = {
            "administrators"
    };

    @Getter
    @SuppressWarnings("java:S1170")
    private final String name = "Content Sync Process";

    Configuration[] configurations;

    @Getter
    @SuppressWarnings("java:S1170")
    private final String[] authorizedGroups = DEFAULT_ALLOWED_GROUPS;


      Set<EnvironmentConfigurationFactory> environmentConfigurations = new TreeSet<>();

    @Reference
    private ConfigurationAdmin configurationAdmin;
    protected String pid = "com.dish.wholesale.core.contentsync.service.impl.EnvironmentConfigurationFactory";

    @Reference
    private ContentSyncPackageInstallService installService;

    private String username;

    private String password;

    @Activate
    void activate(Config config) {
        username = config.username();
        password = config.password();
        log.info("username is:" + username);
        log.info("password is:" + password);
    }

    @ObjectClassDefinition(
            name = "Content Sync Process Definition Process",
            description = "The configuration Content Sync Process Definition Process")
    public @interface Config {

        @AttributeDefinition(name = "username", description = "Username for Content Sync")
        String username() default "dish--content-sync--access";

        @AttributeDefinition(name = "password", description = "Password for Content Sync")
        String password() default "sRa4Zh4tb!9?S*$tSKTAs?*wpSByCh";
    }

    public java.lang.String getPassword() {
        return password;
    }

    /**
     * Create an instance. Used by the OSGi framework.
     */
    @SuppressWarnings("unused")
    public ContentSyncProcessDefinitionFactory() {
        log.info("constructor");
    }

    /**
     * Adds an environment config to the set of configs. Used by the OSGi container.
     *
     * @param config the config to add
     */
    @Reference(
            service = EnvironmentConfigurationFactory.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            unbind = "unbind",
            policy = ReferencePolicy.DYNAMIC)
    synchronized void bind(EnvironmentConfigurationFactory config) {
        log.debug("Adding environment config {}", config);
        environmentConfigurations.add(config);
    }

    /**
     * Removes an environment config from the set of configs. Used by the OSGi container.
     *
     * @param config the config to add
     */
    synchronized void unbind(EnvironmentConfigurationFactory config) {
        log.debug("Removing environment config {}", config);
    }

    /**
     * Creates a {@link ContentSyncProcess} instance, providing necessary configuration/service references to the
     * constructor.
     *
     * @return the created process instance
     */
    @Override
    protected ContentSyncProcess createProcessDefinitionInstance() {
        Set<EnvironmentConfigurationFactory> staticSet = new TreeSet();

        String filter = "(" + ConfigurationAdmin.SERVICE_FACTORYPID + "=" + pid + ")";
        log.info("factory pid filter is:" + filter);
        Set<EnvironmentConfigurationFactory> configsCopy = this.environmentConfigurations.stream()
                .collect(Collectors.toCollection(TreeSet::new));
        try {
            configurations =  configurationAdmin.listConfigurations(filter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        return new ContentSyncProcess(configsCopy, installService, username, password, configurations);
    }

}
