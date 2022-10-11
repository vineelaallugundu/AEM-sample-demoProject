package com.dish.wholesale.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

@Model(adaptables = Resource.class, resourceType = { "wholesale/components/content/Banner" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = { @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class BannerModel {
	
	@Inject
	private String srcOne;

	@Inject
	private String bannerTextOne;
		
	@Inject
	private String bannerHeaderOne;

	@Inject
	private String buttonTextOne;
	
	@Inject
	private String bannerOne;

	public String getBannerOne()
	{
		return bannerOne;
	}
	
	public String getSrcOne() {
		return srcOne;
	}

	public String getBannerTextOne() {
		return bannerTextOne;
	}

	public String getBannerHeaderOne() {
		return bannerHeaderOne;
	}

	public String getButtonTextOne() {
		return buttonTextOne;
	}

}
