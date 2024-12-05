package com.virtual.zone.module.service;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ZoneHelper {

    // Constants
    private static final Logger LOGGER = Logger.getLogger(ZoneHelper.class.getName());
    private static final String EXCEL_FILE_NAME = "/Final Rev.xlsm";
    private static final String EXCEL_APPLICATION = "Excel.Application";
    private static final String APP_ROOT = System.getProperty("user.dir");
    private static final String EXCEL_FILE_PATH = APP_ROOT + EXCEL_FILE_NAME;

    // Public Methods
    public List<String> getMacroData() {
        List<String> zoneData = new ArrayList<>();
        if (!isValidFilePath(EXCEL_FILE_PATH)) {
            LOGGER.severe("Excel file not found: " + EXCEL_FILE_PATH);
            throw new RuntimeException("Excel file not found: " + EXCEL_FILE_PATH);
        }

        String[] cellAddresses = {
                "C11", "D11", "E11", "F11", "G11", "H11", "I11", "J11", "K11", "L11",
                "M11", "N11", "O11", "P11", "L18", "M18", "L22", "I7"
        };

        String[] zoneCells = {
                "I18", "I19", "I20", "I21", "I22", "I23", "I24", "I25"
        };

        ActiveXComponent excelApp = null;
        try {
            excelApp = startExcelApp();
            Dispatch workbook = openWorkbook(excelApp);

            // Get the first worksheet (index starts at 1 in Excel)
            Dispatch sheets = Dispatch.get(workbook, "Sheets").toDispatch();
            Dispatch sheet = Dispatch.call(sheets, "Item", 1).toDispatch();


            // Process general cells and zone cells
            zoneData.addAll(processCellValues(sheet, cellAddresses));

            zoneData.addAll(multiplyCellValuesBy100(sheet, zoneCells));

            // Close the workbook without saving
            Dispatch.call(workbook, "Save");
            Dispatch.call(workbook, "Close", false);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error interacting with Excel", e);
        }
        return zoneData;
    }

    public List<String> getCurrentStateMacroData(boolean isCurrentState, Dispatch workbookFrom) {
        List<String> zoneData = new ArrayList<>();

        if (isCurrentState) {

            if (!isValidFilePath(EXCEL_FILE_PATH)) {
                LOGGER.severe("Excel file not found: " + EXCEL_FILE_PATH);
                throw new RuntimeException("Excel file not found: " + EXCEL_FILE_PATH);
            }
        }
        String[] cellAddresses = {
                "C11", "D11", "E11", "F11", "G11", "H11", "I11", "J11", "K11", "L11",
                "M11", "N11", "O11", "P11", "L18", "M18", "L22", "I7"
        };

        String[] zoneCells = {
                "I18", "I19", "I20", "I21", "I22", "I23", "I24", "I25"
        };

        Dispatch workbook = null;
        ActiveXComponent excelApp = null;

        if (isCurrentState) {


            excelApp = startExcelApp();


        }
        try {

            if (isCurrentState) {
                workbook = openWorkbook(excelApp);
            }

            // Get the first worksheet (index starts at 1 in Excel)
            Dispatch sheets = Dispatch.get(isCurrentState ? workbook : workbookFrom, "Sheets").toDispatch();
            Dispatch sheet = Dispatch.call(sheets, "Item", 1).toDispatch();


            // Process general cells and zone cells
            zoneData.addAll(processCellValues(sheet, cellAddresses));

            zoneData.addAll(multiplyCellValuesBy100(sheet, zoneCells));

            // Close the workbook without saving
            // Save and close the workbook
            Dispatch.call(isCurrentState ? workbook : workbookFrom, "Save");
            Dispatch.call(isCurrentState ? workbook : workbookFrom, "Close", false);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error interacting with Excel", e);
        }
        return zoneData;
    }


    public void writeMacro(String macroValue) {
        if (!isValidFilePath(EXCEL_FILE_PATH)) {
            LOGGER.severe("Excel file not found: " + EXCEL_FILE_PATH);
            return;
        }

        ActiveXComponent excelApp = null;
        excelApp = startExcelApp();
        Dispatch workbook = null;
        try {

            workbook = openWorkbook(excelApp);

            // Access the first worksheet
            Dispatch sheets = Dispatch.get(workbook, "Sheets").toDispatch();
            Dispatch sheet = Dispatch.call(sheets, "Item", new Variant(1)).toDispatch(); // Modify the index if needed

            // Set values in specified cells
            setCellValue(sheet, "I7", macroValue);
            setCellValue(sheet, "L18", "0");
            setCellValue(sheet, "M18", "0");


            // Save and close the workbook
            Dispatch.call(workbook, "Save");
            Dispatch.call(workbook, "Close", false);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error running macro", e);
        }
    }

    private void setCellValue(Dispatch sheet, String cellRef, Object value) {
        Dispatch cell = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[]{cellRef}, new int[1]).toDispatch();
        Dispatch.put(cell, "Value", new Variant(value));
    }


    public void triggerMacro(String macroName) {
        if (!isValidFilePath(EXCEL_FILE_PATH)) {
            LOGGER.severe("Excel file not found: " + EXCEL_FILE_PATH);
            return;
        }


        ActiveXComponent excelApp = null;
        Dispatch workbook = null;

        try {
            // Start Excel application
            excelApp = new ActiveXComponent("Excel.Application");
            excelApp.setProperty("Visible", false); // Run in the background

            // Open workbook
            Dispatch workbooks = excelApp.getProperty("Workbooks").toDispatch();
            workbook = Dispatch.call(workbooks, "Open", EXCEL_FILE_PATH).toDispatch();

            // Run the specified macro
            Dispatch.call(excelApp, "Run", macroName);

//            macroData.addAll(getCurrentStateMacroData(false, workbook));
            // Save and close the workbook
            Dispatch.call(workbook, "Save");
            Dispatch.call(workbook, "Close", false);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error running macro", e);
        }

    }

    // Helper method to kill Excel processes


    // Private Helper Methods
    private boolean isValidFilePath(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    private ActiveXComponent startExcelApp() {
        ActiveXComponent excelApp = new ActiveXComponent(EXCEL_APPLICATION);
        excelApp.setProperty("Visible", new Variant(false)); // Run Excel in the background
        return excelApp;
    }

    private Dispatch openWorkbook(ActiveXComponent excelApp) throws Exception {
        Dispatch workbooks = excelApp.getProperty("Workbooks").toDispatch();
        return Dispatch.call(workbooks, "Open", EXCEL_FILE_PATH).toDispatch();
    }

    private void closeWorkbook(ActiveXComponent excelApp) {
        Dispatch workbooks = excelApp.getProperty("Workbooks").toDispatch();
        Dispatch workbook = Dispatch.call(workbooks, "Item", 1).toDispatch();
        Dispatch.call(workbook, "Close", false);
    }

    private void quitExcelApp(ActiveXComponent excelApp) {
        if (excelApp != null) {
            excelApp.invoke("Quit", new Variant[0]);
        }
    }

    private List<String> processCellValues(Dispatch sheet, String[] cellAddresses) {
        List<String> stringList = new ArrayList<>();
        for (String cellAddress : cellAddresses) {
            Dispatch range = Dispatch.call(sheet, "Range", cellAddress).toDispatch();
            Object cellValue = Dispatch.get(range, "Value");
            stringList.add(logCellValue(cellAddress, cellValue));
        }
        return stringList;
    }

    private List<String> multiplyCellValuesBy100(Dispatch sheet, String[] zoneCells) {
        List<String> list = new ArrayList<>();
        for (String cellAddress : zoneCells) {
            Dispatch range = Dispatch.call(sheet, "Range", cellAddress).toDispatch();
            Object cellValue = Dispatch.get(range, "Value");
            if (cellValue != null) {
                try {
                    double value = Double.parseDouble(cellValue.toString()) * 100;
                    list.add(String.format("%.2f", value));
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, String.format("Invalid numeric value in cell %s", cellAddress), e);
                }
            } else {
                LOGGER.info(String.format("The value of cell %s is: Empty", cellAddress));
            }
        }
        return list;
    }

    private String logCellValue(String cellAddress, Object cellValue) {
        //        LOGGER.info(String.format("The value of cell %s is: %s", cellAddress, valueStr));
        return (cellValue != null) ? cellValue.toString() : "Empty";
    }
}
