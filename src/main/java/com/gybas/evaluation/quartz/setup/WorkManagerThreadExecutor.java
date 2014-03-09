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

import commonj.work.Work;
import commonj.work.WorkManager;
import org.quartz.spi.ThreadExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Copied from {@link org.quartz.commonj.WorkManagerThreadExecutor} until
 * https://jira.terracotta.org/jira/browse/QTZ-433 is fixed.
 */
public class WorkManagerThreadExecutor implements ThreadExecutor {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String workManagerName;
    private WorkManager workManager;

    public void execute(Thread thread) {
        Work work = new DelegatingWork(thread);
        try {
            this.workManager.schedule(work);
        } catch (Exception e) {
            log.error("Error attempting to schedule QuartzSchedulerThread: " + e.getMessage(), e);
        }
    }

    public void initialize() {
        try {
            this.workManager = (WorkManager) new InitialContext().lookup(workManagerName);
        } catch (NamingException e) {
            throw new IllegalStateException("Could not locate WorkManager: " + e.getMessage(), e);
        }
    }

    /**
     * Sets the JNDI name of the work manager to use.
     *
     * @param workManagerName the JNDI name to use to lookup the work manager
     */
    public void setWorkManagerName(String workManagerName) {
        this.workManagerName = workManagerName;
    }

}

class DelegatingWork implements Work {

    private final Runnable delegate;

    /**
     * Create a new DelegatingWork.
     *
     * @param delegate the Runnable implementation to delegate to
     */
    public DelegatingWork(Runnable delegate) {
        this.delegate = delegate;
    }

    /**
     * @return the wrapped Runnable implementation.
     */
    public final Runnable getDelegate() {
        return this.delegate;
    }

    /**
     * Delegates execution to the underlying Runnable.
     */
    public void run() {
        this.delegate.run();
    }

    public boolean isDaemon() {
    	// changed by sgybas to fix https://jira.terracotta.org/jira/browse/QTZ-433
        return true;
    }

    /**
     * This implementation is empty, since we expect the Runnable to terminate
     * based on some specific shutdown signal.
     */
    public void release() {
    	// empty
    }

}
