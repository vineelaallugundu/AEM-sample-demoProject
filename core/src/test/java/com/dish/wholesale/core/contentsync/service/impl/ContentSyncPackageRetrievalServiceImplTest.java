package com.dish.wholesale.core.contentsync.service.impl;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackageId;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ContentSyncPackageRetrievalServiceImplTest {
    @Mock
    Packaging packaging;

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Session session;

    @Mock
    JcrPackageManager jcrPackageManager;

    @Mock
    JcrPackage jcrPackage;

    @Mock
    Node node;

    ContentSyncPackageRetrievalServiceImpl testSubject;

    @BeforeEach
    public void before() throws LoginException, RepositoryException {
        testSubject = spy(new ContentSyncPackageRetrievalServiceImpl(packaging, resourceResolverFactory));
    }

    @Test
    public void findPackage() throws LoginException, RepositoryException, IOException {
        when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(packaging.getPackageManager(session)).thenReturn(jcrPackageManager);
        when(jcrPackageManager.open(any(PackageId.class))).thenReturn(jcrPackage);
        when(jcrPackage.getNode()).thenReturn(node);
        Resource resource = mock(Resource.class);
        InputStream inputStream = mock(InputStream.class);
        PackageId packageId = mock(PackageId.class);

        when(resourceResolver.getResource(any())).thenReturn(resource);
        when(resource.adaptTo(InputStream.class)).thenReturn(inputStream);

        doReturn(packageId).when(testSubject).getId(any());

        testSubject.findPackage(UUID.randomUUID());

        verify(resourceResolverFactory).getServiceResourceResolver(any());
        verify(resourceResolver).adaptTo(Session.class);
        verify(packaging).getPackageManager(session);
        verify(jcrPackageManager).open(packageId);
        verify(jcrPackage).getNode();
        verify(resourceResolver).getResource(any());
        verify(jcrPackage).close();
        verify(resourceResolver).close();
    }

    @Test
    public void getId() {
        UUID uuid = UUID.fromString("0b649b5e-e515-4295-bc9f-01e1c480ee59");
        PackageId actual = testSubject.getId(uuid);

        assertEquals(
                new PackageId("content-sync-tool", uuid.toString(), (String) null),
                actual);

    }

    @Test
    public void deletePackage() throws RepositoryException, LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(packaging.getPackageManager(session)).thenReturn(jcrPackageManager);
        when(jcrPackageManager.open(any(PackageId.class))).thenReturn(jcrPackage);
        when(jcrPackage.getNode()).thenReturn(node);
        PackageId packageId = mock(PackageId.class);
        doReturn(packageId).when(testSubject).getId(any());

        testSubject.deletePackage(UUID.randomUUID());

        verify(resourceResolverFactory).getServiceResourceResolver(any());
        verify(resourceResolver).adaptTo(Session.class);
        verify(packaging).getPackageManager(session);
        verify(jcrPackageManager).open(packageId);
        verify(jcrPackage).getNode();
        verify(node).remove();
        verify(session).save();
        verify(jcrPackage).close();
        verify(resourceResolver).close();
    }
}
