package com.hg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



@Path("/")
public class Services {

	@POST
	@Path("/search")
	@Produces("application/json")	
	public String search(String inputStr) throws Exception {
		JSONObject mainObject = new JSONObject();
		JSONArray jsarray = new JSONArray();
		
		System.out.println("input::Before::" + inputStr);		
		int beginIndex = (inputStr.indexOf('[')) + 1;
		int endIndex =inputStr.indexOf(']');
		inputStr=inputStr.substring(beginIndex, endIndex);
		inputStr=inputStr.replaceAll("\"", "");		
		System.out.println("input::After::" + inputStr);
		
		String[] searchArray = inputStr.split(",");		
		for(int i=0;i<searchArray.length;i++){
			Integer count = getWordCount(searchArray[i]);
			JSONObject jsObj = new JSONObject();
			jsObj.put(searchArray[i].trim(), count);
			jsarray.add(jsObj);
		}
		mainObject.put("counts", jsarray);
		System.out.println(mainObject.toString());
		return mainObject.toString();		
	}

	

	@GET
	@Path("/top/{number}")
	@Produces("text/csv")	
	public Response top(@PathParam("number") int number) throws Exception {
		
		String paragraph=new Scanner(sampleParagraph()).useDelimiter("\\Z").next();
		StringTokenizer s = new StringTokenizer(paragraph.toLowerCase(), " ,.", false);
	    Set<String> uniqueWords = new HashSet<String>();
	    while (s.hasMoreTokens()) {
	        uniqueWords.add(s.nextToken());
	    }
		HashMap<String,Integer> hm = new HashMap<String,Integer>();
	    Iterator iterator = uniqueWords.iterator();
	    while (iterator.hasNext()){
	    	String searchWord=iterator.next().toString().trim();
	    	Integer count = getWordCount(searchWord);
	    	hm.put(searchWord, count);
	    }
	    Set<Entry<String, Integer>> set = hm.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
        File file = new File("output.csv");
        FileWriter fileWriter = new FileWriter(file);
        for(int i=0;i<number;i++){
            System.out.println(list.get(i).getKey()+"|"+list.get(i).getValue());
            fileWriter.append(list.get(i).getKey().toString());
            fileWriter.append(",");
            fileWriter.append(list.get(i).getValue().toString());
            fileWriter.append("\n");
        }  
        fileWriter.flush();
		fileWriter.close();
        return Response.ok(file, "text/csv")
        		      .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) 
        		      .build();
	}	

	@GET
	@Path("/sampleParagraph")
	public File sampleParagraph() {
		File f = new File("sampleParagraph.txt");		
		return f;
	}
	
	private Integer getWordCount(String searchWord)	throws FileNotFoundException {
		Integer count = 0;
		Scanner scanner = new Scanner(sampleParagraph());			
		while (scanner.hasNextLine()) {
			String nextToken = scanner.next();				
			if (nextToken.equalsIgnoreCase(searchWord.trim()))					
				count++;
		}
		return count;
	}
	
	
}
