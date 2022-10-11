package com.dish.wholesale.core.services.impl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JobsServiceImplTest {

    @InjectMocks
    JobsServiceImpl jobsService;

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Test
    public void testAPIUrlActivate() {
        String jobsAPIURL = "https://jobs.dish.com/api/jobs";
        jobsService = aemContext.registerService(new JobsServiceImpl());
        JobsServiceImpl.JobsAPIUrl api =  mock(JobsServiceImpl.JobsAPIUrl.class);
        when(api.apiUrl()).thenReturn(jobsAPIURL);
        jobsService.activate(api);
    }

    @Test
    public void testAPIUrl() {
        String jobsAPIURL = "https://jobs.dish.com/api/jobs";
        jobsService = aemContext.registerService(new JobsServiceImpl());
        JobsServiceImpl.JobsAPIUrl api =  mock(JobsServiceImpl.JobsAPIUrl.class);
        when(api.apiUrl()).thenReturn(jobsAPIURL);
        jobsService.activate(api);
        assertEquals(jobsAPIURL, jobsService.getApiUrl());
    }
}