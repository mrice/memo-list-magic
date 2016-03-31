package com.redhat.mlm;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThreadMetadataTest {
	@Test
	public void testThreadMetadataIsCreatedWithNormalizedString() throws Exception{
		//could pull out the nomalization strategy as a dependency but I won't.
		//would make for a cleaner test with less logic but for now will assume that
		//nomalization of subjects is as known as simple addition
		ThreadMetadata threadMetadata = new ThreadMetadata("RE: Everyday is Awesome.", 9001);
		assertEquals("Everyday is Awesome.", threadMetadata.subject);
		assertEquals(9001, threadMetadata.getReplyCount());
	}
	
	@Test(expected = Exception.class)
	public void testThreadMetadataCanNotBeConstructedWithANullSubject() throws Exception{
		new ThreadMetadata(null, 2);
	}
	
	@Test(expected = Exception.class)
	public void testThreadMetadataCanNotBeConstructedWithNegativeReplyCount() throws Exception{
		new ThreadMetadata("Subject", -1);
	}
	
	@Test(expected = Exception.class)
	public void testSettingReplyCountToMaxAndThenAddingOneThrowsException() throws Exception{
		ThreadMetadata threadMetadata = new ThreadMetadata("Subject", Integer.MAX_VALUE);
		threadMetadata.addOneReplyCount();
	}
}
