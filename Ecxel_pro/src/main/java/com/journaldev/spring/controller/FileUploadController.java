package com.journaldev.spring.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dao.DAO;

/**
 * Handles requests for the application file upload requests
 */
@Controller
public class FileUploadController {

	@Autowired
	DAO dao;

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String uploadFileHandler(@RequestParam("file") MultipartFile file, Model m)
			throws IOException, ClassNotFoundException, SQLException {

		Cell cell = null;
		ByteArrayInputStream byteArrayInputStream = null;
		System.out.println(file);
		if (!file.isEmpty()) {

			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath + File.separator + "tmpFiles");
			if (!dir.exists())
				dir.mkdirs();

			File convertedFile = new File(dir.getAbsolutePath() + File.separator + "test.xlsx");

			file.transferTo(convertedFile);

			FileInputStream fis = new FileInputStream(convertedFile); // obtaining bytes from the file

			XSSFWorkbook wb = new XSSFWorkbook(fis);

			XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
			Iterator<Row> itr = sheet.iterator(); // iterating over excel file

			Row row = itr.next();
			int column = row.getRowNum();
			int totalColumn = column + 1;

			System.out.println("total column is " + totalColumn);

			m.addAttribute("totalColumn", totalColumn);

			// logic code
			int noOfSheet = wb.getNumberOfSheets();

			System.out.println("number of sheets :" + noOfSheet);
			for (int i = 0; i < noOfSheet; i++) {
				sheet = wb.getSheet(wb.getSheetName(i));
				if (sheet != null) {
					row = sheet.getRow(0);

					if (row != null) {
						Connection conn = null;
						Statement stmt = null;
						int lastCellNumebr = row.getLastCellNum();
						StringBuilder createQuery2 = new StringBuilder();
						StringBuilder createQuery3 = new StringBuilder();
						Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
						conn = DriverManager.getConnection("jdbc:sqlserver://192.168.100.55:1440;databaseName=eTest",
								"milan", "F~?'7{e{");
						System.out.println("Connected database & delete table successfully...");
						stmt = conn.createStatement();
						String sql = "DROP TABLE ABC_m";
						stmt.executeUpdate(sql);
						System.out.println("existing table deleted.... ");
						createQuery2.append("CREATE TABLE ABC_m ").append(" ( ");
						createQuery2.append("id INT PRIMARY KEY IDENTITY (1, 1)");
						for (int cellNo = 0; cellNo < lastCellNumebr; cellNo++) {
							try {
								cell = row.getCell(cellNo);
								if (cell != null) {
									createQuery2.append(", ").append(
											cell.getStringCellValue().replace(".", "").replaceAll("\\s", "").trim())
											.append(" VARCHAR(255)");
									System.out.println("  cellNo  :" + cellNo + " cell.getStringCellValue() "
											+ cell.getStringCellValue());
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						createQuery2.append(")");
						System.out.println(createQuery2.toString());

						Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
						conn = DriverManager.getConnection("jdbc:sqlserver://192.168.100.55:1440;databaseName=eTest",
								"milan", "F~?'7{e{");
						System.out.println("Connected database successfully...");
						stmt = conn.createStatement();

						stmt.executeUpdate(createQuery2.toString());

						conn = DriverManager.getConnection("jdbc:sqlserver://192.168.100.55:1440;databaseName=eTest",
								"milan", "F~?'7{e{");
						String sql2 = "insert into ABC_m values(" + getQuestion1(sheet) + ")";
						PreparedStatement ps = conn.prepareStatement(sql2);

						Row row2 = sheet.getRow(0);
						int n = row2.getLastCellNum();
						String t = "";
						int batchSize = 20;

						Iterator<Row> rows = sheet.iterator();
						rows.next();
						int count = 0;
						while (rows.hasNext()) {
							Row nextRow = rows.next();
							Iterator<Cell> cellIterator = nextRow.cellIterator();

							int temp = 1;
							while (cellIterator.hasNext()) {
								Cell nextCell = cellIterator.next();
								if (nextCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									ps.setInt(temp++, (int) nextCell.getNumericCellValue());
								} else {
									ps.setString(temp++, nextCell.getStringCellValue());
								}
							}
							ps.addBatch();

							if (count % batchSize == 0) {
								ps.executeBatch();
							}

						}
						ps.executeBatch();

					}

					/*
					 * for (int cellNo = 1; cellNo <= lastCellNumebr; cellNo++) { try {
					 * System.out.println("cellNo is" + cellNo + "  cell.getStringCellValue()" +
					 * cell.getStringCellValue()); cell = row.getCell(cellNo); if (cell != null) {
					 * 
					 * ps.setString(cellNo, cell.getStringCellValue());
					 * 
					 * } } catch (Exception ex) { ex.printStackTrace(); }
					 */
				}

				System.out.println("sucess---data");

			}

		}

// end
		return "sucess";

	}

	private String getQuestion1(XSSFSheet sheet) {
		Row row = sheet.getRow(0);
		int n = row.getLastCellNum();
		String t = "";
		for (int i = 0; i < n - 1; i++) {
			t += "?,";
		}

		return t + "?";
	}

	/**
	 * Upload multiple file using Spring Controller
	 */

	/*
	 * @RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
	 * public @ResponseBody String uploadMultipleFileHandler(@RequestParam("name")
	 * String[] names,
	 * 
	 * @RequestParam("file") MultipartFile[] files) {
	 * 
	 * if (files.length != names.length) return "Mandatory information missing";
	 * 
	 * String message = ""; for (int i = 0; i < files.length; i++) { MultipartFile
	 * file = files[i]; String name = names[i]; try { byte[] bytes =
	 * file.getBytes();
	 * 
	 * // Creating the directory to store file String rootPath =
	 * System.getProperty("catalina.home"); File dir = new File(rootPath +
	 * File.separator + "tmpFiles"); if (!dir.exists()) dir.mkdirs();
	 * 
	 * // Create the file on server File serverFile = new File(dir.getAbsolutePath()
	 * + File.separator + name); BufferedOutputStream stream = new
	 * BufferedOutputStream(new FileOutputStream(serverFile)); stream.write(bytes);
	 * stream.close();
	 * 
	 * System.out.println("Server File Location=" + serverFile.getAbsolutePath());
	 * 
	 * message = message + "You successfully uploaded file=" + name + "<br />"; }
	 * catch (Exception e) { return "You failed to upload " + name + " => " +
	 * e.getMessage(); } } return message; }
	 */
}
