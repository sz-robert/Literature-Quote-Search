import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import com.mongodb.BulkWriteException;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;

public class Indexer {
	
	private String password; 
	private ServerAddress get_to_em; 
	private MongoCredential credential;
	private MongoClient mongoClient;
	private MongoDatabase db;
	
	private MongoCollection<Document> wordsCollection;
	private MongoCollection<Document> booksCollection;

	private Hashtable<String, Word> wordStatistics = new Hashtable<>();
	private List<WriteModel<Document>> wordDocuments = new ArrayList<>();
	
	public Indexer () {	}
	
	public void insert(String book_title, String book_author, String unzipped, String[] book_sentences, boolean isLocal) {
		if(isLocal) {
			createLocalDBConnection();
		} 
		else {
			createRemoteDBConnection();
		}
		String uniqueId = UUID.randomUUID().toString();
		int sentencePosition = 1;
		Document bookDocument = new Document("bookId", uniqueId)
				.append("title", book_title)
                .append("author", book_author);
                //.append("date", book_year)
                for(String sentence : book_sentences) {
                	if(sentence != null) {
                    	System.out.println("Inserting" + sentence);
                    	bookDocument.append("sentence-" + String.valueOf(sentencePosition), sentence);
                    	System.out.println("Inserted: " + sentencePosition + " sentence for: " + book_title );
                    	String[] words = sentence.split(" "); 
                    	for(String word : words) {
                    		if(!(word.length() < 1) && (word != "") && (!word.isEmpty()) && (word != null) && (word.charAt(0) != '$')) {
                    			updateWordStatistics(word, sentencePosition);
                    		}
                    	}
                	}
                	sentencePosition++;
                }
        booksCollection.insertOne(bookDocument);
        System.out.println("Inserted: "+ book_title );
        
        for(String key : wordStatistics.keySet()) {
    		Word currentWord = wordStatistics.get(key);
    		Document wordDocument = new Document("word", currentWord.getName())
    		.append("bookId", uniqueId)
			.append("title", book_title)
            .append("author", book_author)
            .append("totalOccurrences", currentWord.getTotal())
            .append("locations", currentWord.getLocations());
    //wordsCollection.insertOne(wordDocument);
            //uncomment for bulk insert
            wordDocuments.add(new InsertOneModel<>(wordDocument));
    //System.out.println("Inserted: "+ currentWord.getName() + " for " +  book_title);
    	}
        	//bulk write options
	    	BulkWriteOptions bulkWriteOptions = new BulkWriteOptions();
	    	bulkWriteOptions.ordered(false);
	    	bulkWriteOptions.bypassDocumentValidation(true);
	    	//bulk write
        	wordsCollection.bulkWrite(wordDocuments, bulkWriteOptions);
        	System.out.println("Bulk Inserted words for " +  book_title);
	}
	
	private void updateWordStatistics(String word, int sentencePosition) {
		System.out.println("Inserting from updateWords: " + word);
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
	
	private void bulkInsert(List<WriteModel<Document>> docs, MongoCollection<Document> collection) {

	    BulkWriteOptions bulkWriteOptions = new BulkWriteOptions();
	    bulkWriteOptions.ordered(false);
	    bulkWriteOptions.bypassDocumentValidation(true);
	    
	    com.mongodb.bulk.BulkWriteResult bulkWriteResult = null;
	    try {
	        bulkWriteResult = collection.bulkWrite(docs, bulkWriteOptions);
	    } catch (BulkWriteException e) {
	        List<com.mongodb.BulkWriteError> bulkWriteErrors = e.getWriteErrors();
	        for (com.mongodb.BulkWriteError bulkWriteError : bulkWriteErrors) {
	            int failedIndex = bulkWriteError.getIndex();
	            System.out.println("Failed record: " + failedIndex);
	        }
	    }
	
	}
	
	private void createLocalDBConnection() {
		System.out.println("Creating local connection");
		mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDatabase("library");
		wordsCollection = db.getCollection("wordsM2");
		booksCollection = db.getCollection("booksM2");
		System.out.println("Local connection created.");
		wordsCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("word"), 
				Indexes.ascending("bookId"),
				Indexes.ascending("author"),
				Indexes.ascending("bookId"),
				Indexes.descending("totalOccurrences")));
		booksCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("bookId"), 
				Indexes.ascending("title"),
				Indexes.ascending("author")));
		
	}
	private void createRemoteDBConnection() {
		System.out.println("Creating remote connection");
		password = "password123"; 
		get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
		credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
		mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
		db = mongoClient.getDatabase("t1000");
		
		wordsCollection = db.getCollection("wordsM2");
		booksCollection = db.getCollection("booksM2");
		System.out.println("Remote connection created.");
		wordsCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("word"), 
				Indexes.ascending("bookId"),
				Indexes.ascending("author"),
				Indexes.ascending("bookId"),
				Indexes.descending("totalOccurrences")));
		booksCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("bookId"), 
				Indexes.ascending("title"),
				Indexes.ascending("author")));
	}
}

class Word{
	private String name = "";
	private int totalOccurrences = 0;
	private String locations = "";
	
	String getName() {
		return name;
	}
	String getLocations() {
		return locations;
	}
	Integer getTotal() {
		return totalOccurrences;
	}
	void setName(String name) {
		this.name = name;
	}
	void incrementTotal() {
		this.totalOccurrences++;
	}
	void appendLocations(String location) {
		this.locations += location + "-";
	}
}

class Result {
	String titleAuthor = "";
	ArrayList<String> quotes = new ArrayList<>();
}
