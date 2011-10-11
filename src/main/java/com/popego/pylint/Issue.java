package com.popego.pylint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Issue {

	private final String fileName;
	private final int lineNumber;
	private final String code;
	private final String message;
	private final String className;
	private final ArrayList<String> additionalLines;

	public Issue(String fileName, int lineNumber, String code, String className,
			String message, List<String> additionalLines) {
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.code = code;
		this.message = message;
		this.className = className;
		this.additionalLines = new ArrayList<String>(additionalLines);
	}
	
	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
	public String getClassName() {
		return className;
	}

	public ArrayList<String> getAdditionalLines() {
		return additionalLines;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s:%d: [%s] %s", 
				fileName, lineNumber, code, message)); 
		for( String line: additionalLines) {
			builder.append('\n');
			builder.append(line);
		}
		return builder.toString();
	}

	public static class Builder {
		private String fileName;
		private int lineNumber;
		private String code;
		private String message;
		private String className;
		private List<String> additionalLines;
		private boolean prevLineEmpty;
		private static Pattern pattern = Pattern.compile("([^:]*):([0-9]*): \\[([^\\]]*)\\] (.*)");
		public Builder() {
			additionalLines = new ArrayList<String>();
		}
		
		public void clear() {
			additionalLines.clear();
			fileName = null;
		}
		
		public Issue addLine(String line) throws IndexOutOfBoundsException {
			if( line.trim().isEmpty() ) {
				if( prevLineEmpty ) {
					throw new IndexOutOfBoundsException();
				} else {
					prevLineEmpty = true;
				}
			} else {
				prevLineEmpty = false;
			}

			Matcher matcher = pattern.matcher(line);
			if( !matcher.matches() ) {
				additionalLines.add(line); 
				return null;
			} else {
				Issue answer = build();
				clear();
				setFileName(matcher.group(1));
				setLineNumber(Integer.parseInt(matcher.group(2)));
				String[] p = matcher.group(3).split(",");
				setCode(p[0]);
				if( p.length > 1 ) {
					setClassName(p[1]);
				}
				setMessage(matcher.group(4));
				return answer;
			}
		}		
		
		public Issue build() {
			if( fileName == null ) {
				return null;
			} else {
				return new Issue(fileName, lineNumber, code, className, message, additionalLines);
			}
		}

		private void setClassName(String className) {
			this.className = className;			
		}

		private void setFileName(String fileName) {
			this.fileName = fileName;
		}

		private void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		private void setCode(String code) {
			this.code = code;
		}

		private void setMessage(String message) {
			this.message = message;
		}
	}
	
	@SuppressWarnings("serial")
	public static class ParseException extends Exception {
		public ParseException(String msg) {
			super(msg);
		}
	}
}