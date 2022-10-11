package com.dish.wholesale.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, resourceType = { "wholesale/components/content/hero" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = { @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class HeroModel {
	
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String image;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String alt;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String heroTitle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String heroHeader;
    
	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}

	public String getHeroTitle() {
		return heroTitle;
	}

	public void setHeroTitle(String heroTitle) {
		this.heroTitle = heroTitle;
	}

	public String getHeroHeader() {
		return heroHeader;
	}

	public void setHeroHeader(String heroHeader) {
		this.heroHeader = heroHeader;
	}
	
}
