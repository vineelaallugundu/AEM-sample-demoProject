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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dish.wholesale.core.beans.AccordionItems;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, resourceType = { "wholesale/components/content/accordion" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionModel {

	private static final Logger LOG = LoggerFactory.getLogger(AccordionModel.class);

	@Inject
	SlingHttpServletRequest request;
	
    @Inject
    @Via("resource")
    private List<Resource> accordionItems;

	private List<AccordionItems> items = Collections.emptyList();
	
	@PostConstruct
    protected void init() {
		populateMultiFieldItems(accordionItems);
	}
	
	private void populateMultiFieldItems(List<Resource> resourceList) {
		 if (null != resourceList && !resourceList.isEmpty()) {
			 items = new ArrayList<>();
	            for (Resource item : resourceList) {
	                if (item != null) {
	                	AccordionItems accItems = new AccordionItems();
	                    ValueMap vm = item.getValueMap();
	                    String title = getPropertyValue(vm, "title");
	                    String description = getPropertyValue(vm, "description");
	                    accItems.setTitle(title);
	                    accItems.setDescription(description);                  
	                    items.add(accItems);
	                     
	                } else {
	                	LOG.info("ValueMap not found for resource  : {}", item);
	                }
	            }
	        }		
	}
	
	private String getPropertyValue(final ValueMap properties, final String propertyName) {
        return properties.containsKey(propertyName) ? properties.get(propertyName, String.class) : StringUtils.EMPTY;
    }

	public List<AccordionItems> getData() {
		List<AccordionItems> localItems;
		localItems = items;
		return localItems;
	}
	
}

