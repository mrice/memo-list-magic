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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

public class MemoListRepoTest {
	
	//before setup greenmail pop3 store
	//mock EmailStoreFactory to return imap store
	private static final String USER_PASSWORD = "abcdef123";
    private static final String USER_NAME = "testuser";
    private static final String EMAIL_USER_ADDRESS = "testuser@testdomain.com";
    private static final String LOCALHOST = "127.0.0.1";
    private GreenMailUser user;
    private GreenMail mailServer;
    private IEmailStoreFactory emailStoreFactory;
	
	
	@Before
	public void setUp() throws AddressException, MessagingException{
		// create user on mail server
		mailServer = new GreenMail(ServerSetupTest.ALL);
        mailServer.start();
        
        user = mailServer.setUser(EMAIL_USER_ADDRESS, USER_NAME,
        USER_PASSWORD);
 
        // fetch the e-mail from pop3 using javax.mail ..
        Properties props = new Properties();
        props.setProperty("mail.imap.connectiontimeout", "5000");
        Session session = Session.getInstance(props);
        URLName urlName = new URLName("imap", LOCALHOST,
        ServerSetupTest.IMAP.getPort(), null, user.getLogin(),
        user.getPassword());
        Store store = session.getStore(urlName);
        
        
        emailStoreFactory = mock(IEmailStoreFactory.class);
        when(emailStoreFactory.getEmailStore()).thenReturn(store);
	}
	
	@Test
	public void testGetNewMessages() throws Exception{
		//first message.
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress("memo-list@redhat.com"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
        EMAIL_USER_ADDRESS));
        message.setSubject("The coffee Maker on 16 is the WORST!");
        message.setText("TODO. Make something clever");
        user.deliver(message);
		
        //should be filtered out due to not being from memo list
        MimeMessage message2 = new MimeMessage((Session) null);
        message2.setFrom(new InternetAddress("non-memo-list@redhat.com"));
        message2.addRecipient(Message.RecipientType.TO, new InternetAddress(
        EMAIL_USER_ADDRESS));
        message2.setSubject("The coffee Maker on 16 is the WORST!");
        message2.setText("Thank goodness for the coffee maker on 15.");
        user.deliver(message2);
        
        MimeMessage message3 = new MimeMessage((Session) null);
        message3.setFrom(new InternetAddress("memo-list@redhat.com"));
        message3.addRecipient(Message.RecipientType.TO, new InternetAddress(
        EMAIL_USER_ADDRESS));
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
}
