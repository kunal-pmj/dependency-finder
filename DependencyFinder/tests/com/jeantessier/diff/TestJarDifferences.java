/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.diff;

import java.io.*;
import java.util.*;

import junit.framework.*;

import com.jeantessier.classreader.*;

public class TestJarDifferences extends TestCase {
	public static final String OLD_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "old";
	public static final String NEW_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

	private JarDifferences jar_differences;

	public TestJarDifferences(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		DirectoryClassfileLoader loader;

		ClassfileLoader old_jar = new AggregatingClassfileLoader();
		loader = new DirectoryClassfileLoader(old_jar);
		loader.Load(new DirectoryExplorer(OLD_CLASSPATH));

		ClassfileLoader new_jar = new AggregatingClassfileLoader();
		loader = new DirectoryClassfileLoader(new_jar);
		loader.Load(new DirectoryExplorer(NEW_CLASSPATH));

		Validator old_validator = new ListBasedValidator(new BufferedReader(new FileReader(OLD_CLASSPATH + ".txt")));
		Validator new_validator = new ListBasedValidator(new BufferedReader(new FileReader(NEW_CLASSPATH + ".txt")));

		jar_differences = new JarDifferences("test", "old", old_validator, old_jar, "new", new_validator, new_jar);
	}

	public void testEmpty() throws IOException {
		Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("")));
		JarDifferences empty_differences = new JarDifferences("test", "old", validator, new AggregatingClassfileLoader(), "new", validator, new AggregatingClassfileLoader());

		assertEquals("name",        "test", empty_differences.Name());
		assertEquals("old version", "old",  empty_differences.OldVersion());
		assertEquals("new version", "new",  empty_differences.NewVersion());

		assertTrue("IsEmpty()", empty_differences.IsEmpty());

		assertTrue("!IsEmpty()", !jar_differences.IsEmpty());
		assertEquals("NbPackageDifferences: " + jar_differences.PackageDifferences(), 3, jar_differences.PackageDifferences().size());
	}
	
	public void testModifiedPackage() {
		String name = "ModifiedPackage";
		PackageDifferences differences = (PackageDifferences) Find(name, jar_differences.PackageDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".ClassDifferences: " + differences.ClassDifferences(), 10, differences.ClassDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testNewPackage() {
		String name = "NewPackage";
		PackageDifferences differences = (PackageDifferences) Find(name, jar_differences.PackageDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".ClassDifferences: " + differences.ClassDifferences(), 0, differences.ClassDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}

	public void testRemovedPackage() {
		String name = "RemovedPackage";
		PackageDifferences differences = (PackageDifferences) Find(name, jar_differences.PackageDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".ClassDifferences: " + differences.ClassDifferences(), 0, differences.ClassDifferences().size());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".ModifiedClass";
		ClassDifferences differences = (ClassDifferences) ((DecoratorDifferences) Find(name, package_differences.ClassDifferences())).LeafComponent();
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 3, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedInterface() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".ModifiedInterface";
		ClassDifferences differences = (ClassDifferences) ((DecoratorDifferences) Find(name, package_differences.ClassDifferences())).LeafComponent();
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 3, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testNewClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".NewClass";
		ClassDifferences differences = (ClassDifferences) ((DecoratorDifferences) Find(name, package_differences.ClassDifferences())).LeafComponent();
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testNewInterface() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".NewInterface";
		ClassDifferences differences = (ClassDifferences) ((DecoratorDifferences) Find(name, package_differences.ClassDifferences())).LeafComponent();
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testRemovedClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".RemovedClass";
		ClassDifferences differences = (ClassDifferences) ((DecoratorDifferences) Find(name, package_differences.ClassDifferences())).LeafComponent();
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testRemovedInterface() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".RemovedInterface";
		ClassDifferences differences = (ClassDifferences) ((DecoratorDifferences) Find(name, package_differences.ClassDifferences())).LeafComponent();
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testClassModifiedMethod() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) ((DecoratorDifferences) Find(class_name, package_differences.ClassDifferences())).LeafComponent();

		String name = class_name + ".ModifiedMethod";
		FeatureDifferences differences = (FeatureDifferences) ((DecoratorDifferences) Find(name, class_differences.FeatureDifferences())).LeafComponent();
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}

	public void testClassFeatures() {
		PackageDifferences package_differences = (PackageDifferences) jar_differences.PackageDifferences().iterator().next();

		Iterator i = package_differences.ClassDifferences().iterator();
		ClassDifferences class_differences = (ClassDifferences) ((DecoratorDifferences) i.next()).Component();
		
		DecoratorDifferences[] feature_differences = (DecoratorDifferences[]) class_differences.FeatureDifferences().toArray(new DecoratorDifferences[0]);
		
		FeatureDifferences differences;

		differences = (FeatureDifferences) feature_differences[0].Component();
		assertEquals("[0][0][0]", "ModifiedPackage.ModifiedClass.ModifiedMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()",  differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (FeatureDifferences) feature_differences[1].Component();
		assertEquals("[0][0][1]", "ModifiedPackage.ModifiedClass.NewMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",       differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (FeatureDifferences) feature_differences[2].Component();
		assertEquals("[0][0][2]", "ModifiedPackage.ModifiedClass.RemovedMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());
	}		
	
	public void testInterfaceFeatures() {
		PackageDifferences package_differences = (PackageDifferences) jar_differences.PackageDifferences().iterator().next();

		Iterator i = package_differences.ClassDifferences().iterator();
		ClassDifferences class_differences = (ClassDifferences) ((DecoratorDifferences) i.next()).Component();
		ClassDifferences interface_differences = (ClassDifferences) ((DecoratorDifferences) i.next()).Component();
		
		DecoratorDifferences[] feature_differences = (DecoratorDifferences[]) interface_differences.FeatureDifferences().toArray(new DecoratorDifferences[0]);

		FeatureDifferences differences;

		differences = (FeatureDifferences) feature_differences[0].Component();
		assertEquals("[0][1][0]", "ModifiedPackage.ModifiedInterface.ModifiedMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()",  differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (FeatureDifferences) feature_differences[1].Component();
		assertEquals("[0][1][1]", "ModifiedPackage.ModifiedInterface.NewMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",       differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (FeatureDifferences) feature_differences[2].Component();
		assertEquals("[0][1][2]", "ModifiedPackage.ModifiedInterface.RemovedMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());
	}

	private Differences Find(String name, Collection differences) {
		Differences result = null;

		Iterator i = differences.iterator();
		while (result == null && i.hasNext()) {
			Differences candidate = (Differences) i.next();
			if (name.equals(candidate.Name())) {
				result = candidate;
			}
		}

		return result;
	}
}
