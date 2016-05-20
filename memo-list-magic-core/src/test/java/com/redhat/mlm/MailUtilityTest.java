package com.redhat.mlm;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.Message;

import org.junit.Test;

public class MailUtilityTest {
	@Test
    public void testNormalizeSubjectLine() throws Exception {

        Message oneMessage = mock(Message.class);
        Message replyMessage = mock(Message.class);

        String whinyMemoListSubjectLne = "The coffee Maker on 16 is the WORST!";
        when(oneMessage.getSubject()).thenReturn(whinyMemoListSubjectLne);
        when(replyMessage.getSubject()).thenReturn("RE: "+whinyMemoListSubjectLne);

        assertEquals(whinyMemoListSubjectLne, MailUtility.normalizeSubjectLine(oneMessage));
        assertEquals(whinyMemoListSubjectLne, MailUtility.normalizeSubjectLine(replyMessage));
        assertEquals(whinyMemoListSubjectLne, MailUtility.normalizeSubjectLine(oneMessage.getSubject()));
        assertEquals(whinyMemoListSubjectLne, MailUtility.normalizeSubjectLine(replyMessage.getSubject()));
    }
	
	@Test(expected = NullPointerException.class)
	public void testNomalizeSubjectLineThrowsExceptionWhenSubjectIsNull(){
		String subject = null;
		MailUtility.normalizeSubjectLine(subject);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNomalizeSubjectLineThrowsExceptionWhenMessageIsNull(){
		Message subject = null;
		MailUtility.normalizeSubjectLine(subject);
	}
}
