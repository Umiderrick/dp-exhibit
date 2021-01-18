package com.zeusas.dp.util;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSSFWorkUtil {

	public static String readCell(XSSFSheet sheet, int rownum, int cellnum) throws IOException {
		XSSFRow row = sheet.getRow(rownum);
		if (row == null) {
			return null;
		}
		XSSFCell cell = row.getCell(cellnum);
		if (cell == null) {
			return null;
		}
		return cell.getStringCellValue();
	}

	public static XSSFWorkbook createWorkBook(String sheetName) {
		XSSFWorkbook wb = new XSSFWorkbook();
		wb.createSheet(sheetName);
		return wb;
	}

	static XSSFCell createCell(XSSFSheet sheet, int row, int col) {
		XSSFRow xssRow = sheet.getRow(row);
		if (xssRow == null) {
			xssRow = sheet.createRow(row);
		}
		return xssRow.createCell(col);
	}
}
