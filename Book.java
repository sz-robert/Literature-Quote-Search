
public class Book {
	String title;
	String author;
	String fileLocation;
	String[] sentences;
	
	public Book(String title, String author, String fileLocation, String[] sentences) {
		this.title = title;
		this.author = author;
		this.fileLocation = fileLocation;
		this.sentences = sentences;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public String[] getSentences() {
		return sentences;
	}
	public void setSentences(String[] sentences) {
		this.sentences = sentences;
	}
}
