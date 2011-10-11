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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.ServerExtension;


public class PyLintRuleManager implements ServerExtension, BatchExtension {

	private static Logger logger = LoggerFactory.getLogger(PyLintRuleManager.class);
	
	private List<PyLintRule> rules = new ArrayList<PyLintRule>();
	
//	public static final String OTHER_RULES_KEY = "OTHER_RULES";
//	public static final String UNUSED_NAMES_KEY = "UNUSED_NAMES";
//	public static final String CYCLOMATIC_COMPLEXITY_KEY = "CYCLOMATIC_COMPLEXITY";

	public PyLintRuleManager() throws IOException {
		rules = new PyLintCsvRuleParser().parse(
				new InputStreamReader(getClass().getResourceAsStream("rules.csv")));
		logger.info("{} rules loader", rules.size());
	}

	public List<PyLintRule> getPyLintRules() {
		return rules;
	}

//	public String getRuleCodeByMessage(String message) {
//		for (PyLintRule rule : rules) {
//			if (rule.getMessage().equals(message)) {
//				return rule.getCode();
//			}
//		}
//		return OTHER_RULES_KEY;
//	}
}
