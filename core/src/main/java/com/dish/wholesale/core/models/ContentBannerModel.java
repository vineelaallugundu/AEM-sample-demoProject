package com.dish.wholesale.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, resourceType = { "wholesale/components/content/content-banner" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = { @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class ContentBannerModel {
	
	@Inject
	private String bannerHeader;
	
	@Inject
	private String bannerDesc;
	
	@Inject
	private String image;
	
	@Inject
	private String buttonText;
	
	@Inject
	private String src;

	public String getBannerHeader() {
		return bannerHeader;
	}

	public String getBannerDesc() {
		return bannerDesc;
	}

	public String getImage() {
		return image;
	}

	public String getButtonText() {
		return buttonText;
	}

	public String getSrc() {
		return src;
	}

		

}
