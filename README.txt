Overview:
The purpose of the program is to retrieve quotes from Gutenberg.org ebooks containing search terms.

Installation Instructions:
1. Download Literature-Quote-Search-Engine.jar from the repository.
2. Download and install Java 8
3. Download and install MongoDB 4
4. Start MongoDB with mongod command
5. Run Literature-Quote-Search-Engine.jar from the command line using java -jar Literature-Quote-Search-Engine.jar

User Guide
The program asks for three directories:
1. A directory containing zipped books downloaded from gutenberg.org such as https://www.gutenberg.org/files/46/46-0.zip and https://www.gutenberg.org/files/3300/3300-8.zip
2. An empty directory for storing unzipped files
3. An empty text file for keeping a record of processed books such as log.txt.

After providing the required directories, click the "Process Books" button to store the books in the MongoDB database.
After processing is completed, type a search term into the textbox labeled "Search Terms and press the "Search" button to retrieve quotes containing the search term. AND/OR/NOT operations are planned for the next release.
