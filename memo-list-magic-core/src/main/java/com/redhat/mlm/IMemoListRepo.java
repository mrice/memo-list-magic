package com.redhat.mlm;

import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;

public interface IMemoListRepo {
	public void connect() throws MessagingException;
	public List<Message> getNewMessages() throws MessagingException, Exception;
	public void close() throws MessagingException;
}
