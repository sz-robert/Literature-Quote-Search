Overview:
The purpose of the program is to retrieve quotes from Gutenberg.org ebooks containing search terms.

1. Search Engine Desktop Application (Moogle) 

1.1. Installation Instructions:
1. Download Literature-Quote-Search-Engine.jar from the repository.
2. Download and install Java 8.
3. Download and install MongoDB 4. (Not necessary if using only remote features.)
4. Start MongoDB with mongod command from the command line. (Not necessary if using only remote features.)
5. Run Literature-Quote-Search-Engine.jar from the command line using java -jar Literature-Quote-Search-Engine.jar

1.2. User Guide
Searches on PCs with MongoDB installed:
The program asks for three directories:
1. A directory containing zipped books downloaded from gutenberg.org such as https://www.gutenberg.org/files/46/46-0.zip and https://www.gutenberg.org/files/3300/3300-8.zip
2. An empty directory for storing unzipped files
3. An empty text file for keeping a record of processed books such as log.txt.

After providing the required directories, click the "Process Books" button to store the books in the MongoDB database.
After processing is completed, type a search term into the textbox labeled "Search Terms and press the "Search" button to retrieve quotes containing the search term. AND/OR/NOT operations are planned for the next release.

1.3. Remote Searches
An alternative to installing MongdDB and creating a folder of zipped files is to use a remote search.
1. Select "Server" from the menu bar.
2. Select "Use Remote Database"
3. Select "Get Remote Booklist"
4. Select the books you want to search.
5. Click the "Submit Remote List" button.
After the books are processed on the remote server, they can be searched. Books retrieved from the remote server can be stored on a local database. To store the book(s) locally, start MongoDB with mongod command from the command line, ensure directory locations are provided (first directory does not have to contain any zipped books), and change step 2 to select "Use Local Database".

1.4. Search Constraints
Searches can use AND/OR/NOT operations and can be limited to a specific author or title. The author input has to be exact. For example, a search for "Charles Dickens" will work but "Charles" or "Dickens" won't return any results. The title also has to be exact.
To use AND or OR operations, type several words into the "Search Terms" field, choose either AND or OR from the drop down box, and press the "Search" button. To exclude certain terms, type them into the "Exclude Words" fields.

2. Search Engine Web Application (Moogle) 
The search engine project has been improved by using Angular frame work to add a web front end to the project. Click on the link bellow to enter the "Moogle" web site. (Moogle web site is the web version of the Search Engine Project.) 
https://searchengineapp.github.io/ 
Note: In case that clicking on the link does not work, copy and paste it in your browser. 

The display of the web site is located at:
 https://github.com/sz-robert/Literature-Quote-Search/Example_WebApp_Input.PNG https://github.com/sz-robert/Literature-Quote-Search/Example_WebApp_Output.PNG 
Source: The sourse code for this web page is located at:
 https://github.com/sz-robert/Literature-Quote-Search/Moogle/src 
Enjoy Moogle!
