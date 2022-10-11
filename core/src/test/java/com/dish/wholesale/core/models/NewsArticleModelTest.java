package com.dish.wholesale.core.models;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junit.framework.Assert;


@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class NewsArticleModelTest {

	@InjectMocks
	private NewsArticleModel newsModel;

	@Test
	void getNewsItems()
	{
		
		newsModel.init();
		Assert.assertNotNull(newsModel.getNewsItems());
		
	}
	
}