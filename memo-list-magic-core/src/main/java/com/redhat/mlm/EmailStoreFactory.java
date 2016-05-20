package com.redhat.mlm;

import javax.mail.Store;

public interface EmailStoreFactory {
	Store getEmailStore();
}
