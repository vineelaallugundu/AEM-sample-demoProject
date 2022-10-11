package com.dish.wholesale.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, resourceType = { "wholesale/components/content/navigation" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = { @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class NavigationModel {
	
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String url;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String loginpageurl;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String node;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String iconpath;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String iconpageurl;

    @SlingObject
    private ResourceResolver resourceResolver;

    private Page parentPage;

    @PostConstruct
    protected void init() {
        if (url != null) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            parentPage = pageManager.getPage(url);           
        }
    }

    public Page getParentPage() {
        return parentPage;
    }
    public String getUrl() {
        return url;
    }
    public String getLoginPageUrl() {
        return loginpageurl;
    }
    public String getNode() {
        return node;
    }
	public String getIconPath() {
		return iconpath;
	}
	public String getIconPageUrl() {
		return iconpageurl;
	}

}
