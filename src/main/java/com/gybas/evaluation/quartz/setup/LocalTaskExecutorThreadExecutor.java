/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
package com.gybas.evaluation.quartz.setup;

import java.util.concurrent.Executor;

import org.quartz.spi.ThreadExecutor;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.scheduling.quartz.LocalTaskExecutorThreadPool;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz ThreadExecutor adapter that delegates to a Spring-managed TaskExecutor
 * instance, specified on SchedulerFactoryBean.
 * 
 * @author Stefan Gybas
 * @see LocalTaskExecutorThreadPool
 * @see SchedulerFactoryBean#setTaskExecutor
 */
public class LocalTaskExecutorThreadExecutor implements ThreadExecutor {

	/**
	 * Prevents a warning about possibly hanging threads with a CommonJ WorkManager on
	 * WebSphere Application Server.
	 */
	static class LongLivedRunnable implements SchedulingAwareRunnable {

		private final Runnable runnable;

		LongLivedRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			this.runnable.run();
		}

		@Override
		public boolean isLongLived() {
			return true;
		}
	}

	/** Spring task executor that is configured in SchedulerFactoryBean. */
	private Executor taskExecutor;

	@Override
	public void initialize() {
		// Absolutely needs thread-bound TaskExecutor to initialize.
		this.taskExecutor = SchedulerFactoryBean.getConfigTimeTaskExecutor();
		if (this.taskExecutor == null) {
			throw new IllegalStateException("No local TaskExecutor found for configuration - "
					+ "'taskExecutor' property must be set on SchedulerFactoryBean");
		}
	}

	@Override
	public void execute(Thread thread) {
		this.taskExecutor.execute(new LongLivedRunnable(thread));
	}
}
