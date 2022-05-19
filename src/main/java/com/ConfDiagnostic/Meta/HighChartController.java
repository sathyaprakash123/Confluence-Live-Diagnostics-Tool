package com.ConfDiagnostic.Meta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



@RestController
public class HighChartController {

	@Value("${confluence.log.path}")
	private String logPath;

	Logger logger = LoggerFactory.getLogger(HighChartController.class);

	@GetMapping("/get-data")
	public ResponseEntity<Map<String, Integer>> getPieChart() {

		ConfluenceLogParser confParser = new ConfluenceLogParser();
		ArrayList<String> ConfLogData = confParser.doparse(logPath);
		//getBarCharDataFromConfluenceParser(ConfLogData);
		
		//Map<String, Integer> graphData = new TreeMap<>();
		
		//System.out.println(graphData.size());
		//graphData = getBarCharDataFromConfluenceParser(ConfLogData);

		Map<String, Integer> graphData = new TreeMap<>();
		graphData.put("Catalina Felix Star level", 147);
		graphData.put("OutOfMemory...", 1256);
		graphData.put("Arsenale plugin error", 3856);
		graphData.put("Catalina-Utility", 19807);

		return new ResponseEntity<>(graphData, HttpStatus.OK);
	}

	@GetMapping("/piechart")
	public ResponseEntity<?> getPieChart1() {

		PieChartData pdata1 = new PieChartData();
		PieChartData pdata2 = new PieChartData();
		pdata1.setId("1");
		pdata1.setName("OutOfMemoryErrors");
		pdata1.setYaxis(45);

		pdata2.setId("1");
		pdata2.setName("Plugin Exception");
		pdata2.setYaxis(55);

		List<PieChartData> piechartlist = new ArrayList<PieChartData>();

		piechartlist.add(pdata1);
		piechartlist.add(pdata2);

		return new ResponseEntity<>(piechartlist, HttpStatus.OK);
	}

	private Map<String, Integer> getBarCharDataFromConfluenceParser(ArrayList<String> confLogData)
	
{
		logger.info("$$$ getBarCharDataFromConfluenceParser Reached.....");
		ArrayList<String> newList = new ArrayList<String>();
		for (String holder : confLogData)
		{
			System.out.println("$$$ "+ holder);
			String marker = "WARN";
			int sepPos = holder.indexOf(marker);
			System.out.println("$$$ Index Integer : "+ sepPos);
			newList.add(holder.substring(sepPos  + marker.length()));
			
		}
		
		logger.info("$$$ Character truncation completed.....");
		
		Map<String, Integer> hm = new HashMap<String, Integer>();
		  
        for (String i : newList) {
            Integer j = hm.get(i);
            hm.put(i, (j == null) ? 1 : j + 1);
        }
        
        logger.info("$$$ Map created for occurence and count.....");
  
        // displaying the occurrence of elements in the arraylist
       // for (Map.Entry<String, Integer> val : hm.entrySet()) {
           // System.out.println("Element " + val.getKey() + " "
             //                  + "occurs"
               //                + ": " + val.getValue() + " times");
       // }
		
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
        
        hm.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
        .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue() * 1000));
        
        System.out.println("#### Reverse Sorted Map   : " + reverseSortedMap);
		return reverseSortedMap;

	}
	
	
}
