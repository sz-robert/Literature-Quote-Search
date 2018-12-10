//package test_stuff;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
import org.jsoup.select.Elements;

//import parser_class.book_containers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import com.mongodb.*;

//import javafx.util.Pair; 
 

public class specialized_ops {

	public final String USER_AGENT = "Mozilla/5.0";
	
	public String book_author = ""; 
	public String book_title = ""; 
	public String book_location = "";  
	//private String [] book_lines = null; 
	public List<String> book_lines = new Vector<String>();
	public String [] book_senteces; 
																//format htm, html, or text
						//were going to store raw book text
						//and book split sentences to make the index portion easier to do. 
	
	public MongoConnection conn;
	//get all books currently parsed into MONGO CLOUD! 
	public String [] mongo_retrieve_parsed_titles()	//we can see all the books in 
													//out cloud for the indexer
	{
		DBCursor cursor3 = null; 
		boolean book_present = false; 
		String [] array_for_return = null; 
		try {
			//MongoClient mongo = new MongoClient( "ec2-34-222-109-70.us-west-2.compute.amazonaws.com" , 9999 );
			String password = "password123"; 
			ServerAddress get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
			MongoCredential credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
			MongoClient mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
			DB db = mongoClient.getDB( "t1000" );
			DBCollection coll = db.getCollection("book_collection");
			BasicDBObject fields = new BasicDBObject();
			//fields.put("book_senteces", 1);
			fields.put("book_title", 1);
			BasicDBObject whereQuery = new BasicDBObject();
			  whereQuery.put("document_type", "gutenburg_book"); 
			  whereQuery.put("parsed", true);
			  //whereQuery.put("book_sentences", 1);
			  //whereQuery.put("book_title", 1);
			   cursor3 = coll.find(whereQuery, fields);
			   
			  if(cursor3.size() == 0)
			  {
				  System.out.println("ERROR: Book not in cloud.");
			  }
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null; 
		}
		System.out.println("Book in cloud. Retrieving sentences");
		try {
				int throttle = 0; 
				array_for_return = new String[cursor3.size()]; 
				if(cursor3.size() == 0)
				{
					System.out.println("ERROR: No Books in DB");
					return null; //there are not titles in that book. 
				}
				
				System.out.println("Number of Books Parsed in DB: " + cursor3.size());
				while(cursor3.hasNext()) {
				    DBObject resultElement = cursor3.next();
				    Map resultElementMap = resultElement.toMap();  
				    String scores = (String) resultElementMap.get("book_title");
				    System.out.println("Book Title: " + scores);
				    //Do something with the values
				    array_for_return[throttle] = scores; 
				    throttle += 1; 
				}
					return array_for_return; 
		}
					
		catch(Exception e)
		{
			System.out.println(e);
			return null; 
		}
	}	//end func
	
	//return an array of sentences or return nul  **WORKS NOW GOOD TO GO**
	public String [] mongo_retrieve_sentences(String book_title)
	{
		DBCursor cursor3 = null; 
		boolean book_present = false; 
		String [] array_for_return = null; 
		try {
			//MongoClient mongo = new MongoClient( "ec2-34-222-109-70.us-west-2.compute.amazonaws.com" , 9999 );
			String password = "password123"; 
			ServerAddress get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
			MongoCredential credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
			MongoClient mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
			DB db = mongoClient.getDB( "t1000" );
			DBCollection coll = db.getCollection("book_collection");
			BasicDBObject fields = new BasicDBObject();
			fields.put("book_sentences", 1);	 //lets just get the book sentences so we dont 
										//slow everything down with all that downloading. 
			BasicDBObject whereQuery = new BasicDBObject();
			  whereQuery.put("book_title", book_title);
			  //whereQuery.put("format_type", book_type);
			  whereQuery.put("parsed", true);
			   cursor3 = coll.find(whereQuery, fields);
			  if(cursor3.size() == 0)
			  {
				  System.out.println("ERROR: Book not in cloud.");
			  }
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null; 
		}
		System.out.println("SUCCESS: Book in cloud. Retrieving sentences");
		try {
			int throttle = 0; 
			while(cursor3.hasNext()) {
			    DBObject resultElement = cursor3.next();
			    Map resultElementMap = resultElement.toMap();  
			    BasicDBList scores = (BasicDBList) resultElementMap.get("book_sentences");
			    if(scores.size() != 0)
			    {
			    		array_for_return = new String[scores.size()]; 
				    	//System.out.println("Storinng the sentences");
				    for(Object i : scores)
					    {
					    	array_for_return[throttle] = (String)i; 
					    	throttle += 1; 
					    }
			    }
			    //Do something with the values
			    return array_for_return; 
			}
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return null; 
	}
	public String [] mongo_retrieve_sentences(String book_title, MongoConnection conn)
	{
		DBCursor cursor3 = null; 
		boolean book_present = false; 
		String [] array_for_return = null; 
		try {
			BasicDBObject fields = new BasicDBObject();
			fields.put("book_sentences", 1);	 //lets just get the book sentences so we dont 
										//slow everything down with all that downloading. 
			BasicDBObject whereQuery = new BasicDBObject();
			  whereQuery.put("book_title", book_title);
			  //whereQuery.put("format_type", book_type);
			  whereQuery.put("parsed", true);
			   cursor3 = conn.coll.find(whereQuery, fields);
			  if(cursor3.size() == 0)
			  {
				  System.out.println("ERROR: Book not in cloud.");
			  }
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null; 
		}
		System.out.println("SUCCESS: Book in cloud. Retrieving sentences");
		try {
			int throttle = 0; 
			while(cursor3.hasNext()) {
			    DBObject resultElement = cursor3.next();
			    Map resultElementMap = resultElement.toMap();  
			    BasicDBList scores = (BasicDBList) resultElementMap.get("book_sentences");
			    if(scores.size() != 0)
			    {
			    		array_for_return = new String[scores.size()]; 
				    	//System.out.println("Storinng the sentences");
				    for(Object i : scores)
					    {
					    	array_for_return[throttle] = (String)i; 
					    	throttle += 1; 
					    }
			    }
			    //Do something with the values
			    return array_for_return; 
			}
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return null; 
	}
	public String mongo_retrieve_entire_book(String book_title)
	{
		DBCursor cursor3 = null; 
		boolean book_present = false; 
		String [] array_for_return = null; 
		try {
			//MongoClient mongo = new MongoClient( "ec2-34-222-109-70.us-west-2.compute.amazonaws.com" , 9999 );
			String password = "password123"; 
			ServerAddress get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
			MongoCredential credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
			MongoClient mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
			DB db = mongoClient.getDB( "t1000" );
			DBCollection coll = db.getCollection("book_collection");
			BasicDBObject fields = new BasicDBObject();
			fields.put("book_text", 1);	 //lets just get the book sentences so we dont 
										//slow everything down with all that downloading. 
			BasicDBObject whereQuery = new BasicDBObject();
			  whereQuery.put("book_title", book_title);
			  //whereQuery.put("format_type", book_type);
			  whereQuery.put("parsed", true);
			   cursor3 = coll.find(whereQuery, fields);
			  if(cursor3.size() == 0)
			  {
				  System.out.println("ERROR: Book not in cloud.");
			  }
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null; 
		}
		System.out.println("SUCCESS: Book in cloud. Retrieving book text");
		try {
			int throttle = 0; 
			while(cursor3.hasNext()) {
			    DBObject resultElement = cursor3.next();
			    Map resultElementMap = resultElement.toMap();  
			    String scores = (String) resultElementMap.get("book_text");
			    if(scores.length() != 0)
			    {
			    		return scores; 	//return the first one that is not null
			    }
	 
			}
			
			return null; 
		}
		
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		return null; 
	}
	
	
	
	//return 1 if the books already up there in the cloud
	public int mongo_check_if_book_exist(String book_title, String book_type)
	{
		
		//DBCursor cursor3 = null; 
		try {
			//MongoClient mongo = new MongoClient( "ec2-34-222-109-70.us-west-2.compute.amazonaws.com" , 9999 );
			String password = "password123"; 
			ServerAddress get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
			MongoCredential credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
			MongoClient mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
			DB db = mongoClient.getDB( "t1000" );
			DBCollection coll = db.getCollection("book_collection");
			
			BasicDBObject whereQuery = new BasicDBObject();
			  whereQuery.put("book_title", book_title);
			  whereQuery.put("book_sentences", book_type);
			  DBCursor cursor3 = coll.find(whereQuery);
			  if(cursor3.size() != 0)
			  {
				  System.out.println("Book Already Stored in Cloud.");
				  return 1; 
			  }
		}
		catch(Exception e)
		{
			System.out.println(e);
			return 0; 
		}
		
		return 0; 
		
	}
	
	public int mongo_cloud_insert_book(String booktitle, String booktext, String bookauthor,  
			String [] book_senteces ,String formattype, String booklocation, boolean parse_success) 
	{
		int for_return = 0; 

		try {
			//MongoClient mongo = new MongoClient( "ec2-34-222-109-70.us-west-2.compute.amazonaws.com" , 9999 );
			String password = "password123"; 
			ServerAddress get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
			MongoCredential credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
			MongoClient mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
			DB db = mongoClient.getDB( "t1000" );
			DBCollection coll = db.getCollection("book_collection");
			
			BasicDBObject whereQuery = new BasicDBObject();
			  whereQuery.put("book_title", booktitle);
			  whereQuery.put("format_type", formattype);
			  DBCursor cursor3 = coll.find(whereQuery);
			  if(cursor3.size() != 0)
			  {
				  System.out.println("Book Already Stored in Cloud.");
				  return 1; 
			  }
			  //while (cursor3.hasNext()) {
				//System.out.println(cursor3.next());
			  //}
			 
			  ArrayList <String> sentence_list = new ArrayList<String>();	//so we can store our split sentences in cloud may as well
			  for(String sentence : book_senteces)
			  {
				  sentence_list.add(sentence); 
			  }
			  
			  if(book_senteces.length == 0)	//screwed up couldnt parse book store anywayz
			  {
				  BasicDBObject doc = new BasicDBObject("gutenburg_book", "MongoDB")
						  .append("document_type", "gutenburg_book")
							.append("format_type", formattype)
							.append("parsed", false)	 // so the indexer can know not to index this
							.append("book_title", booktitle)
							.append("book_text", booktext);
				  return 1; 
			  }
			  
			  //if books not in cloud store him in cloud! 
			BasicDBObject doc = new BasicDBObject("gutenburg_book", "MongoDB")
					.append("document_type", "gutenburg_book")
					.append("format_type", formattype)
					.append("indexed", false)
					.append("parsed", parse_success)
					.append("book_location", booklocation)
					.append("book_title", booktitle)
					.append("book_author", bookauthor)
					.append("book_text", booktext)
					.append("book_sentences", sentence_list); 
			coll.insert(doc);
			System.out.println("SUCCESS: Uploaded book to Cloud");
			return 1; 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
			System.out.println("CRITICAL ERROR: CANNOT ACCESS CLOUD DB");
			return 0; 
		}
		
	}
	
	public void cleanup_class()
	{
		this.book_author = ""; 
		this.book_title = ""; 
		this.book_location = ""; 
		this.book_lines = new Vector<String>(); 
	}
	
	public String[] splitAndKeep(String input, String regex, int offset) {
        ArrayList<String> res = new ArrayList<String>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        int pos = 0;
        while (m.find()) {
            res.add(input.substring(pos, m.end() - offset));
            pos = m.end() - offset;
        }
        if(pos < input.length()) res.add(input.substring(pos));
        return res.toArray(new String[res.size()]);
    }
	
	public String[] splitAndKeep(String input, String regex) {
        return splitAndKeep(input, regex, 0);
    }
	
	public int ensure_hash_file(String hash_check_file)
	{
		
		File file = new File(hash_check_file);
		try {
			if (file.createNewFile()) {
			    
			    System.out.println("HASH File has been created.");
			    System.out.println(hash_check_file);
			} else {
			
			    //System.out.println("HASH File already exists.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR: COULD NOT CREATE HASH FILE");
			return 0; 
		}
		
		return 1; //hash log file good to go
	}
	
	public int parse_html(String URL_or_file, String hash_check_file)
	{
		
		if(ensure_hash_file(hash_check_file) == 0)
		{
			System.out.println("ERROR: Unknown Error with Hash Log File");
			return 0; 
		}
		
		this.book_location = URL_or_file; 
		String [] book_lines; 
	
		parser_class local_i = new parser_class(); 
//		local_i.setup_hash_log(hash_check_file);
		
		int for_return = 0; 
		
		try {
			File input = new File(URL_or_file);
			Document doc = Jsoup.parse(input, "UTF-8");
		
			String title_c = ""; 
			title_c = doc.title(); 
			
			this.book_title = title_c; 
			
			Element content = doc.body();
		
			Elements prelinks = content.getElementsByTag("pre"); 
			for (Element link : prelinks) {
				String book_line = link.text();
				String lines[] = book_line.split("\\r?\\n");
				for(String de_line : lines)
				{
					if(de_line.startsWith("by"))
					{
						de_line = de_line.replace("by", ""); 
						de_line = de_line.trim(); 
						this.book_author = de_line; 
					}
					
					if(de_line.startsWith("Author:")) //might be able to pick up author in this way
					{
						de_line = de_line.replace("Author:", ""); 
						de_line = de_line.trim(); 
						this.book_author = de_line; 
					}
					
					if(de_line.startsWith("Title:")) //might be able to pick up title in this way
					{								//THIS TITLE MORE ACCURATE
						de_line = de_line.replace("Title:", ""); 
						de_line = de_line.trim(); 
						this.book_title = de_line; 
					}
				}
			}
			
			if(this.book_title == null)
			{			//if we dont have a title we should not parse the book
						//the title , location, and lines are critical! 
				System.out.println("ERROR: Couldnt parse html book title");
				this.cleanup_class();
				return 0; 
			}
			
			if(this.book_title == "" )
			{
				System.out.println("ERROR: Couldnt parse html book title");
				this.cleanup_class();
				return 0; 
			}
			
			if(this.book_title.length() == 0 )
			{
				System.out.println("ERROR: Couldnt parse html book title");
				this.cleanup_class();
				return 0; 
			}
			
			if(this.book_title.contains("New File")) //invalid title
			{
				System.out.println("ERROR: Couldnt parse html book title");
				this.cleanup_class();
				return 0; 
			}
			
			System.out.println("Parsing html book: " + this.book_title); 
			
			int title_check = local_i.check_log(this.book_title, 2); //lets make sure we have already parsed this title :) 
			if(title_check == 1)
			{
				System.out.println("Error: Already parsed this book (title)");
				return 0; 
			}
			
			String doc_type = ""; 
			if(URL_or_file.endsWith(".htm"))
			{
				doc_type = "htm"; 
			}
			
			if(URL_or_file.endsWith(".html"))
			{
				doc_type = "html"; 
			}
			
			int cloud_check = mongo_check_if_book_exist(this.book_title, doc_type);
			//dont parse if its already in cloud! 
			if(cloud_check == 1)
			{
				System.out.println("Error: Already parsed this book (title) IN CLOUD");
				return 0; 
			}
					
			if(this.book_author == "")
			{
				System.out.println("Could not find book author: Proceeding");
			}
			else
			{
				System.out.println("BOOK Author: " + this.book_author);
			}
			
			Elements links = content.getElementsByTag("p");
			String full_book_text = ""; 
			for (Element link : links) {
				String book_line = link.text();
				full_book_text += book_line; 
			  //System.out.println(book_line); 
			}
															 
			//String [] da_book_lines = full_book_text.split("[a-z]*\\."); 
			String [] da_book_lines = splitAndKeep(full_book_text, "[A-Z]*\\.\\s");
			//int my_len = da_book_lines.length; 
			//String [] lines_for_index = new String[my_len]; 
			int throttle = 0; 
			for(String temp_string : da_book_lines)
			{
				temp_string = temp_string.trim(); 
				if(temp_string.length() > 6)	 // if its got less than 6 chars its prob
											//not a real sentence.
				{
					this.book_lines.add(temp_string); 
				}
			}
			
			int my_len = da_book_lines.length; 
			String [] lines_for_index = new String[my_len]; 
			int throttle2 = 0; 
			for (String temp : this.book_lines)
			{
				lines_for_index[throttle2] = temp; 
				throttle2 += 1; 
			}
			
			if(this.book_lines.size() < 4)
			{
				System.out.println("ERROR: Not Enough Lines Not parsing book"); 
				return 0; 
			}
			System.out.println("Number of lines in HTML book: " + this.book_lines.size());
			System.out.println("Location of HTM file: " + this.book_location);
			
			this.book_senteces = lines_for_index; //so the caller can get the sentences
			
			for_return = 1; 
		
		}
		catch (Exception e)
		{
				System.out.println("ERROR: Could Not Read HTML File");
				System.out.println(e);
				return 0; //lets return zero if we can read the html file!
		}
		
		/*
		try {
		Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/Virginia").get();
		System.out.print(doc.title());
		Elements newsHeadlines = doc.select("cite");
		for (Element headline : newsHeadlines) {
		  String elem_text = headline.text();
		  System.out.println(elem_text);
		}
		}
		catch (Exception e)
		{
			System.out.print(e);
		}
		*/
		return for_return; 
	}
	
	public int grab_stock_data(String company_symbol)
	{
		int for_return = 1; 
		String api_key = "KF19RYWNRUNO7T6D"; 
		try {
			String urlParameters = "function=TIME_SERIES_MONTHLY&symbol=" + company_symbol + "&apikey=" + api_key;
		String url = "https://www.alphavantage.co/query?";
		url = url + urlParameters; 
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		//add reuqest header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		//con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		//String urlParameters = "function=TIME_SERIES_MONTHLY&symbol=" + company_symbol + "&apikey=" + api_key;
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		StringBuilder jsonString = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			jsonString.append(inputLine);
		}
		in.close();
		
		JSONParser parser = new JSONParser(); 
		JSONObject json = (JSONObject) parser.parse(response.toString());
		JSONObject json2 = new JSONObject(); 
		 Set keyz = json.keySet();
		 Iterator <String> keys = keyz.iterator();
		while(keys.hasNext()) {
		    String da_key = keys.next(); 
		    if(da_key.contains("Monthly"))
		    		{
		    			json2 = (JSONObject) json.get(da_key); 
		    			//json2 = (JSONObject) parser.parse(response.toString());
		    		}
		    //System.out.println(json.get(da_key)); 
		}
		
		keyz = json2.keySet();
		keys = keyz.iterator();
		while(keys.hasNext())
		{
			String da_key = keys.next(); 
			System.out.println(json2.get(da_key));
		}
		
		
		//print result
		//System.out.println(response.toString());
		}
		catch (Exception e) //if exception return zero
		{
			for_return = 0; 
			System.out.println("Error grabbing stock data!");
			System.out.println(e);
		}
		
		return for_return; 
	}
	
}