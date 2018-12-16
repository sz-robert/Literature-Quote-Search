
public class SearchInput {
	private String searchTerms = "";
	private String logicalOperator = "";
	private String excludedterms = "";
	private String titleConstraint = "";
	private String authorConstraint = "";
	private Integer skip;
	private Integer limit;
	private int totalBooksToSearch;
	private Integer currentResultsFound = 0;
	private Integer maxResultsPerPage = 10;
	
	public SearchInput(String searchTerms, String logicalOperator, String excludedTerms, String authorConstraint, String titleConstraint) {
		setSearchTerms(searchTerms);
		setLogicalOperator(logicalOperator);
		setExcludedterms(excludedTerms);
		setAuthorConstraint(authorConstraint);
		setTitleConstraint(titleConstraint);
		
		setSkip(0);
		setLimit(10);
	}
	public String getSearchTerms() {
		return searchTerms;
	}
	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}
	public String getLogicalOperator() {
		return logicalOperator;
	}
	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}
	public String getExcludedterms() {
		return excludedterms;
	}
	public void setExcludedterms(String excludedterms) {
		this.excludedterms = excludedterms;
	}
	public String getAuthorConstraint() {
		return authorConstraint;
	}
	public void setAuthorConstraint(String authorConstraint) {
		this.authorConstraint = authorConstraint;
	}
	public String getTitleConstraint() {
		return titleConstraint;
	}
	public void setTitleConstraint(String titelConstraint) {
		this.titleConstraint = titelConstraint;
	}
	public int getSkip() {
		return skip;
	}
	public void setSkip(Integer skip) {
		this.skip = skip;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public Integer getMaxResultsPerPage() {
		return maxResultsPerPage;
	}
	public void setmaxResultsPerPage(Integer resultsPerPage) {
		this.maxResultsPerPage = resultsPerPage;
	}
	public Integer getTotalBooksToSearch() {
		return totalBooksToSearch;
	}
	public void setTotalBooksToSearch(int totalBooksToSearch) {
		this.totalBooksToSearch = totalBooksToSearch;
	}
	public Integer getCurrentResultsFound() {
		return currentResultsFound;
	}
	public void setCurrentResultsFound(Integer currentResultsFound) {
		this.currentResultsFound = currentResultsFound;
	}
}