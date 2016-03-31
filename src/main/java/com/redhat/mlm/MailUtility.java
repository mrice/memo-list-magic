package com.redhat.mlm;

import java.util.Objects;

import javax.mail.Message;
import javax.mail.MessagingException;

public class MailUtility {
	
	public static String normalizeSubjectLine(Message message) {
        try {
            Objects.requireNonNull(message, "Message is null");
            return normalizeSubjectLine(message.getSubject());
        } catch (MessagingException e) {
            return null;
        }
    }
	
	public static String normalizeSubjectLine(String subjectLine){
		checkSubjectLineNotNull(subjectLine);
		String normalizedSubjectLine;

        //TODO there's a lot we need to do here because there are soooooo many permutations and this implementation is waaaaayyyy too naive (and lame too)
        if (subjectLine.startsWith("RE: "))
            normalizedSubjectLine = subjectLine.substring("RE: ".length());
        else
            normalizedSubjectLine = subjectLine;
        return normalizedSubjectLine;
	}
	
	private static void checkSubjectLineNotNull(String subjectLine){
		Objects.requireNonNull(subjectLine, "Message subject line is null, and that's just not going to work");
	}
}
