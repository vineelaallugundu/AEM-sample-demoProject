package com.dish.wholesale.core.models;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;

/**
 * The Class ContainerCardModel.
 */
@Model(adaptables = Resource.class, resourceType = {
		"wholesale/components/content/container-card" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
		@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class ContainerCardModel {

	public static final String LINEAR_GRADIENT = "linear-gradient(";
	/** The container bg color primary. */
	@Inject
	private String containerBgColorPrimary;

	/** The container bg color secondary. */
	@Inject
	private String containerBgColorSecondary;

	/** The container bg image. */
	@Inject
	private String containerBgImage;

	@Inject @Default(values = "to bottom right")
	private String bgGradientDirection;

	@Inject @Default(values = "cover")
	private String bgSize;

	@Inject @Default(values = "center")
	private String bgPosition;

	/** The guid. */
	private String guid;

	/**
	 * Gets the guid.
	 *
	 * @return the guid
	 */
	public String getGuid() {
		guid = java.util.UUID.randomUUID().toString();
		return guid;
	}

	/**
	 * Gets the container bg color primary.
	 *
	 * @return the container bg color primary
	 */
	public String getContainerBgColorPrimary() {
		return containerBgColorPrimary;
	}

	/**
	 * Gets the container bg color secondary.
	 *
	 * @return the container bg color secondary
	 */
	public String getContainerBgColorSecondary() {
		return containerBgColorSecondary;
	}

	/**
	 * Gets the container bg image.
	 *
	 * @return the container bg image
	 */
	public String getContainerBgImage() {
		return containerBgImage;
	}

	public String getBgGradientDirection() {
		return bgGradientDirection;
	}

	public String getBgSize() {
		return bgSize;
	}

	public String getBgPosition() {
		return bgPosition;
	}

	/**
	 * Gets the background css.
	 *
	 * @return the background css
	 */
	public String getBackgroundCss() {
		if (StringUtils.isEmpty(getContainerBgColorSecondary()) && StringUtils.isEmpty(getContainerBgImage())
				&& StringUtils.isNotEmpty(getContainerBgColorPrimary())) {
			return LINEAR_GRADIENT + getBgGradientDirection() + "," + getContainerBgColorPrimary() + ","
					+ getContainerBgColorPrimary() + ")";
		} else if (StringUtils.isEmpty(getContainerBgColorSecondary())
				&& StringUtils.isNotEmpty(getContainerBgColorPrimary())
				&& StringUtils.isNotEmpty(getContainerBgImage())) {
			return LINEAR_GRADIENT + getBgGradientDirection() + "," + getContainerBgColorPrimary() + ","
					+ getContainerBgColorPrimary() + ")," + "url(" + getContainerBgImage() + ")";
		} else if (StringUtils.isEmpty(getContainerBgColorPrimary())
				&& StringUtils.isNotEmpty(getContainerBgColorSecondary())
				&& StringUtils.isNotEmpty(getContainerBgImage())) {
			return LINEAR_GRADIENT + getBgGradientDirection() + "," + getContainerBgColorSecondary() + ","
					+ getContainerBgColorSecondary() + ")," + "url(" + getContainerBgImage() + ")";
		} else if (StringUtils.isNotEmpty(getContainerBgColorSecondary())
				&& StringUtils.isNotEmpty(getContainerBgColorPrimary())
				&& StringUtils.isNotEmpty(getContainerBgImage())) {
			return LINEAR_GRADIENT + getBgGradientDirection() + "," + getContainerBgColorPrimary() + ","
					+ getContainerBgColorSecondary() + ")," + "url(" + getContainerBgImage() + ")";
		} else if (StringUtils.isNotEmpty(getContainerBgColorSecondary())
				&& StringUtils.isNotEmpty(getContainerBgColorPrimary()) && StringUtils.isEmpty(getContainerBgImage())) {
			return LINEAR_GRADIENT + getBgGradientDirection() + "," + getContainerBgColorPrimary() + ","
					+ getContainerBgColorSecondary() + ")";
		} else if (StringUtils.isEmpty(getContainerBgColorSecondary())
				&& StringUtils.isEmpty(getContainerBgColorPrimary()) && StringUtils.isNotEmpty(getContainerBgImage())) {
			return "url(" + getContainerBgImage() + ")";
		} else {
			return "inherit";
		}
	}

}
