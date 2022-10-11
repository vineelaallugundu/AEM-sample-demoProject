package com.dish.wholesale.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.dish.wholesale.core.configs.HcaptchaConfig;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;

/**
 * The Class FeedbackModel.
 */
@Model(adaptables = Resource.class, resourceType = {
		"wholesale/components/content/feedback" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
		@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class FeedbackModel {

	/** The feedback text. */
	@Inject
	private String feedbackText;

	/** The feedback button label. */
	@Inject
	private String feedbackButtonLabel;
	
	private String hcaptchaSiteKey;

	@OSGiService
	private HcaptchaConfig hcaptchaConfig;
	
	@PostConstruct
    protected void init() {
		this.hcaptchaSiteKey = hcaptchaConfig.getHcaptchaSiteKey();
	}
	
	/**
	 * Gets the feedback text.
	 *
	 * @return the feedback text
	 */
	public String getFeedbackText() {
		return feedbackText;
	}

	/**
	 * Gets the feedback button label.
	 *
	 * @return the feedback button label
	 */
	public String getFeedbackButtonLabel() {
		return feedbackButtonLabel;
	}

	public String getHcaptchaSiteKey() {
		return hcaptchaSiteKey;
	}


}
