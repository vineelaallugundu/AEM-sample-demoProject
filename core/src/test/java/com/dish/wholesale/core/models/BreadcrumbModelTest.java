package com.dish.wholesale.core.models;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.Page;
import com.day.crx.BaseException;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.framework.Assert;

/**
 * The Class BreadcrumbModelTest.
 */
@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class BreadcrumbModelTest {

	/** The breadcrumb model. */
	@InjectMocks
	private BreadcrumbModel breadcrumbModel;

	/** The current page. */
	@Mock
	private Page currentPage;

	/** The nav list. */
	@Mock
	List<Page> navList;

	/**
	 * Method loads the context, resource and language selector model.
	 *
	 * @param context the new up
	 * @throws BaseException the base exception
	 */
	@BeforeEach
	public void setup(AemContext context) throws BaseException {
		Mockito.lenient().when(currentPage.getDepth()).thenReturn(5);
		Mockito.lenient().when(currentPage.getAbsoluteParent((int) 4L)).thenReturn(currentPage);
		navList.add(currentPage.getAbsoluteParent((int) 4L));
		context.registerService(Page.class, currentPage);
		context.registerService(BreadcrumbModel.class, breadcrumbModel);

		context.registerService(List.class, navList);
	}

	/**
	 * Sets the bread crumb items.
	 *
	 * @throws Exception the exception
	 */
	@Test
	void setBreadCrumbItems() throws Exception {
		breadcrumbModel.init();
		Assert.assertNotNull(breadcrumbModel);
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 * @throws Exception the exception
	 */
	@Test
	void getData() throws Exception {
		Assert.assertNotNull(breadcrumbModel.getData());
	}

}
