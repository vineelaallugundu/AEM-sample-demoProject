package com.dish.wholesale.core.configs.impl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class HcaptchaConfigImplTest{

    String hcaptchaSiteKey = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxx";
	
	String hcaptchaSecretKey = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxx";

    AemContext aemContext = new AemContext();
    HcaptchaConfigImpl hcaptchaConfig;


    @BeforeEach
    void setUp() {
        hcaptchaConfig=aemContext.registerService(new HcaptchaConfigImpl());
        HcaptchaConfigImpl.Config config=mock(HcaptchaConfigImpl.Config.class);
        when(config.hcaptchaSiteKey()).thenReturn(hcaptchaSiteKey);
        when(config.hcaptchaSecretKey()).thenReturn(hcaptchaSecretKey);
        hcaptchaConfig.activate(config);
    }


    @Test
    void getLaunchScript() {

        assertEquals("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxx", hcaptchaConfig.getHcaptchaSiteKey());
        assertEquals("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxx", hcaptchaConfig.getHcaptchaSecretKey());
    }

}
