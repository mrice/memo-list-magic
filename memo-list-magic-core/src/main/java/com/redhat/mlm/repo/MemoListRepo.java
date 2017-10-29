package com.redhat.mlm.repo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

public class MemoListRepo {

	private Store emailStore;
	
	
	public MemoListRepo(EmailStoreFactory storeFactory){
		Objects.requireNonNull(storeFactory, "EmailStoreFactory can not be null");
		emailStore = storeFactory.getEmailStore();
		Objects.requireNonNull(emailStore, "EmailStore can not be null");
	}
	
	public void connect() throws MessagingException{
		if(!emailStore.isConnected()){
			emailStore.connect();
		}
	}
	
	public List<Message> getNewMessages() throws Exception {
		//if store is not connected, throw error.
		if(emailStore == null || !emailStore.isConnected()){
			throw new Exception("Please make sure that the email store is not null and connected before getting new messages.");
		}
		
		Message[] messages = retrieveUnseenMessages();
		List<Message> filteredMessages = filterMemoListMessagesToList(messages); 
		return filteredMessages;
	}
	
	private Message[] retrieveUnseenMessages() throws MessagingException{
		Folder inbox = emailStore.getFolder("Inbox");
        inbox.open(Folder.READ_ONLY);

        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
        Message messages[] = inbox.search(unseenFlagTerm);
        
        //TODO find out why I cant close this. Should I copy Message contents over to email domain objects?
        //inbox.close(true);
		return messages;
	}
	
	private List<Message> filterMemoListMessagesToList(Message[] unfilteredMessages) {
        return Stream.of(unfilteredMessages)
                .filter(message -> {
                    try { // this is a nasty little issue with lambdas
                        return addressesContainMemoList(message.getReplyTo());
                    } catch (MessagingException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
	
	private boolean addressesContainMemoList(Address[] addresses) {
        return Stream.of(addresses)
                .anyMatch((address) -> "memo-list@redhat.com".equals(address.toString()));
    }
	
	public void close() throws MessagingException {
		if(emailStore != null && emailStore.isConnected()){
			emailStore.close();
		}
	}
}
