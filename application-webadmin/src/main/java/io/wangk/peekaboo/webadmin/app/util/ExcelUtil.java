package io.wangk.peekaboo.webadmin.app.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelUtil {

	public static List<List<String>> readExcel(String filePath) {
		ArrayList<List<String>> lists = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(new File(filePath));
			 Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				// 遍历每一行
				Row row = sheet.getRow(rowNum);
				List<String> contents = Lists.newArrayList();
				for (Cell cell : row) {
					// 遍历每一列
					DataFormatter dataFormatter = new DataFormatter();
					String value = dataFormatter.formatCellValue(cell);
					contents.add(value);
				}
				lists.add(contents);
			}
			return lists;
		} catch (IOException e) {
			log.error("读取Excel文件出错", e);
			lists=null;
		}
		finally {
			return lists;
		}
	}

	public static void main(String[] args) {
		File file = new File("D:\\tmp\\test.xlsx");
		System.out.println(readExcel(file.getAbsolutePath()));
	}
}
