package com.redhat.mlm.standalone;

import com.redhat.mlm.*;

public class MemoListMagicStandaloneRunner {

	public static void main(String... args) throws Exception{

        if (args.length < 3) {
            System.out.println("Usage: run email-host email-user email-password");
            return;
        }

        String emailHost = args[0];
        String emailUser = args[1];
        String emailPassword = args[2];
        
        //MemoListMagic memoListMagic = wireDependencies(emailHost,emailUser,emailPassword);
        //memoListMagic.run();
        System.out.println("WE ARE RUNNING!");
        Thread.sleep(120000);
    }
    
    //poor mans constructor dependency injection. Set everything up here.
    private static MemoListMagic wireDependencies(String emailHost, String emailUser, String emailPassword){
    	IEmailStoreFactory emailStoreFactory = new EmailStoreFactory(emailHost, emailUser, emailPassword);
        IMemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
    	IThreadMetadataRepo threadMetadataRepo = new MongoThreadMetadataRepo();
        return new MemoListMagic(memoListRepo, threadMetadataRepo);
    }

}
