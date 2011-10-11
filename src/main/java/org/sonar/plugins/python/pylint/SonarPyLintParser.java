package org.sonar.plugins.python.pylint;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.python.PythonFile;

import com.popego.pylint.Issue;
import com.popego.pylint.Message;
import com.popego.pylint.PyLintParser;
import com.popego.pylint.Table;

public class SonarPyLintParser extends PyLintParser {
	
	private static final Logger LOG = LoggerFactory.getLogger(SonarPyLintParser.class);
	
	private Project project;
	private SensorContext sensorContext;
	private RuleFinder ruleFinder;
	
	public SonarPyLintParser(Project project, SensorContext sensorContext, RuleFinder ruleFinder) {
		this.project = project;
		this.sensorContext = sensorContext;
		this.ruleFinder = ruleFinder;
	}
	
	@Override
	protected void processIssue(Issue issue) {
        LOG.debug("PyLint issue: {}", issue.toString());
        
        Rule rule = ruleFinder.findByKey(PyLintRuleRepository.REPOSITORY_KEY, issue.getCode());
        if( rule == null ) {
        	LOG.error("Rule {} not found", issue.getCode());
        	return;
        }
        
        if( issue.getFileName() == null ) {
        	LOG.error("Issue has no filename");
        	return;
        }
        
        PythonFile pythonFile = PythonFile.fromIOFile(new File(issue.getFileName()), 
        		project.getFileSystem().getSourceDirs(), false);
        
		LOG.debug(String.format("Issue for file %s parent %s longname %s name %s", issue.getFileName(),
				pythonFile.getParent(), pythonFile.getLongName(), pythonFile.getName()));
        
        Violation violation = Violation.create(rule, pythonFile);
        violation.setLineId(issue.getLineNumber());
        violation.setMessage(issue.getMessage());

        sensorContext.saveViolation(violation);
	}

	@Override
	protected void processTable(Table table) {
        LOG.debug("PyLint table: {}", table.toString());        
		if( table.getTitle().equals("Statistics by type") )
			processStatisticsByType(table);
		else if( table.getTitle().equals("Raw metrics") )
			processRawMetrics(table);
		else if( table.getTitle().equals("Duplication") )
			processDuplication(table);
	}

	@Override
	protected void processMessage(Message message) {
		if( message.getTitle().equals("Global evaluation") ) 
			processGlobalEvaluation(message);
	}

	private void processDuplication(Table table) {
		for( Map<String,String> row: table.getRows() ) {
			double number = Double.parseDouble(row.get("now").trim());
			String type = row.get("");
			if( "nb duplicated lines".equals(type) ) {
				sensorContext.saveMeasure(project, CoreMetrics.DUPLICATED_LINES, number);
		        LOG.debug("PyLint number of duplicated lines {}", number);
			}
		}
	}
			
	private void processStatisticsByType(Table table) {
		for( Map<String,String> row: table.getRows() ) {
			int number = Integer.parseInt(row.get("number").trim());
			String type = row.get("type");
			if( "function".equals(type) ) {
				// TODO
			} else if( "class".equals(type) ) {
				sensorContext.saveMeasure(project, CoreMetrics.CLASSES, (double) number);
		        LOG.debug("PyLint number of classes {}", number);
			} else if( "module".equals(type) ) {
				// TODO
				// sensorContext.saveMeasure(project, CoreMetrics.PACKAGES, (double) number);
		        // LOG.debug("PyLint number of modules {}", number);
			} else if( "method".equals(type) ) {
				sensorContext.saveMeasure(project, CoreMetrics.FUNCTIONS, (double) number);
		        LOG.debug("PyLint number of methods {}", number);
			}
		}
	}
	
	private void processRawMetrics(Table table) {
		for( Map<String,String> row: table.getRows() ) {
			int number = Integer.parseInt(row.get("number").trim());
			String type = row.get("type");
			if( "code".equals(type) ) {
				sensorContext.saveMeasure(project, CoreMetrics.NCLOC, (double) number);
		        LOG.debug("PyLint number of code lines {}", number);    			  
  		  	} else if( "docstring".equals(type) ) {
  		  		// TODO
  		  	} else if( "comment".equals(type) ) {
  		  		sensorContext.saveMeasure(project, CoreMetrics.COMMENT_LINES, (double) number);
		        LOG.debug("PyLint number of comment lines {}", number);    			  
  		  	} else if( "empty".equals(type) ) {
  			  	// TODO
  		  	}
  	  	}
	}

	private void processGlobalEvaluation(Message message) {
		Matcher matcher = Pattern.compile("Your code has been rated at ([0-9\\.]+)/10").matcher(
				message.getMessage());
		if( matcher.find() ) {
			double number = Double.parseDouble(matcher.group(1));
			sensorContext.saveMeasure(project, CoreMetrics.VIOLATIONS_DENSITY, number * 10);
			LOG.debug("PyLint evaluation number {}", number * 10);
		}
	}
}
