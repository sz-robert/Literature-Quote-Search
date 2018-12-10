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