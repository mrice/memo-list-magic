package com.redhat.mlm.repo;

import com.redhat.mlm.model.ThreadMetadata;

public interface ThreadMetadataRepo {
	ThreadMetadata getBySubject(String subject);
	ThreadMetadata update(ThreadMetadata threadMetadata);
	ThreadMetadata add(ThreadMetadata threadMetadata);
}
