import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;


public class parser_class {

	
	public static class book_containers{
		String author = ""; 
		String title = ""; 
		String file_location = "";  
		String [] book_sentences; 
	}

	public String book_storage_location = "";
	public String hash_check_file = ""; 
	public String book_directory = ""; 
	
	List<book_containers> book_storage = new Vector<book_containers>();	//CLASS WILL GIVE
	//TO PARENT APP!
	
		public void clear_books()
		{
			book_storage.clear(); 	//CLEAR PARSED BOOKS FROM RAM! 
		}
	
		public int get_book_count()
		{
			int book_count = book_storage.size(); 
			return book_count; 
		}
	
		//REMOVE LAST CHARACTER
		public String removeLastChar(String str) {
	        return str.substring(0, str.length() - 1);
	    }
		
		//ALLOW USER TO CONFIGURE CLASS
		public int setup_env(String book_storage_location, String hash_check_file, String book_directory)
		{
			//DO NOT USE CLASS UNLESS THIS FUNC RETURNS 1! 
			//if it returns ZERO CLASS IS NOT CONFIGURED CORRECTLY
			
			this.book_storage_location = book_storage_location; 
			this.hash_check_file = hash_check_file; 
			this.book_directory = book_directory; 
			
			int config_good = 0; 
			
			
			if (Files.isDirectory(Paths.get(this.book_storage_location))) {
				
				  config_good += 1; 
				}
			
			if (Files.isDirectory(Paths.get(this.book_directory))) {
				  config_good += 1; 
				}
			
			if(config_good != 2)
			{
				System.out.println("ERROR: BOOK FOLDERS DO NOT EXIST");
				return 0; 
			}
			
			if(this.book_storage_location.endsWith("/"))
			{
				this.book_storage_location = removeLastChar(this.book_storage_location); 
			}
			
			if(this.book_directory.endsWith("/"))
			{
				this.book_directory = removeLastChar(this.book_directory); 
			}
			
			if(config_good == 2)
			{
				File file = new File(this.hash_check_file);
				try {
					if (file.createNewFile()) {
					    
					    System.out.println("HASH File has been created.");
					} else {
					
					    System.out.println("HASH File already exists.");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("ERROR: COULD NOT CREATE HASH FILE");
					return 0; 
				}
				
			return 1;
			}
			
			return 0; 
		}
		
		public String get_da_MD5(String filename) {
			
			String result = ""; //we will return this as the hash value
			try {
				FileInputStream gotcha = new FileInputStream(filename);
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.reset();
				byte[] bytes = new byte[2048];
				int numBytes;
				while ((numBytes = gotcha.read(bytes)) != -1) {
					md.update(bytes, 0, numBytes);
				}
				byte[] digest = md.digest();
			 result = new String(DatatypeConverter.printHexBinary(digest));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result; 
	}
		
	public List<book_containers> get_parsed_books()
	{
		if(this.book_storage_location.equals(""))
		{
			System.out.println("ERROR: Please Configure the class first");
		}
		
		if(this.hash_check_file.equals(""))
		{
			System.out.println("ERROR: Please Configure the class first");
		}
		
		if(this.book_directory.equals(""))
		{
			System.out.println("ERROR: Please Configure the class first"); 
		}
		
		return this.book_storage;	//return our stack of books! 
	}
	
	public int parse_text_version(String unzipped)
	{
		int parse_success = 0; 
		boolean delete_decide = false; //delete unzipped book if we cant parse it (DONT WASTE SPACE)
		boolean read_success = false;
		boolean book_metadata_success = false; 

				String file_hash = get_da_MD5(unzipped); 
				List<String> hashes_array = read_file(hash_check_file); 
				
				for (String dat_hash : hashes_array)
				{
					if(dat_hash.equals(file_hash))
					{
						System.out.println("Book already (ATTEMPTED) parsed. Dont parse again.");
						return parse_success; //DONT PARSE AGAIN WE ALREADY TRIED TO PARSE
						
					}
				}
						//LOG TO THE HASH CHECK FILE SO WE DONT PARSE THE SAME FILEZ! 
						append_log("BOOK_MD5: " + file_hash); 
				
				List<String> file_linez = read_file(unzipped);
				
				String book_title = ""; 
				String book_author = ""; 
				List<String> book_sentences = new Vector<String>();
				
				if(file_linez.size() > 3) //dont parse if we know we dont have
				{								//the full book. Waste of time. 
					book_title = ""; 
					book_author = "";
					int meta_count = 0; 
					String book_text = ""; 
					boolean found_text = false; 
					
					for( String individual_line : file_linez)	//simpy line by line in book
					{
						individual_line = individual_line.trim(); 
						
						if(individual_line.contains("Title:"))
						{
							//System.out.println("Book Title Found");
							System.out.println(individual_line);
							individual_line = individual_line.replace("Title:", ""); 
							individual_line = individual_line.trim(); 
							book_title = individual_line; 
							meta_count += 1; 
							int title_check = check_log(book_title, 2); //lets make sure we have already parsed this title :) 
							if(title_check == 1)
							{
								System.out.println("Error: Already parsed this book (title)");
								return parse_success; 
							}
						}
						
						if(individual_line.contains("Author:"))
						{
							//System.out.println("Book Title Found");
							System.out.println(individual_line);
							individual_line = individual_line.replace("Author:", ""); 
							individual_line = individual_line.trim();
							book_author = individual_line;
							meta_count += 1; 
						}
						
						if(individual_line.contains("***") && found_text == true)
						{
							//System.out.println("Found End of Book");
							found_text = false; 
						}
						
						if(found_text)
						{
							//System.out.println("Recording Book Line");
							if(individual_line != "" && individual_line.length() != 0)
							{	
							book_text += individual_line; //lets build up the strings.
															//place book into memory. 
							}
						}	
						
						if(individual_line.contains("START OF THIS PROJECT"))
						{
							//System.out.println("Found Beginning of Book");
							found_text = true; 
						}
							
				}									
				
					if(meta_count > 1)
					{	
						//lets only archive the book
						//if we got all the fields. 
						if(book_title != "" && book_author != "" && book_text != "")
						{
						book_containers book_copy = new book_containers(); 
						book_copy.author = book_author; 
						book_copy.title = book_title; 
						
						//int size_nec = book_text.length() - book_text.replace(".", "").length();
						int size_nec = book_text.split("\\.",-1).length-1;
						String [] book_senteces2 = new String[size_nec];  
						//System.out.println(book_text);  //DEBUG SHOW ALL THE BOOK SENTECES
						book_senteces2 = book_text.split("\\."); 
						//for(String x : book_senteces2) //DEBUG SHOW THEM ALL SPLIT UP BY REGEX
						//{
						//	System.out.println(x);
						//}
						
						book_copy.book_sentences = book_senteces2;
						book_copy.file_location = unzipped; 
						book_storage.add(book_copy); //lets store the book
						parse_success = 1; 
						
						//LOG TO THE HASH CHECK FILE SO WE DONT PARSE THE SAME FILEZ! 
						
						append_log("BOOK_TITLE: " + book_title); 
						
						Indexer indexer = new Indexer();
						indexer.insert(book_title, book_author, unzipped, book_senteces2, true);
						}							//so the caller can get it	
					}
				}
				
				File file = new File(unzipped); 
		        if(parse_success == 0)
		        {
		        		delete_decide = true; //IF WE CANT PARSE BOOK
		        								//DONT KEEP IT ON DISK WASTE OF SPACE. 
		     
		        }
				if(delete_decide) //we can decide to delete the unzipped books or not.
				{
			        if(file.delete()) 
			        { 
			            System.out.println("File deleted successfully"); 
			        } 
			        else
			        { 
			            System.out.println("Failed to delete the file"); 
			        }
				}
				
				return parse_success; 
	}	//***END FUNC
	
	public int check_log(String data, int type) //1 for hash value, 2 for book title
	{
		List<String> hashes_array = read_file(hash_check_file); 
		
		int for_return = 0; 
		
		List<String> local_check_array = new Vector<String>(); 
		
		if(type == 1)
		{
			for(String i : hashes_array)
			{
				if(i.contains("BOOK_MD5:"))	//lets get the hashes! 
				{
					String temp = i.replace("BOOK_MD5:", "");
					temp = temp.trim(); 
					local_check_array.add(temp); 
				}
			}
		}
		
		if(type == 2)
		{
			for(String i : hashes_array)
			{
				if(i.contains("BOOK_TITLE:"))	//lets get the hashes! 
				{
					String temp = i.replace("BOOK_TITLE:", "");
					temp = temp.trim(); 
					local_check_array.add(temp); 
				}
			}
		}
		
		for (String dat_hash : local_check_array)
		{
			if(dat_hash.equals(data))
			{
				System.out.println("Book already (ATTEMPTED) parsed. Dont parse again.");
				return 1; //DONT PARSE AGAIN WE ALREADY TRIED TO PARSE
			}
		}
		
		return for_return; 
		
	}
	
	public int append_log(String log_entry)
	{
		int for_return = 0; 
		
		try(FileWriter fw = new FileWriter(hash_check_file, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println(log_entry);
			    //more code
			    out.close(); ;
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
				for_return = 0;
				System.out.println("ERROR: COULD NOT LOG BOOK"); 
				return 0; 
			}
		
		return for_return; 
	}
	
	public void start_parsing_em(int number_to_parse) {
		
		if(this.book_storage_location.equals(""))
		{
			System.out.println("Please Configure the class first");
			return; 
		}
		
		if(this.hash_check_file.equals(""))
		{
			System.out.println("Please Configure the class first");
			return; 
		}
		
		if(this.book_directory.equals(""))
		{
			System.out.println("Please Configure the class first");
			return; 
		}
		
		// TODO Auto-generated method stub
		//String book_directory = "/Volumes/Untitled 1/GUTENBERG/gut_books/"; 
		List<String> file_list = new Vector<String>();
		stream_dir_contents(book_directory, file_list);
		int number_of_zips = file_list.size();
		System.out.println("Number of ZIPS/BOOKS: " + number_of_zips);
		
		List<String> md5_hash_list = new Vector<String>();
		boolean delete_decide = false; 
		boolean read_success = false;
		boolean book_metadata_success = false; 
		
		int book_counter = 0; 
		for(String zip_archive : file_list)	//ITERATE THROUGH ALL THE ARCHIVES
		{
			List<String> unzipped_filez = unzip_file(zip_archive); 
			
			for ( String unzipped : unzipped_filez) //iterate through all the unzipped books
			{
				System.out.println("\n\n");
				System.out.println("Attempting to Parse Book: " + unzipped);
				
				if(parse_text_version(unzipped) == 1)
				{
					System.out.println("Success: Parsed Book: " + unzipped);
					book_counter += 1; 
					if(number_to_parse != 0)
					{
						if(number_to_parse == book_counter)
						{
							return; //THIS WILL ALLOW THE USER TO STREAM TO BOOKS
									//SO THEY DONT FILL UP THEYRE RAM :) 
						}
					}
					
				}
				else
				{
					System.out.println("Error: Could Not Parsed Book: " + unzipped);
				}
				System.out.println("Book Parse Count: " + book_counter);
			}
		}
	}
	
	public static List<String> read_file(String da_file) //RETURNS FILE DATA LINE BY LINE
	{
		List<String> for_return = new Vector<String>();

		    try {
				Scanner scan = new Scanner(new File(da_file));
				while(scan.hasNextLine()){
				    String line = scan.nextLine();
				    if(line != "") //NO POINT in adding empty lines! 
				    {	
				    for_return.add(line);
				    }
				    //Here you can manipulate the string the way you want
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				for_return.clear();
				System.out.println("ERROR READING BOOK");
				return for_return; 	
			}
		
		return for_return;
	}
	
	public static int stream_dir_contents(String da_directory, List<String> da_file_names) {
		boolean parse_success = true; 
		DirectoryStream<Path> stream = null;
		try{
	          Path dir = Paths.get(da_directory);
	    	  stream = Files.newDirectoryStream(dir);
	          for (Path p: stream) {
	        	  		Path file_namee = p.getFileName();	//doesnt need "/" string on dir
	        	  		String abs_path = da_directory + "/" + file_namee.toString(); 
	        	  	if(!Files.isDirectory(p))
	        	  	{ 
	        	  	if(abs_path.endsWith(".zip"))
		        	  	{
			        System.out.println(abs_path);
			        da_file_names.add(abs_path);
		        	  	}
	        	  	}
	        	  	else {
	        	  		System.out.println("Call Func Recursively");
	        	  		stream_dir_contents(abs_path, da_file_names); 
	        	  	}
		       }
		   } catch (IOException ex) {
		       ex.printStackTrace();
		   }finally{
			   try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
		if(da_file_names.size() == 0)
		{
			return 0;
		}
		
		return 1; 
		
	}
	
	public List<String> unzip_file(String da_file_name) //UNZIP THE BOOKS
	{		
		boolean book_already_unzipped = false; 
		//RETURN THEIR DATA IN BYTE ARRAY LIST
		List<String> file_list = new Vector<String>();
		Random random = new Random();
		try {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(da_file_name));
        ZipEntry zipEntry = zis.getNextEntry();
        while(zipEntry != null){
        	String logFileName = new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(new Date());
        	double rand_int = random.nextDouble();
        	logFileName = logFileName + rand_int + "._parsed_book"; 
        	logFileName = book_storage_location + "/" + logFileName; 
            String fileName = zipEntry.getName();
            File newFile = new File(logFileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            
            //test hash
            
            String file_hash = get_da_MD5(logFileName); 
			List<String> hashes_array = read_file(hash_check_file); 
			book_already_unzipped = false; 
			
			if(check_log(file_hash, 1) == 1)
			{
				book_already_unzipped = true; 
			}
  
            //test hash
            if(book_already_unzipped == false)
            {
            file_list.add(logFileName);
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
		}
		catch (Exception e)
		{
			System.out.println("Could Not Unzip File!");
			System.out.println(e);
		}
		
		return file_list; 
		
	}
	
}