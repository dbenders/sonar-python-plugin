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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.BatchExtension;
import org.sonar.api.ServerExtension;

import com.googlecode.pylint4java.Option;

public class PyLintRuleManager implements ServerExtension, BatchExtension {

  private List<PyLintRule> rules = new ArrayList<PyLintRule>();

  public static final String OTHER_RULES_KEY = "OTHER_RULES";
  public static final String UNUSED_NAMES_KEY = "UNUSED_NAMES";
  public static final String CYCLOMATIC_COMPLEXITY_KEY = "CYCLOMATIC_COMPLEXITY";

  public PyLintRuleManager() {

    rules = new PyLintXmlRuleParser().parse(PyLintRuleManager.class.getResourceAsStream("/org/sonar/plugins/python/pylint/rules.xml"));
    System.out.println("Python rules");
  }

  public List<PyLintRule> getPyLintRules() {
    return rules;
  }

  public String getRuleIdByMessage(String message) {
    for (PyLintRule rule : rules) {
      if (rule.hasMessage(message)) {
        return rule.getKey();
      }
    }
    return OTHER_RULES_KEY;
  }

  public boolean isRuleInverse(String ruleKey) {
    for (PyLintRule rule : rules) {
      if (ruleKey.equals(rule.getKey())) {
        return rule.isInverse();
      }
    }
    return false;
  }

  public Option getOptionByName(String name) {
    for (Option o : Option.values()) {
      if (o.name().equalsIgnoreCase(name)) {
        return o;
      }
    }
    return null;
  }
}
