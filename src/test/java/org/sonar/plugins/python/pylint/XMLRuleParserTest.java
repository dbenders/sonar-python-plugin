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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.List;

import junit.framework.Assert;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.SonarException;

public class XMLRuleParserTest {

  @Test
  public void parseXml() {
    List<PyLintRule> rules = new PyLintXmlRuleParser().parse(getClass().getResourceAsStream(
        "/org/sonar/plugins/python/pylint/rules.xml"));
    assertThat(rules.size(), is(3));

    PyLintRule rule = rules.get(0);
    assertThat(rule.getName(), is("Line too long."));
    assertThat(
        rule.getDescription(),
        is("Checks the maximum number of characters in a line."));
    assertThat(rule.getPriority(), Is.is(RulePriority.MINOR));
    Assert.assertNull(rule.getRulesCategory());

    assertThat(rule.getMessages().size(), is(1));
    assertThat(rule.isInverse(), is(false));

    PyLintRule minimalRule = rules.get(0);
    assertThat(minimalRule.getKey(), is("MAXLEN"));
    assertThat(minimalRule.getParams().size(), is(1));
    assertThat(minimalRule.isInverse(), is(false));

  }

  @Test(expected = SonarException.class)
  public void failIfMissingRuleKey() {
    new PyLintXmlRuleParser().parse(new StringReader("<rules><rule><name>Foo</name></rule></rules>"));
  }

  @Test(expected = SonarException.class)
  public void failIfMissingPropertyKey() {
    new PyLintXmlRuleParser().parse(new StringReader("<rules><rule><key>foo</key><name>Foo</name><param></param></rule></rules>"));
  }

  @Test
  public void utf8Encoding() {
    List<PyLintRule> rules = new PyLintXmlRuleParser().parse(getClass().getResourceAsStream(
        "/org/sonar/api/rules/XMLRuleParserTest/utf8.xml"));
    assertThat(rules.size(), is(1));
    PyLintRule rule = rules.get(0);
    assertThat(rule.getKey(), is("com.puppycrawl.tools.checkstyle.checks.naming.LocalVariableNameCheck"));
    assertThat(rule.getName(), is("M & M"));
    assertThat(rule.getDescription().charAt(0), is('\u00E9'));
    assertThat(rule.getDescription().charAt(1), is('\u00E0'));
    assertThat(rule.getDescription().charAt(2), is('\u0026'));
  }
}