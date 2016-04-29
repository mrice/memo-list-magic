package com.redhat.mlm;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmailStoreFactoryTest {
	@Test
	public void testFactoryReturnsNonNullStore(){
		IMAPEmailStoreFactory factory = new IMAPEmailStoreFactory("host", "user", "pass");
		assertNotNull(factory.getEmailStore());
	}
	
	@Test(expected = NullPointerException.class)
	public void testFactoryThrowsErrorWhenCreatedWithNullHost(){
		new IMAPEmailStoreFactory(null, "user", "pass");
	}
	
	@Test(expected = NullPointerException.class)
	public void testFactoryThrowsErrorWhenCreatedWithNullUser(){
		new IMAPEmailStoreFactory("host", null, "pass");
	}
	
	@Test(expected = NullPointerException.class)
	public void testFactoryThrowsErrorWhenCreatedWithNullPassword(){
		new IMAPEmailStoreFactory("host", "user", null);
	}
}
