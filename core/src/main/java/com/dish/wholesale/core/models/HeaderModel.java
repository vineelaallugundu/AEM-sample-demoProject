package com.dish.wholesale.core.models;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dish.wholesale.core.beans.HeaderItems;

/**
 * The Class HeaderModel.
 */
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, resourceType = {
		"wholesale/components/structure/header" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(HeaderModel.class);

	/** The request. */
	@SlingObject
	SlingHttpServletRequest request;

	/** The header items. */
	@Inject
	@Via("resource")
	private List<Resource> header;

	/** The items. */
	private Map<String, List<HeaderItems>> items;

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		populateMultiFieldItems(request);
	}

	/**
	 * Populate multi field items.
	 *
	 * @param resourceList the resource list
	 */
	private void populateMultiFieldItems(SlingHttpServletRequest resourceList) {
		if (null != resourceList) {
			Resource resource = resourceList.getResource();
			if (null != resource) {
				items = new HashMap<>();

				if (null != resource.getChild("headerItems")) {
					Resource primaryHeaderResource = resource.getChild("headerItems");
					List<HeaderItems> headerItemsList = populateMap(primaryHeaderResource);
					items.put(primaryHeaderResource.getName(), headerItemsList);
				}

				if (null != resource.getChild("secondaryHeaderItems")) {
					Resource secondaryHeaderResource = resource.getChild("secondaryHeaderItems");
					List<HeaderItems> headerItemsList2 = populateMap(secondaryHeaderResource);
					items.put(secondaryHeaderResource.getName(), headerItemsList2);
				}
			}
		}
	}

	private List<HeaderItems> populateMap(Resource headerResource) {
		List<HeaderItems> headeritems = new ArrayList<>();
		for (Resource item : headerResource.getChildren()) {
			if (item != null) {
				HeaderItems headItems = new HeaderItems();
				ValueMap vm = item.getValueMap();
				String pageLabel = getPropertyValue(vm, "pageLabel");
				String pageUrl = getPropertyValue(vm, "pageUrl");
				headItems.setPageLabel(pageLabel);
				headItems.setPageUrl(pageUrl);
				headeritems.add(headItems);
			} else {
				LOG.info("ValueMap not found for resource  : {}", item);
			}
		}
		return headeritems;
	}

	/**
	 * Gets the property value.
	 *
	 * @param properties   the properties
	 * @param propertyName the property name
	 * @return the property value
	 */
	private String getPropertyValue(final ValueMap properties, final String propertyName) {
		return properties.containsKey(propertyName) ? properties.get(propertyName, String.class) : StringUtils.EMPTY;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public Map<String, List<HeaderItems>> getData() {
		return items;
	}
}
