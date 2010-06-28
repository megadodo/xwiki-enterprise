/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.it.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.elements.ClassEditPage;
import org.xwiki.it.ui.framework.elements.FormElement;
import org.xwiki.it.ui.framework.elements.ObjectEditPage;
import org.xwiki.it.ui.framework.elements.ViewPage;
import org.xwiki.it.ui.framework.elements.WikiEditPage;

/**
 * Test XObject editing.
 *
 * @version $Id$
 * @since 2.4M2
 */
public class EditObjectsTest extends AbstractAdminAuthenticatedTest
{
    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage("Test", "EditObjectsTestClass");
        getUtil().deletePage("Test", "EditObjectsTestObject");
    }

    /**
     * Tests that XWIKI-1621 remains fixed.
     */
    @Test
    public void testChangeMultiselectProperty()
    {
        // Create a class with a database list property set to return all documents
        ClassEditPage cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.addProperty("prop", "com.xpn.xwiki.objects.classes.DBListClass");
        cep.getDatabaseListClassEditElement("prop").setHibernateQuery(
            "select doc.fullName from XWikiDocument doc where doc.space = 'Test'");
        ViewPage vp = cep.clickSaveAndView();

        // Create a second page to hold the Object and set its content
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestObject");
        wep.setContent("this is the content");
        vp = wep.clickSaveAndView();

        // Add an object of the class created and set the value to be the test page
        ObjectEditPage oep = vp.clickEditObjects();
        FormElement objectForm = oep.addObject("Test.EditObjectsTestClass");
        objectForm.setFieldValue(By.id("Test.EditObjectsTestClass_0_prop"), "Test.EditObjectsTestClass");
        vp = oep.clickSaveAndView();

        // Set multiselect to true
        cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.getDatabaseListClassEditElement("prop").isMultiSelect(true);
        vp = cep.clickSaveAndView();

        // Select a second document in the DB list select field.
        oep = new ObjectEditPage();
        oep.switchToEdit("Test", "EditObjectsTestObject");
        oep.getObjectsOfClass("Test.EditObjectsTestClass").get(0).setFieldValue(
            By.id("Test.EditObjectsTestClass_0_prop"), "Test.EditObjectsTestObject");
        vp = oep.clickSaveAndView();

        Assert.assertEquals("this is the content", vp.getContent());
    }
}