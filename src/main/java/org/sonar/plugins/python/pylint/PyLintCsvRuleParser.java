package org.sonar.plugins.python.pylint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVParser;

public class PyLintCsvRuleParser {

	public List<PyLintRule> parse(Reader reader) throws IOException {
		List<PyLintRule> answer = new ArrayList<PyLintRule>();
		CSVParser parser = new CSVParser(';','"');
		BufferedReader br = new BufferedReader(reader);
		String s;
		while( (s=br.readLine()) != null ) {
			String[] row = parser.parseLine(s);
			answer.add(new PyLintRule(row[0], row[1]));
		}
		return answer;
	}
	
}
