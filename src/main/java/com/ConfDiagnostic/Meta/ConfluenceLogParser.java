package com.ConfDiagnostic.Meta;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfluenceLogParser {

	Logger logger = LoggerFactory.getLogger(ConfluenceLogParser.class);

	@Value("${confluence.log.path}")
	private String logpath;

	private int parseRange = 2500;

	@GetMapping("/parselog")
	public ArrayList<String> doparse(String logpath) {

		return getErrorData("Error");

	}

	@GetMapping("/test")
	public ArrayList<Data> getException() {

		ArrayList<String> logData = new ArrayList<String>();
		logData = getErrorData("Exception");
		return parseErrorLog(logData, "Exception");
	}

	@GetMapping("/errors")
	public ArrayList<Data> getError() {

		ArrayList<String> logData = new ArrayList<String>();
		logData = getErrorOnly("ERROR");
		logger.warn("GET ERROR DATA COMPLETED. STARTING PARSE ERROR LOG. LOG COUNT WITH ERRORS--" + logData.size());
		return parseErrorLog(logData, "ERROR");

	}

	@GetMapping("/exceptions")
	public ArrayList<Data> getErrorAndException() {

		ArrayList<String> logData = new ArrayList<String>();
		logData = getErrorAndExceptionData("Exception");
		logger.warn("GET ERROR DATA COMPLETED. STARTING PARSE ERROR LOG. LOG COUNT WITH ERRORS--" + logData.size());
		return parseErrorLog(logData, "ERROR");

	}

	@GetMapping("/warnings")
	public ArrayList<Warning> getWarning() {

		ArrayList<String> logData = new ArrayList<String>();
		logData = getErrorData("WARN");
		return parseWarningLog(logData, "WARN");

	}

	// Exception Detail
	@RequestMapping(value = "/exception/detail", method = RequestMethod.GET)
	public ArrayList<ErrorDetail> getErrorDetail(@RequestParam(name = "name") String errorName)

	{

		logger.warn("EXTRACT ERROR TRACE FOR --" + errorName);
		ArrayList<String> logData = new ArrayList<String>();
		logData = getErrorAndExceptionData("ERROR");
		return extractErrorDetail(logData, "ERROR", errorName);

	}

	public ArrayList<String> getErrorData(String errorDetail) {
		ArrayList<String> logData = new ArrayList<String>();
		try {

			logger.warn("READING LOGS FROM : " + logpath);
			FileInputStream fstream = new FileInputStream(logpath + "/atlassian-confluence.log");
			Pattern p = Pattern.compile(errorDetail);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String line;

			for (int i = 0; i < parseRange; i++) {

				int lineCount = 0;

				// while (br.readLine() != null) {
				line = br.readLine();
				if (line == null) {
					Thread.sleep(50);
				} else {
					if (p.matcher(line).find())

						// Condition for error and exception

						logData.add(line);

				}

			} // End of While

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return logData;
	}

	// Method to receive an array list of string values and obtain occurences and
	// date values
	public ArrayList<Data> parseErrorLog(ArrayList<String> logDump, String substring) {
		ArrayList<String> logDumpTruncated = new ArrayList<String>();
		ArrayList<Data> dataList = new ArrayList<Data>();

		logger.warn("STARTING DATE TRUNCATION");
		for (String line : logDump) {

			logger.warn("PROCESSING LINE --" + line);

			String s1 = line.substring(line.indexOf(substring) + substring.length());
			logger.warn("DATE TRUNCATED LINE--" + s1);
			logDumpTruncated.add(s1.trim());

		}
		logger.warn("COMPLETED DATE TRUNCATION");

		// Find unique occurences of each error set
		HashSet<String> uniqueErrors = new HashSet<String>(logDumpTruncated);
		logger.warn("UNIQUE ERRORS EXTRACTED --" + uniqueErrors.size());
		ArrayList<String> uniqueErrorValues = new ArrayList<String>(uniqueErrors);

		// Find timestamp of each occurence of the error
		logger.warn("EXTRACTING TIMESTAMP OF UNIQUE ERRORS");

		for (String lineValue : uniqueErrorValues) {

			ArrayList<String> dateValues = new ArrayList<String>();

			for (String logLine : logDump) {

				if (logLine.contains(lineValue))

				{

					try {
						String s1 = logLine.substring(0, logLine.indexOf(substring));
						logger.warn("DATE AFTER TRUNCATION " + s1);
						dateValues.add(s1);
					}

					catch (Exception e) {
						logger.warn("SKIPPING ERROR VALUE");
					}

				}

			}
			Data data = new Data();
			data.setName(lineValue);
			data.setTimestamp(dateValues);
			dataList.add(data);

		}

		return dataList;

	}

	// Extract Error Detail
	public ArrayList<ErrorDetail> extractErrorDetail(ArrayList<String> logDump, String substring, String searchError) {
		ArrayList<String> logDumpTruncated = new ArrayList<String>();
		ArrayList<Data> dataList = new ArrayList<Data>();
		ArrayList<ErrorDetail> errorList = new ArrayList<ErrorDetail>();

		// Removing the front part of error

		for (String line : logDump) {
			String s1 = line.substring(line.indexOf(substring) + substring.length());
			logDumpTruncated.add(s1.trim());

		}

		// Find unique occurrence of each error set
		HashSet<String> uniqueErrors = new HashSet<String>(logDumpTruncated);

		logger.warn("UNIQUE ERRORS --" + uniqueErrors.toString());
		ArrayList<String> uniqueErrorValues = new ArrayList<String>(uniqueErrors);
		ArrayList<String> matchingErrorDetail = new ArrayList<String>();

		for (String error : uniqueErrorValues) {
			if (error.contains(searchError)) {
				matchingErrorDetail.add(error);
			}
		}

		// Find timestamp of each occurence of the error
		for (String lineValue : matchingErrorDetail) {

			ArrayList<String> dateValues = new ArrayList<String>();
			ArrayList<String> stackTrace = new ArrayList<String>();

			for (String logLine : logDump) {

				if (logLine.contains(lineValue))

				{
					String s1 = logLine.substring(0, logLine.indexOf(substring));
					logger.warn("ERROR DETAIL AFTER TRUNCATION " + s1);
					dateValues.add(s1);
					stackTrace.add(logLine);

				}

			}

			ErrorDetail errordetail = new ErrorDetail();
			errordetail.setName(lineValue);
			errordetail.setTimestamps(dateValues);
			errordetail.setStacktrace(stackTrace);
			errorList.add(errordetail);

		}

		return errorList;

	}

	// Creating method for Warning list

	public ArrayList<Warning> parseWarningLog(ArrayList<String> logDump, String substring) {
		ArrayList<String> logDumpTruncated = new ArrayList<String>();
		ArrayList<Data> dataList = new ArrayList<Data>();
		ArrayList<Warning> warningList = new ArrayList<Warning>();

		for (String line : logDump) {
			String s1 = line.substring(line.indexOf(substring) + substring.length());
			logDumpTruncated.add(s1.trim());

		}
		// Find unique occurences of each error set
		HashSet<String> uniqueErrors = new HashSet<String>(logDumpTruncated);
		logger.warn("UNIQUE ERRORS --" + uniqueErrors.toString());
		ArrayList<String> uniqueErrorValues = new ArrayList<String>(uniqueErrors);

		// Find timestamp of each occurence of the error
		for (String lineValue : uniqueErrorValues) {

			Warning warning = new Warning();
			warning.setName(lineValue.substring(0, 130));
			warning.setMessage(lineValue);
			warningList.add(warning);

		}

		return warningList;

	} // End of Warning logs

	public ArrayList<String> getErrorAndExceptionData(String errorDetail) { // To process error and exceptions
		ArrayList<String> logData = new ArrayList<String>();
		try {

			logger.warn("READING LOGS FROM : " + logpath);
			FileInputStream fstream = new FileInputStream(logpath + "/atlassian-confluence.log");
			Pattern p = Pattern.compile(errorDetail);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String line;

			for (int i = 0; i < parseRange; i++) {

				int lineCount = 0;

				// while (br.readLine() != null) {
				line = br.readLine();
				if (line == null) {
					Thread.sleep(50);
				} else {
					if (p.matcher(line).find())

						if (line.contains("ERROR")) {

							logData.add(line);
						}

				}

			} // End of While

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return logData;
	} // End of error and exception data parsing

	public ArrayList<String> getErrorOnly(String errorDetail) { // To process only error
		ArrayList<String> logData = new ArrayList<String>();
		try {

			logger.warn("READING LOGS FROM : " + logpath);
			FileInputStream fstream = new FileInputStream(logpath + "/atlassian-confluence.log");
			Pattern p = Pattern.compile(errorDetail);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String line;

			for (int i = 0; i < parseRange; i++) {

				int lineCount = 0;

				// while (br.readLine() != null) {
				line = br.readLine();
				if (line == null) {
					Thread.sleep(50);
				} else {
					if (p.matcher(line).find())

						if (line.contains("ERROR")) {

							if (line.contains("Exception")) // Checking case of exception and ignoring
							{
								logger.warn("Ignoring Exceptions");
							}

							else

							{
								logData.add(line);
							}
						}

				}

			} // End of While

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return logData;
	} // End of error and exception data parsing

}
