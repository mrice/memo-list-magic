package com.redhat.mlm;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
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

    String normalizeSubjectLine(Message message) throws Exception {
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

    Message[] connectToServerAndRetrieveUnseenMsgs(String host, String user, String password) throws Exception {

        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties);
        Store store = session.getStore("pop3");
        store.connect(host, user, password);
        Folder inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_ONLY);

        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
        Message messages[] = inbox.search(unseenFlagTerm);

        inbox.close(true);
        store.close();

        return messages;
    }

    public void run(String emailHost, String emailUser, String emailPassword) throws Exception {
        while(true) {
            System.out.println(String.format("Connecting to server %s", emailHost));
            Message messages[] = connectToServerAndRetrieveUnseenMsgs(emailHost, emailUser, emailPassword);
            System.out.println(String.format("Found %d messages", messages.length));

            List<Message> memoListMessages = filterMemoListMessagesToList(messages);

            //TODO - database insertion here

            System.out.println("Sleeping... zzzz");
            Thread.sleep(5000);
        }
    }

    public static void main(String... args) throws Exception {

        if (args.length < 3) {
            System.out.println("Usage: run email-host email-user email-password");
            return;
        }

        String emailHost = args[0];
        String emailUser = args[1];
        String emailPassword = args[2];

        MemoListMagic memoListMagic = new MemoListMagic();
        memoListMagic.run(emailHost, emailUser, emailPassword);

    }

}
