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

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForTotalCount;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Registers the {@link UserTransactionLookupJob} with the Quartz scheduler.
 * 
 * @author Stefan Gybas
 */
public class UserTransactionLookupJobRegistrar implements InitializingBean {
	
	static final String JOB_NAME = " UserTransactionLookupJob";
	static final String TRIGGER_NAME = " UserTransactionLookupTrigger";

	private static final Logger LOGGER = LoggerFactory.getLogger(UserTransactionLookupJobRegistrar.class);

	// injected dependencies
	private Scheduler scheduler;
	private int totalCount;

	@Override
	public void afterPropertiesSet() {
		final JobDetail job = newJob(UserTransactionLookupJob.class)
				.withIdentity(JOB_NAME)
				.build();
		final JobKey jobKey = job.getKey();

		final Trigger trigger = newTrigger()
				.withIdentity(TRIGGER_NAME)
				.startNow()
				.withSchedule(repeatSecondlyForTotalCount(totalCount))
				.build();

		try {
			if (scheduler.deleteJob(jobKey)) {
				LOGGER.info("Deleted existing Quartz job [" + jobKey + "]");
			}
			scheduler.scheduleJob(job, trigger);
			LOGGER.info("Registered Quartz job [" + jobKey + "]");
		} catch (SchedulerException e) {
			LOGGER.warn("Could not register Quartz job [" + jobKey + "]", e);
		}
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
