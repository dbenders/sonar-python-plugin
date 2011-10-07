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
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.api.rules.RuleRepository;
import org.sonar.plugins.python.Python;

public class PyLintRuleRepository extends RuleRepository implements BatchExtension {

  private PyLintRuleManager pyLintRuleManager;

  public PyLintRuleRepository(Python python, PyLintRuleManager pyLintRuleManager) {
    super(REPOSITORY_KEY, python.getKey());
    setName(REPOSITORY_NAME);

    this.pyLintRuleManager = pyLintRuleManager;
  }

  public static final String REPOSITORY_NAME = "Python";
  public static final String REPOSITORY_KEY = "Python";

  @Override
  public List<Rule> createRules() {

    List<Rule> rulesList = new ArrayList<Rule>();

    for (PyLintRule pyLintRule : pyLintRuleManager.getPyLintRules()) {
      Rule rule = Rule.create(REPOSITORY_KEY, pyLintRule.getKey(), pyLintRule.getName());

      rule.setDescription(pyLintRule.getDescription());
      rule.setPriority(pyLintRule.getPriority());

      for (RuleParam ruleParam : pyLintRule.getParams()) {
        RuleParam param = rule.createParameter();
        param.setKey(ruleParam.getKey());
        param.setDefaultValue(ruleParam.getDefaultValue());
        param.setDescription(ruleParam.getDescription());
        param.setType(ruleParam.getType());
      }

      // this is removed in Sonar 2.5
      // rule.setRulesCategory(Iso9126RulesCategories.MAINTAINABILITY);

      rulesList.add(rule);
    }

    return rulesList;
  }
}