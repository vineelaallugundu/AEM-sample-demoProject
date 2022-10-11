package com.dish.wholesale.core.models;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * The Class UtilityModel.
 */
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, resourceType = {
		"wholesale/components/structure/Utility" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UtilityModel {
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
}
