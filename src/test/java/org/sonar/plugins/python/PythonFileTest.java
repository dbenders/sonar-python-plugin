package org.sonar.plugins.python;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;

import edu.emory.mathcs.backport.java.util.Arrays;

public class PythonFileTest {

	@Before
	public void setUp() {
		new Python(null);
	}
	
	@Test
	public void testFromIOFileFileAbsolute() {
		File file = new File("/home/diego/popego/svn/main/utils/trunk/utils/decorators/deprecated.py");
		List<File> sourceDirs = Arrays.asList(new File[]{new File("/home/diego/popego/svn/main/utils/trunk")});
		PythonFile resource = PythonFile.fromIOFile(file, sourceDirs, false);
		
		assertEquals(resource.getKey(), "utils/decorators/deprecated.py");
		assertEquals(resource.getName(), "deprecated.py");
		assertEquals(resource.getLanguage().getKey(), "py");
		assertEquals(resource.getLongName(), "utils/decorators/deprecated.py");
		assertEquals(resource.getQualifier(), Qualifiers.CLASS);
		assertEquals(resource.getScope(), Scopes.FILE);		
		
		Resource parent = resource.getParent();
		assertEquals(parent.getName(), "utils/decorators");
		assertEquals(parent.getLanguage().getKey(), "py");
		assertEquals(parent.getLongName(), "utils/decorators");
		assertEquals(parent.getQualifier(), Qualifiers.PACKAGE);
		assertEquals(parent.getScope(), Scopes.DIRECTORY);		
	}

	@Test
	public void testFromIOFileRelative() {
		File file = new File("utils/decorators/deprecated.py");
		List<File> sourceDirs = Arrays.asList(new File[]{new File("/home/diego/popego/svn/main/utils/trunk")});
		PythonFile resource = PythonFile.fromIOFile(file, sourceDirs, false);
		
		assertEquals(resource.getKey(), "utils/decorators/deprecated.py");
		assertEquals(resource.getName(), "deprecated.py");
		assertEquals(resource.getLanguage().getKey(), "py");
		assertEquals(resource.getLongName(), "utils/decorators/deprecated.py");
		assertEquals(resource.getQualifier(), Qualifiers.CLASS);
		assertEquals(resource.getScope(), Scopes.FILE);		
		
		Resource parent = resource.getParent();
		assertEquals(parent.getName(), "utils/decorators");
		assertEquals(parent.getLanguage().getKey(), "py");
		assertEquals(parent.getLongName(), "utils/decorators");
		assertEquals(parent.getQualifier(), Qualifiers.PACKAGE);
		assertEquals(parent.getScope(), Scopes.DIRECTORY);		
	}
	
	@Test
	public void testFromIOFileFileUnitTest() {
		File file = new File("/home/diego/popego/svn/main/utils/trunk/utils/decorators/deprecated.py");
		List<File> sourceDirs = Arrays.asList(new File[]{new File("/home/diego/popego/svn/main/utils/trunk")});
		PythonFile resource = PythonFile.fromIOFile(file, sourceDirs, true);
		
		assertEquals(resource.getQualifier(), Qualifiers.UNIT_TEST_FILE);
		assertEquals(resource.getScope(), Scopes.FILE);				
	}

}
