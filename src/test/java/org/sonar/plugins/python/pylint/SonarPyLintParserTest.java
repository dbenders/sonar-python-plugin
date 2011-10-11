package org.sonar.plugins.python.pylint;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;

public class SonarPyLintParserTest {
	private SonarPyLintParser parser;
	private Project project;
	private SensorContext sensorContext;
	private RuleFinder ruleFinder;
	
	@Before
	public void setUp() {
		ProjectFileSystem projectFileSystemMock = mock(ProjectFileSystem.class);
		when(projectFileSystemMock.getSourceDirs()).thenReturn(Arrays.asList(new File(".")));
		project = mock(Project.class);
		when(project.getFileSystem()).thenReturn(projectFileSystemMock);
		
		sensorContext = mock(SensorContext.class);
		ruleFinder = mock(RuleFinder.class);
		parser = new SonarPyLintParser(project, sensorContext, ruleFinder);
	}
	
	@Test
	public void parseDetail() throws IOException {
		final Rule rule_w0311 = new Rule();
		rule_w0311.setKey("W0311");
		final Rule rule_c0111 = new Rule();
		rule_w0311.setKey("C0111");
		final Rule rule_c0322 = new Rule();
		rule_w0311.setKey("C0322");
		
		when(ruleFinder.findByKey(eq(PyLintRuleRepository.REPOSITORY_KEY), eq("W0311"))).thenReturn(rule_w0311);
		when(ruleFinder.findByKey(eq(PyLintRuleRepository.REPOSITORY_KEY), eq("C0111"))).thenReturn(rule_c0111);
		when(ruleFinder.findByKey(eq(PyLintRuleRepository.REPOSITORY_KEY), eq("C0322"))).thenReturn(rule_c0322);

		InputStream is = getClass().getResourceAsStream("small/pylint.txt");
		Reader reader = new InputStreamReader(is);
		parser.parseReport(reader);

		verify(sensorContext).saveViolation(argThat(new ArgumentMatcher<Violation>() {
			@Override
			public boolean matches(Object obj) {
				return ((Violation)obj).getRule() == rule_w0311;
			}
		}));
	
		verify(sensorContext).saveViolation(argThat(new ArgumentMatcher<Violation>() {
			@Override
			public boolean matches(Object obj) {
				return ((Violation)obj).getRule() == rule_c0111;
			}
		}));

		verify(sensorContext).saveViolation(argThat(new ArgumentMatcher<Violation>() {
			@Override
			public boolean matches(Object obj) {
				return ((Violation)obj).getRule() == rule_c0322;
			}
		}));
		
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.VIOLATIONS_DENSITY), eq(55.7));
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.FUNCTIONS), eq(178.0));
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.CLASSES), eq(35.0));
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.NCLOC), eq(1689.0));
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.COMMENT_LINES), eq(106.0));		
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.DUPLICATED_LINES), eq(0.0));		
	}
	
	@Test
	public void parseBigFile() throws IOException {
		Rule rule = new Rule();
		when(ruleFinder.findByKey(eq(PyLintRuleRepository.REPOSITORY_KEY), anyString()))
			.thenReturn(rule);
		
		Reader reader = new InputStreamReader(getClass().getResourceAsStream("big/pylint.txt"));
		parser.parseReport(reader);
		
		verify(ruleFinder, times(668)).findByKey(eq(PyLintRuleRepository.REPOSITORY_KEY), anyString());
		verify(sensorContext, times(668)).saveViolation(any(Violation.class));
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.VIOLATIONS_DENSITY), anyDouble());
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.FUNCTIONS), anyDouble());
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.CLASSES), anyDouble());
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.NCLOC), anyDouble());
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.COMMENT_LINES), anyDouble());
		verify(sensorContext).saveMeasure(eq(project), eq(CoreMetrics.DUPLICATED_LINES), anyDouble());
	}
}
