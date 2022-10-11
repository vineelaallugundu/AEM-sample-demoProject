package com.dish.wholesale.core.contentsync.service.impl;

import com.day.cq.replication.Replicator;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.vault.packaging.*;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import javax.jcr.Node;
import javax.jcr.Session;
import java.io.InputStream;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ContentSyncPackageInstallServiceImplTest {
    @Mock
    Packaging packaging;

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    Replicator replicator;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Session session;

    @Mock
    JcrPackageManager jcrPackageManager;

    @Mock
    JcrPackage jcrPackage;

    @Mock
    JcrPackageDefinition jcrPackageDefinition;

    @Mock
    Node node;

    @Mock
    InputStream inputStream;

    UUID uuid = UUID.randomUUID();

    ContentSyncPackageInstallServiceImpl testSubject;

    @Test
    public void upload() throws Exception {
        testSubject = spy(new ContentSyncPackageInstallServiceImpl(packaging, resourceResolverFactory, replicator));
        when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(packaging.getPackageManager(session)).thenReturn(jcrPackageManager);

        when(jcrPackageManager.upload(inputStream, true)).thenReturn(jcrPackage);

        when(jcrPackage.getDefinition()).thenReturn(jcrPackageDefinition);
        testSubject.uploadPackage(inputStream);

        verify(resourceResolverFactory).getServiceResourceResolver(any());
        verify(resourceResolver).adaptTo(Session.class);
        verify(packaging).getPackageManager(session);
        verify(jcrPackageManager).upload(inputStream, true);
        verify(jcrPackage).close();
        verify(resourceResolver).close();
    }

    @Test
    public void installPackage_throws_noInstallNoSave() throws Exception {
        testSubject = spy(new ContentSyncPackageInstallServiceImpl(packaging, resourceResolverFactory, replicator));
        try {
            testSubject.installPackage(uuid, false, false, false);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void installPackage_throws_noInstallReplicate() throws Exception {
        testSubject = spy(new ContentSyncPackageInstallServiceImpl(packaging, resourceResolverFactory, replicator));

        try {
            testSubject.installPackage(uuid, false, true, false);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void installPackage_installNoReplicateNoSave() throws Exception {
        testSubject = spy(new ContentSyncPackageInstallServiceImpl(packaging, resourceResolverFactory, replicator));
        when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(packaging.getPackageManager(session)).thenReturn(jcrPackageManager);
        when(jcrPackageManager.open(any(PackageId.class))).thenReturn(jcrPackage);

        when(jcrPackage.getNode()).thenReturn(node);

        testSubject.installPackage(uuid, true, false, false);

        verify(resourceResolverFactory).getServiceResourceResolver(any());
        verify(resourceResolver).adaptTo(Session.class);
        verify(packaging).getPackageManager(session);
        verify(jcrPackage).getNode();
        verify(jcrPackage).install(any());
        verify(replicator, never()).replicate(any(), any(), any());
        verify(node).remove();
        verify(session).save();
        verify(jcrPackage).close();
        verify(resourceResolver).close();
    }

    @Test
    public void installPackage_installReplicateNoSave() throws Exception {
        testSubject = spy(new ContentSyncPackageInstallServiceImpl(packaging, resourceResolverFactory, replicator));
        when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(packaging.getPackageManager(session)).thenReturn(jcrPackageManager);
        when(jcrPackageManager.open(any(PackageId.class))).thenReturn(jcrPackage);

        when(jcrPackage.getNode()).thenReturn(node);

        testSubject.installPackage(uuid, true, true, false);

        verify(resourceResolverFactory).getServiceResourceResolver(any());
        verify(resourceResolver).adaptTo(Session.class);
        verify(packaging).getPackageManager(session);
        verify(jcrPackage).getNode();
        verify(jcrPackage).install(any());
        verify(replicator).replicate(any(), any(), any());
        verify(node).remove();
        verify(session).save();
        verify(jcrPackage).close();
        verify(resourceResolver).close();
    }

    @Test
    public void installPackage_noInstallNoReplicateSave() throws Exception {
        testSubject = spy(new ContentSyncPackageInstallServiceImpl(packaging, resourceResolverFactory, replicator));
        when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(packaging.getPackageManager(session)).thenReturn(jcrPackageManager);
        when(jcrPackageManager.open(any(PackageId.class))).thenReturn(jcrPackage);
        when(jcrPackage.getNode()).thenReturn(node);
        testSubject.installPackage(uuid, false, false, true);

        verify(resourceResolverFactory).getServiceResourceResolver(any());
        verify(resourceResolver).adaptTo(Session.class);
        verify(packaging).getPackageManager(session);
        verify(jcrPackage).getNode();
        verify(jcrPackage, never()).install(any());
        verify(replicator, never()).replicate(any(), any(), any());
        verify(node, never()).remove();
        verify(session, never()).save();
        verify(jcrPackage).close();
        verify(resourceResolver).close();
    }
}
