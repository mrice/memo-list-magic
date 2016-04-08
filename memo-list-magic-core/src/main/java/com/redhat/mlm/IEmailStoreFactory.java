package com.redhat.mlm;

import javax.mail.Store;

public interface IEmailStoreFactory {
	Store getEmailStore();
}
