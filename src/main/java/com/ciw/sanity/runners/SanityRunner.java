package com.ciw.sanity.runners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.ciw.sanity.config.Utility;

public class SanityRunner implements Runnable{
	
	
	private static final String ENDPOINT = "endpoint.";

	private Path rootDir;
	
	private Utility utility;
	
	public SanityRunner(Path rootDir, Utility utility) {
		this.rootDir = rootDir;
		this.utility = utility;
	}
	@Override
	public void run() {
		String rootName = rootDir.getFileName().toString();
		String rootEndpoint = utility.getProperty(ENDPOINT + rootName);

		if(Utility.STC_LIST.contains(rootName)) {
			handleSTCServices(rootName, rootEndpoint);
		} else {
			handleServices(rootName, rootEndpoint);
		}
	}
	private void handleSTCServices(String rootName, String rootEndpoint) {
		utility.getStcServiceList().stream().forEach(excelServiceName -> {
			String jsonName = utility.getJsonNameByServiceName(excelServiceName);
			String jsonPath = rootName + "/" + jsonName + ".json";
			String propertyValue = utility.getProperty(ENDPOINT + jsonName);
			if (Objects.isNull(propertyValue)) {
				System.out.println("Property Value is Null for : " + jsonName);
				return;
			}
			String  finalEndpoint = rootEndpoint + propertyValue;

			ClassLoader classLoader = ClassLoader.getSystemClassLoader();

			String content;
			try {
				File file = new File(classLoader.getResource(jsonPath).getFile());
				content = new String(Files.readAllBytes(file.toPath()));
				if (finalEndpoint != null && content != null) {
					String serviceStatus = utility.getPostServiceResponseStatus(finalEndpoint, content,
							excelServiceName);
					utility.getMap(rootName).put(excelServiceName, serviceStatus);
				}
			} catch (NullPointerException e) {
				System.out.println("NPE while loading the json: "+jsonPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	private void handleServices(String rootName, String rootEndpoint) {
		utility.getExcelServiceList().stream().forEach(excelServiceName -> {
			String jsonName = utility.getJsonNameByServiceName(excelServiceName);
			if (excelServiceName.equals(Utility.LIABILITY_SEARCH) || excelServiceName.equals(Utility.LIABILITY_DETAIL)) {
				String propertyValue = utility.getProperty(ENDPOINT + rootName + "." +jsonName);
				if (Objects.isNull(propertyValue)) {
					System.out.println("Property Value is Null for : " + jsonName);
					return;
				}
				String finalEndpoint = rootEndpoint + propertyValue;
				String serviceStatus = utility.getGetServiceResponseStatus(finalEndpoint, excelServiceName);
				utility.getMap(rootName).put(excelServiceName, serviceStatus);
			} else {
				String jsonPath = rootName + "/" + jsonName + ".json";
				String propertyValue = utility.getProperty(ENDPOINT + jsonName);
				if (Objects.isNull(propertyValue)) {
					System.out.println("Property Value is Null for : " + jsonName);
					return;
				}
				String  finalEndpoint = rootEndpoint + propertyValue;

				ClassLoader classLoader = ClassLoader.getSystemClassLoader();

				File file = new File(classLoader.getResource(jsonPath).getFile());
				String content;
				try {
					content = new String(Files.readAllBytes(file.toPath()));
					if (finalEndpoint != null && content != null) {
						String serviceStatus = utility.getPostServiceResponseStatus(finalEndpoint, content,
								excelServiceName);
						utility.getMap(rootName).put(excelServiceName, serviceStatus);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
