package com.dish.wholesale.core.configs.impl;

import com.dish.wholesale.core.configs.LaunchScriptConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = LaunchScriptConfig.class, immediate = true)
@Designate(ocd = LaunchScriptConfigImpl.Config.class, factory = false)
@ServiceDescription("A service used to configure Launch script")
@ServiceVendor("Dish Wholesale")
public class LaunchScriptConfigImpl implements LaunchScriptConfig {
	
	@SuppressWarnings("PMD.DefaultPackage")
    String launchScript;

    /**
     * Set launch script configuration
     * @param config
     */
    @Activate
    public void activate(final Config config) {
        launchScript = config.launchScript();
    }

    @ObjectClassDefinition(
            name = "Wholesale Adobe Launch Script",
            description = "The configuration for the adobe launch script added to the page.")
    public @interface Config {

        @AttributeDefinition(name = "Adobe Launch Script", description = "Adobe Launch Script")
        String launchScript() default "";
    }

	@Override
	public String getLaunchScript() {
		return launchScript;
	}
}
