package com.redhat.mlm;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

public class MemoListRepoTest {
	
	@Test
	public void testGetNewMessages() throws Exception{
		final String userPassword = "abcdef123";
	    final String userName = "testuser";
	    final String userEmailAddress = "testuser@testdomain.com";
	    final String localhost = "127.0.0.1";
	    GreenMailUser user;
	    GreenMail mailServer;
	    IEmailStoreFactory emailStoreFactory;
	    
		// create user on mail server
		mailServer = new GreenMail(ServerSetupTest.ALL);
        mailServer.start();
        
        user = mailServer.setUser(userPassword, userName, userPassword);
 
        // fetch the e-mail from pop3 using javax.mail ..
        Properties props = new Properties();
        props.setProperty("mail.imap.connectiontimeout", "5000");
        Session session = Session.getInstance(props);
        URLName urlName = new URLName("imap", localhost,
        ServerSetupTest.IMAP.getPort(), null, user.getLogin(),
        user.getPassword());
        Store store = session.getStore(urlName);
        
        
        emailStoreFactory = mock(IEmailStoreFactory.class);
        when(emailStoreFactory.getEmailStore()).thenReturn(store);
		
		//first message.
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress("memo-list@redhat.com"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
        		userEmailAddress));
        message.setSubject("The coffee Maker on 16 is the WORST!");
        message.setText("TODO. Make something clever");
        user.deliver(message);
		
        //should be filtered out due to not being from memo list
        MimeMessage message2 = new MimeMessage((Session) null);
        message2.setFrom(new InternetAddress("non-memo-list@redhat.com"));
        message2.addRecipient(Message.RecipientType.TO, new InternetAddress(
        		userEmailAddress));
        message2.setSubject("The coffee Maker on 16 is the WORST!");
        message2.setText("Thank goodness for the coffee maker on 15.");
        user.deliver(message2);
        
        MimeMessage message3 = new MimeMessage((Session) null);
        message3.setFrom(new InternetAddress("memo-list@redhat.com"));
        message3.addRecipient(Message.RecipientType.TO, new InternetAddress(
        		userEmailAddress));
        message3.setSubject("The coffee Maker on 16 is the WORST!");
        message3.setText("Drink tea instead! BTW about slack...");
        //Set a mail to seen with IMAP.
        MailFolder inbox = mailServer.getManagers().getImapHostManager().getInbox(user);
        final Flags flagsSeen = new Flags();
        flagsSeen.add(Flag.SEEN);
        
        inbox.appendMessage(message3, flagsSeen, new Date());
        user.deliver(message3);
        
        MemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
		memoListRepo.connect();
        
		List<Message> newMessages = memoListRepo.getNewMessages();
		assertEquals(1, newMessages.size());
		String content = (String) newMessages.get(0).getContent();
		assertEquals("TODO. Make something clever", content);
		newMessages = memoListRepo.getNewMessages();
		assertEquals(0, newMessages.size());
	}
	
	@Test(expected=Exception.class)
	public void testGettingMessagesWhenConnectionIsClosedThrowsException() throws Exception{
		Store store = mock(Store.class);
        
		IEmailStoreFactory emailStoreFactory = mock(IEmailStoreFactory.class);
        when(emailStoreFactory.getEmailStore()).thenReturn(store);
        
        MemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
        memoListRepo.getNewMessages();
	}
	
	@Test(expected=NullPointerException.class)
	public void testMemoListRepoCantBeConstructedWithNullEmailStoreFactory(){
		new MemoListRepo(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testMemoListRepoCantBeConstructedWithNullEmailStore(){
		IEmailStoreFactory emailStoreFactory = mock(IEmailStoreFactory.class);
        when(emailStoreFactory.getEmailStore()).thenReturn(null);
		new MemoListRepo(emailStoreFactory);
	}
	
	@Test
	public void testStartingOpensTheConnection() throws MessagingException{
		Store store = mock(Store.class);
        
		IEmailStoreFactory emailStoreFactory = mock(IEmailStoreFactory.class);
        when(emailStoreFactory.getEmailStore()).thenReturn(store);
        
        MemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
        when(store.isConnected()).thenReturn(false);
		memoListRepo.connect();
		
		Mockito.verify(store, Mockito.times(1)).connect();
	}
	
	@Test
	public void testStartingTwiceDoesNotOpenTheConnectionTwice() throws MessagingException{
		Store store = mock(Store.class);
        
		IEmailStoreFactory emailStoreFactory = mock(IEmailStoreFactory.class);
        when(emailStoreFactory.getEmailStore()).thenReturn(store);
        
        MemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
        when(store.isConnected()).thenReturn(false);
		memoListRepo.connect();
		when(store.isConnected()).thenReturn(true);
		memoListRepo.connect();
		
		Mockito.verify(store, Mockito.times(1)).connect();
	}
	
	@Test
	public void testConnectingAndThenClosingClosesTheConnection() throws MessagingException{
		Store store = mock(Store.class);
        
		IEmailStoreFactory emailStoreFactory = mock(IEmailStoreFactory.class);
        when(emailStoreFactory.getEmailStore()).thenReturn(store);
        
        MemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
        when(store.isConnected()).thenReturn(false);
		memoListRepo.connect();
		when(store.isConnected()).thenReturn(true);
		memoListRepo.close();
		
		Mockito.verify(store, Mockito.times(1)).connect();
		Mockito.verify(store, Mockito.times(1)).close();
	}
	
	@Test
	public void testClosingWhenNotConnectedDoesNotTryToCloseTheConnection() throws MessagingException{
		Store store = mock(Store.class);
        
		IEmailStoreFactory emailStoreFactory = mock(IEmailStoreFactory.class);
        when(emailStoreFactory.getEmailStore()).thenReturn(store);
        
        MemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
        when(store.isConnected()).thenReturn(false);
		memoListRepo.close();
		
		Mockito.verify(store, Mockito.times(0)).close();
	}
	
}
