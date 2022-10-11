package com.dish.wholesale.core.servlets;

import com.dish.wholesale.core.models.CareersModel;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobSearchServletTest {

    @InjectMocks
    private JobSearchServlet jobSearchServlet;

    @Mock
    private SlingHttpServletRequest mockSlingRequest;

    @Mock
    private SlingHttpServletResponse mockSlingResponse;

    @Mock
    CareersModel careersModel;

    @Test
    void getJobsAPIResponseErrorNull() throws IOException {
        boolean thrown = false;
        try {
            jobSearchServlet.doGet(mockSlingRequest, mockSlingResponse);
        } catch (NullPointerException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void getJobsAPIResponseErrorHttpException() throws IOException {
        boolean thrown = false;
        try {
            when(mockSlingRequest.adaptTo(CareersModel.class)).thenReturn(careersModel);
            when(mockSlingRequest.getParameter("query")).thenReturn("World");
            when(careersModel.getJobsApiUrl()).thenReturn("http://www.dish.com/job");
            jobSearchServlet.doGet(mockSlingRequest, mockSlingResponse);
        } catch (UncheckedExecutionException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}