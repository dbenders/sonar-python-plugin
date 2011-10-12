package org.sonar.plugins.python.cobertura;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

public class CoberturaUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	
	public void testRelativizePathAbsolute() {
		String path = "/home/diego/popego/git/zaubersoftware/pycommons/src/pycommons/decorator.py";
		ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
		when(fileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File("src")));
		when(fileSystem.getBasedir()).thenReturn(new File("/home/diego/popego/git/zaubersoftware/pycommons"));
		String newPath = CoverageUtils.relativizePath(fileSystem, path);
		assertEquals("pycommons/decorator.py", newPath);
	}

	
	public void testRelativizePathRelative() {
		String path = "src/pycommons/decorator.py";
		ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
		when(fileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File("src")));
		when(fileSystem.getBasedir()).thenReturn(new File("/home/diego/popego/git/zaubersoftware/pycommons"));
		String newPath = CoverageUtils.relativizePath(fileSystem, path);
		assertEquals("pycommons/decorator.py", newPath);
	}

	@Test
	public void testRelativizePathFile() {
		String path = "decorator.py";
		ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
		when(fileSystem.getSourceDirs()).thenReturn(Arrays.asList(new File("src")));
		when(fileSystem.getBasedir()).thenReturn(new File("/home/diego/popego/git/zaubersoftware/pycommons"));
		String newPath = CoverageUtils.relativizePath(fileSystem, path);
		assertEquals("decorator.py", newPath);		
	}

	public void testGetReportFromDefault() {
		Project project = mock(Project.class);
		assertEquals("coverage.xml", CoverageUtils.getReport(project));
	}

	public void testGetReportFromPropertyAbsolute() {
		Project project = mock(Project.class);
		when(project.getProperty(CoverageUtils.COVERAGE_REPORT_PATH_PROPERTY)).thenReturn("/home/diego/reports");
		ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
		when(fileSystem.getBasedir()).thenReturn(new File("/home/diego/sonar"));
		when(project.getFileSystem()).thenReturn(fileSystem);
		assertEquals("/home/diego/reports/coverage.xml", CoverageUtils.getReport(project));
	}
	
	public void testGetReportFromPropertyRelative() {
		Project project = mock(Project.class);
		when(project.getProperty(CoverageUtils.COVERAGE_REPORT_PATH_PROPERTY)).thenReturn("reports");
		ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
		when(fileSystem.getBasedir()).thenReturn(new File("/home/diego/sonar"));
		when(project.getFileSystem()).thenReturn(fileSystem);
		assertEquals("/home/diego/sonar/reports/coverage.xml", CoverageUtils.getReport(project));
	}
	
}
