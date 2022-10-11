package com.dish.wholesale.core.models;

import com.dish.wholesale.core.services.JobsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareersModelTest {

    @InjectMocks
    CareersModel careersModel;

    @Mock
    JobsService jobsService;

    @Test
    void whenGettingJobsApiUrl() {
        when(jobsService.getApiUrl()).thenReturn("https://jobs.dish.com/api/jobs");
        assertEquals(careersModel.getJobsApiUrl(), "https://jobs.dish.com/api/jobs");
    }
}