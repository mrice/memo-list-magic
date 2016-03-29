package com.redhat.mlm;

public class ThreadMetadata {
	public final String subject;
	public int replyCount;
	
	public ThreadMetadata(String subject, int replyCount){
		this.subject = MailUtility.normalizeSubjectLine(subject);
		this.replyCount = replyCount;
	}
}
