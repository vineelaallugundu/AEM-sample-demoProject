package com.dish.wholesale.core.servlets;

import com.dish.wholesale.core.models.CareersModel;
import org.apache.commons.httpclient.HttpException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareersPageServletTest {

    @InjectMocks
    private CareersPageServlet careersPageServlet;

    @Mock
    private SlingHttpServletRequest mockSlingRequest;

    @Mock
    private SlingHttpServletResponse mockSlingResponse;

    @Mock
    CareersModel careersModel;

    @Mock
    PrintWriter printWriter;

    @Disabled
    @Test
    void getJobsResponse() throws IOException {
        when(mockSlingRequest.adaptTo(CareersModel.class)).thenReturn(careersModel);
        when(mockSlingResponse.getWriter()).thenReturn(printWriter);
        when(careersModel.getJobsApiUrl()).thenReturn("https://api.staging.jibe.com/v1/jobs/find?tags4=DISH+Wireless");
        careersPageServlet.doGet(mockSlingRequest, mockSlingResponse);
        assertEquals(0, mockSlingResponse.getStatus());
    }

    @Test
    void getJobsAPIResponseErrorNull() throws IOException {
        boolean thrown = false;
        try {
            when(mockSlingRequest.adaptTo(CareersModel.class)).thenReturn(careersModel);
            when(careersModel.getJobsApiUrl()).thenReturn("http://www.dish.com");
            careersPageServlet.doGet(mockSlingRequest, mockSlingResponse);
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
            when(careersModel.getJobsApiUrl()).thenReturn("http://www.dish.com/job");
            careersPageServlet.doGet(mockSlingRequest, mockSlingResponse);
        } catch (HttpException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Disabled
    @Test
    void getJobsAPIJSONExceptionError() throws IOException {
        when(mockSlingRequest.adaptTo(CareersModel.class)).thenReturn(careersModel);
        when(mockSlingResponse.getWriter()).thenReturn(printWriter);
        when(careersModel.getJobsApiUrl()).thenReturn("https://api.staging.jibe.com/v1/jobs/find?limit=0&tags4=DISH+Wireless");
        careersPageServlet.doGet(mockSlingRequest, mockSlingResponse);
        assertNotNull(careersPageServlet);
    }
}