/*
 * Copyright 2014 Stefan Gybas-
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.gybas.evaluation.quartz.jobs;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jndi.JndiTemplate;

/**
 * Spring bean with a dependency that performs a JNDI lookup for the JTA user transaction. The
 * lookup will fail if the current thread is not managed by WebSphere since the thread can't be
 * associated with an application in this case.
 * 
 * @author Stefan Gybas
 */
public class UserTransactionLookup implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserTransactionLookup.class);
	private static final String USER_TRANSACTION_JNDI_NAME = "java:comp/UserTransaction";

	// injected dependencies
	private final JndiTemplate jndiTemplate;

	@Inject
	public UserTransactionLookup(JndiTemplate jndiTemplate) {
		this.jndiTemplate = jndiTemplate;
	}

	@Override
	public void run() {
		try {
			final UserTransaction userTransaction = jndiTemplate.lookup(USER_TRANSACTION_JNDI_NAME,
					UserTransaction.class);
			LOGGER.info("Status of user transaction is [{}]", userTransaction.getStatus());
		} catch (NamingException e) {
			LOGGER.error("Could not lookup user transaction", e);
		} catch (SystemException e) {
			LOGGER.error("Could not determine user transaction status", e);
		}
	}
}
