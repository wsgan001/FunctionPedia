/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hssf.usermodel;

import java.io.*;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.apache.poi.hssf.HSSFTestDataSamples;
import org.apache.poi.hssf.HSSFITestDataProvider;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.record.*;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.TempFile;
import org.apache.poi.ss.usermodel.BaseTestWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.POIDataSamples;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.hpsf.ClassID;

/**
 * Tests for {@link HSSFWorkbook}
 */
public final class TestHSSFWorkbook extends BaseTestWorkbook {
    public TestHSSFWorkbook() {
        super(HSSFITestDataProvider.instance);
    }

    /**
     * gives test code access to the {@link InternalWorkbook} within {@link HSSFWorkbook}
     */
    public static InternalWorkbook getInternalWorkbook(HSSFWorkbook wb) {
        return wb.getWorkbook();
    }

    public void testSetRepeatingRowsAndColumns() {
        // Test bug 29747
        HSSFWorkbook b = new HSSFWorkbook( );
        b.createSheet();
        b.createSheet();
        b.createSheet();
        b.setRepeatingRowsAndColumns( 2, 0,1,-1,-1 );
        NameRecord nameRecord = b.getWorkbook().getNameRecord( 0 );
        assertEquals(3, nameRecord.getSheetNumber());
    }

    public void testWindowOneDefaults() {
        HSSFWorkbook b = new HSSFWorkbook( );
        try {
            assertEquals(b.getActiveSheetIndex(), 0);
            assertEquals(b.getFirstVisibleTab(), 0);
        } catch (NullPointerException npe) {
            fail("WindowOneRecord in Workbook is probably not initialized");
        }
    }

    /**
     * Tests for {@link HSSFWorkbook#isHidden()} etc
     */
    public void testHidden() {
        HSSFWorkbook wb = new HSSFWorkbook();

        WindowOneRecord w1 = wb.getWorkbook().getWindowOne();

        assertEquals(false, wb.isHidden());
        assertEquals(false, w1.getHidden());

        wb.setHidden(true);
        assertEquals(true, wb.isHidden());
        assertEquals(true, w1.getHidden());

        wb = HSSFTestDataSamples.writeOutAndReadBack(wb);
        w1 = wb.getWorkbook().getWindowOne();

        wb.setHidden(true);
        assertEquals(true, wb.isHidden());
        assertEquals(true, w1.getHidden());

        wb.setHidden(false);
        assertEquals(false, wb.isHidden());
        assertEquals(false, w1.getHidden());
    }

    public void testSheetClone() {
        // First up, try a simple file
        HSSFWorkbook b = new HSSFWorkbook();
        assertEquals(0, b.getNumberOfSheets());
        b.createSheet("Sheet One");
        b.createSheet("Sheet Two");

        assertEquals(2, b.getNumberOfSheets());
        b.cloneSheet(0);
        assertEquals(3, b.getNumberOfSheets());

        // Now try a problem one with drawing records in it
        b = HSSFTestDataSamples.openSampleWorkbook("SheetWithDrawing.xls");
        assertEquals(1, b.getNumberOfSheets());
        b.cloneSheet(0);
        assertEquals(2, b.getNumberOfSheets());
    }
    
    public void testReadWriteWithCharts() {
        HSSFWorkbook b;
        HSSFSheet s;

        // Single chart, two sheets
        b = HSSFTestDataSamples.openSampleWorkbook("44010-SingleChart.xls");
        assertEquals(2, b.getNumberOfSheets());
        assertEquals("Graph2", b.getSheetName(1));
        s = b.getSheetAt(1);
        assertEquals(0, s.getFirstRowNum());
        assertEquals(8, s.getLastRowNum());

        // Has chart on 1st sheet??
        // FIXME
        assertNotNull(b.getSheetAt(0).getDrawingPatriarch());
        assertNull(b.getSheetAt(1).getDrawingPatriarch());
        assertFalse(b.getSheetAt(0).getDrawingPatriarch().containsChart());

        // We've now called getDrawingPatriarch() so
        //  everything will be all screwy
        // So, start again
        b = HSSFTestDataSamples.openSampleWorkbook("44010-SingleChart.xls");

        b = HSSFTestDataSamples.writeOutAndReadBack(b);
        assertEquals(2, b.getNumberOfSheets());
        s = b.getSheetAt(1);
        assertEquals(0, s.getFirstRowNum());
        assertEquals(8, s.getLastRowNum());


        // Two charts, three sheets
        b = HSSFTestDataSamples.openSampleWorkbook("44010-TwoCharts.xls");
        assertEquals(3, b.getNumberOfSheets());

        s = b.getSheetAt(1);
        assertEquals(0, s.getFirstRowNum());
        assertEquals(8, s.getLastRowNum());
        s = b.getSheetAt(2);
        assertEquals(0, s.getFirstRowNum());
        assertEquals(8, s.getLastRowNum());

        // Has chart on 1st sheet??
        // FIXME
        assertNotNull(b.getSheetAt(0).getDrawingPatriarch());
        assertNull(b.getSheetAt(1).getDrawingPatriarch());
        assertNull(b.getSheetAt(2).getDrawingPatriarch());
        assertFalse(b.getSheetAt(0).getDrawingPatriarch().containsChart());

        // We've now called getDrawingPatriarch() so
        //  everything will be all screwy
        // So, start again
        b = HSSFTestDataSamples.openSampleWorkbook("44010-TwoCharts.xls");

        b = HSSFTestDataSamples.writeOutAndReadBack(b);
        assertEquals(3, b.getNumberOfSheets());

        s = b.getSheetAt(1);
        assertEquals(0, s.getFirstRowNum());
        assertEquals(8, s.getLastRowNum());
        s = b.getSheetAt(2);
        assertEquals(0, s.getFirstRowNum());
        assertEquals(8, s.getLastRowNum());
    }

    public void testSelectedSheet_bug44523() {
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet1 = wb.createSheet("Sheet1");
        HSSFSheet sheet2 = wb.createSheet("Sheet2");
        HSSFSheet sheet3 = wb.createSheet("Sheet3");
        HSSFSheet sheet4 = wb.createSheet("Sheet4");

        confirmActiveSelected(sheet1, true);
        confirmActiveSelected(sheet2, false);
        confirmActiveSelected(sheet3, false);
        confirmActiveSelected(sheet4, false);

        wb.setSelectedTab(1);

        // Demonstrate bug 44525:
        // Well... not quite, since isActive + isSelected were also added in the same bug fix
        if (sheet1.isSelected()) {
            throw new AssertionFailedError("Identified bug 44523 a");
        }
        wb.setActiveSheet(1);
        if (sheet1.isActive()) {
            throw new AssertionFailedError("Identified bug 44523 b");
        }

        confirmActiveSelected(sheet1, false);
        confirmActiveSelected(sheet2, true);
        confirmActiveSelected(sheet3, false);
        confirmActiveSelected(sheet4, false);
    }

    public void testSelectMultiple() {
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet1 = wb.createSheet("Sheet1");
        HSSFSheet sheet2 = wb.createSheet("Sheet2");
        HSSFSheet sheet3 = wb.createSheet("Sheet3");
        HSSFSheet sheet4 = wb.createSheet("Sheet4");
        HSSFSheet sheet5 = wb.createSheet("Sheet5");
        HSSFSheet sheet6 = wb.createSheet("Sheet6");

        wb.setSelectedTabs(new int[] { 0, 2, 3});

        assertEquals(true, sheet1.isSelected());
        assertEquals(false, sheet2.isSelected());
        assertEquals(true, sheet3.isSelected());
        assertEquals(true, sheet4.isSelected());
        assertEquals(false, sheet5.isSelected());
        assertEquals(false, sheet6.isSelected());

        wb.setSelectedTabs(new int[] { 1, 3, 5});

        assertEquals(false, sheet1.isSelected());
        assertEquals(true, sheet2.isSelected());
        assertEquals(false, sheet3.isSelected());
        assertEquals(true, sheet4.isSelected());
        assertEquals(false, sheet5.isSelected());
        assertEquals(true, sheet6.isSelected());

        assertEquals(true, sheet1.isActive());
        assertEquals(false, sheet2.isActive());


        assertEquals(true, sheet1.isActive());
        assertEquals(false, sheet3.isActive());
        wb.setActiveSheet(2);
        assertEquals(false, sheet1.isActive());
        assertEquals(true, sheet3.isActive());

        if (false) { // helpful if viewing this workbook in excel:
            sheet1.createRow(0).createCell(0).setCellValue(new HSSFRichTextString("Sheet1"));
            sheet2.createRow(0).createCell(0).setCellValue(new HSSFRichTextString("Sheet2"));
            sheet3.createRow(0).createCell(0).setCellValue(new HSSFRichTextString("Sheet3"));
            sheet4.createRow(0).createCell(0).setCellValue(new HSSFRichTextString("Sheet4"));

            try {
                File fOut = TempFile.createTempFile("sheetMultiSelect", ".xls");
                FileOutputStream os = new FileOutputStream(fOut);
                wb.write(os);
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void testActiveSheetAfterDelete_bug40414() {
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet0 = wb.createSheet("Sheet0");
        HSSFSheet sheet1 = wb.createSheet("Sheet1");
        HSSFSheet sheet2 = wb.createSheet("Sheet2");
        HSSFSheet sheet3 = wb.createSheet("Sheet3");
        HSSFSheet sheet4 = wb.createSheet("Sheet4");

        // confirm default activation/selection
        confirmActiveSelected(sheet0, true);
        confirmActiveSelected(sheet1, false);
        confirmActiveSelected(sheet2, false);
        confirmActiveSelected(sheet3, false);
        confirmActiveSelected(sheet4, false);

        wb.setActiveSheet(3);
        wb.setSelectedTab(3);

        confirmActiveSelected(sheet0, false);
        confirmActiveSelected(sheet1, false);
        confirmActiveSelected(sheet2, false);
        confirmActiveSelected(sheet3, true);
        confirmActiveSelected(sheet4, false);

        wb.removeSheetAt(3);
        // after removing the only active/selected sheet, another should be active/selected in its place
        if (!sheet4.isSelected()) {
            throw new AssertionFailedError("identified bug 40414 a");
        }
        if (!sheet4.isActive()) {
            throw new AssertionFailedError("identified bug 40414 b");
        }

        confirmActiveSelected(sheet0, false);
        confirmActiveSelected(sheet1, false);
        confirmActiveSelected(sheet2, false);
        confirmActiveSelected(sheet4, true);

        sheet3 = sheet4; // re-align local vars in this test case

        // Some more cases of removing sheets

        // Starting with a multiple selection, and different active sheet
        wb.setSelectedTabs(new int[] { 1, 3, });
        wb.setActiveSheet(2);
        confirmActiveSelected(sheet0, false, false);
        confirmActiveSelected(sheet1, false, true);
        confirmActiveSelected(sheet2, true,  false);
        confirmActiveSelected(sheet3, false, true);

        // removing a sheet that is not active, and not the only selected sheet
        wb.removeSheetAt(3);
        confirmActiveSelected(sheet0, false, false);
        confirmActiveSelected(sheet1, false, true);
        confirmActiveSelected(sheet2, true,  false);

        // removing the only selected sheet
        wb.removeSheetAt(1);
        confirmActiveSelected(sheet0, false, false);
        confirmActiveSelected(sheet2, true,  true);

        // The last remaining sheet should always be active+selected
        wb.removeSheetAt(1);
        confirmActiveSelected(sheet0, true,  true);
    }

    private static void confirmActiveSelected(HSSFSheet sheet, boolean expected) {
        confirmActiveSelected(sheet, expected, expected);
    }


    private static void confirmActiveSelected(HSSFSheet sheet,
            boolean expectedActive, boolean expectedSelected) {
        assertEquals("active", expectedActive, sheet.isActive());
        assertEquals("selected", expectedSelected, sheet.isSelected());
    }

    /**
     * If Sheet.getSize() returns a different result to Sheet.serialize(), this will cause the BOF
     * records to be written with invalid offset indexes.  Excel does not like this, and such
     * errors are particularly hard to track down.  This test ensures that HSSFWorkbook throws
     * a specific exception as soon as the situation is detected. See bugzilla 45066
     */
    public void testSheetSerializeSizeMismatch_bug45066() {
        HSSFWorkbook wb = new HSSFWorkbook();
        InternalSheet sheet = wb.createSheet("Sheet1").getSheet();
        List<RecordBase> sheetRecords = sheet.getRecords();
        // one way (of many) to cause the discrepancy is with a badly behaved record:
        sheetRecords.add(new BadlyBehavedRecord());
        // There is also much logic inside Sheet that (if buggy) might also cause the discrepancy
        try {
            wb.getBytes();
            throw new AssertionFailedError("Identified bug 45066 a");
        } catch (IllegalStateException e) {
            // Expected badly behaved sheet record to cause exception
            assertTrue(e.getMessage().startsWith("Actual serialized sheet size"));
        }
    }

    /**
     * Checks that us and HSSFName play nicely with named ranges
     *  that point to deleted sheets
     */
    public void testNamesToDeleteSheets() {
        HSSFWorkbook b = HSSFTestDataSamples.openSampleWorkbook("30978-deleted.xls");
        assertEquals(3, b.getNumberOfNames());

        // Sheet 2 is deleted
        assertEquals("Sheet1", b.getSheetName(0));
        assertEquals("Sheet3", b.getSheetName(1));

        Area3DPtg ptg;
        NameRecord nr;
        HSSFName n;

        /* ======= Name pointing to deleted sheet ====== */

        // First at low level
        nr = b.getWorkbook().getNameRecord(0);
        assertEquals("On2", nr.getNameText());
        assertEquals(0, nr.getSheetNumber());
        assertEquals(1, nr.getExternSheetNumber());
        assertEquals(1, nr.getNameDefinition().length);

        ptg = (Area3DPtg)nr.getNameDefinition()[0];
        assertEquals(1, ptg.getExternSheetIndex());
        assertEquals(0, ptg.getFirstColumn());
        assertEquals(0, ptg.getFirstRow());
        assertEquals(0, ptg.getLastColumn());
        assertEquals(2, ptg.getLastRow());

        // Now at high level
        n = b.getNameAt(0);
        assertEquals("On2", n.getNameName());
        assertEquals("", n.getSheetName());
        assertEquals("#REF!$A$1:$A$3", n.getRefersToFormula());


        /* ======= Name pointing to 1st sheet ====== */

        // First at low level
        nr = b.getWorkbook().getNameRecord(1);
        assertEquals("OnOne", nr.getNameText());
        assertEquals(0, nr.getSheetNumber());
        assertEquals(0, nr.getExternSheetNumber());
        assertEquals(1, nr.getNameDefinition().length);

        ptg = (Area3DPtg)nr.getNameDefinition()[0];
        assertEquals(0, ptg.getExternSheetIndex());
        assertEquals(0, ptg.getFirstColumn());
        assertEquals(2, ptg.getFirstRow());
        assertEquals(0, ptg.getLastColumn());
        assertEquals(3, ptg.getLastRow());

        // Now at high level
        n = b.getNameAt(1);
        assertEquals("OnOne", n.getNameName());
        assertEquals("Sheet1", n.getSheetName());
        assertEquals("Sheet1!$A$3:$A$4", n.getRefersToFormula());


        /* ======= Name pointing to 3rd sheet ====== */

        // First at low level
        nr = b.getWorkbook().getNameRecord(2);
        assertEquals("OnSheet3", nr.getNameText());
        assertEquals(0, nr.getSheetNumber());
        assertEquals(2, nr.getExternSheetNumber());
        assertEquals(1, nr.getNameDefinition().length);

        ptg = (Area3DPtg)nr.getNameDefinition()[0];
        assertEquals(2, ptg.getExternSheetIndex());
        assertEquals(0, ptg.getFirstColumn());
        assertEquals(0, ptg.getFirstRow());
        assertEquals(0, ptg.getLastColumn());
        assertEquals(1, ptg.getLastRow());

        // Now at high level
        n = b.getNameAt(2);
        assertEquals("OnSheet3", n.getNameName());
        assertEquals("Sheet3", n.getSheetName());
        assertEquals("Sheet3!$A$1:$A$2", n.getRefersToFormula());
    }

    /**
     * result returned by getRecordSize() differs from result returned by serialize()
     */
    private static final class BadlyBehavedRecord extends Record {
        public BadlyBehavedRecord() {
            //
        }
        public short getSid() {
            return 0x777;
        }
        public int serialize(int offset, byte[] data) {
            return 4;
        }
        public int getRecordSize() {
            return 8;
        }
    }

    /**
     * The sample file provided with bug 45582 seems to have one extra byte after the EOFRecord
     */
    public void testExtraDataAfterEOFRecord() {
        try {
            HSSFTestDataSamples.openSampleWorkbook("ex45582-22397.xls");
        } catch (RecordFormatException e) {
            if (e.getCause() instanceof LittleEndian.BufferUnderrunException) {
                throw new AssertionFailedError("Identified bug 45582");
            }
        }
    }

    /**
     * Test to make sure that NameRecord.getSheetNumber() is interpreted as a
     * 1-based sheet tab index (not a 1-based extern sheet index)
     */
    public void testFindBuiltInNameRecord() {
        // testRRaC has multiple (3) built-in name records
        // The second print titles name record has getSheetNumber()==4
        HSSFWorkbook wb = HSSFTestDataSamples.openSampleWorkbook("testRRaC.xls");
        NameRecord nr;
        assertEquals(3, wb.getWorkbook().getNumNames());
        nr = wb.getWorkbook().getNameRecord(2);
        // TODO - render full row and full column refs properly
        assertEquals("Sheet2!$A$1:$IV$1", HSSFFormulaParser.toFormulaString(wb, nr.getNameDefinition())); // 1:1

        try {
            wb.setRepeatingRowsAndColumns(3, 4, 5, 8, 11);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Builtin (7) already exists for sheet (4)")) {
                // there was a problem in the code which locates the existing print titles name record
                throw new RuntimeException("Identified bug 45720b");
            }
            throw e;
        }
        wb = HSSFTestDataSamples.writeOutAndReadBack(wb);
        assertEquals(3, wb.getWorkbook().getNumNames());
        nr = wb.getWorkbook().getNameRecord(2);
        assertEquals("Sheet2!E:F,Sheet2!$A$9:$IV$12", HSSFFormulaParser.toFormulaString(wb, nr.getNameDefinition())); // E:F,9:12
    }

    /**
     * Test that the storage clsid property is preserved
     */
    public void test47920() throws IOException {
        POIFSFileSystem fs1 = new POIFSFileSystem(POIDataSamples.getSpreadSheetInstance().openResourceAsStream("47920.xls"));
        HSSFWorkbook wb = new HSSFWorkbook(fs1);
        ClassID clsid1 = fs1.getRoot().getStorageClsid();

        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        wb.write(out);
        byte[] bytes = out.toByteArray();
        POIFSFileSystem fs2 = new POIFSFileSystem(new ByteArrayInputStream(bytes));
        ClassID clsid2 = fs2.getRoot().getStorageClsid();

        assertTrue(clsid1.equals(clsid2));
    }
    
    /**
     * Tests that we can work with both {@link POIFSFileSystem}
     *  and {@link NPOIFSFileSystem}
     */
    public void testDifferentPOIFS() throws Exception {
       // Open the two filesystems
       DirectoryNode[] files = new DirectoryNode[2];
       files[0] = (new POIFSFileSystem(HSSFTestDataSamples.openSampleFileStream("Simple.xls"))).getRoot();
       files[1] = (new NPOIFSFileSystem(HSSFTestDataSamples.getSampleFile("Simple.xls"))).getRoot();
       
       // Open without preserving nodes 
       for(DirectoryNode dir : files) {
          HSSFWorkbook workbook = new HSSFWorkbook(dir, false);
          HSSFSheet sheet = workbook.getSheetAt(0);
          HSSFCell cell = sheet.getRow(0).getCell(0);
          assertEquals("replaceMe", cell .getRichStringCellValue().getString());
       }

       // Now re-check with preserving
       for(DirectoryNode dir : files) {
          HSSFWorkbook workbook = new HSSFWorkbook(dir, true);
          HSSFSheet sheet = workbook.getSheetAt(0);
          HSSFCell cell = sheet.getRow(0).getCell(0);
          assertEquals("replaceMe", cell .getRichStringCellValue().getString());
       }
    }
    
    public void testWordDocEmbeddedInXls() throws IOException {
       // Open the two filesystems
       DirectoryNode[] files = new DirectoryNode[2];
       files[0] = (new POIFSFileSystem(HSSFTestDataSamples.openSampleFileStream("WithEmbeddedObjects.xls"))).getRoot();
       files[1] = (new NPOIFSFileSystem(HSSFTestDataSamples.getSampleFile("WithEmbeddedObjects.xls"))).getRoot();
       
       // Check the embedded parts
       for(DirectoryNode root : files) {
          HSSFWorkbook hw = new HSSFWorkbook(root, true);
          List<HSSFObjectData> objects = hw.getAllEmbeddedObjects();
          boolean found = false;
          for (int i = 0; i < objects.size(); i++) {
             HSSFObjectData embeddedObject = objects.get(i);
             if (embeddedObject.hasDirectoryEntry()) {
                DirectoryEntry dir = embeddedObject.getDirectory();
                if (dir instanceof DirectoryNode) {
                   DirectoryNode dNode = (DirectoryNode)dir;
                   if (hasEntry(dNode,"WordDocument")) {
                      found = true;
                   }
                }
             }
          }
          assertTrue(found);
       }
    }

    /**
     * Checks that we can open a workbook with NPOIFS, and write it out
     *  again (via POIFS) and have it be valid
     * @throws IOException
     */
    public void testWriteWorkbookFromNPOIFS() throws IOException {
       InputStream is = HSSFTestDataSamples.openSampleFileStream("WithEmbeddedObjects.xls");
       NPOIFSFileSystem fs = new NPOIFSFileSystem(is);
       
       // Start as NPOIFS
       HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
       assertEquals(3, wb.getNumberOfSheets());
       assertEquals("Root xls", wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
       
       // Will switch to POIFS
       wb = HSSFTestDataSamples.writeOutAndReadBack(wb);
       assertEquals(3, wb.getNumberOfSheets());
       assertEquals("Root xls", wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
    }
   
    public void testCellStylesLimit() {
        HSSFWorkbook wb = new HSSFWorkbook();
        int numBuiltInStyles = wb.getNumCellStyles();
        int MAX_STYLES = 4030;
        int limit = MAX_STYLES - numBuiltInStyles;
        for(int i=0; i < limit; i++){
            HSSFCellStyle style = wb.createCellStyle();
        }

        assertEquals(MAX_STYLES, wb.getNumCellStyles());
        try {
            HSSFCellStyle style = wb.createCellStyle();
            fail("expected exception");
        } catch (IllegalStateException e){
            assertEquals("The maximum number of cell styles was exceeded. " +
                    "You can define up to 4000 styles in a .xls workbook", e.getMessage());
        }
        assertEquals(MAX_STYLES, wb.getNumCellStyles());
    }

    public void testSetSheetOrderHSSF(){
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet s1 = wb.createSheet("first sheet");
        HSSFSheet s2 = wb.createSheet("other sheet");

        HSSFName name1 = wb.createName();
        name1.setNameName("name1");
        name1.setRefersToFormula("'first sheet'!D1");

        HSSFName name2 = wb.createName();
        name2.setNameName("name2");
        name2.setRefersToFormula("'other sheet'!C1");


        HSSFRow s1r1 = s1.createRow(2);
        HSSFCell c1 = s1r1.createCell(3);
        c1.setCellValue(30);
        HSSFCell c2 = s1r1.createCell(2);
        c2.setCellFormula("SUM('other sheet'!C1,'first sheet'!C1)");

        HSSFRow s2r1 = s2.createRow(0);
        HSSFCell c3 = s2r1.createCell(1);
        c3.setCellFormula("'first sheet'!D3");
        HSSFCell c4 = s2r1.createCell(2);
        c4.setCellFormula("'other sheet'!D3");

        // conditional formatting
        HSSFSheetConditionalFormatting sheetCF = s1.getSheetConditionalFormatting();

        HSSFConditionalFormattingRule rule1 = sheetCF.createConditionalFormattingRule(
                CFRuleRecord.ComparisonOperator.BETWEEN, "'first sheet'!D1", "'other sheet'!D1");

        HSSFConditionalFormattingRule [] cfRules = { rule1 };

        CellRangeAddress[] regions = {
            new CellRangeAddress(2, 4, 0, 0), // A3:A5
        };
        sheetCF.addConditionalFormatting(regions, cfRules);

        wb.setSheetOrder("other sheet", 0);

        // names
        assertEquals("'first sheet'!D1", wb.getName("name1").getRefersToFormula());
        assertEquals("'other sheet'!C1", wb.getName("name2").getRefersToFormula());

        // cells
        assertEquals("SUM('other sheet'!C1,'first sheet'!C1)", c2.getCellFormula());
        assertEquals("'first sheet'!D3", c3.getCellFormula());
        assertEquals("'other sheet'!D3", c4.getCellFormula());

        // conditional formatting
        HSSFConditionalFormatting cf = sheetCF.getConditionalFormattingAt(0);
        assertEquals("'first sheet'!D1", cf.getRule(0).getFormula1());
        assertEquals("'other sheet'!D1", cf.getRule(0).getFormula2());
    }
    
    private boolean hasEntry(DirectoryNode dirNode, String entryName) {
       try {
           dirNode.getEntry(entryName);
           return true;
       } catch (FileNotFoundException e) {
           return false;
       }
   }

    public void testClonePictures() throws IOException {
        HSSFWorkbook wb = HSSFTestDataSamples.openSampleWorkbook("SimpleWithImages.xls");
        InternalWorkbook iwb = wb.getWorkbook();
        iwb.findDrawingGroup();
        
        for(int pictureIndex=1; pictureIndex <= 4; pictureIndex++){
            EscherBSERecord bse = iwb.getBSERecord(pictureIndex);
            assertEquals(1, bse.getRef());
        }

        wb.cloneSheet(0);
        for(int pictureIndex=1; pictureIndex <= 4; pictureIndex++){
            EscherBSERecord bse = iwb.getBSERecord(pictureIndex);
            assertEquals(2, bse.getRef());
        }

        wb.cloneSheet(0);
        for(int pictureIndex=1; pictureIndex <= 4; pictureIndex++){
            EscherBSERecord bse = iwb.getBSERecord(pictureIndex);
            assertEquals(3, bse.getRef());
        }
    }

    public void testChangeSheetNameWithSharedFormulas() {
        changeSheetNameWithSharedFormulas("shared_formulas.xls");
    }

}
