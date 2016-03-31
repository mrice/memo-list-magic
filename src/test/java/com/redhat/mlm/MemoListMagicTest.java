package com.redhat.mlm;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.mail.Message;
import javax.mail.MessagingException;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MemoListMagicTest {
	@SuppressWarnings("serial")
	@Test
	public void testReplyCountAddition() throws MessagingException, Exception{
		IMemoListRepo memoListRepo = mock(IMemoListRepo.class);
		
		Message messageOne = mock(Message.class);
		when(messageOne.getSubject()).thenReturn("A Subject");
        Message messageTwo = mock(Message.class);
        when(messageTwo.getSubject()).thenReturn("A Subject");
        Message messageThree = mock(Message.class);
        when(messageThree.getSubject()).thenReturn("A Subject");
        when(memoListRepo.getNewMessages()).thenReturn(
        		new ArrayList<Message>(){{
        			add(messageOne);
        			add(messageTwo);
        			add(messageThree);
        		}});
        
        ThreadMetadata savedMetadata = new ThreadMetadata("A Subject", 1);
        
        
        IThreadMetadataRepo threadMetadataRepo = mock(IThreadMetadataRepo.class);
        //return no nothing the first call then a saved metadata the second call
        when(threadMetadataRepo.getBySubject("A Subject"))
        	.thenReturn(null)
        	.thenReturn(savedMetadata)
        	.thenReturn(savedMetadata);
        
        MemoListMagic mlm = new MemoListMagic(memoListRepo, threadMetadataRepo);
        mlm.run();
        Thread.sleep(4000);
        mlm.stop();
        
        assertEquals(3, savedMetadata.getReplyCount());
        //assert repo add method is called once and that update is called once.
        Mockito.verify(threadMetadataRepo, Mockito.times(1)).add(Matchers.any());
        Mockito.verify(threadMetadataRepo, Mockito.times(2)).update(Matchers.any());
	}
	
	@Test
	public void testRunningThenStoppingStopsRunning() throws Exception{
		IMemoListRepo memoListRepo = mock(IMemoListRepo.class);
		IThreadMetadataRepo threadMetadataRepo = mock(IThreadMetadataRepo.class);
		MemoListMagic mlm = new MemoListMagic(memoListRepo, threadMetadataRepo);
		mlm.run();
		assertTrue(mlm.isRunning());
		mlm.stop();
		assertFalse(mlm.isRunning());
	}
	
	@Test(expected = Exception.class)
	public void testRunningMagicTwiceThrowsException() throws Exception{
		IMemoListRepo memoListRepo = mock(IMemoListRepo.class);
		IThreadMetadataRepo threadMetadataRepo = mock(IThreadMetadataRepo.class);
		MemoListMagic mlm = new MemoListMagic(memoListRepo, threadMetadataRepo);
		mlm.run();
		assertTrue(mlm.isRunning());
		mlm.run();
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullMemoListRepoThrowsException() throws Exception{
		IThreadMetadataRepo threadMetadataRepo = mock(IThreadMetadataRepo.class);
		new MemoListMagic(null, threadMetadataRepo);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullThreadMetadataThrowsException() throws Exception{
		IMemoListRepo memoListRepo = mock(IMemoListRepo.class);
		new MemoListMagic(memoListRepo, null);
	}
}
