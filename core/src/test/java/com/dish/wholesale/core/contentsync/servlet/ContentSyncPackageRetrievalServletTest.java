package com.dish.wholesale.core.contentsync.servlet;

import com.dish.wholesale.core.contentsync.service.ContentSyncPackageRetrievalService;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ContentSyncPackageRetrievalServletTest {
    @Mock
    ContentSyncPackageRetrievalService retrievalService;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    SlingHttpServletResponse response;

    @Mock
    ServletOutputStream outputStream;

    @Mock
    InputStream inputStream;

    ContentSyncPackageRetrievalServlet testSubject;

    @BeforeEach
    public void before() throws Exception {
        testSubject = spy(new ContentSyncPackageRetrievalServlet(retrievalService));
    }

    @Test
    public void doPost_authorized() throws Exception {
        when(response.getOutputStream()).thenReturn(outputStream);
        when(retrievalService.findPackage(any())).thenReturn(inputStream);

        doNothing().when(testSubject).copy(any(), any());
        doReturn(UUID.randomUUID()).when(testSubject).uuid(any());

        testSubject.doPost(request, response);

        verify(testSubject).uuid(request);
        verify(retrievalService).findPackage(any());
        verify(response).getOutputStream();
        verify(inputStream).close();
        verify(retrievalService).deletePackage(any());
    }

    @Test
    public void uuid() {
        when(request.getParameter(any())).thenReturn(UUID.randomUUID().toString());

        testSubject.uuid(request);

        verify(request).getParameter("uuid");
    }

    @Test
    public void uuid_null() {
        when(request.getParameter(any())).thenReturn(null);

        try {
            testSubject.uuid(request);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }

        verify(request).getParameter("uuid");
    }

    @Test
    public void uuid_notValid() {
        when(request.getParameter(any())).thenReturn("not-a-uuid");

        try {
            testSubject.uuid(request);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }

        verify(request).getParameter("uuid");
    }
}
