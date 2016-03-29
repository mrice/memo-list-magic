package com.redhat.mlm;

import javax.mail.*;
import java.util.List;

public class MemoListMagic {
	
	private IMemoListRepo memoListRepo;
	private IThreadMetadataRepo threadMetadataRepo;
	private boolean keepRunning = false;
    public static void main(String... args){

        if (args.length < 3) {
            System.out.println("Usage: run email-host email-user email-password");
            return;
        }

        String emailHost = args[0];
        String emailUser = args[1];
        String emailPassword = args[2];
        
        MemoListMagic memoListMagic = wireDependencies(emailHost,emailUser,emailPassword);
        memoListMagic.run();
    }
    
    //poor mans constructor dependency injection. Set everything up here.
    private static MemoListMagic wireDependencies(String emailHost, String emailUser, String emailPassword){
    	IEmailStoreFactory emailStoreFactory = new EmailStoreFactory(emailHost, emailUser, emailPassword);
        IMemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
    	IThreadMetadataRepo threadMetadataRepo = new MongoThreadMetadataRepo();
        return new MemoListMagic(memoListRepo, threadMetadataRepo);
    }
    
    public MemoListMagic(IMemoListRepo memoListRepo, IThreadMetadataRepo threadMetadataRepo){
		this.memoListRepo = memoListRepo;
		this.threadMetadataRepo = threadMetadataRepo;
	}
    
    public void run() {
    	if(keepRunning) return;
    	keepRunning = true;
    	Runnable runnable = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
				memoListRepo.connect();
				while(keepRunning){
					List<Message> memoListMessages = memoListRepo.getNewMessages();
					updateOrInsertDatabaseMetrics(memoListMessages);
					System.out.println("Sleeping... zzzz");
					Thread.sleep(5000);}
				}
		        catch (Exception e) {
		        	e.printStackTrace();
		        	closeMemoListRepo();
				}
			}
    	};
    	Thread t1 = new Thread(runnable);
    	t1.start();
    }
    
    public void stop(){
    	keepRunning = false;
    }
    
    //TODO - lots and lots and lots to improve here!!!
    private void updateOrInsertDatabaseMetrics(List<Message> messages){
        messages.stream().forEach((message) -> {
            String subjectLine = MailUtility.normalizeSubjectLine(message);
            ThreadMetadata threadMetadata = threadMetadataRepo.getBySubject(subjectLine); 
            if (threadMetadata != null) {
                threadMetadata.replyCount++;
                threadMetadataRepo.update(threadMetadata);
            } else {
            	threadMetadataRepo.add(new ThreadMetadata(subjectLine, 0));
            }
        });
    }
    
    private void closeMemoListRepo(){
    	if(memoListRepo != null){
			try {
				memoListRepo.close();
			} catch (MessagingException e1) {
				//error closing connection. Not good.
				e1.printStackTrace();
			}
		}
    }

}
