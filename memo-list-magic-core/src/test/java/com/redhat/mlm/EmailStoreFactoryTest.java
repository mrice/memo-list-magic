package com.redhat.mlm;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmailStoreFactoryTest {
	@Test
	public void testFactoryReturnsNonNullStore(){
		EmailStoreFactory factory = new EmailStoreFactory("host", "user", "pass");
		assertNotNull(factory.getEmailStore());
	}
	
	@Test(expected = NullPointerException.class)
	public void testFactoryThrowsErrorWhenCreatedWithNullHost(){
		new EmailStoreFactory(null, "user", "pass");
	}
	
	@Test(expected = NullPointerException.class)
	public void testFactoryThrowsErrorWhenCreatedWithNullUser(){
		new EmailStoreFactory("host", null, "pass");
	}
	
	@Test(expected = NullPointerException.class)
	public void testFactoryThrowsErrorWhenCreatedWithNullPassword(){
		new EmailStoreFactory("host", "user", null);
	}
}
