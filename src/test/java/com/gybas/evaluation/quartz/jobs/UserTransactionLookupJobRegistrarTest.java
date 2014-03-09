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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration tests using a Spring context for {@link UserTransactionLookupJobRegistrar}.
 * 
 * @author Stefan Gybas
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring-config/applicationContext-test.xml")
public final class UserTransactionLookupJobRegistrarTest {

	@Autowired
	Scheduler scheduler;

	@Test
	public void testUserTransactionLookupJobHasBeenRegistered() throws SchedulerException {
		final JobKey jobKey = new JobKey(UserTransactionLookupJobRegistrar.JOB_NAME);
		assertThat(scheduler.checkExists(jobKey)).isTrue();
	}
}
