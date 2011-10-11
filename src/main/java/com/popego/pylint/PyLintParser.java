package com.popego.pylint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;


public abstract class PyLintParser {
	private enum Section {ISSUES, REPORTS};

	private Issue.Builder issueBuilder;
	private Table.Builder tableBuilder;
	private String prevLine;
	private Section section;
	private boolean insideBlockString;
		
	protected void parseLine(String line) {
		
		if( section == Section.ISSUES ) {
			
			if( line.trim().isEmpty() ) {
				if( prevLine.trim().endsWith("\"\"\"") )
					insideBlockString = true;
				
				if( !insideBlockString && prevLine.trim().isEmpty() ) {
					section = Section.values()[section.ordinal() + 1];
				} else {
					prevLine = line;
				}
				return;
			} else if( line.trim().endsWith("\"\"\"") )
				insideBlockString = !insideBlockString;
			
			Issue issue = issueBuilder.addLine(line);
			if( issue != null ) {
				processIssue(issue);
			}
		} else if( section == Section.REPORTS ) {
			if( line.startsWith("---") ) {
				tableBuilder.setTitle(prevLine);
			}
			else if( line.startsWith("+===") ) {
				tableBuilder.setHeaders(prevLine);
			} else if( line.startsWith("+---") && tableBuilder.hasTitle() ) {
				tableBuilder.addRow(prevLine);
			} else if( line.trim().isEmpty() && prevLine.trim().isEmpty() ) {
				Table table = tableBuilder.build();
				if( table != null ) {
					processTable(table);
				}
			} else {
				if( !line.trim().isEmpty() && prevLine.startsWith("---") ) {
					Message message = new Message(tableBuilder.getTitle(), line);
					processMessage(message);
					tableBuilder.clean();
				}
			}
		}
		prevLine = line;
	}
	
	public void parseReport(Reader reportReader) throws IOException {
		issueBuilder = new Issue.Builder();
		tableBuilder = new Table.Builder();
		prevLine = "X";
		section = Section.ISSUES;
		insideBlockString = false;
		
		BufferedReader br = new BufferedReader(reportReader);
		String line;
		while( (line=br.readLine()) != null ) {
			parseLine(line);
		}
	}


	protected abstract void processIssue(Issue issue);
	
	protected abstract void processTable(Table table);
	
	protected abstract void processMessage(Message message);
}
