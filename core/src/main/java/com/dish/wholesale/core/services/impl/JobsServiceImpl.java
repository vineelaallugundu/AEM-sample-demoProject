package com.dish.wholesale.core.services.impl;

import com.dish.wholesale.core.services.JobsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = JobsService.class, immediate = true)
@Designate(ocd = JobsServiceImpl.JobsAPIUrl.class)
public class JobsServiceImpl implements JobsService {

    @ObjectClassDefinition(name = "Wholesale Wireless - Jobs API Url",
            description = "OSGi Configuration for Jobs API Url.")
    public @interface JobsAPIUrl {
        @AttributeDefinition(name = "Jobs API URL", description = "Enter api url.")
        public String apiUrl() default "https://jobs.dish.com/api/jobs?tags4=DISH Wireless";
    }

    private String apiUrl;

    @Activate
    protected void activate(JobsAPIUrl jobsAPIUrl) {
        apiUrl = jobsAPIUrl.apiUrl();
    }

    @Override
    public String getApiUrl() {
        return apiUrl;
    }
}
