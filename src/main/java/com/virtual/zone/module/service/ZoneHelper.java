package com.virtual.zone.module.service;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.springframework.stereotype.Service;

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
                "C11", "D11", "E11", "F11", "G11", "H11", "I11","J11", "K11", "L11",
                "M11", "N11", "O11", "P11", "L18", "M18", "L22","I7"
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
            closeWorkbook(excelApp);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error interacting with Excel", e);
        } finally {
            quitExcelApp(excelApp);
        }
        return zoneData;
    }


    public  void writeMacro(String macroValue) {
        if (!isValidFilePath(EXCEL_FILE_PATH)) {
            LOGGER.severe("Excel file not found: " + EXCEL_FILE_PATH);
            return;
        }

        ActiveXComponent excelApp = null;
        try {
            excelApp = startExcelApp();
            Dispatch workbook  = openWorkbook(excelApp);

            // Access the first worksheet
            Dispatch sheets = Dispatch.get(workbook, "Sheets").toDispatch();
            Dispatch sheet = Dispatch.call(sheets, "Item", new Variant(1)).toDispatch(); // Modify the index if needed

            // Find the cell (e.g., I7) and set the value
            Dispatch range = Dispatch.invoke(sheet, "Range", Dispatch.Get, new Object[]{"I7"}, new int[1]).toDispatch();
            Dispatch.put(range, "Value", new Variant(macroValue)); // Write the value to the cell

            // Save and close the workbook
            Dispatch.call(workbook, "Save");
            Dispatch.call(workbook, "Close", false);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error running macro", e);
        } finally {
            quitExcelApp(excelApp);
        }
    }

    public  void triggerMacro(String macroName) {
        if (!isValidFilePath(EXCEL_FILE_PATH)) {
            LOGGER.severe("Excel file not found: " + EXCEL_FILE_PATH);
            return;
        }

        ActiveXComponent excelApp = null;
        try {
            excelApp = startExcelApp();
            Dispatch workbook  = openWorkbook(excelApp);

            // Run the specified macro
            Dispatch.call(excelApp, "Run", macroName);

            // Save and close the workbook
            // Save and close the workbook
            Dispatch.call(workbook, "Save");
            Dispatch.call(workbook, "Close", false);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error running macro", e);
        } finally {
            quitExcelApp(excelApp);
        }
    }

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
