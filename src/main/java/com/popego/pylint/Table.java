package com.popego.pylint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
	private String[] headers;
	private List<Map<String,String>> rows;
	private String title;
	
	public Table(String title, String[] headers, List<Map<String,String>> rows) {
		this.title = title;
		this.headers = headers.clone();
		this.rows = new ArrayList<Map<String,String>>();
		for( Map<String,String> row: rows ) {
			this.rows.add(new HashMap<String,String>(row));
		}
	}
	
	public final String[] getHeaders() {
		return headers;
	}
	
	public final List<Map<String,String>> getRows() {
		return rows;
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public String toString() {
		StringBuilder answer = new StringBuilder();
		answer.append(title);
		answer.append('\n');
		for( int i=0; i<title.length(); i++ )
			answer.append('-');
		answer.append('\n');
		for( String header: headers ) {
			answer.append(String.format("|%s", header));
		}
		answer.append("\n----------------------------------------------\n");
		for( Map<String,String> row: rows ) {
			for( String header: headers ) {
				answer.append(String.format("|%s", row.get(header)));		
			}
			answer.append('\n');
		}
		answer.append("\n----------------------------------------------\n");
		return answer.toString();
	}

	public static class Builder {
		private String[] headers;
		private List<Map<String,String>> rows;
		private String title;
		
		public Builder() {
			clean();
		}
		
		public void clean() {
			headers = null;
			rows = new ArrayList<Map<String,String>>();
			title = null;
		}
		public void setHeaders(String line) {
			String[] p = line.trim().split("\\|");
			headers = new String[p.length];
			for( int i=0; i<headers.length; i++ ) {
				headers[i] = p[i].trim().toLowerCase();
			}
		}
	
		public void addRow(String line) {
			if( !line.trim().isEmpty() ) {
				Map<String,String> row = new HashMap<String,String>();
				String[] vals =line.trim().split("\\|");
				for( int i=0; i<headers.length; i++ ) {
					row.put(headers[i],vals[i].trim());
				}
				rows.add(row);
			}
		}
	
		public boolean hasTitle() {
			return title != null;
		}
	
		public Table build() {
			Table answer;
			if( title == null || headers == null || rows.isEmpty() )
				answer = null;
			else 
				answer = new Table(title, headers, rows);
			clean();
			return answer;
			
		}

		public void setTitle(String line) {
			this.title = line.trim();			
		}

		public String getTitle() {
			return title;
		}
		
	}
}