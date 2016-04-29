package com.redhat.mlm;

import javax.mail.*;
import java.util.List;
import java.util.Objects;

public class MemoListMagic {
	
	private MemoListRepo memoListRepo;
	private ThreadMetadataRepo threadMetadataRepo;
	private final Runnable runnable;
	private Thread runningThread;
	private static final int PULL_DELAY_MS = 5000;
	private boolean keepRunning = false;
    
    
    public MemoListMagic(final MemoListRepo memoListRepo, ThreadMetadataRepo threadMetadataRepo){
    	Objects.requireNonNull(memoListRepo);
    	Objects.requireNonNull(threadMetadataRepo);
    	//check not null.
		this.memoListRepo = memoListRepo;
		this.threadMetadataRepo = threadMetadataRepo;
		runnable = new Runnable(){
			@Override
			public void run() {
				try{
				memoListRepo.connect();
				while(keepRunning){
					List<Message> memoListMessages = memoListRepo.getNewMessages();
					updateOrInsertDatabaseMetrics(memoListMessages);
					System.out.println("Sleeping... zzzz");
					Thread.sleep(PULL_DELAY_MS);}
				}
		        catch (Exception e) {
		        	e.printStackTrace();
		        	keepRunning = false;
		        	closeMemoListRepo();
				}
			}
    	};
	}
    
    public boolean isRunning(){
    	//close enough.
    	return keepRunning;
    }
    
    public void run() throws Exception {
    	if(isRunning()) throw new Exception("Unable To Run The Magic Twice. Please Stop Before Running Again.");
    	keepRunning = true;
    	runningThread = new Thread(runnable);
    	runningThread.start();
    }
    
    public void stop() throws InterruptedException{
    	if(isRunning()){
    		System.out.println("Stoping. Please Wait.");
    		keepRunning = false;
    		runningThread.join();
    	}
    }
    
    //TODO - lots and lots and lots to improve here!!!
    private void updateOrInsertDatabaseMetrics(List<Message> messages){
        messages.stream().forEach((message) -> {
            String subjectLine = MailUtility.normalizeSubjectLine(message);
            ThreadMetadata threadMetadata = threadMetadataRepo.getBySubject(subjectLine); 
            try {
            	if (threadMetadata != null) {
            		threadMetadata.addOneReplyCount();
            		threadMetadataRepo.update(threadMetadata);
            	} else {
            		threadMetadataRepo.add(new ThreadMetadata(subjectLine, 0));
            	}
            }catch (Exception e) {
				e.printStackTrace();
			}
        });
    }
    
    private void closeMemoListRepo(){
    	if(memoListRepo != null){
			try {
				memoListRepo.close();
			} catch (MessagingException e1) {
				System.out.println("//error closing connection. Not good.");
				e1.printStackTrace();
			}
		}
    }

}
