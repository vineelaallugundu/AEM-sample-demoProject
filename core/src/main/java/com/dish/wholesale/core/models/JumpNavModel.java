package com.dish.wholesale.core.models;



import java.util.ArrayList;
import java.util.Collections;
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
import com.dish.wholesale.core.beans.JumpNavItems;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, resourceType = { "wholesale/components/content/jump-nav" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class JumpNavModel {

	private static final Logger LOG = LoggerFactory.getLogger(JumpNavModel.class);

	@Inject
	SlingHttpServletRequest request;
	
    @Inject
    @Via("resource")
    private List<Resource> menulinks;

	private List<JumpNavItems> items = Collections.emptyList();
	
	@PostConstruct
    protected void init() {
		populateMultiFieldItems(menulinks);
	}
	
	private void populateMultiFieldItems(List<Resource> resourceList) {
		 if (null != resourceList && !resourceList.isEmpty()) {
			 items = new ArrayList<>();
	            for (Resource item : resourceList) {
	                if (item != null) {
	                	JumpNavItems jumpNavItems = new JumpNavItems();
	                    ValueMap vm = item.getValueMap();
	                    String label = getPropertyValue(vm, "label");
	                    String id = getPropertyValue(vm, "id");
	                    jumpNavItems.setId(id);
	                    jumpNavItems.setLabel(label);               
	                    items.add(jumpNavItems);
	                     
	                } else {
	                	LOG.info("ValueMap not found for resource  : {}", item);
	                }
	            }
	        }		
	}
	
	private String getPropertyValue(final ValueMap properties, final String propertyName) {
        return properties.containsKey(propertyName) ? properties.get(propertyName, String.class) : StringUtils.EMPTY;
    }

	public List<JumpNavItems> getData() {
		 return items;
	}
	
}

