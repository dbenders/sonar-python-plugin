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

import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.python.Python;
import org.sonar.plugins.python.PythonFile;

public class PythonCoberturaSensor extends AbstractCoverageExtension implements Sensor {
	  private Python python;
//  private PythonCoberturaMavenPluginHandler handler;

//  public PythonCoberturaSensor(PythonCoberturaMavenPluginHandler handler) {
//    this.handler = handler;
//  }

  public PythonCoberturaSensor(Python python) {
	  this.python = python;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    //return super.shouldExecuteOnProject(project) && project.getFileSystem().hasJavaSourceFiles();
	  return project.getLanguage().equals(python);
  }

  public void analyse(Project project, SensorContext context) {
    File report = CoberturaUtils.getReport(project);
    if (report != null) {
      parseReport(report, context);
    }
  }

//  public MavenPluginHandler getMavenPluginHandler(Project project) {
//    if (project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC)) {
//      return handler;
//    }
//    return null;
//  }

  protected void parseReport(File xmlFile, final SensorContext context) {
    LoggerFactory.getLogger(PythonCoberturaSensor.class).info("parsing {}", xmlFile);
    new AbstractCoberturaParser() {
      @Override
      protected Resource<?> getResource(String fileName) {
    	LoggerFactory.getLogger(PythonCoberturaSensor.class).debug("resource {}", fileName);
        return new PythonFile(fileName, false);
      }
    }.parseReport(xmlFile, context);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}