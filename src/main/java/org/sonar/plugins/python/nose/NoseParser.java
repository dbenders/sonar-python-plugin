package org.sonar.plugins.python.nose;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.StaxParser;
import org.sonar.plugins.python.PythonFile;
import org.sonar.plugins.surefire.data.SurefireStaxHandler;
import org.sonar.plugins.surefire.data.UnitTestClassReport;
import org.sonar.plugins.surefire.data.UnitTestIndex;

/**
* @since 2.4
*/
public abstract class NoseParser {

	private static final Logger LOG = LoggerFactory.getLogger(NoseParser.class);

	public void collect(Project project, SensorContext context, File reportsFile) {
		parseFile(context, reportsFile);
	}

	private void parseFile(SensorContext context, File report) {
		UnitTestIndex index = new UnitTestIndex();
		parseFile(report, index);
		sanitize(index);
		save(index, context);
	}

	private void parseFile(File report, UnitTestIndex index) {
		SurefireStaxHandler staxParser = new SurefireStaxHandler(index);
		StaxParser parser = new StaxParser(staxParser, false);
		try {
			parser.parse(report);
		} catch (XMLStreamException e) {
			throw new SonarException("Fail to parse the Surefire report: " + report, e);
		}
	}

	private void sanitize(UnitTestIndex index) {
		for (String classname : index.getClassnames()) {
			if (StringUtils.contains(classname, "$")) {
				// Surefire reports classes whereas sonar supports files
				String parentClassName = StringUtils.substringBeforeLast(classname, "$");
				index.merge(classname, parentClassName);
			}
		}
	}

	private void save(UnitTestIndex index, SensorContext context) {
		for (Map.Entry<String, UnitTestClassReport> entry : index.getIndexByClassname().entrySet()) {
			UnitTestClassReport report = entry.getValue();
			if (report.getTests() > 0) {
				Resource resource = getUnitTestResource(entry.getKey());
				double testsCount = report.getTests() - report.getSkipped();
				saveMeasure(context, resource, CoreMetrics.SKIPPED_TESTS, report.getSkipped());
				saveMeasure(context, resource, CoreMetrics.TESTS, testsCount);
				saveMeasure(context, resource, CoreMetrics.TEST_ERRORS, report.getErrors());
				saveMeasure(context, resource, CoreMetrics.TEST_FAILURES, report.getFailures());
				saveMeasure(context, resource, CoreMetrics.TEST_EXECUTION_TIME, report.getDurationMilliseconds());
				double passedTests = testsCount - report.getErrors() - report.getFailures();
				if (testsCount > 0) {
					double percentage = passedTests * 100d / testsCount;
					saveMeasure(context, resource, CoreMetrics.TEST_SUCCESS_DENSITY, ParsingUtils.scaleValue(percentage));
				}
				saveResults(context, resource, report);
			}
		}
	}

	private void saveMeasure(SensorContext context, Resource resource, Metric metric, double value) {
		if (!Double.isNaN(value)) {
			context.saveMeasure(resource, metric, value);
		}
	}

	private void saveResults(SensorContext context, Resource resource, UnitTestClassReport report) {
		context.saveMeasure(resource, new Measure(CoreMetrics.TEST_DATA, report.toXml()));
	}
 
	protected Resource<?> getUnitTestResource(String classKey) {
		String fileName = StringUtils.substringBeforeLast(classKey, ".");
		LOG.debug("Analyzing resource {}", fileName);
		return new PythonFile(fileName, true);
	}
}
