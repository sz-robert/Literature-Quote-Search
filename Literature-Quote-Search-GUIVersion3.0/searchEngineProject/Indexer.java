package searchEngineProject;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import org.bson.Document;
import com.mongodb.BulkWriteException;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

public class Indexer implements Runnable {

	MongoConnection conn;
	private Hashtable<String, Word> wordStatistics = new Hashtable<>();
	private List<WriteModel<Document>> wordDocuments = new ArrayList<>();
	public static Queue<Book> books = new LinkedList<>(); 
	public static List<String> currentlyProcessing = Collections.synchronizedList(new ArrayList<String>());
	
	public Indexer (MongoConnection conn) {
		this.conn = conn;
	}

	public void insert(Book book, boolean isLocal) {
		insert(book.getTitle(), book.getAuthor(), book.getFileLocation(), book.getSentences());
	}
	public void insert(String book_title, String book_author, String unzipped, String[] book_sentences) {
		
		// Insert Sentences into database
		String uniqueId = UUID.randomUUID().toString();
		int sentencePosition = 1;
		Document bookDocument = new Document("bookId", uniqueId)
				.append("title", book_title)
                .append("author", book_author);
                //.append("date", book_year)
                for(String sentence : book_sentences) {
                	if(sentence != null && !sentence.isEmpty()) {
                    	bookDocument.append("sentence-" + String.valueOf(sentencePosition), sentence);
                    	String[] words = sentence.split(" "); 
                    	for(String word : words) {
                    		if(!(word.length() < 1) 
                    				&& (word != "") 
                    				&& (word != " ")
                    				&& (!word.isEmpty()) 
                    				&& (word != null)
                    				&& (word.charAt(0) != '$')
                    				&& (!isKeyTooLarge(word))
                    				) {
                    			updateWordStatistics(word, sentencePosition);
                    		}
                    	}
                	}
                	sentencePosition++;
                }
        try {
        	if (!bookDocument.isEmpty() && bookDocument != null) {
    	        conn.booksCollection.insertOne(bookDocument);
    	        System.out.println("Indexer inserted Sentences for: "+ book_title );
        	}
        	else {
        		System.out.println("Indexer did not insert empty book document  for: "+ book_title );
        	}

        } catch(com.mongodb.MongoTimeoutException mongoTimeoutException) {
        	{
        		if (conn.get_to_em != null) {
        			conn.createRemote();
        		}
        		else {
        			conn.createLocal();
        		}
        	}
        }
        
        //Count Occurrences of Words
        for(String key : wordStatistics.keySet()) {
    		Word currentWord = wordStatistics.get(key);
    		Document wordDocument = new Document("word", currentWord.getName())
    	    		.append("bookId", uniqueId)
    				.append("title", book_title)
    	            .append("author", book_author)
    	            .append("totalOccurrences", currentWord.getTotal())
    	            .append("locations", currentWord.getLocations());
    	            wordDocuments.add(new InsertOneModel<>(wordDocument));
    	}
        
    	//Bulk Write Options
    	BulkWriteOptions bulkWriteOptions = new BulkWriteOptions();
    	bulkWriteOptions.ordered(false);
    	bulkWriteOptions.bypassDocumentValidation(true);
    	
    	//Insert Words into database
    	try {
    		if (!wordDocuments.isEmpty()) { //state should be: writes is not an empty list
        		conn.wordsCollection.bulkWrite(wordDocuments, bulkWriteOptions); 
            	System.out.println("Indexer inserted words for " +  book_title);
    		}
    		else {
    			System.out.println("Indexer did not insert empty words document  for: "+ book_title );
    		}

    	} catch (BulkWriteException e) {
	        List<com.mongodb.BulkWriteError> bulkWriteErrors = e.getWriteErrors();
	        for (com.mongodb.BulkWriteError bulkWriteError : bulkWriteErrors) {
	            int errorIndex = bulkWriteError.getIndex();
	            System.out.println("Bulkwrite Error: " + errorIndex);
	        }
    	}
	}
	
	private boolean isKeyTooLarge(String word) {
		// Test to see if word will generate MongoDB error: key too large to index
		boolean isTooLargeToIndex = true;
		byte[] isoBytes = null;
		try {
			isoBytes = word.getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(isoBytes.length < 1024 ) {
			isTooLargeToIndex = false;
		}
		return isTooLargeToIndex;
	}

	private void updateWordStatistics(String word, int sentencePosition) {
		// Counts word occurrences in each book
		if (wordStatistics.containsKey(word)) {
			Word existingWord = wordStatistics.get(word);
			//existingWord.setName(word);
			existingWord.incrementTotal();
			existingWord.appendLocations(String.valueOf(sentencePosition));
		}
		else {
			Word newWord = new Word();
			newWord.setName(word);
			newWord.incrementTotal();
			newWord.appendLocations(String.valueOf(sentencePosition));
			wordStatistics.put(word, newWord);
		}
	}
	 
	@Override
	public void run() {
		while (!books.isEmpty()) {
			insert(books.remove(), true);
		}
	}
}