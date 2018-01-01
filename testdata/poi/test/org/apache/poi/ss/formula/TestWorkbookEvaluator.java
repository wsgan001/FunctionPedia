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

package org.apache.poi.ss.formula;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.poi.hssf.HSSFTestDataSamples;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.DeletedArea3DPtg;
import org.apache.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefErrorPtg;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.ErrorConstants;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Tests {@link WorkbookEvaluator}.
 *
 * @author Josh Micich
 */
public class TestWorkbookEvaluator extends TestCase {

	private static ValueEval evaluateFormula(Ptg[] ptgs) {
		OperationEvaluationContext ec = new OperationEvaluationContext(null, null, 0, 0, 0, null);
		return new WorkbookEvaluator(null, null, null).evaluateFormula(ec, ptgs);
	}

	/**
	 * Make sure that the evaluator can directly handle tAttrSum (instead of relying on re-parsing
	 * the whole formula which converts tAttrSum to tFuncVar("SUM") )
	 */
	public void testAttrSum() {

		Ptg[] ptgs = {
			new IntPtg(42),
			AttrPtg.SUM,
		};

		ValueEval result = evaluateFormula(ptgs);
		assertEquals(42, ((NumberEval)result).getNumberValue(), 0.0);
	}

	/**
	 * Make sure that the evaluator can directly handle (deleted) ref error tokens
	 * (instead of relying on re-parsing the whole formula which converts these
	 * to the error constant #REF! )
	 */
	public void testRefErr() {

		confirmRefErr(new RefErrorPtg());
		confirmRefErr(new AreaErrPtg());
		confirmRefErr(new DeletedRef3DPtg(0));
		confirmRefErr(new DeletedArea3DPtg(0));
	}
	private static void confirmRefErr(Ptg ptg) {
		Ptg[] ptgs = {
			ptg,
		};

		ValueEval result = evaluateFormula(ptgs);
		assertEquals(ErrorEval.REF_INVALID, result);
	}

	/**
	 * Make sure that the evaluator can directly handle tAttrSum (instead of relying on re-parsing
	 * the whole formula which converts tAttrSum to tFuncVar("SUM") )
	 */
	public void testMemFunc() {

		Ptg[] ptgs = {
			new IntPtg(42),
			AttrPtg.SUM,
		};

		ValueEval result = evaluateFormula(ptgs);
		assertEquals(42, ((NumberEval)result).getNumberValue(), 0.0);
	}


	public void testEvaluateMultipleWorkbooks() {
		HSSFWorkbook wbA = HSSFTestDataSamples.openSampleWorkbook("multibookFormulaA.xls");
		HSSFWorkbook wbB = HSSFTestDataSamples.openSampleWorkbook("multibookFormulaB.xls");

		HSSFFormulaEvaluator evaluatorA = new HSSFFormulaEvaluator(wbA);
		HSSFFormulaEvaluator evaluatorB = new HSSFFormulaEvaluator(wbB);

		// Hook up the workbook evaluators to enable evaluation of formulas across books
		String[] bookNames = { "multibookFormulaA.xls", "multibookFormulaB.xls", };
		HSSFFormulaEvaluator[] evaluators = { evaluatorA, evaluatorB, };
		HSSFFormulaEvaluator.setupEnvironment(bookNames, evaluators);

		HSSFCell cell;

		HSSFSheet aSheet1 = wbA.getSheetAt(0);
		HSSFSheet bSheet1 = wbB.getSheetAt(0);

		// Simple case - single link from wbA to wbB
		confirmFormula(wbA, 0, 0, 0, "[multibookFormulaB.xls]BSheet1!B1");
		cell = aSheet1.getRow(0).getCell(0);
		confirmEvaluation(35, evaluatorA, cell);


		// more complex case - back link into wbA
		// [wbA]ASheet1!A2 references (among other things) [wbB]BSheet1!B2
		confirmFormula(wbA, 0, 1, 0, "[multibookFormulaB.xls]BSheet1!$B$2+2*A3");
		// [wbB]BSheet1!B2 references (among other things) [wbA]AnotherSheet!A1:B2
		confirmFormula(wbB, 0, 1, 1, "SUM([multibookFormulaA.xls]AnotherSheet!$A$1:$B$2)+B3");

		cell = aSheet1.getRow(1).getCell(0);
		confirmEvaluation(264, evaluatorA, cell);

		// change [wbB]BSheet1!B3 (from 50 to 60)
		HSSFCell cellB3 = bSheet1.getRow(2).getCell(1);
		cellB3.setCellValue(60);
		evaluatorB.notifyUpdateCell(cellB3);
		confirmEvaluation(274, evaluatorA, cell);

		// change [wbA]ASheet1!A3 (from 100 to 80)
		HSSFCell cellA3 = aSheet1.getRow(2).getCell(0);
		cellA3.setCellValue(80);
		evaluatorA.notifyUpdateCell(cellA3);
		confirmEvaluation(234, evaluatorA, cell);

		// change [wbA]AnotherSheet!A1 (from 2 to 3)
		HSSFCell cellA1 = wbA.getSheetAt(1).getRow(0).getCell(0);
		cellA1.setCellValue(3);
		evaluatorA.notifyUpdateCell(cellA1);
		confirmEvaluation(235, evaluatorA, cell);
	}

	private static void confirmEvaluation(double expectedValue, HSSFFormulaEvaluator fe, HSSFCell cell) {
		assertEquals(expectedValue, fe.evaluate(cell).getNumberValue(), 0.0);
	}

	private static void confirmFormula(HSSFWorkbook wb, int sheetIndex, int rowIndex, int columnIndex,
			String expectedFormula) {
		HSSFCell cell = wb.getSheetAt(sheetIndex).getRow(rowIndex).getCell(columnIndex);
		assertEquals(expectedFormula, cell.getCellFormula());
	}

	/**
	 * This test makes sure that any {@link MissingArgEval} that propagates to
	 * the result of a function gets translated to {@link BlankEval}.
	 */
	public void testMissingArg() {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Sheet1");
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellFormula("1+IF(1,,)");
		HSSFFormulaEvaluator fe = new HSSFFormulaEvaluator(wb);
		CellValue cv;
		try {
			cv = fe.evaluate(cell);
		} catch (RuntimeException e) {
			throw new AssertionFailedError("Missing arg result not being handled correctly.");
		}
		assertEquals(HSSFCell.CELL_TYPE_NUMERIC, cv.getCellType());
		// adding blank to 1.0 gives 1.0
		assertEquals(1.0, cv.getNumberValue(), 0.0);

		// check with string operand
		cell.setCellFormula("\"abc\"&IF(1,,)");
		fe.notifySetFormula(cell);
		cv = fe.evaluate(cell);
		assertEquals(HSSFCell.CELL_TYPE_STRING, cv.getCellType());
		// adding blank to "abc" gives "abc"
		assertEquals("abc", cv.getStringValue());

		// check CHOOSE()
		cell.setCellFormula("\"abc\"&CHOOSE(2,5,,9)");
		fe.notifySetFormula(cell);
		cv = fe.evaluate(cell);
		assertEquals(HSSFCell.CELL_TYPE_STRING, cv.getCellType());
		// adding blank to "abc" gives "abc"
		assertEquals("abc", cv.getStringValue());
	}

	/**
	 * Functions like IF, INDIRECT, INDEX, OFFSET etc can return AreaEvals which
	 * should be dereferenced by the evaluator
	 */
	public void testResultOutsideRange() {
		Workbook wb = new HSSFWorkbook();
		Cell cell = wb.createSheet("Sheet1").createRow(0).createCell(0);
		cell.setCellFormula("D2:D5"); // IF(TRUE,D2:D5,D2) or  OFFSET(D2:D5,0,0) would work too
		FormulaEvaluator fe = wb.getCreationHelper().createFormulaEvaluator();
		CellValue cv;
		try {
			cv = fe.evaluate(cell);
		} catch (IllegalArgumentException e) {
			if ("Specified row index (0) is outside the allowed range (1..4)".equals(e.getMessage())) {
				throw new AssertionFailedError("Identified bug in result dereferencing");
			}
			throw new RuntimeException(e);
		}
		assertEquals(Cell.CELL_TYPE_ERROR, cv.getCellType());
		assertEquals(ErrorConstants.ERROR_VALUE, cv.getErrorValue());

		// verify circular refs are still detected properly
		fe.clearAllCachedResultValues();
		cell.setCellFormula("OFFSET(A1,0,0)");
		cv = fe.evaluate(cell);
		assertEquals(Cell.CELL_TYPE_ERROR, cv.getCellType());
		assertEquals(ErrorEval.CIRCULAR_REF_ERROR.getErrorCode(), cv.getErrorValue());
	}
}
