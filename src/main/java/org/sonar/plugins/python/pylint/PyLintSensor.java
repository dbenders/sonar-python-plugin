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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.plugins.python.Python;

public class PyLintSensor implements Sensor {

	private static final Logger LOG = LoggerFactory.getLogger(PyLintSensor.class);

	private Configuration configuration;
	private RulesProfile rulesProfile;
	private RuleFinder ruleFinder;
	private PyLintRuleManager pyLintRuleManager;

	public PyLintSensor(RuleFinder ruleFinder, RulesProfile rulesProfile,
			PyLintRuleManager pyLintRuleManager, Configuration configuration) {
		this.configuration = configuration;
		this.ruleFinder = ruleFinder;
		this.rulesProfile = rulesProfile;
		this.pyLintRuleManager = pyLintRuleManager;
		initializePyLint();
	}

	private boolean isActivated(String ruleKey, List<ActiveRule> rules) {
		for (ActiveRule rule: rules) {
			if (ruleKey.equals(rule.getRuleKey())) {
				return true;
			}
		}
		return false;
	}

	public void analyse(Project project, SensorContext sensorContext) {
	    File reportFile = PyLintUtils.getReport(project);
	    if( reportFile != null ) {
		    LOG.debug("Analyzing pylint report {}", reportFile.getAbsolutePath());
	    	collect(project, sensorContext, reportFile);
	    }
	}
	
	protected void collect(final Project project, final SensorContext sensorContext, final File reportFile) {
		SonarPyLintParser parser = new SonarPyLintParser(project, sensorContext, ruleFinder);
		Reader reportReader = null;
		try {
			reportReader = new FileReader(reportFile);
			parser.parseReport(reportReader);
		} catch (IOException e) {
		} finally {
			if( reportReader != null ) {
				IOUtils.closeQuietly(reportReader);
			}
		}		
	}

	public boolean shouldExecuteOnProject(Project project) {
		return Python.KEY.equals(project.getLanguageKey());
		//project.getLanguage().equals(python);
	}

	private void initializePyLint() {
		RuleQuery query = RuleQuery.create();
		query.withRepositoryKey(PyLintRuleRepository.REPOSITORY_KEY);

		List<ActiveRule> activeRules = this.rulesProfile.getActiveRules();
		LOG.debug("Adding PyLint options. Activated rules: {}", activeRules.size());

//		// set PyLint options for activated rules
//		for (Option option : Option.values()) {
//			if ( isActivated(option.name(), activeRules)) {
//				LOG.debug("Adding PyLint option from rule: {}", option.name());
//				this.pyLint.addOption(option);
//
//      	} 
//
//		}

    /*
     * order of these two functions is important as values set from project/global settings can be overwritten by rule parameters
     */
		setOptionsSpecifiedAsProjectSettings();
    	setOptionsSpecifiedAsRuleParameters(activeRules);
	}

	private void setOptionsSpecifiedAsRuleParameters(List<ActiveRule> activeRules) {
//		LOG.debug("Adding Options Specified As Rule Parameters");
//
//		for (ActiveRule activeRule : activeRules) {
//			for (ActiveRuleParam activeRuleParam : activeRule.getActiveRuleParams()) {
//
//				String value = activeRuleParam.getValue();
//				Option option = pyLintRuleManager.getOptionByName(activeRuleParam.getKey());
//
//				LOG.debug("Rule: " + activeRule.getRuleKey() + ", ruleParam: " + activeRuleParam.getKey() + ", ruleParamValue: " + value);
//
//				if (option != null && value != null) {
//					LOG.debug("Adding PyLint option from rule parameter: {} with value: {}", option.name(), value);
//					this.pyLint.addOption(option, value);
//				}
//
//			}
//		}
	}

	private void setOptionsSpecifiedAsProjectSettings() {
//		LOG.debug("Adding Options Specified As Project Settings");
//		for (String fullparameterName : PythonPlugin.GLOBAL_PARAMETERS) {
//			String parameterName = fullparameterName.substring(fullparameterName.lastIndexOf(".") + 1);
//			String value = configuration.getString(fullparameterName);
//			
//			LOG.debug("Project/global setting name retrieved from global parameter: {} with value {}", parameterName, value);
//			Option option = pyLintRuleManager.getOptionByName(parameterName);
//
//			if (option != null && value != null) {
//				LOG.debug("Adding PyLint option from project/global settings: {} with value: {}", option, value);
//
//				this.pyLint.addOption(option, value);
//			}
//		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
