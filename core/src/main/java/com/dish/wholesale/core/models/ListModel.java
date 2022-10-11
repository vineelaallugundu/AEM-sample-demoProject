package com.dish.wholesale.core.models;


import com.dish.wholesale.core.beans.ListItems;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class ListModel.
 */
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, resourceType = {
		"wholesale/components/content/list" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ListModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(List.class);

	/** The request. */
	@Inject
	SlingHttpServletRequest request;

	/** The list items. */
	@Inject
	@Via("resource")
	private List<Resource> listItems;

	/** The items. */
	private List<ListItems> items;

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		populateMultiFieldItems(listItems);
	}

	/**
	 * Populate multi field items.
	 *
	 * @param resourceList the resource list
	 */
	private void populateMultiFieldItems(List<Resource> resourceList) {
		if (null != resourceList && !resourceList.isEmpty()) {
			items = new ArrayList<>();
			for (Resource item : resourceList) {
				if (item != null) {
					ListItems liItems = new ListItems();
					ValueMap vm = item.getValueMap();
					String listTitle = getPropertyValue(vm, "listTitle");
					String listSubText = getPropertyValue(vm, "listSubText");
					String listButtonText = getPropertyValue(vm, "listButtonText");
					String listButtonLink = getPropertyValue(vm, "listButtonLink");
					liItems.setListTitle(listTitle);
					liItems.setListSubText(listSubText);
					liItems.setListButtonText(listButtonText);
					liItems.setListButtonLink(listButtonLink);

					items.add(liItems);

				} else {
					LOG.info("ValueMap not found for resource  : {}", item);
				}
			}
		}
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
	public List<ListItems> getData() {
		List<ListItems> localItems = items;
		return localItems;
	}

}
