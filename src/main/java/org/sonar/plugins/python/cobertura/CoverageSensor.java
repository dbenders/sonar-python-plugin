/*
 * Sonar Python Plugin
 * Copyright (C) 2011 Eriks Nukis
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.python.cobertura;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.python.Python;
import org.sonar.plugins.python.PythonFile;
import org.sonar.plugins.python.nose.NoseSensor;

public class CoverageSensor implements Sensor {

	private static final Logger LOG = LoggerFactory.getLogger(CoverageSensor.class);

	public boolean shouldExecuteOnProject(Project project) {
		return project.getAnalysisType().isDynamic(true) && Python.KEY.equals(project.getLanguageKey());
	}

	public void analyse(Project project, SensorContext context) {      
		File report = CoverageUtils.getReport(project);

		if (report != null) {
			LOG.info("report: {}", report.getAbsolutePath());
			parseReport(project, report, context);
		} else {
			LOG.info("report is null!");
		}
	}

//  public MavenPluginHandler getMavenPluginHandler(Project project) {
//    if (project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC)) {
//      return handler;
//    }
//    return null;
//  }

	protected void parseReport(Project project, File xmlFile, final SensorContext context) {
		LOG.info("parsing {}", xmlFile);
		new CoverageParser(project).parseReport(xmlFile, context);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
