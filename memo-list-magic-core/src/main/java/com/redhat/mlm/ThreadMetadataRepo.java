package com.redhat.mlm;

public interface ThreadMetadataRepo {
	ThreadMetadata getBySubject(String subject);
	ThreadMetadata update(ThreadMetadata threadMetadata);
	ThreadMetadata add(ThreadMetadata threadMetadata);
}
