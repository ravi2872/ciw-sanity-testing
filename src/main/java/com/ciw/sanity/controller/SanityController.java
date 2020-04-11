package com.ciw.sanity.controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciw.sanity.config.Utility;
import com.ciw.sanity.service.SanityService;

@RestController
public class SanityController {

	@Autowired
	public SanityService service;

	@GetMapping("/sanityReport")
	public ResponseEntity<InputStreamResource> getSanityReport() throws IOException {

		InputStream generatedSanityReport = service.generateSanityReport();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition",
				"attachment; filename=SanityReport_" + Utility.getCurrentDateAsddMMYYFormat() + ".xlsx");
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(generatedSanityReport));
	}
}
