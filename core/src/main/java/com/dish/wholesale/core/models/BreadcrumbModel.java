package com.dish.wholesale.core.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, resourceType = { "wholesale/components/content/breadcrumb" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {
	
	@Inject
	private Page currentPage;
	
	private List<Page> navList = new ArrayList<>();
	
	@PostConstruct
    protected void init() {
		setBreadCrumbItems();
	}
	
	private void setBreadCrumbItems()
    {
        long level = 4L;
        int currentLevel = currentPage.getDepth();
        while (level < currentLevel)
        {
			Page trailPage = currentPage.getAbsoluteParent((int) level);
			if (trailPage == null)
			{
				break;
			}
			this.navList.add(trailPage);
			level++;
        }
    }

	public List<Page> getData() {
		return navList;
	}
	
}
