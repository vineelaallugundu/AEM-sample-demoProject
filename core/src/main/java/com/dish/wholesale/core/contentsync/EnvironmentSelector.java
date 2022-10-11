package com.dish.wholesale.core.contentsync;

import com.adobe.acs.commons.mcp.form.SelectComponent;
import com.dish.wholesale.core.contentsync.service.EnvironmentConfiguration;
import com.dish.wholesale.core.contentsync.service.impl.EnvironmentConfigurationFactory;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A custom field type that displays a select driven by available {@link EnvironmentConfiguration} services in the AEM
 * instance. The environment name ({@link EnvironmentConfiguration#getEnvName()}) is used as both the name and value for
 * the select.
 */
@Slf4j
public class EnvironmentSelector extends SelectComponent {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentSelector.class);
	
	
    @Override
    public Map<String, String> getOptions() {
        Map<String, String> optionsMap;
        SlingScriptHelper helper = getHelper();
        EnvironmentConfigurationFactory[] environmentConfigs = getEnvironmentConfigs(helper);

        List<EnvironmentConfigurationFactory> envConfigList = Optional.ofNullable(environmentConfigs)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toList());

        log.trace("Environment configs ({}): {}", envConfigList.size(), envConfigList);

        optionsMap =  getOptionsFromConfigs(envConfigList);
        log.trace("options map is:" + optionsMap);
        return optionsMap;
    }

    /**
     * Retrieves an array of {@link EnvironmentConfiguration} services by using {@link SlingScriptHelper} provided by
     * {@link #getHelper()}.
     *
     * @param helper the sling script helper
     * @return an array of services, or null
     */
    EnvironmentConfigurationFactory[] getEnvironmentConfigs(@NotNull SlingScriptHelper helper) {
        return helper.getServices(EnvironmentConfigurationFactory.class, null);
    }

    /**
     * Converts the provided list of {@link EnvironmentConfiguration}s into a {@link Map}. The map will contain each
     * configs' {@link EnvironmentConfiguration#getEnvName()} as both the key and value for its map entry. Duplicates
     * will cause an
     *
     * @param environmentConfigs the list of environment configs.
     * @return the mapping
     *
     * @throws IllegalArgumentException if an env config with duplicate names are found
     */
    Map<String, String> getOptionsFromConfigs(@NotNull List<EnvironmentConfigurationFactory> environmentConfigs) {
        if (environmentConfigs.isEmpty()) {
            throw new IllegalArgumentException("No environment configurations exist");
        }

        return environmentConfigs.stream()
                .collect(Collectors.toMap(
                        EnvironmentConfigurationFactory::getEnvName,
                        EnvironmentConfigurationFactory::getEnvName,
                        (s1, s2) -> {
                            throw new IllegalArgumentException(
                                    "Duplicate environment name detected in configuration");
                        },
                        LinkedHashMap::new
                ));
    }
}
