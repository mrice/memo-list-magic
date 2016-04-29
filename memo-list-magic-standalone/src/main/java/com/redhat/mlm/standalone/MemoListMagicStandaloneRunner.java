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
        
        System.out.println("WE ARE RUNNING! Sleeping 15 seconds.");
        Thread.sleep(15000);
        
        MemoListMagic memoListMagic = wireDependencies(emailHost,emailUser,emailPassword);
        //memoListMagic.run();
    }
    
    //poor mans constructor dependency injection. Set everything up here.
    private static MemoListMagic wireDependencies(String emailHost, String emailUser, String emailPassword){
    	EmailStoreFactory emailStoreFactory = new IMAPEmailStoreFactory(emailHost, emailUser, emailPassword);
        IMemoListRepo memoListRepo = new MemoListRepo(emailStoreFactory);
    	ThreadMetadataRepo threadMetadataRepo = new CrateThreadMetadataRepo();
        return new MemoListMagic(memoListRepo, threadMetadataRepo);
    }

}
