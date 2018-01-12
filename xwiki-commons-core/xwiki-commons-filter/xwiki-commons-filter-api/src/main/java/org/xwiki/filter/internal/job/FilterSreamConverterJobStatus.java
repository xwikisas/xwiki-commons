/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.filter.internal.job;

import org.xwiki.filter.job.FilterStreamConverterJobRequest;
import org.xwiki.job.DefaultJobStatus;
import org.xwiki.job.annotation.Serializable;
import org.xwiki.job.event.status.JobStatus;
import org.xwiki.logging.LoggerManager;
import org.xwiki.observation.ObservationManager;

/**
 * Job status for {@link FilterSreamConverterJobStatus}.
 * 
 * @version $Id$
 */
@Serializable(false)
public class FilterSreamConverterJobStatus extends DefaultJobStatus<FilterStreamConverterJobRequest>
{
    /**
     * @param jobType the type of the job
     * @param request the request provided when started the job
     * @param parentJobStatus the status of the parent job (i.e. the status of the job that started this one); pass
     *            {@code null} if this job hasn't been started by another job (i.e. if this is not a sub-job)
     * @param observationManager the observation manager component
     * @param loggerManager the logger manager component
     */
    public FilterSreamConverterJobStatus(String jobType, FilterStreamConverterJobRequest request,
        JobStatus parentJobStatus, ObservationManager observationManager, LoggerManager loggerManager)
    {
        super(FilterStreamConverterJob.JOBTYPE, request, parentJobStatus, observationManager, loggerManager);

        setIsolated(false);
    }

}
