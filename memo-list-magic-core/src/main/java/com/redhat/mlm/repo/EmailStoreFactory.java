package com.redhat.mlm.repo;

import javax.mail.Store;

public interface EmailStoreFactory {
	Store getEmailStore();
}
