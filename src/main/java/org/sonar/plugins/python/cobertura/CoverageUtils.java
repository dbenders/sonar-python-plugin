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

import org.sonar.api.CoreProperties;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenUtils;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.Logs;

/**
 * @since 2.4
 */
public final class CoverageUtils {

	//public static String COBERTURA_GROUP_ID = MavenUtils.GROUP_ID_CODEHAUS_MOJO;
  	//public static String COBERTURA_ARTIFACT_ID = "cobertura-maven-plugin";

	static final String COVERAGE_REPORT_PATH_PROPERTY = "sonar.nose.reportPath";

	public static String relativizePath(ProjectFileSystem fileSystem, String path) {
		File file = new File(path);
		if( !file.isAbsolute() )
			file = new File(fileSystem.getBasedir(), path);
		//File file = fileSystem.resolvePath(path);
		File parent = file.getParentFile();
		while( parent != null && !fileSystem.getSourceDirs().contains(parent) ) {
			parent = parent.getParentFile();
		}
		if( parent != null ) {
			String answer = file.getPath().substring(parent.getPath().length());
			if( answer.startsWith("/") )
				answer = answer.substring(1);
			return answer;
		} else 
			return path;
	}

	public static File getReport(Project project) {
		File report = getReportFromProperty(project);
//    	if (report == null) {
//      	report = getReportFromPluginConfiguration(project);
//    	}
		if (report == null) {
			report = getReportFromDefaultPath(project);
		}

		if (report == null || !report.exists() || !report.isFile()) {
			Logs.INFO.warn("Cobertura report not found at {}", report);
			report = null;
		}
		return report;
	}

	private static File getReportFromProperty(Project project) {
		String path = (String) project.getProperty(COVERAGE_REPORT_PATH_PROPERTY);
		if (path != null) {
			return project.getFileSystem().resolvePath(path);
		}
		return null;
	}

//  private static File getReportFromPluginConfiguration(Project project) {
//    MavenPlugin mavenPlugin = MavenPlugin.getPlugin(project.getPom(), COBERTURA_GROUP_ID, COBERTURA_ARTIFACT_ID);
//    if (mavenPlugin != null) {
//      String path = mavenPlugin.getParameter("outputDirectory");
//      if (path != null) {
//        return new File(project.getFileSystem().resolvePath(path), "coverage.xml");
//      }
//    }
//    return null;
//  }

	private static File getReportFromDefaultPath(Project project) {
		return new File(project.getFileSystem().getReportOutputDir(), "coverage.xml");
	}

	private CoverageUtils() {
	}

}
