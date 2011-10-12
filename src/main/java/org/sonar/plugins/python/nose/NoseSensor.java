/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.python.nose;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.plugins.python.Python;

public class NoseSensor implements Sensor {

	private static final Logger LOG = LoggerFactory.getLogger(NoseSensor.class);

	public boolean shouldExecuteOnProject(Project project) {
		return project.getAnalysisType().isDynamic(true) && Python.KEY.equals(project.getLanguageKey());
	}

	public void analyse(Project project, SensorContext sensorContext) {
	    File reportFile = NoseUtils.getReport(project);
	    if( reportFile != null ) {
		    LOG.debug("Analyzing xunit report {}", reportFile.getAbsolutePath());
	    	collect(project, sensorContext, reportFile);
	    }
	}

	protected void collect(Project project, SensorContext context, File reportsFile) {
		new NoseParser() {
		}.collect(project, context, reportsFile);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
