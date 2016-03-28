package com.redhat.mlm;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoListMagic {

    List<Message> filterMemoListMessagesToList(Message[] unfilteredMessages) throws Exception {
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

    boolean addressesContainMemoList(Address[] addresses) {
        return Stream.of(addresses)
                .anyMatch((address) -> "memo-list@redhat.com".equals(address.toString()));
    }

    public String normalizeSubjectLine(Message message) throws Exception {
        Objects.requireNonNull(message, "Message is null");
        Objects.requireNonNull(message.getSubject(), "Message subject line is null, and that's just not going to work");

        String normalizedSubjectLine;

        //TODO there's a lot we need to do here because there are soooooo many permutations and this implementation is waaaaayyyy too naive (and lame too)
        if (message.getSubject().startsWith("RE: "))
            normalizedSubjectLine = message.getSubject().substring("RE: ".length());
        else
            normalizedSubjectLine = message.getSubject();

        return normalizedSubjectLine;

    }
}
