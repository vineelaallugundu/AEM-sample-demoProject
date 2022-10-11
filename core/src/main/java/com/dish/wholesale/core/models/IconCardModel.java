package com.dish.wholesale.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

/**
 * The Class IconCardModel.
 */
@Model(adaptables = Resource.class, resourceType = {
		"wholesale/components/content/icon-card" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
		@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class IconCardModel {

	/** The iconCard title. */
	@Inject
	private String iconCardTitle;

	/** The iconCard description. */
	@Inject
	private String iconCardDescription;

	/** The icon card image. */
	@Inject
	private String iconCardImage;

	/** The icon card image alt text. */
	@Inject
	private String iconCardImageAltText;

	/**
	 * Gets the icon card title.
	 *
	 * @return the icon card title
	 */
	public String getIconCardTitle() {
		return iconCardTitle;
	}

	/**
	 * Gets the icon card description.
	 *
	 * @return the icon card description
	 */
	public String getIconCardDescription() {
		return iconCardDescription;
	}

	/**
	 * Gets the icon card image.
	 *
	 * @return the icon card image
	 */
	public String getIconCardImage() {
		return iconCardImage;
	}

	/**
	 * Gets the icon card image alt text.
	 *
	 * @return the icon card image alt text
	 */
	public String getIconCardImageAltText() {
		return iconCardImageAltText;
	}

}
