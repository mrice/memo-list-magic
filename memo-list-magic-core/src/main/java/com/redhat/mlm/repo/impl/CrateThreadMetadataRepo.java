package com.redhat.mlm.repo.impl;

import java.util.Arrays;

import com.redhat.mlm.model.ThreadMetadata;
import com.redhat.mlm.repo.ThreadMetadataRepo;

import io.crate.action.sql.SQLResponse;
import io.crate.client.CrateClient;

public class CrateThreadMetadataRepo implements ThreadMetadataRepo{

	public CrateThreadMetadataRepo(){
		//take this in as a configuration.
		System.out.println("Trying to create client at 172.18.0.2:4300");
		CrateClient client = new CrateClient("172.18.0.2:4300");
		SQLResponse response = client.sql("SELECT * FROM sys.nodes").actionGet();
		System.out.println("Accessing Rows.");
		for (Object[] row: response.rows()){
		    System.out.println(Arrays.toString(row));
		}
	}
	
	@Override
	public ThreadMetadata getBySubject(String subject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThreadMetadata update(ThreadMetadata threadMetadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThreadMetadata add(ThreadMetadata threadMetadata) {
		// TODO Auto-generated method stub
		return null;
	}

}
