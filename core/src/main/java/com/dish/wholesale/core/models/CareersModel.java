package com.dish.wholesale.core.models;

import com.dish.wholesale.core.services.JobsService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = { SlingHttpServletRequest.class }, resourceType = {
        "wholesale/components/content/jobs" })
@Exporter(name = "jackson", extensions = "json")
public class CareersModel {

    @OSGiService
    JobsService jobsService;

    public String getJobsApiUrl() {
       return jobsService.getApiUrl();
    }
}


