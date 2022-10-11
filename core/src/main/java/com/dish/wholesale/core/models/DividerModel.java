package com.dish.wholesale.core.models;

import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

/**
 * The Class DividerModel.
 */
@Model(adaptables = Resource.class, resourceType = {
		"wholesale/components/content/divider" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
		@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class DividerModel {

	/** The divider style. */
	@Inject
	private String thickness;

	/** The divider label. */
	@Inject
	private String margin;

	/**
	 * Gets the divider thickness.
	 *
	 * @return the thickness
	 */
	public String getThickness() {
		return thickness;
	}

	/**
	 * Gets the divider margin.
	 *
	 * @return the margin
	 */
	public String getMargin() {
		return margin;
	}
}
