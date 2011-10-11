package org.sonar.plugins.python.pylint;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PyLintCsvRuleParserTest {

	private PyLintCsvRuleParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new PyLintCsvRuleParser();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testParseBig() throws IOException {
		Reader reader = new InputStreamReader(
				getClass().getResourceAsStream("rules_big.csv"));
		
		List<PyLintRule> rules = parser.parse(reader);
		assertEquals(131, rules.size());
	}

	@Test
	public void testParseSmall() throws IOException {
		Reader reader = new InputStreamReader(
				getClass().getResourceAsStream("rules_big.csv"));
		
		List<PyLintRule> rules = parser.parse(reader);
		assertEquals("C0102", rules.get(0).getCode());
		assertEquals("Black listed name \"%s\"", rules.get(0).getMessage());
		
		assertEquals("C0103", rules.get(1).getCode());
		assertEquals("Invalid name \"%s\"(should match %s)", rules.get(1).getMessage());
	}
}
