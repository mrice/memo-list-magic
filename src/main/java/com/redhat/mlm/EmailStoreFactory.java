package com.redhat.mlm;

import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

public class EmailStoreFactory implements IEmailStoreFactory {
	
	private Session session;
	
	//possibly implement IMAP here
	//TODO integration testing for this.
	public EmailStoreFactory(String emailHost, String emailUser, String emailPassword){
		Objects.requireNonNull(emailHost, "Store creation requires host");
		Objects.requireNonNull(emailUser, "Store creation requires user");
		Objects.requireNonNull(emailPassword, "Store creation requires password");
		Properties props = new Properties();
		props.put("mail.store.protocol", "pop3s");
		//put host here.
	    props.put("mail.pop3.host", "pop.gmail.com");     
	    props.put("mail.pop3.user", emailUser);
	    props.put("mail.pop3.socketFactory", 995);
	    props.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	    props.put("mail.pop3.port", 995);
	    props.setProperty("mail.pop3.connectiontimeout", "5000");
	    session = Session.getInstance(props, new Authenticator(){
	    	@Override
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(emailUser, emailPassword);

	        }
	    });
	    
	    //could be useful
	    //session.setDebug(true);
	}
	
	@Override
	public Store getEmailStore() {
		try {
			Store emailStore = session.getStore("pop3s");
			return emailStore;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}

}
