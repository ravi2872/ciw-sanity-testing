package com.ciw.sanity.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class Utility {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private Environment env;

	public static final String SRC_MAIN_RESOURCES = "src/main/resources";

	Map<String, String> imsiMap = new LinkedHashMap<String, String>();
	Map<String, String> imshMap = new LinkedHashMap<String, String>();
	Map<String, String> imsuMap = new LinkedHashMap<String, String>();
	Map<String, String> imsmMap = new LinkedHashMap<String, String>();
	Map<String, String> imsfMap = new LinkedHashMap<String, String>();
	Map<String, String> im$tMap = new LinkedHashMap<String, String>();
	Map<String, String> im$dMap = new LinkedHashMap<String, String>();

	Map<String, String> im$rMap = new LinkedHashMap<String, String>();
	Map<String, String> im$lMap = new LinkedHashMap<String, String>();
	Map<String, String> im$bMap = new LinkedHashMap<String, String>();
	Map<String, String> im$kMap = new LinkedHashMap<String, String>();

	private static List<String> SERVICE_LIST = Arrays.asList("WGS Claim Entry", "Detail Search", "Member Eligibility",
			"ITS Host Service", "WGS Claim Adjustment", "R delete", "A close", "Non WGS Adjustment", "CS90 Claim Entry",
			"CS90 Claim Adjustment", "Mass Adjustment", "Member Search", "Group Search", "Provider Search",
			"Provider Detail", "Liability Search", "Liability Detail");

	private static List<String> STC_SERVICE_LIST = Arrays.asList("Member Eligibility", "Detail Search",
			"ITS Host Service");

	public static final List<String> STC_LIST = Arrays.asList("im$r", "im$l", "im$b", "im$k");

	public static final String LIABILITY_DETAIL = "Liability Detail";

	public static final String LIABILITY_SEARCH = "Liability Search";

	private static final String UNKNOWN_HOST = "UnknownHost";

	private static final String WORKING_AS_EXPECTED = "Working As Expected";

	private static final String IMSI_TOKEN = "imsi.token";

	private static final String APPLICATION_JSON = "application/json";

	public String getProperty(String pPropertyKey) {
		return env.getProperty(pPropertyKey);
	}

	public List<String> getExcelServiceList() {
		return SERVICE_LIST;
	}

	public List<String> getStcServiceList() {
		return STC_SERVICE_LIST;

	}

	public Map<String, String> getMap(String mapName) {
		switch (mapName) {
		case "imsi":
			return imsiMap;
		case "imsh":
			return imshMap;
		case "imsu":
			return imsuMap;
		case "imsm":
			return imsmMap;
		case "imsf":
			return imsfMap;
		case "im$t":
			return im$tMap;
		case "im$d":
			return im$dMap;

		case "im$r":
			return im$rMap;
		case "im$l":
			return im$lMap;
		case "im$b":
			return im$bMap;
		case "im$k":
			return im$kMap;
		}
		return null;
	}

	public int getColumnNumber(String mapName) {
		switch (mapName) {
		case "imsi":
			return 1;
		case "imsm":
			return 2;
		case "imsu":
			return 3;
		case "imsh":
			return 4;
		case "imsf":
			return 5;
		case "im$t":
			return 6;
		case "im$d":
			return 7;
		}
		return 0;
	}

	public List<Map<String, String>> getAllMaps() {
		List<Map<String, String>> allMaps = new ArrayList<Map<String, String>>();
		allMaps.add(imsiMap);
		allMaps.add(imsmMap);
		allMaps.add(imsuMap);
		allMaps.add(imshMap);
		allMaps.add(imsfMap);
		allMaps.add(im$tMap);
		allMaps.add(im$dMap);
		return allMaps;
	}

	public List<Map<String, String>> getSTCMaps() {
		List<Map<String, String>> stsMaps = new ArrayList<Map<String, String>>();
		stsMaps.add(im$rMap);
		stsMaps.add(im$lMap);
		stsMaps.add(im$bMap);
		stsMaps.add(im$kMap);

		return stsMaps;
	}

	public String getJsonNameByServiceName(String jsonName) {
		switch (jsonName) {
		case "WGS Claim Entry":
			return "claimentry";
		case "Detail Search":
			return "claimdetail";
		case "ITS Host Service":
			return "itshostgroup";
		case "WGS Claim Adjustment":
			return "claimadjustment";
		case "R delete":
		case "A close":
			return "acloserdelete";
		case "Non WGS Adjustment":
			return "nonwgsclaimadjustment";
		case "CS90 Claim Entry":
			return "cs90claimentry";
		case "CS90 Claim Adjustment":
			return "cs90claimadjustment";
		case "Mass Adjustment":
			return "claimmassadjustment";
		case "Member Search":
			return "membersearch";
		case "Provider Detail":
			return "providerdetail";
		case "Member Eligibility":
			return "membereligibility";
		case "Group Search":
			return "groupsearch";
		case "Provider Search":
			return "providersearch";
		case "Liability Search":
			return "liabilitysearch";
		case "Liability Detail":
			return "liabilitydetail";
		}
		return jsonName;
	}

	public String getPostServiceResponseStatus(String finalEndpoint, String requestContent, String excelServiceName) {
		if (StringUtils.isEmpty(requestContent.trim())) {
			return "Service Unavailable";
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", getProperty(IMSI_TOKEN));
		HttpEntity<String> request = new HttpEntity<String>(requestContent, headers);
		try {
			restTemplate.postForEntity(finalEndpoint, request, String.class);
		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404 || e.getRawStatusCode() == 400) {
				return WORKING_AS_EXPECTED;
			} else {
				return getFormattedErrorString(e.getResponseBodyAsString());
			}
		} catch (HttpServerErrorException se) {
			return getFormattedErrorString(se.getResponseBodyAsString());
		} catch (ResourceAccessException rae) {
			System.out.println("UNKNOWN HOST: " + finalEndpoint);
			return UNKNOWN_HOST;
		}

		return WORKING_AS_EXPECTED;
	}

	public String getGetServiceResponseStatus(String finalEndpoint, String excelServiceName) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-Type", APPLICATION_JSON);
		headers.set("Authorization", getProperty(IMSI_TOKEN));
		try {
			restTemplate.exchange(finalEndpoint, HttpMethod.GET, new HttpEntity<Object>(headers), String.class);

		} catch (HttpClientErrorException e) {
			if (e.getRawStatusCode() == 404 || e.getRawStatusCode() == 400) {
				return WORKING_AS_EXPECTED;
			} else {
				return getFormattedErrorString(e.getResponseBodyAsString());
			}
		} catch (HttpServerErrorException e) {
			return getFormattedErrorString(e.getResponseBodyAsString());
		} catch (ResourceAccessException rae) {
			return UNKNOWN_HOST;
		}

		return WORKING_AS_EXPECTED;
	}

	private String getFormattedErrorString(String respBody) {
		String detailString = respBody.substring(respBody.lastIndexOf("detail"), respBody.length());
		if(detailString.contains(":")) {
			String error = detailString.substring(detailString.lastIndexOf(":")+1, detailString.lastIndexOf("\""));
			return error;
		}
		return detailString;
	}

	public InputStream writeAndGetExcelReport() throws IOException {
		System.out.println("Writing into Excel");
		try {
			Files.deleteIfExists(Paths.get(SRC_MAIN_RESOURCES + "/output.xlsx"));
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(SRC_MAIN_RESOURCES + "/template.xlsx"));
			FileOutputStream fileOut = new FileOutputStream(SRC_MAIN_RESOURCES + "/output.xlsx");
			XSSFSheet sheet = wb.getSheetAt(0);

			CellStyle errorStyle = createErrorStyle(wb);
			int row = 2;
			int col = 1;
			// Writing Main Service Data
			writeDataToExcel(getAllMaps(), sheet, row, col, errorStyle);

			col = 1;
			row = 23;
			// Writing STC service call data
			writeDataToExcel(getSTCMaps(), sheet, row, col,errorStyle);

			wb.write(fileOut);
			wb.close();
			fileOut.close();
			System.out.println("Excel Written Successfully");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return Files.newInputStream(Paths.get(SRC_MAIN_RESOURCES + "/output.xlsx"));
	}

	private CellStyle createErrorStyle(XSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        style.setFont(font);
		return style;
	}

	public void writeDataToExcel(List<Map<String, String>> inputMap, XSSFSheet sheet, int row, int col, CellStyle errorStyle) {
		int localRow = row;
		for (Map<String, String> map : inputMap) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				XSSFRow sheetRow = sheet.getRow(localRow);
				XSSFCell sheetCell = sheetRow.getCell(col);
				if (sheetCell == null) {
					sheetCell = sheetRow.createCell(col);
				} 
				sheetCell.setCellValue(entry.getValue());
				if(!entry.getValue().equals(Utility.WORKING_AS_EXPECTED)) {
					sheetCell.setCellStyle(errorStyle);
				}
				localRow++;
			}
			col++;
			localRow = row;
		}
	}

	public static String getCurrentDateAsddMMYYFormat() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMYY");
		LocalDate localDate = LocalDate.now();
		return dtf.format(localDate);
	}
}