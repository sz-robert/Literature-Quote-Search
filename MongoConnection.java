import java.util.Arrays;

import javax.swing.JOptionPane;

import org.bson.Document;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

public class MongoConnection {
	
	private String password = null; 
	ServerAddress get_to_em = null;
	private MongoCredential credential = null;
	private MongoClient mongoClient = null;
	private MongoDatabase db = null;
	
	private DB bc_db = null;
	DBCollection coll;
	
	MongoCollection<Document> wordsCollection = null;
	MongoCollection<Document> booksCollection = null;
	
	MongoConnection() {}
	
	MongoConnection(boolean isLocal) {
		if (isLocal) {
			createLocal();
		}
		else {
			createRemote();
		}
	}
	
	MongoConnection(String name) {
		if(name.contentEquals("books_collection")) {
			createRemoteBooksList();
		}
		else {
			System.out.println("Can not create connection to unknown database.");
		}
	}
	
	 void createLocal() {
		System.out.println("Creating Local Connection");
		try {
		mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDatabase("library");
		wordsCollection = db.getCollection("wordsM3");
		booksCollection = db.getCollection("booksM3");
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Error connecting to database", "Exception", JOptionPane.WARNING_MESSAGE);
		}
		System.out.println("Local Connection Created.");
	}
	 
	 void createRemote() {
			try {
				System.out.println("Creating Remote Connection");
				password = "password123"; 
				get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
				credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
				mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
				db = mongoClient.getDatabase("t1000");
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error connecting to database", "Exception", JOptionPane.WARNING_MESSAGE);
			}
			wordsCollection = db.getCollection("wordsM3");
			booksCollection = db.getCollection("booksM3");
			createIndices();
			System.out.println("Remote Connection Created.");
		}
	 void createRemoteBooksList() {
			System.out.println("Creating Remote Connection to books_collection");
			try {
				password = "password123"; 
				get_to_em = new ServerAddress("ec2-34-210-26-240.us-west-2.compute.amazonaws.com" , 9999); 
				credential = MongoCredential.createCredential("terminator", "t1000", password.toCharArray());
				mongoClient = new MongoClient(get_to_em, Arrays.asList(credential));
				bc_db = mongoClient.getDB( "t1000" );
				coll = bc_db.getCollection("book_collection");
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error connecting to database", "Exception", JOptionPane.WARNING_MESSAGE);
			}
			System.out.println("Remote Connection to books_collection Created.");
		}
	 
		void createIndices() {
			wordsCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("word"), 
					Indexes.ascending("bookId"),
					Indexes.ascending("author"),
					Indexes.descending("totalOccurrences")));
			booksCollection.createIndex(Indexes.compoundIndex(Indexes.ascending("bookId"), 
					Indexes.ascending("title"),
					Indexes.ascending("author")));	
		}

}
