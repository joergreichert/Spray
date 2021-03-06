/** ****************************************************************************
 * Copyright (c)  The Spray Project.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Spray Dev Team - initial API and implementation
 **************************************************************************** */
package org.eclipselabs.spray.styles.tests;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipselabs.spray.styles.tests.util.StyleTestsInjectorProvider;
import org.eclipselabs.xtext.utils.unittesting.XtextRunner2;
import org.eclipselabs.xtext.utils.unittesting.XtextTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the Styles DSL.
 */
@RunWith(XtextRunner2.class)
@InjectWith(StyleTestsInjectorProvider.class)
public class StyleModelTest extends XtextTest {

    @Before
    public void before() {
        super._before();
        suppressSerialization();
    }

    @After
    public void after() {
        // Note that the CI server only supports font "Arial". This may lead to unasserted warnings.
        if (issues != null) {
           assertConstraints(issues.warningsOnly().allOfThemContain("The selected font is not installed on the system (default 'Arial' will be taken)."));
        }
        super._after();
    }

    @Test
    public void test_01_FullFilledStyle() {
        testFile("testcases/01_FullFilledStyle.style");
    }

    @Test
    public void test_02_NoTransparency() {
        testFile("testcases/02_NoTransparency.style");
    }

    @Test
    public void test_03_DefaultBackground() {
        testFile("testcases/03_DefaultBackground.style");
    }

    @Test
    public void test_04_TransparentBackground() {
        testFile("testcases/04_TransparentBackground.style");
    }

    @Test
    public void test_05_NoLinecolor() {
        testFile("testcases/05_NoLinecolor.style");
    }

    @Test
    public void test_06_TransparentLineColor() {
        testFile("testcases/06_TransparentLineColor.style");
        assertConstraints(issues.errorsOnly().allOfThemContain("Setting the line-color to transparent isn't supported, use line-width = 0 instead."));
    }

    @Test
    public void test_07_NoFontname() {
        testFile("testcases/07_NoFontname.style");
    }

    @Test
    public void test_08_NoFontsize() {
        testFile("testcases/08_NoFontsize.style");
    }

    @Test
    public void test_09_NoFontnameAndSize() {
        testFile("testcases/09_NoFontnameAndSize.style");
    }

    @Test
    public void test_10_NoFontStyle() {
        testFile("testcases/10_NoFontStyle.style");
    }

    @Test
    public void test_11_EmptyStyle() {
        testFile("testcases/11_EmptyStyle.style");
    }

    @Test
    public void test_12_StyleInheritance() {
        testFile("testcases/12_StyleInheritance.style");
    }

    @Test
    public void test_13_DefaultLine() {
        testFile("testcases/13_DefaultLine.style");
    }

    @Test
    public void test_14_DefaultFont() {
        testFile("testcases/14_DefaultFont.style");
    }

}
