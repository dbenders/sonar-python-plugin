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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.python.cobertura.PythonCoberturaSensor;
import org.sonar.plugins.python.pylint.PyLintRuleManager;
import org.sonar.plugins.python.pylint.PyLintSensor;
import org.sonar.plugins.python.pylint.PyLintRuleRepository;
import org.sonar.plugins.python.surefire.PythonSurefireSensor;

@Properties({
    @Property(key = PythonPlugin.FILE_SUFFIXES_KEY, defaultValue = PythonPlugin.FILE_SUFFIXES_DEFVALUE, name = "File suffixes",
        description = "Comma-separated list of suffixes for files to analyze. To not filter, leave the list empty.", global = true,
        project = true)
})
public class PythonPlugin implements Plugin {

  public String getKey() {
    return PYTHON_PLUGIN;
  }

  public String getName() {
    return "Python";
  }

  public String getDescription() {
    return "Analysis of Python projects";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    //list.add(PythonColorizerFormat.class);
    list.add(Python.class);
    list.add(PythonSourceImporter.class);

    //list.add(PythonCpdMapping.class);

    list.add(PyLintRuleRepository.class);

    //list.add(PythonSquidSensor.class);

    list.add(PyLintSensor.class);

    list.add(PyLintRuleManager.class);

    list.add(PythonDefaultProfile.class);
    
    //list.add(PythonComplexitySensor.class);
    
    list.add(PythonSurefireSensor.class);

    list.add(PythonCoberturaSensor.class);
    
    return list;
  }

  public final static String FALSE = "false";
  public final static String PYTHON_PLUGIN = "PythonPlugin";

  public static final String FILE_SUFFIXES_KEY = "sonar.python.file.suffixes";
  public static final String FILE_SUFFIXES_DEFVALUE = "py";

  public static final String[] GLOBAL_PARAMETERS = new String[] {};
}
