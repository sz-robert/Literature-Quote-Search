
import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;
import org.json.simple.JSONObject;
import com.mongodb.client.AggregateIterable;

public class Retriever {
	
	MongoConnection conn; 
	SearchInput searchInput = null;
	boolean allChecked = false;
	public Retriever (MongoConnection conn) {
		this.conn = conn;
	}
	
	public ArrayList<String> findQuotes(SearchInput searchInput, boolean isNext) {
		allChecked = false;
		this.searchInput = searchInput;
		ArrayList<String> results = new ArrayList<>();
		ArrayList<WordResult> wordsList = new ArrayList<>();
		String searchTermsList[] = searchInput.getSearchTerms().split(" ");
		
		int booksCount = (int) conn.booksCollection.count();
		if (booksCount < 10) {
			searchInput.setmaxResultsPerPage(booksCount);
		}
		
		if(searchTermsList.length > 0 && searchTermsList != null) {
			if (searchInput.getLogicalOperator().equals("AND")) {
				searchInput.setCurrentResultsFound(0);
				while (searchInput.getCurrentResultsFound() < searchInput.getMaxResultsPerPage()) {
							wordsList = findWords(searchTermsList[0]);
							if(wordsList.isEmpty()) {
								break;
							}
							findSentences(wordsList, results, searchInput);
				}
			} else 
			if (searchInput.getLogicalOperator().equals("OR")) {
				for(String searchTerm : searchTermsList) {
					wordsList = findWords(searchTerm);
					findSentences(wordsList, results, searchInput);
				}
			} else {
					results.add("Logical Operator must be AND/OR.");
			}
		}
		else {
			results.add("Search Terms Field is Empty.");
		}
		return results;
	}
	
	private ArrayList<WordResult> findWords(String word) {
		ArrayList<WordResult> wordsList = new ArrayList<>();
        Document projectWordFields = new Document("_id", 0);
        projectWordFields.put("bookId", 1);
        projectWordFields.put("totalOccurrences", 1);
        projectWordFields.put("locations", 1);
        AggregateIterable<Document> aggregateWords = null;

        if (searchInput.getAuthorConstraint().isEmpty() && searchInput.getTitleConstraint().isEmpty()) {
    		aggregateWords = conn.wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", searchInput.getLimit()),
    	                    new Document("$skip", searchInput.getSkip()),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }
        else if (!searchInput.getAuthorConstraint().isEmpty() && searchInput.getTitleConstraint().isEmpty()) {
    		aggregateWords = conn.wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document("$match", new Document("author", searchInput.getAuthorConstraint())),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", searchInput.getLimit()),
    	                    new Document("$skip", searchInput.getSkip()),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }
        else if (searchInput.getAuthorConstraint().isEmpty() && !searchInput.getTitleConstraint().isEmpty()) {
    		aggregateWords = conn.wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document("$match", new Document("title", searchInput.getTitleConstraint())),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", searchInput.getLimit()),
    	                    new Document("$skip", searchInput.getSkip()),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }
        else if (!searchInput.getAuthorConstraint().isEmpty() && !searchInput.getTitleConstraint().isEmpty()) {
    		aggregateWords = conn.wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document("$match", new Document("author", searchInput.getAuthorConstraint())),
    	                    new Document("$match", new Document("title", searchInput.getTitleConstraint())),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", searchInput.getLimit()),
    	                    new Document("$skip", searchInput.getSkip()),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }
        else  {
    		aggregateWords = conn.wordsCollection.aggregate(
    	            Arrays.asList(
    	                    new Document("$match", new Document("word", word)),
    	                    new Document ("$sort", new Document("totalOccurrences", -1)),
    	                    new Document("$limit", searchInput.getLimit()),
    	                    new Document("$skip", searchInput.getSkip()),
    	                    new Document("$project", new Document(projectWordFields)
    	                    )));
        }
        int counter = 0;
		for (Document doc : aggregateWords) {
			counter++;
			WordResult retreivedWord = new WordResult();
			String[] jsonSplit = doc.toJson().split("[\\s,;\"]+");
			retreivedWord.setBookId(jsonSplit[3]);
			retreivedWord.setTotalOccurrences(jsonSplit[6]);
			String locations = jsonSplit[9];
			retreivedWord.setLocations(locations.split("-"));
			wordsList.add(retreivedWord);
		}
		if(counter == 0) {
			allChecked = true;
		}
		return wordsList;
	}
	private void findSentences(ArrayList<WordResult> wordsList,
									 ArrayList<String> results, 
									 SearchInput searchInput) {
		for(WordResult wordResult : wordsList) {
			 Document projectBookFields = new Document("_id", 0);
		        projectBookFields.put("title", 1);
		        projectBookFields.put("author", 1);
		        for(String location : wordResult.getLocations()) {
		        	projectBookFields.put("sentence-" + location, 1);
		        }
		        
				AggregateIterable<Document> aggregateSentences = conn.booksCollection.aggregate(
			            Arrays.asList(
			                    new Document("$match", new Document("bookId", wordResult.getBookId())),
			                   // new Document("$limit", limit),
	    	                   // new Document("$skip", skip),
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
						if(searchInput.getLogicalOperator().equals("AND")) {
							if(checkAndTerms(searchInput.getSearchTerms(), sentence) && !(containsNotTerms(searchInput.getExcludedterms(), sentence))) {
								results.add(occurrencesCounter + "          " + sentence);
							}
						}
						else {
							if(!(containsNotTerms(searchInput.getExcludedterms(), sentence))) {
								results.add(occurrencesCounter + "          " + sentence);
							}
						}
						occurrencesCounter++;
					}
					// Remove book citation if no sentences from book were added
					if(indexOfLastAdded == (results.size()-1)) {
						results.remove(indexOfLastAdded);
					}
					else {
						int currentTotalFound = searchInput.getCurrentResultsFound();
						searchInput.setCurrentResultsFound(++currentTotalFound);
					}

					int currentSkip = searchInput.getSkip();
					int currentLimit = searchInput.getLimit();
					searchInput.setSkip(++currentSkip);
					searchInput.setLimit(++currentLimit);

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
}