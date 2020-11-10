package com.journaldev.spring.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dao.DAO;
import com.journaldev.spring.model.MobileInfo;

/**
 * Handles requests for the application file upload requests
 */
@Controller
public class FileUploadController {

	// private static final Logger logger =
	// LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	DAO dao;

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String uploadFileHandler(@RequestParam("file") MultipartFile file) throws IOException {
		System.out.println(file);
		if (!file.isEmpty()) {

			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath + File.separator + "tmpFiles");
			if (!dir.exists())
				dir.mkdirs();

			File convertedFile = new File(dir.getAbsolutePath() + File.separator + "test.xlsx");

			file.transferTo(convertedFile);

			FileInputStream fis = new FileInputStream(convertedFile); // obtaining bytes from the file

			// creating Workbook instance that refers to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(fis);

			XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
			Iterator<Row> itr = sheet.iterator(); // iterating over excel file

			List<MobileInfo> mobileList = new ArrayList<MobileInfo>();

			MobileInfo mobileInfo;

			while (itr.hasNext()) {

				Row row = itr.next();

				if (row.getRowNum() > 0) {
					mobileInfo = new MobileInfo();
					mobileInfo.setId((int) row.getCell(0).getNumericCellValue());
					mobileInfo.setName(row.getCell(1).getStringCellValue());
					mobileInfo.setAmount((int) row.getCell(2).getNumericCellValue());
					mobileInfo.setTotal((int) row.getCell(3).getNumericCellValue());

					mobileList.add(mobileInfo);

				}
			}

			dao.save(mobileList);

			System.out.println(mobileList.toString());
			return "sucess";

		} else {
			return "You failed to upload  because the file was empty.";
		}
	}

	/**
	 * Upload multiple file using Spring Controller
	 */
	@RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
	public @ResponseBody String uploadMultipleFileHandler(@RequestParam("name") String[] names,
			@RequestParam("file") MultipartFile[] files) {

		if (files.length != names.length)
			return "Mandatory information missing";

		String message = "";
		for (int i = 0; i < files.length; i++) {
			MultipartFile file = files[i];
			String name = names[i];
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File(rootPath + File.separator + "tmpFiles");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				System.out.println("Server File Location=" + serverFile.getAbsolutePath());

				message = message + "You successfully uploaded file=" + name + "<br />";
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		}
		return message;
	}
}
