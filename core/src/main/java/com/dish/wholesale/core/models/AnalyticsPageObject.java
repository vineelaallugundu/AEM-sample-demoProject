package com.dish.wholesale.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.dish.wholesale.core.configs.LaunchScriptConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.inject.Inject;
import java.util.*;

/**
 * The Class AnalyticsPageObject.
 */
@Model(adaptables = {
		SlingHttpServletRequest.class }, resourceType = "", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AnalyticsPageObject {

	/** The Constant SLASH. */
	public static final String SLASH = "/";

	/** The resource. */
	@SlingObject
	private Resource resource;

	/** The request. */
	@Self
	private SlingHttpServletRequest request;

	/** The current page. */
	@Inject
	public Page currentPage;

	/** The launch script config. */
	@OSGiService
	private LaunchScriptConfig launchScriptConfig;

	/**
	 * Gets the page name.
	 *
	 * @return the page name
	 */
	public String getPageName() {
		return StringUtils.isNotBlank(getCurrentPage().getName()) ? getCurrentPage().getName()
				: getPageTitle(getCurrentPage());
	}

	/**
	 * Gets the site section.
	 *
	 * @return the site section
	 */
	public String getSiteSection() {
		return getSubSection(1);
	}

	/**
	 * Gets the launch url.
	 *
	 * @return the launch url
	 */
	public String getLaunchUrl() {
		return launchScriptConfig.getLaunchScript();
	}

	/**
	 * Gets the page path.
	 *
	 * @return the page path
	 */
	private String getPagePath() {
		return getCurrentPage().getPath();
	}

	/**
	 * Gets the current page.
	 *
	 * @return the current page
	 */
	private Page getCurrentPage() {
		return Optional.ofNullable(currentPage).orElseGet(() -> pageManager().getContainingPage(resource));
	}

	/**
	 * Page manager.
	 *
	 * @return the page manager
	 */
	private PageManager pageManager() {
		return resource.getResourceResolver().adaptTo(PageManager.class);
	}

	/**
	 * Gets the sub section.
	 *
	 * @param pageLevel the page level
	 * @return the sub section
	 */
	private String getSubSection(final int pageLevel) {
		String pageTitle = StringUtils.EMPTY;
		final String language = SLASH + getLanguage();
		if (pageLevel > 0) {
			final String path = StringUtils.substringAfter(getPagePath(), language);
			final String separator = path.split(SLASH).length > pageLevel ? path.split(SLASH)[pageLevel] : "";
			final String pagePath = StringUtils.substringBefore(getPagePath(), separator).concat(separator);
			pageTitle = getPageTitle(pagePath);
		}
		return pageTitle;
	}

	/**
	 * Gets the page title.
	 *
	 * @param page the page
	 * @return the page title
	 */
	private String getPageTitle(Page page) {
		return StringUtils.isNotBlank(page.getTitle()) ? page.getTitle() : "";
	}

	/**
	 * Gets the page title.
	 *
	 * @param pagePath the page path
	 * @return the page title
	 */
	private String getPageTitle(final String pagePath) {
		return Optional.of(pageManager()).map(pageManager -> pageManager.getPage(pagePath)).map(this::getPageTitle)
				.orElse("");
	}

	/**
	 * Gets the domain.
	 *
	 * @return the domain
	 */
	public String getDomain() {
		return request.getServerName();
	}

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return getCurrentPage().getLanguage().getLanguage();
	}

}
