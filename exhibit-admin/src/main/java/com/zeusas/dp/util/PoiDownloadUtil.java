package com.zeusas.dp.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PoiDownloadUtil {
	
	private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
	
	private static XSSFWorkbook xssfCreateSheet(XSSFWorkbook wb,XSSFSheet sheet,String sheetName,String[] header, List<Record> data,int beginRow,int beginCol){
		wb.setSheetName(wb.getNumberOfSheets()-1, sheetName);
		// 表头格式
				XSSFCellStyle style0 = wb.createCellStyle();
				style0.setAlignment(HorizontalAlignment.CENTER);
				style0.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
				style0.setFillForegroundColor(IndexedColors.LIME.getIndex());
				style0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style0.setBorderBottom(BorderStyle.THIN);
				style0.setBorderLeft(BorderStyle.THIN);
				style0.setBorderRight(BorderStyle.THIN);
				style0.setBorderTop(BorderStyle.THIN);

				XSSFFont font = wb.createFont();
				font.setFontName("黑体");
				font.setFontHeightInPoints((short) 16);
				style0.setFont(font);

				// 表格格式
				XSSFCellStyle style1 = wb.createCellStyle();
				style1.setAlignment(HorizontalAlignment.CENTER);
				style1.setBorderBottom(BorderStyle.THIN);
				style1.setBorderLeft(BorderStyle.THIN);
				style1.setBorderRight(BorderStyle.THIN);
				style1.setBorderTop(BorderStyle.THIN);
				style1.setVerticalAlignment(VerticalAlignment.CENTER);
				style1.setAlignment(HorizontalAlignment.CENTER);
				style1.setWrapText(true);
				// 备注格式
				XSSFCellStyle style2 = wb.createCellStyle();
				XSSFFont font1 = wb.createFont();
				font1.setColor(HSSFColor.RED.index);
				style2.setFont(font1);
				
				
		for (int i = 0; i < header.length; i++) {
			XSSFCell cell0 = XSSFWorkUtil.createCell(sheet, beginRow, i+beginCol);
			cell0.setCellValue(header[i]);
			cell0.setCellStyle(style0);
		}
		int[] size = new int[header.length];
		for(int i =0;i<header.length;i++){
			size[i]=header[i].length()*1024;
		}
		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < header.length; j++) {
				XSSFCell cell = XSSFWorkUtil.createCell(sheet, i + 1+beginRow, j+beginCol);
				Object val = data.get(i).get(j);
				if (val == null) {
					cell.setCellValue("");
				} else if (val instanceof Double || val instanceof BigDecimal) {
					cell.setCellValue(decimalFormat.format(val).toString());
					sheet.autoSizeColumn((short) (j+beginCol));
				} 
				 else {
					cell.setCellValue(val.toString());
				    size[j] = size[j] > val.toString().length()*512 ? size[j] : val.toString().length()*512;
				    if(size[j]<256*256){
				    	sheet.setColumnWidth(j+beginCol, size[j]);
				    }else{
				    	sheet.setColumnWidth(j+beginCol,6000);
				    }
					
				}
				cell.setCellStyle(style1);
			}
		}
		XSSFCell cell1 = XSSFWorkUtil.createCell(sheet, data.size()+2+beginRow, 0);
		cell1.setCellValue("注：由于小数点之后的数据经四舍五入处理保留两位小数，数据产生极微小差距属正常现象");
		cell1.setCellStyle(style2);
		return wb;
	}
	
	public static XSSFWorkbook addSheet(XSSFWorkbook excel, String sheetName, String[] header, List<Record> data,int beginRow,int beginCol) {
		XSSFSheet sheet = excel.createSheet();
		PoiDownloadUtil.xssfCreateSheet(excel, sheet, sheetName, header, data, beginRow, beginCol);
		return excel;
	}
	
	private static String getLongName(Record r, int i){
		StringBuilder longName = new StringBuilder();
		for(int m=0;m<=i;m++){
			longName.append(r.get(m).toString());
		}
		return longName.toString();
	}
	
	//有总计行
	public static XSSFSheet mergeCellSum(XSSFSheet sheet,List<Record> levelList,int level){
		int[] row = new int[level]; 
		String[] name = new String[level]; 
		for(int i=0;i<level;i++){
			row[i] = 1;
			name[i] = getLongName(levelList.get(0),i);
			for(int j=0;j<levelList.size()-1;j++){
				if(!getLongName(levelList.get(j),i).equals(name[i])){
					name[i] = getLongName(levelList.get(j),i);
					sheet.addMergedRegion(new CellRangeAddress(row[i],j,i,i));
					row[i] = j+1;
				}
				if(j==levelList.size()-2){
					sheet.addMergedRegion(new CellRangeAddress(row[i],j+1,i,i));
				}
			}
		}
		return sheet;
	}
	
	//无总计行
	public static XSSFSheet mergeCell(XSSFSheet sheet,List<Record> levelList,int level){
		int[] row = new int[level]; 
		String[] name = new String[level]; 
		for(int i=0;i<level;i++){
			row[i] = 1;
			name[i] = levelList.get(0).get(i).toString();
			for(int j=0;j<levelList.size();j++){
				if(!levelList.get(j).get(i).toString().equals(name[i])){
					name[i] = levelList.get(j).get(i).toString();
					sheet.addMergedRegion(new CellRangeAddress(row[i],j,i,i));
					row[i] = j+1;
				}
				if(j==levelList.size()-1){
					sheet.addMergedRegion(new CellRangeAddress(row[i],j+1,i,i));
				}
			}
		}
		return sheet;
	}
	
	//无总计行
		public static XSSFSheet mergeCell1(XSSFSheet sheet,List<Record> levelList,int level){
			int r1 = 1;
			if(level ==3){
				String area = levelList.get(0).get(0).toString();
				for(int i=1;i<levelList.size();i++){
					if(!levelList.get(i).get(0).toString().equals(area)){
						area = levelList.get(i).get(0).toString();
						sheet.addMergedRegion(new CellRangeAddress(r1,i,0,0));
						sheet.addMergedRegion(new CellRangeAddress(r1,i,1,1));
						r1 = i+1;
					}
					if(i==levelList.size()-1){
						sheet.addMergedRegion(new CellRangeAddress(r1,i+1,0,0));
						sheet.addMergedRegion(new CellRangeAddress(r1,i+1,1,1));
					}
				}
		    }else if(level ==2){
		    	String area = levelList.get(0).get(0).toString();
		    	for(int i=1;i<levelList.size()-1;i++){
					if(!levelList.get(i).get(0).toString().equals(area)){
						area = levelList.get(i).get(0).toString();
						sheet.addMergedRegion(new CellRangeAddress(r1,i,0,0));
						r1 = i+1;
					}
					if(i==levelList.size()-1){
						sheet.addMergedRegion(new CellRangeAddress(r1,i+1,0,0));
					}
				}
		    }
			return sheet;
		}
	
}  
