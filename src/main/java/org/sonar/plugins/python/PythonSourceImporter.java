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

package org.sonar.plugins.python;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSourceImporter;

public class PythonSourceImporter extends AbstractSourceImporter {

	private static Logger logger = LoggerFactory.getLogger(PythonSourceImporter.class);

	public PythonSourceImporter(Python python) {
		super(python);
	}

	@Override
	protected PythonFile createResource(File file, List<File> sourceDirs, boolean unitTest) {
		if( file == null )
			return null;
		PythonFile answer = PythonFile.fromIOFile(file, sourceDirs, unitTest);
		logger.debug(String.format("Importing file %s", answer));
		return answer;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
