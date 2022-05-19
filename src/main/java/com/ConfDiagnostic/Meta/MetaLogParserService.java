package com.ConfDiagnostic.Meta;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("parser")
public class MetaLogParserService {

	@Value("${confluence.log.path}")
	private String logPath;

	@GetMapping("/logpath")
	public String displayPath() {
		return logPath;

	}
	
	

}
