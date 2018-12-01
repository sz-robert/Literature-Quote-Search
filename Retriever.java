import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;
import org.json.simple.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Retriever {
	public Retriever () {}
	
	private String password; 
	private ServerAddress get_to_em; 
	private MongoCredential credential;
	private MongoClient mongoClient;
	private MongoDatabase db;
	
	private MongoCollection<Document> wordsCollection;
	private MongoCollection<Document> booksCollection;
	
	ArrayList<String> results = new ArrayList<>();
	
	public ArrayList<String> findSearchTerms(String searchTerms, String logicalOperator, String excludedTerms, String titleConstraint, String authorConstraint, boolean isLocal) {
		if(isLocal) {
			createLocalDBConnection();
		} 
		else {
			createRemoteDBConnection();
		}
		findQuotes(searchTerms, logicalOperator, excludedTerms, authorConstraint, titleConstraint);
		return results;
	}
	
	public ArrayList<String> findQuotes(String searchTerms, String logicalOperator, String excludedTerms, String authorConstraint, String titleConstraint) {
		ArrayList<WordResult> wordsList = new ArrayList<>();
		String searchTermsList[] = searchTerms.split(" ");
		if(!searchTerms.isEmpty()) {
			if (logicalOperator.equals("AND")) {
							wordsList = findWords(searchTermsList[0], 10, 0, authorConstraint, titleConstraint);
							//limit is 10000
							findSentences(wordsList, results, logicalOperator, searchTerms, 10000, 0, excludedTerms);
			} else 
			if (logicalOperator.equals("OR")) {
					results.add("Logical Operator OR chosen.");
					// Max Results is 10000
					int maxResults = 10000;
					int skip = 0;
					int limit = 1;
					ArrayList<WordResult> wordsListInterlaced = new ArrayList<>();
					while (maxResults > 0) {
						for (String word : searchTermsList) {
							ArrayList<WordResult> remoteWordResult = findWords(word, limit, skip, authorConstraint, titleConstraint);
							for (WordResult wr : remoteWordResult) {
								wordsListInterlaced.add(wr);
							}
						}
						skip++;
						limit++;
						maxResults--;
					}
					//limit is 1000
					for(WordResult wr : wordsListInterlaced)  {
						System.out.println(wr.getBookId() + "<--- bookid");
					}
					findSentences(wordsListInterlaced, results, logicalOperator, searchTerms, 10000, 0, excludedTerms);
			} else {
					results.add("Logical Operator must be AND/OR.");
			}
		}
		else {
			results.add("Search Terms Field is Empty.");
		}
		return results;
	}
	
	private ArrayList<WordResult> findWords(String word, int limit, int skip, String authorConstraint, String titleConstraint) {
		ArrayList<WordResult> wordsList = new ArrayList<>();
        Document projectWordFields = new Document("_id", 0);
        projectWordFields.put("bookId", 1);
        projectWordFields.put("totalOccurrences", 1);
        projectWordFields.put("locations", 1);
        //AggregateIterable<Document> aggregateWords = wordsCollection.aggregate(
        AggregateIterable<Document> aggregateWords = null;
        if (authorConstraint.isEmpty() && titleConstraint.isEmpty()) {
    		aggregateWords = wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", limit),
    	                    new Document("$skip", skip),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }
        else if (!authorConstraint.isEmpty() && titleConstraint.isEmpty()) {
    		aggregateWords = wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document("$match", new Document("author", authorConstraint)),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", limit),
    	                    new Document("$skip", skip),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }
        else if (authorConstraint.isEmpty() && !titleConstraint.isEmpty()) {
    		aggregateWords = wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document("$match", new Document("title", titleConstraint)),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", limit),
    	                    new Document("$skip", skip),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }
        else if (!authorConstraint.isEmpty() && !titleConstraint.isEmpty()) {
    		aggregateWords = wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document("$match", new Document("author", authorConstraint)),
    	                    new Document("$match", new Document("title", titleConstraint)),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", limit),
    	                    new Document("$skip", skip),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }

		for (Document doc : aggregateWords) {
			WordResult retreivedWord = new WordResult();
			String[] jsonSplit = doc.toJson().split("[\\s,;\"]+");
			retreivedWord.setBookId(jsonSplit[3]);
			retreivedWord.setTotalOccurrences(jsonSplit[6]);
			String locations = jsonSplit[9];
			retreivedWord.setLocations(locations.split("-"));
			wordsList.add(retreivedWord);
		}
		return wordsList;
	}
	private void findSentences(ArrayList<WordResult> wordsList,
									 ArrayList<String> results, 
									 String logicalOperator, 
									 String searchTerms, 
									 int limit, 
									 int skip,
									 String notTerms) {
		for(WordResult wordResult : wordsList) {
			 Document projectBookFields = new Document("_id", 0);
		        projectBookFields.put("title", 1);
		        projectBookFields.put("author", 1);
		        for(String location : wordResult.getLocations()) {
		        	projectBookFields.put("sentence-" + location, 1);
		        }
				AggregateIterable<Document> aggregateSentences = booksCollection.aggregate(
			            Arrays.asList(
			                    new Document("$match", new Document("bookId", wordResult.getBookId())),
			                    //new Document("$limit", 15),
			                    new Document("$project", new Document(projectBookFields)
			                    )));
				for (Document bookDoc : aggregateSentences) {
					JSONObject bookResult = new JSONObject(bookDoc);
					String bookTitle = (String) bookResult.get("title");
					String bookAuthor = (String) bookResult.get("author");
					String citation = bookTitle + ", " + bookAuthor + " (" + wordResult.getTotalOccurrences() + " occurrences)";
					results.add(citation);
					int indexOfLastAdded = results.indexOf(results.get(results.size()-1));
					int occurrencesCounter = 1;
					for (String location : wordResult.getLocations()) {
						String currentSentence = "sentence-" + location;
						String sentence = (String) bookResult.get(currentSentence);
						if(logicalOperator.equals("AND")) {
							if(checkAndTerms(searchTerms, sentence) && !(containsNotTerms(notTerms, sentence))) {
								results.add(occurrencesCounter + "          " + sentence);
							}
						}
						else {
							if(!(containsNotTerms(notTerms, sentence))) {
								results.add(occurrencesCounter + "          " + sentence);
							}
						}
						occurrencesCounter++;
					}
					// Remove book citation if no sentences from book were added
					if(indexOfLastAdded == (results.size()-1)) {
						results.remove(indexOfLastAdded);
					}
				}
		}
	}
	
	private boolean checkAndTerms(String searchTerms, String sentence) {
		boolean containsAll = false;
		int contains = 0;
		String[] splitSearchTerms = searchTerms.split(" ");
		
		if (splitSearchTerms.length > 1) {
			String splitSentence[] = sentence.split(" ");
			for(int i = splitSearchTerms.length - 1; i > 0; i--) {
				for(String word : splitSentence) {
					if(splitSearchTerms[i].equalsIgnoreCase(word)) {
						contains++;
						break;
					}
					else {
						//check the next word
					}
				}
			}
			if(contains == splitSearchTerms.length-1) {
				containsAll = true;
			}
		} 
		else {
			containsAll = true;
		}
		return containsAll;
	}
	private boolean containsNotTerms(String notTerms, String sentence) {
		boolean containsNOtTerms = false;
		if (!notTerms.isEmpty()) {
			String[] notTermsSplit = notTerms.split(" ");
			String[] sentenceSplit = sentence.split(" ");
			for(String word : sentenceSplit) {
				for (String notTerm : notTermsSplit) {
					if (word.equals(notTerm)) {
						containsNOtTerms = true;
						break;
					}
					else {
						// check next word 
					}
				}
			}
		}
		else {
			containsNOtTerms = false;
		}
		return containsNOtTerms;
		
	}
	private void createLocalDBConnection() {
		mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDatabase("library");
		wordsCollection = db.getCollection("wordsM2");
		booksCollection = db.getCollection("booksM2");
		
	}
	private void createRemoteDBConnection() {
		password = "password123"; 
		get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
		credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
		mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
		db = mongoClient.getDatabase("t1000");
		
		wordsCollection = db.getCollection("wordsM2");
		booksCollection = db.getCollection("booksM2");
	}
}

class WordResult {
	String bookId;
	String totalOccurrences;
	String[] locations;
	
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getTotalOccurrences() {
		return totalOccurrences;
	}
	public void setTotalOccurrences(String totalOccurrences) {
		this.totalOccurrences = totalOccurrences;
	}
	public String[] getLocations() {
		return locations;
	}
	public void setLocations(String[] locations) {
		this.locations = locations;
	}
}