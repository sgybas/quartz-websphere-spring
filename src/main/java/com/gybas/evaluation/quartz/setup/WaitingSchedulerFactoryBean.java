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
package com.gybas.evaluation.quartz.setup;

import org.quartz.SchedulerException;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Waits for all Quartz jobs to complete when {@link #stop()} is called and
 * {@link #setWaitForJobsToCompleteOnShutdown(boolean)} is set to {@code true}. This avoids problems
 * with Quartz jobs that access beans in Spring's application context. These beans might already be
 * destroyed so the jobs fails.
 * 
 * @author Stefan Gybas
 */
public class WaitingSchedulerFactoryBean extends SchedulerFactoryBean {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		try {
			super.destroy();
		} catch (SchedulerException e) {
			throw new SchedulingException("Could not destroy scheduler", e);
		}
	}
}
