package com.redhat.mlm;

public interface IThreadMetadataRepo {
	ThreadMetadata getBySubject(String subject);
	ThreadMetadata update(ThreadMetadata threadMetadata);
	ThreadMetadata add(ThreadMetadata threadMetadata);
}
