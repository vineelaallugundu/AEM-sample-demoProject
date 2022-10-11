package com.dish.wholesale.core.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class UtilTest.
 */
class UtilTest {

	/** Logger Instantiation. */
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilTest.class);

	/**
	 * Test load and getters.
	 *
	 * @param methods     the methods
	 * @param modelObject the modelObject
	 * @param resource    the resource
	 */
	@Test
	static void testLoadAndGetters(String methods[], Object modelObject, Resource resource) {

		assertNotNull(modelObject, "Checking if Object is not null");
		assertNotNull(resource, "Checking if resource is not null");

		for (String methodStr : methods) {
			try {
				Method method = modelObject.getClass().getMethod(methodStr);
				assertNotNull(method.invoke(modelObject), "Checking All Getters of the model class");

			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LOGGER.error("Exception in the getters test method :: {}", e);
			}
		}
	}

}
