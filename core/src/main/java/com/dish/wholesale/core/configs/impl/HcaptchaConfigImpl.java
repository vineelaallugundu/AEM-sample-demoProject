package com.dish.wholesale.core.configs.impl;

import com.dish.wholesale.core.configs.HcaptchaConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = HcaptchaConfig.class, immediate = true)
@Designate(ocd = HcaptchaConfigImpl.Config.class, factory = false)
@ServiceDescription("A service used to configure Hcaptcha keys")
@ServiceVendor("Dish Wholesale")
public class HcaptchaConfigImpl implements HcaptchaConfig {

    private static final Logger log = LoggerFactory.getLogger(HcaptchaConfigImpl.class);

    String hcaptchaSiteKey;
	
	String hcaptchaSecretKey;

    @Activate
    public void activate(final Config config) {
        try {
            hcaptchaSiteKey = config.hcaptchaSiteKey();
            log.info("hcaptchaSiteKey -> " + hcaptchaSiteKey);
            hcaptchaSecretKey = config.hcaptchaSecretKey();
            log.info("hcaptchaSecretKey -> " + hcaptchaSecretKey);
        } catch (Exception e) {
            log.error("Exception in Activate Method of HcaptchaConfigImpl", e);
        }
    }

    @ObjectClassDefinition(
            name = "Hcaptcha Config for keys",
            description = "The configuration for the Hcaptcha keys")
    public @interface Config {

        @AttributeDefinition(name = "Hcaptcha Site Key", description = "Hcaptcha Site Key")
        String hcaptchaSiteKey() default "";
        
        @AttributeDefinition(name = "Hcaptcha Secret Key", description = "Hcaptcha Secret Key")
        String hcaptchaSecretKey() default "";
    }


	@Override
	public String getHcaptchaSiteKey() {
		return hcaptchaSiteKey;
	}

	@Override
	public String getHcaptchaSecretKey() {
		return hcaptchaSecretKey;
	}
	
}
