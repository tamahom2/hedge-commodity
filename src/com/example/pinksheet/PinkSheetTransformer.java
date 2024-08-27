package com.example.pinksheet;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PinkSheetTransformer {

    private String filePath;

    public PinkSheetTransformer(String filePath) {
        this.filePath = filePath;
    }

    public static Map<String, Commodity> loadCommodityData(String excelFilePath) {
        Map<String, Commodity> commodityMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Monthly Prices");

            Iterator<Row> rowIterator = sheet.iterator();
            Row headerRow = null;
            Row unitRow = null;

            // Skip the first 4 rows
            for (int i = 0; i < 4; i++) {
                rowIterator.next();
            }

            if (rowIterator.hasNext()) {
                headerRow = rowIterator.next();
            }

            if (rowIterator.hasNext()) {
                unitRow = rowIterator.next();
            }

            if (headerRow != null && unitRow != null) {
                while (rowIterator.hasNext()) {
                    Row dataRow = rowIterator.next();
                    String month = dataRow.getCell(0).getStringCellValue();

                    for (int i = 1; i < dataRow.getLastCellNum(); i++) {
                        Cell cell = dataRow.getCell(i);
                        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                            String commodityName = headerRow.getCell(i).getStringCellValue();
                            String unit = unitRow.getCell(i).getStringCellValue();
                            double price = cell.getNumericCellValue();

                            Commodity commodity = commodityMap.getOrDefault(commodityName, new Commodity(commodityName, unit));
                            commodity.addMonthlyPrice(month, price);
                            commodityMap.put(commodityName, commodity);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return commodityMap;
    }
    
    public static void main(String[] args) {
        Map<String, Commodity> commodityMap = loadCommodityData("./CMO-Historical-Data-Monthly.xlsx");
		
		// Example of printing the map content
		for (Map.Entry<String, Commodity> entry : commodityMap.entrySet()) {
		    System.out.println("Commodity: " + entry.getKey());
		    Commodity cmp = entry.getValue();
		    System.out.println("Unit: " + cmp.getUnit());
		    /*
		     for (int i = 0; i < cmp.getMonths().size(); i++) {
		        System.out.println("Month: " + cmp.getMonths().get(i) + ", Price: " + cmp.getPrices().get(i));
		    }*/
		    System.out.println();
		}
    }
    
}
