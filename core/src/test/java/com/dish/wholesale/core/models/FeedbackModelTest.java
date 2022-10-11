package com.dish.wholesale.core.models;

import com.dish.wholesale.core.configs.HcaptchaConfig;
import com.dish.wholesale.core.configs.impl.HcaptchaConfigImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.poi.ss.formula.functions.Columns;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class FeedbackModelTest {

    private final AemContext aemContext = new AemContext();

    private Resource resource;

    private FeedbackModel feedbackModel;

    private HcaptchaConfig hcaptchaConfig;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(ColumnsModel.class);
        aemContext.load().json("/feedback/feedback.json", "/component");
        hcaptchaConfig = aemContext.registerService(new HcaptchaConfigImpl());
    }



    @Test
    void Feedback() {
        resource = aemContext.currentResource("/component/feedback");
        feedbackModel = resource.adaptTo(FeedbackModel.class);
        feedbackModel.getFeedbackText();
        feedbackModel.getFeedbackButtonLabel();
        feedbackModel.getHcaptchaSiteKey();
    }
}