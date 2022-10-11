package com.dish.wholesale.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dish.wholesale.core.beans.FooterItems;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, resourceType = {
"wholesale/components/structure/footer" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModel {

	private static final Logger LOG = LoggerFactory.getLogger(FooterModel.class);

	@Inject
	SlingHttpServletRequest request;

	@Inject
	@Via("resource")
	private List<Resource> footerItems;

	private List<FooterItems> items;

	@PostConstruct
	protected void init() {
		populateMultiFieldItems(footerItems);
	}

	private void populateMultiFieldItems(List<Resource> resourceList) {
		if (null != resourceList && !resourceList.isEmpty()) {
			items = new ArrayList<>();
			for (Resource item : resourceList) {
				if (item != null) {
					FooterItems headerItems = new FooterItems();
					ValueMap vm = item.getValueMap();
					String pageLabel = getPropertyValue(vm, "pageLabel");
					String pageUrl = getPropertyValue(vm, "pageUrl");
					headerItems.setPageLabel(pageLabel);
					headerItems.setPageUrl(pageUrl);
					items.add(headerItems);

				} else {
					LOG.info("ValueMap not found for resource  : {}", item);
				}
			}
		}
	}

	private String getPropertyValue(final ValueMap properties, final String propertyName) {
		return properties.containsKey(propertyName) ? properties.get(propertyName, String.class) : StringUtils.EMPTY;
	}

	public List<FooterItems> getData() {
		return items;
	}
}
