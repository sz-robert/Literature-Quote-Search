import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



public class parser {


	public static void parserMain(String gutBooksDirectory, String bookStorageDirectory, String hashesFileDirectory) {
		// TODO Auto-generated method stub
		 String book_storage_location = bookStorageDirectory;                                            
		 //String hash_check_file = "parsed_hashes.txt"; 
		 String hash_check_file = hashesFileDirectory;													
		 String book_directory = gutBooksDirectory;														
		/*
		 String book_storage_location = "/Users/user/Desktop/untitled folder 2/gutenberg_book_storage";
		 //String hash_check_file = "parsed_hashes.txt"; 
		 String hash_check_file = "/Users/user/Desktop/untitled folder 2/gutenberg_book_storage/parsed_hashes.txt";
		 String book_directory = "/Volumes/Untitled 1/GUTENBERG/gut_books"; 
		*/
		parser_class main_parser = new parser_class(); 
		
		main_parser.setup_env(book_storage_location, hash_check_file, book_directory);
		
		int throttle = 0; 
		
		List <parser_class.book_containers> all_parsed_books = new Vector<parser_class.book_containers>(); 
		
		while(throttle != 3 + 1) //three iterations 1000 attempts each. 
		{
			main_parser.start_parsing_em(1000);
			
			List <parser_class.book_containers> my_bookz = main_parser.get_parsed_books();
			
			System.out.println("TESTING PARSE");
			
			for (parser_class.book_containers element : my_bookz)
			{
				//System.out.println("AUTHOR: " + element.author);
				System.out.println("parsed TITLE: " + element.title);
				
				/*
				System.out.println("BOOK LOCATION: " + element.file_location);
				String [] book_sentences = element.book_sentences;
				for(String sentence : book_sentences)
				{
					System.out.println(sentence);
				}
				*/
			}
			
			for(parser_class.book_containers ii : my_bookz)
			{
				all_parsed_books.add(ii); //ADD TO MAIN LIST
			}
			
			main_parser.clear_books();
			throttle += 1; 
		}
		
		int total_bookz_parsed = 0; 
		for(parser_class.book_containers i : all_parsed_books)
		{
			System.out.println("TITLE: " + i.title);
			String [] book_sentences = i.book_sentences;
			total_bookz_parsed += 1; 
		}
		
		System.out.println("Total number of parsed books: " + total_bookz_parsed);
		
	}

	

}