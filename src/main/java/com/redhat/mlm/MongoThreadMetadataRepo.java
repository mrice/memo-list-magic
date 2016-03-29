package com.redhat.mlm;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

public class MongoThreadMetadataRepo implements IThreadMetadataRepo {

	private MongoDatabase db;
	public MongoThreadMetadataRepo(){
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient();
		db = mongoClient.getDatabase("memoList");
	}
	
	//TODO integration testing with Mongo
	@Override
	public ThreadMetadata getBySubject(String subject) {
		Document record = db.getCollection("messages").find(eq("subject", subject)).first();
		if(record == null){
			return null;
		}
		ThreadMetadata threadMetadata = createNewThreadMetadataFromDocument(record);
		return threadMetadata;
	}
	
	private ThreadMetadata createNewThreadMetadataFromDocument(Document document){
		return new ThreadMetadata(document.getString("subject"), document.getInteger("replyCount"));
	}
	
	//should this be async?
	@Override
	public ThreadMetadata update(ThreadMetadata threadMetadata) {
		Document record = db.getCollection("messages").find(eq("subject", threadMetadata)).first();
		if(record == null){
			return null;
		}
		//is there a callback here?
		UpdateResult updatedResult = db.getCollection("messages").updateOne(record,
                new Document("$set", new Document("replyCount", threadMetadata.replyCount++)));
		
		//should wait on result.
		if(!updatedResult.wasAcknowledged()){
			return null;
		}else{
			//only way to get back result?
			return getBySubject(threadMetadata.subject);
		}
	}

	@Override
	public ThreadMetadata add(ThreadMetadata threadMetadata) {
		//TODO Check if threadMetadata is valid.
		db.getCollection("messages").insertOne(
                new Document()
                    .append("subject", threadMetadata.subject)
                    .append("replyCount", new Integer(threadMetadata.replyCount))
        );
		return getBySubject(threadMetadata.subject);
	}
}
