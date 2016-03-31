package com.redhat.mlm;

public class ThreadMetadata {
	public final String subject;
	private int replyCount;
	
	public ThreadMetadata(String subject, int replyCount) throws Exception{
		this.subject = MailUtility.normalizeSubjectLine(subject);
		checkNonNegative(replyCount);
		this.replyCount = replyCount;
	}
	
	public int setReplyCount(int value) throws Exception{
		checkNonNegative(value);
		replyCount = value;
		return replyCount;
	}
	
	public void addOneReplyCount() throws Exception{
		replyCount++;
		checkNonNegative(replyCount);
	}
	
	public int getReplyCount(){
		return replyCount;
	}
	
	private void checkNonNegative(int value) throws Exception{
		if(value < 0)
			throw new Exception("Value Should Be Non Negative.");
	}
	
	
}
