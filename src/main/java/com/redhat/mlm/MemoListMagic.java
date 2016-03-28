package com.redhat.mlm;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;

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

    String normalizeSubjectLine(Message message) {
        try {
            Objects.requireNonNull(message, "Message is null");
            Objects.requireNonNull(message.getSubject(), "Message subject line is null, and that's just not going to work");

            String normalizedSubjectLine;

            //TODO there's a lot we need to do here because there are soooooo many permutations and this implementation is waaaaayyyy too naive (and lame too)
            if (message.getSubject().startsWith("RE: "))
                normalizedSubjectLine = message.getSubject().substring("RE: ".length());
            else
                normalizedSubjectLine = message.getSubject();
            return normalizedSubjectLine;
        } catch (MessagingException e) {
            return null;
        }
    }

    //TODO - lots and lots and lots to improve here!!!
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

    //TODO - lots and lots and lots to improve here!!!
    void updateOrInsertDatabaseMetrics(List<Message> messages) throws Exception {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("memoList");
        messages.stream().forEach((message) -> {
            String subjectLine = normalizeSubjectLine(message);
            if (db.getCollection("messages").find(eq("subject", subjectLine)).first() != null) {
                Document record = db.getCollection("messages").find(eq("subject", subjectLine)).first();
                Integer currentReplyCound = record.getInteger("replyCount");
                db.getCollection("messages").updateOne(record,
                        new Document("$set", new Document("replyCount", currentReplyCound++)));
            } else {
                db.getCollection("messages").insertOne(
                        new Document()
                            .append("subject", subjectLine)
                            .append("replyCount", new Integer(0))
                );
            }
        });
    }

    public void run(String emailHost, String emailUser, String emailPassword) throws Exception {
        while(true) {
            System.out.println(String.format("Connecting to server %s", emailHost));
            Message messages[] = connectToServerAndRetrieveUnseenMsgs(emailHost, emailUser, emailPassword);
            System.out.println(String.format("Found %d messages", messages.length));

            List<Message> memoListMessages = filterMemoListMessagesToList(messages);

            updateOrInsertDatabaseMetrics(memoListMessages);

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
