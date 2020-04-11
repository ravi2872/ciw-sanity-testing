package com.ciw.sanity.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ciw.sanity.config.Utility;
import com.ciw.sanity.runners.SanityRunner;

@Service
public class SanityService {
	
	@Autowired
	Utility utility;
	
	public InputStream generateSanityReport() throws IOException {
		long start = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(7);
		List<SanityRunner> taskList = new ArrayList<SanityRunner>();
		
		Files.list(Paths.get(Utility.SRC_MAIN_RESOURCES)).filter(path -> Files.isDirectory(path)).forEach(rootDir -> {
			taskList.add(new SanityRunner(rootDir, utility));
		});
		taskList.forEach(task -> executorService.execute(task));
		executorService.shutdown();
		
		try {
			executorService.awaitTermination(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		System.out.println("Time taken to retrive from all services: "+TimeUnit.MILLISECONDS.toSeconds(end-start)+" Sec(s)");
		start = System.currentTimeMillis();
		InputStream excelStream = utility.writeAndGetExcelReport();
		end = System.currentTimeMillis();
		System.out.println("Time taken to write to excel: "+(end-start)+" ms");
	
		return excelStream;
	}
}
