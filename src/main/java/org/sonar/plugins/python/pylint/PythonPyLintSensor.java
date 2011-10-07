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

package org.sonar.plugins.python.pylint;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.python.Python;
import org.sonar.plugins.python.PythonFile;
import org.sonar.plugins.python.PythonPlugin;

import com.googlecode.pylint4java.Issue;
import com.googlecode.pylint4java.Message;
import com.googlecode.pylint4java.Option;
import com.googlecode.pylint4java.PyLint;
import com.googlecode.pylint4java.PyLintBuilder;
import com.googlecode.pylint4java.PyLintResult;
import com.googlecode.pylint4java.Report;

public class PythonPyLintSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(PythonPyLintSensor.class);

  private Configuration configuration;
  private RulesProfile rulesProfile;
  private RuleFinder ruleFinder;
  private Python python;
  private PyLint pyLint;
  private PyLintRuleManager pyLintRuleManager;

  public PythonPyLintSensor(RuleFinder ruleFinder, Python python, RulesProfile rulesProfile,
      PyLintRuleManager pyLintRuleManager, Configuration configuration) {
    this.configuration = configuration;
    this.ruleFinder = ruleFinder;
    this.python = python;
    this.rulesProfile = rulesProfile;
    this.pyLintRuleManager = pyLintRuleManager;
    this.pyLint = new PyLintBuilder().fromDefault();

    LOG.debug("Using PyLint version: {}", this.pyLint.getVersion());

    initializePyLint();

  }

  private boolean isActivated(String ruleKey, List<ActiveRule> rules) {
    for (ActiveRule rule : rules) {
      if (ruleKey.equals(rule.getRuleKey())) {
        return true;
      }
    }
    return false;
  }

  public void analyse(Project project, SensorContext sensorContext) {
    for (File pythonFile : project.getFileSystem().getSourceFiles(python)) {
      try {
        analyzeFile(pythonFile, project.getFileSystem(), sensorContext);
      } catch (IOException e) {
        LOG.error("Can not analyze the file {}", pythonFile.getAbsolutePath());
      }
    }
  }

  protected void analyzeFile(File file, ProjectFileSystem projectFileSystem, SensorContext sensorContext) throws IOException {

    Resource resource = PythonFile.fromIOFile(file, projectFileSystem.getSourceDirs());

    Reader reader = null;
    try {
      //reader = new StringReader(FileUtils.readFileToString(file, projectFileSystem.getSourceCharset().name()));

      PyLintResult result = pyLint.lint(file.getPath());

      // capture function count in file      
      Report report = result.getReport("Statistics by type");
      if( report != null ) {
    	  for( Map<String,String> row: report.getRows() ) {
			  int number = Integer.parseInt(row.get("number"));
			  String type = row.get("type");
    		  if( "function".equals(type) ) {
    			  sensorContext.saveMeasure(resource, CoreMetrics.FUNCTIONS, (double) number);
    		        LOG.debug("PyLint number of functions {}", number);
    		  } else if( "class".equals(type) ) {
    			  sensorContext.saveMeasure(resource, CoreMetrics.CLASSES, (double) number);
    		        LOG.debug("PyLint number of classes {}", number);
    		  } else if( "module".equals(type) ) {
    			  sensorContext.saveMeasure(resource, CoreMetrics.PACKAGES, (double) number);
    		        LOG.debug("PyLint number of modules {}", number);
			  } else if( "method".equals(type) ) {
				  // TODO
			  }
    	  }
      }

      report = result.getReport("Raw metrics");
      if( report != null ) {
    	  for( Map<String,String> row: report.getRows() ) {
			  int number = Integer.parseInt(row.get("number"));
			  String type = row.get("type");
    		  if( "code".equals(type) ) {
  			  		sensorContext.saveMeasure(resource, CoreMetrics.NCLOC, (double) number);
		        	LOG.debug("PyLint number of code lines {}", number);    			  
    		  } else if( "docstring".equals(type) ) {
    			  // TODO
    		  } else if( "comment".equals(type) ) {
    			  	sensorContext.saveMeasure(resource, CoreMetrics.COMMENT_LINES, (double) number);
  		        	LOG.debug("PyLint number of comment lines {}", number);    			  
    		  } else if( "empty".equals(type) ) {
    			  // TODO
    		  }
    	  }
      }
    		  
      // process issues found by PyLint
      List<Issue> issues = result.getIssues();
      for (Issue issue : issues) {

        LOG.debug("PyLint warning message {}", issue.toString());

        Rule rule = ruleFinder.findByKey(PythonRuleRepository.REPOSITORY_KEY, pyLintRuleManager.getRuleIdByMessage(issue.getMessage()));

        Violation violation = Violation.create(rule, resource);

        violation.setLineId(issue.getLineNumber());
        violation.setMessage(issue.getMessage());

        sensorContext.saveViolation(violation);
      }
      
      // add pylint index
      Message msg = result.getMessage("Global evaluation");
      if( msg != null ) {
    	  	Matcher matcher = Pattern.compile("Your code has been rated at (.*)/10").matcher(msg.getMessage());
    	  	if( matcher.find() ) {
    	  		double number = Double.parseDouble(matcher.group(1));
    	  		sensorContext.saveMeasure(resource, CoreMetrics.VIOLATIONS_DENSITY, number);
    	  		LOG.debug("PyLint evaluation number {}", number);
    	  	}
      }
      
      // TODO: add special violation for unused names
//      List<PyIdentifier> unused = result.getUnused();
//      for (PyIdentifier unusedName : unused) {
//        Violation violation = Violation.create(
//            ruleFinder.findByKey(PythonRuleRepository.REPOSITORY_KEY, PyLintRuleManager.UNUSED_NAMES_KEY), resource);
//
//        violation.setLineId(unusedName.getLine());
//        violation.setMessage("'" + unusedName.getName() + "' is unused");
//
//        sensorContext.saveViolation(violation);
//      }

    } catch (InterruptedException e) {
	} finally {
      IOUtils.closeQuietly(reader);
    }

  }

  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(python);
  }

  private void initializePyLint() {
    RuleQuery query = RuleQuery.create();
    query.withRepositoryKey(PythonRuleRepository.REPOSITORY_KEY);

    List<ActiveRule> activeRules = this.rulesProfile.getActiveRules();
    LOG.debug("Adding PyLint options. Activated rules: {}", activeRules.size());

    // set PyLint options for activated rules
    for (Option option : Option.values()) {
      // not inverse rule and activated
      if ( !pyLintRuleManager.isRuleInverse(option.name()) && isActivated(option.name(), activeRules)) {

        LOG.debug("Adding PyLint option from rule: {}", option.name());
        this.pyLint.addOption(option);

        // inverse rule and not activated
      } else if (pyLintRuleManager.isRuleInverse(option.name()) && !isActivated(option.name(), activeRules)) {

        LOG.debug("Adding PyLint option from inverse rule:  {}", option.name());
        this.pyLint.addOption(option);
      }

    }

    /*
     * order of these two functions is important as values set from project/global settings can be overwritten by rule parameters
     */
    setOptionsSpecifiedAsProjectSettings();
    setOptionsSpecifiedAsRuleParameters(activeRules);

  }

  private void setOptionsSpecifiedAsRuleParameters(List<ActiveRule> activeRules) {
    LOG.debug("Adding Options Specified As Rule Parameters");

    for (ActiveRule activeRule : activeRules) {
      for (ActiveRuleParam activeRuleParam : activeRule.getActiveRuleParams()) {

        String value = activeRuleParam.getValue();
        Option option = pyLintRuleManager.getOptionByName(activeRuleParam.getKey());

        LOG.debug("Rule: " + activeRule.getRuleKey() + ", ruleParam: " + activeRuleParam.getKey() + ", ruleParamValue: " + value);

        /*
         * predefined variables are already set from project global settings, this will concatenate value with value set on rule be defined
         * on rule level and
         */
        if (Option.PREDEF.equals(option)) {
          String predefinedVariablesOnProjectLevel = configuration.getString(PythonPlugin.PREDEFINED_KEY, "");
          if ( !"".equals(predefinedVariablesOnProjectLevel.trim())) {
            value = value + "," + predefinedVariablesOnProjectLevel;
          }
        }

        if (option != null && value != null) {

          LOG.debug("Adding PyLint option from rule parameter: {} with value: {}", option.name(), value);

          this.pyLint.addOption(option, value);
        }

      }
    }
  }

  private void setOptionsSpecifiedAsProjectSettings() {
    LOG.debug("Adding Options Specified As Project Settings");
    for (String fullparameterName : PythonPlugin.GLOBAL_PARAMETERS) {

      String parameterName = fullparameterName.substring(fullparameterName.lastIndexOf(".") + 1);

      String value = configuration.getString(fullparameterName);

      LOG.debug("Project/global setting name retrieved from global parameter: {} with value {}", parameterName, value);

      Option option = pyLintRuleManager.getOptionByName(parameterName);

      if (option != null && value != null) {
        LOG.debug("Adding PyLint option from project/global settings: {} with value: {}", option, value);

        this.pyLint.addOption(option, value);
      }
    }

  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
