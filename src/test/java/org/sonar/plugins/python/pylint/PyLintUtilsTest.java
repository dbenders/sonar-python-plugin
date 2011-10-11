package org.sonar.plugins.python.pylint;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class PyLintUtilsTest {

	private Project project;

	@Before
	public void setUp() throws Exception {
		project = mock(Project.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetReportWithProperty() {
		File desired = new File(getClass().getResource("big/pylint.txt").getFile());
		String dir = desired.getParent();
		ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
		when(fileSystem.resolvePath(anyString())).thenReturn(new File(dir));
		when(project.getFileSystem()).thenReturn(fileSystem);
		when(project.getProperty(PyLintUtils.PYLINT_REPORT_PATH_PROPERTY))
			.thenReturn(dir);
		File report = PyLintUtils.getReport(project);
		
		assertEquals(desired, report);
	}
	
	@Test
	public void testGetReportWithDefault() {
		File desired = new File(getClass().getResource("small/pylint.txt").getFile());
		String dir = desired.getParent();
		ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
		when(fileSystem.getBasedir()).thenReturn(new File(dir));
		when(project.getFileSystem()).thenReturn(fileSystem);
		
		File report = PyLintUtils.getReport(project);
		
		assertEquals(desired, report);
	}

}
