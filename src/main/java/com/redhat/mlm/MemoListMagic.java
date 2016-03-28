package com.redhat.mlm;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoListMagic {

    List<Message> filterMemoListMessagesToList(Message[] unfilteredMessages) throws Exception {
        return Stream.of(unfilteredMessages)
                .filter(message -> {
                    try {
                        return addressesContainMemoList(message.getReplyTo());
                    } catch (MessagingException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    boolean addressesContainMemoList(Address[] addresses) {
        return Stream.of(addresses)
                .anyMatch((address) -> "memo-list@redhat.com".equals(address.toString()));
    }

}
