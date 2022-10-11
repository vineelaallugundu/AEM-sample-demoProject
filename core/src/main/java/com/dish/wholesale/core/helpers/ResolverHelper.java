package com.dish.wholesale.core.helpers;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResolverHelper {
		
	private static final Logger LOG = LoggerFactory.getLogger(ResolverHelper.class);
	
	private static final String WHOLESALE_SERVICE_USER = "wholesaleserviceuser";
	
	private ResolverHelper() {	
	}
	
	public static ResourceResolver newResolver(ResourceResolverFactory rrf)  {
		try {
			final Map<String, Object> paramMap = new HashMap<>();
			paramMap.put(ResourceResolverFactory.SUBSERVICE, WHOLESALE_SERVICE_USER);
				return rrf.getServiceResourceResolver(paramMap);
		} catch (LoginException e) {
			LOG.error(e.getMessage());
		}
		return null;		
	}

}
