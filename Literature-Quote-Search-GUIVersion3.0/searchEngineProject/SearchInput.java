package searchEngineProject;


public class SearchInput {
	private String searchTerms = "";
	private String logicalOperator = "";
	private String excludedterms = "";
	private String titleConstraint = "";
	private String authorConstraint = "";
	private int skip = 0;
	private int limit = 0;
	
	public SearchInput(String searchTerms, String logicalOperator, String excludedTerms, String authorConstraint, String titleConstraint, int skip, int limit) {
		setSearchTerms(searchTerms);
		setLogicalOperator(logicalOperator);
		setExcludedterms(excludedTerms);
		setAuthorConstraint(authorConstraint);
		setTitleConstraint(titleConstraint);
		setSkip(skip);
		setLimit(limit);
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
	public void setSkip(int skip) {
		this.skip = skip;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
}