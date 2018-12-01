
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
//import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;

public class GUI extends JFrame 
{
	static final long serialVersionUID = 123L;
	
	//variables used by the GUI interface 
	JPanel browsePanel = new JPanel ();
	JPanel resultsPanel = new JPanel ();
	JPanel browseBooksPanel = new JPanel ();
	JPanel browseStoragePanel = new JPanel ();
	JPanel browseHashPanel = new JPanel ();
	JPanel directorySubmitPanel = new JPanel ();
	JPanel selectPanel = new JPanel ();
	JPanel authorPanel = new JPanel ();
	JPanel searchPanel = new JPanel ();
	JPanel searchPanel1 = new JPanel ();
	JPanel searchPanel2 = new JPanel ();
	JPanel searchButtonPanel = new JPanel ();
	JTextField selectField = new JTextField (20);
	JTextField authorField = new JTextField (20);
	JTextField searchField = new JTextField (20);
	JTextField excludeField = new JTextField (20);
	JTextField browseBooksField = new JTextField (20);
	JTextField browseStorageField = new JTextField (20);
	JTextField browseHashField = new JTextField (20);
	JButton browseBooksButton = new JButton ("Browse");
	JButton browseStorageButton = new JButton ("Browse");
	JButton browseHashButton = new JButton ("Browse");
	JButton selectButton = new JButton ("Select");
	JButton searchButton = new JButton ("Search");
	JButton submitButton = new JButton ("Process Books");
	JLabel browseLabel = new JLabel ("Zipped books: ");
	JLabel browseStorageLabel = new JLabel ("Unzip Books To: ");
	JLabel browseHashLabel = new JLabel ("Parsed Books Log: ");
	JLabel searchLabel = new JLabel ("Search Terms: ");
	JLabel searchLabel2 = new JLabel ("Exclude Words: ");
	JLabel selectLabel = new JLabel ("Search Title: ");
	JLabel authorLabel = new JLabel ("Search Author: ");
	//JLabel blankLabel = new JLabel ("                    ");//blank label to create gap 
	JScrollPane scrollPane;
	
	JFrame frame;
	JPanel panel;
	JPanel panel1;
	JPanel panel2;
	JTable table;
	
	JComboBox <String> searchOptions = new JComboBox <String> ();
	
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem bookListMenuItem;
	JRadioButtonMenuItem localMenuItem;
	JRadioButtonMenuItem remoteMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	//creating an instance object to initialize the table
	private Object[][] searchResultTable = new Object[50][1];
	String column[] = {"Results"};
	DefaultTableModel defaultTableModel = new DefaultTableModel(column, 0);
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	static String[] titlesAvailable;
	
	//constructor to build the main GUI 
	public GUI () 
	{	
		setTitle ("Search Engine");
		setSize(800, 370);
		setLocation( // Center window on screen.
				(screen.width - 600)/2, 
				(screen.height - 600)/2 );
		setLayout(new BorderLayout());
		browsePanel.setLayout(new GridLayout(4,1));
		searchPanel.setLayout(new GridLayout(4,1));
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	
		browseBooksPanel.add (browseLabel);
		browseBooksPanel.add (browseBooksField);
		browseBooksPanel.add (browseBooksButton);
		browseStoragePanel.add(browseStorageLabel);
		browseStoragePanel.add(browseStorageField);
		browseStoragePanel.add(browseStorageButton);
		browseHashPanel.add(browseHashLabel);
		browseHashPanel.add(browseHashField);
		browseHashPanel.add(browseHashButton);
		directorySubmitPanel.add (submitButton);

		
		browsePanel.add(browseBooksPanel);
		browsePanel.add(browseStoragePanel);
		browsePanel.add(browseHashPanel);
		browsePanel.add(directorySubmitPanel, BorderLayout.CENTER);
		//browsePanel.add(searchPanel1, BorderLayout.SOUTH);
		//browsePanel.add(searchPanel2);
		
		selectPanel.add(selectLabel);
		selectPanel.add (selectField);
		//selectPanel.add (selectButton);
		
		authorPanel.add(authorLabel);
		authorPanel.add(authorField);
		
		searchPanel1.add(searchLabel);
		searchPanel1.add (searchField);
		searchOptions.addItem ("AND");
		searchOptions.addItem ("OR");
		//searchOptions.addItem ("NOT");
		searchPanel1.add (searchOptions);
		
		
		searchPanel2.add(searchLabel2);
		searchPanel2.add (excludeField);
		searchButtonPanel.add (searchButton);
		
		searchPanel.add(searchPanel1, BorderLayout.CENTER);
		searchPanel.add(searchPanel2, BorderLayout.SOUTH);
		searchPanel.add(selectPanel);
		searchPanel.add(authorPanel);
		searchPanel.add(new JLabel("   "));
		searchPanel.add(searchButtonPanel);
		searchPanel.add(new JLabel("   "));
		
		add (browsePanel, BorderLayout.PAGE_START);
		add (searchPanel, BorderLayout.CENTER);
		
		//Where the GUI is created:

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		menu = new JMenu("Server");
		menu.setMnemonic(KeyEvent.VK_A);
		menuBar.add(menu);

		//a group of radio button menu items
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		localMenuItem = new JRadioButtonMenuItem("Use Local Database");
		localMenuItem.setSelected(true);
		localMenuItem.setMnemonic(KeyEvent.VK_R);
		group.add(localMenuItem);
		menu.add(localMenuItem);
		remoteMenuItem = new JRadioButtonMenuItem("Use Remote Database");
		remoteMenuItem.setMnemonic(KeyEvent.VK_O);
		group.add(remoteMenuItem);
		menu.add(remoteMenuItem);

		//a group of JMenuItems
		bookListMenuItem = new JMenuItem("Get Remote Booklist", KeyEvent.VK_T);
		bookListMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		bookListMenuItem.addActionListener( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				selectBooks(new JTextField());
			} // end method
		} );// end inner class
		menu.add(bookListMenuItem);
		
		this.setJMenuBar(menuBar);
		
		validate ();
		//pack();
		setVisible (true);
		
		browseBooksButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				browseDirectoryLocation (browseBooksField);
			} // end method
		} );// end inner class
		
		browseStorageButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				browseDirectoryLocation (browseStorageField);
			} // end method
		} );// end inner class
		
		browseHashButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				browseFileLocation (browseHashField);
			} // end method
		} );// end inner class
	
		submitButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				submitDirectory (browseBooksField.getText(), browseStorageField.getText(), browseHashField.getText());
			} // end method
		} );// end inner class
		
		
		selectButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) 
			{
				selectBooks (selectField);
			} // end method
		} );// end inner class
		
		searchButton.addActionListener ( new ActionListener () 
		{
			public void actionPerformed (ActionEvent e) {
				search ();
			} // end method
		} );// end inner class
	
	} // end constructor
	
	
	//browseLocation method 
	public void browseDirectoryLocation (JTextField setTextField) 
	{
		
	    JFrame directoryFrame = new JFrame(); 
	    
	    final JFileChooser chooser= new JFileChooser(); 
	
	    directoryFrame.setSize(500, 100); 
	
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Directory");
	    
	    // comment out this line to enable choosing files instead of directories
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
	
	    chooser.setAcceptAllFileFilterUsed(false);
	
	    if (chooser.showOpenDialog(directoryFrame) == JFileChooser.APPROVE_OPTION) 
	    { 
	    		    	
	    	String result = chooser.getSelectedFile().toString();
	    		    	
	    	Object[] options = {"Submit", "Cancel"};
	    	
			Component frame = null;
			
			int answer = JOptionPane.showOptionDialog(frame, 
					"Are you sure you want to select the following directory?\n\n" + result +
					"\n\nSubmit to Continue/Cancel to Exit" + "\n ",
					"Confirm Destination",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					
					null,     //do not use a custom Icon
					options,  //the titles of buttons
					options[0]);
						
		    if (answer == JOptionPane.YES_OPTION) 
		    {
		    	setTextField.setText(chooser.getSelectedFile().toString());
		    } 
		    
		    else if (answer == JOptionPane.NO_OPTION) 
		    {
		    	JOptionPane.showMessageDialog(null, "Browse again");
		    	setTextField.setText(null);
		    }
	    }
	    else 
	    {
	    	setTextField.setText("No directory has been selected");
	    }  
	} 
	
	public void browseFileLocation (JTextField setTextField) 
	{
	    JFrame directoryFrame = new JFrame(); 
	    
	    final JFileChooser chooser= new JFileChooser(); 
	
	    directoryFrame.setSize(500, 100); 
	
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Directory");
	    
	    // comment out this line to enable choosing files instead of directories
	    // chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
	
	    chooser.setAcceptAllFileFilterUsed(false);
	
	    if (chooser.showOpenDialog(directoryFrame) == JFileChooser.APPROVE_OPTION) 
	    { 
	    	
	    	String result = chooser.getSelectedFile().toString();
	    		    	
	    	Object[] options = {"Submit", "Cancel"};
	    	
			Component frame = null;
			
			int answer = JOptionPane.showOptionDialog(frame, 
					"Are you sure you want to select the following directory?\n\n" + result +
					"\n\nSubmit to Continue/Cancel to Exit" + "\n ",
					"Confirm Destination",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					
					null,     //do not use a custom Icon
					options,  //the titles of buttons
					options[0]);
			
		    if (answer == JOptionPane.YES_OPTION) 
		    {
		    	setTextField.setText(chooser.getSelectedFile().toString());
		    } 
		    
		    else if (answer == JOptionPane.NO_OPTION) 
		    {
		    	JOptionPane.showMessageDialog(null, "Browse again");
		    	setTextField.setText(null);
		    }
	    	
	    }
	    else 
	    {
	    	setTextField.setText("No directory has been selected");
	    }   
	} 
	
	//submitDirectory method
	public void submitDirectory (String gutBooksDirectory, String bookStorageDirectory, String hashesFileDirectory) 
	{
		parser.parserMain(gutBooksDirectory, bookStorageDirectory, hashesFileDirectory);
	} 
	
	public void selectBooks (JTextField setTextField) 
	{
		specialized_ops so = new specialized_ops();
		String[] bookTitles = so.mongo_retrieve_parsed_titles();
		//Retriever retriever = new Retriever();

		//ArrayList<String> resultsList = retriever.findSearchTerms(searchField.getText(), logicalOperator, searchField2.getText());
		
		/*
		String[] bookTitles = new String[]{ "Title ", "Title 2", "Title 3","Title 4", "Title 5", 
				"Title 6", "Title 7", "Title 8","Title 9", "Title 10", 
				"Title 11", "Title 12", "Title 13","Title 14", "Title 15",
				"Title 16", "Title 17", "Title 18","Title 19", "Title 20",
				"Title 21", "Title 22", "Title 23","Title 24", "Title 25",
				"Title 26", "Title 27", "Title 28","Title 29", "Title 30",}; 
		*/
		//table variables
		frame = new JFrame("");
		panel = new JPanel(new GridLayout(2, 0));
		panel2 = new JPanel(new GridLayout(1, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		
	    final JTable table=new JTable();
		
	    //the model of the table
	    DefaultTableModel model=new DefaultTableModel()
	    {
			private static final long serialVersionUID = 1L;
		
			public Class<?> getColumnClass(int column)
			{
				switch(column)
				{
				case 0:
					return Boolean.class;
				case 1:
					return String.class;

				default:
					return String.class;
				}
			}
	    };

	    //assign the model to the table
	    table.setModel(model);
	    model.addColumn("Select");
	    model.addColumn("");

	    //the rows
	    for(int i=0; i<=bookTitles.length-1; i++)
	    {
	      model.addRow(new Object[0]);
	      model.setValueAt(false, i, 0);
	      model.setValueAt(bookTitles[i], i, 1);
	    }
	    
	    //submit button
	    JButton submitButton = new JButton("Submit Remote List");
	    submitButton.addActionListener(new ActionListener() {

	      @Override
	      public void actionPerformed(ActionEvent arg0) {
	    	  ArrayList<String> checkedBooks = new ArrayList<>();
	        //checked row
	        for(int i=0; i<table.getRowCount(); i++)
	        {
	          Boolean checked = Boolean.valueOf(table.getValueAt(i, 0).toString());
	          String col = table.getValueAt(i, 1).toString();

	          //display
	          if(checked)
	          {
	            //JOptionPane.showMessageDialog(null, col);
	        	  checkedBooks.add(col);
	          }
	        }
	        for (String title : checkedBooks) {
	        	specialized_ops so = new specialized_ops();
	        	Indexer ri = new Indexer();
	        	ri.insert(title, "remote author", "unzipped", so.mongo_retrieve_sentences(title), localMenuItem.isSelected() );
	        }

	      }
	    });
	    

		table.setFillsViewportHeight(true);
		 
		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoscrolls(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(screen.width - 100);

		panel.add(submitButton);
		panel2.add(scrollPane);    
		panel.setOpaque(true);

		frame = new JFrame("Book Titles");		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(500, 310);
        frame.setLocation( // Center window on screen.
                (screen.width - 500)/2, 
                (screen.height - 500)/2 );
  
		frame.add (panel, BorderLayout.PAGE_START);
		frame.add (panel2, BorderLayout.CENTER);

		frame.setVisible(true);  
		frame.validate ();

		//message dialog
		JOptionPane.showMessageDialog(frame,
				"Maximize to see the book titles in full screen or use the scroll bar",
				"Message",
				JOptionPane.PLAIN_MESSAGE);	
	} 
	
	
	//search method 
	public void search () 
	{
		Retriever retriever = new Retriever();
		//Retriever retriever = new Retriever();
		/*
		//message dialog
		Component frame0 = null;		
		JOptionPane.showMessageDialog(frame0,
		    "Please wait while searching....",
		    "Message",
		    JOptionPane.PLAIN_MESSAGE);
		*/
		ArrayList<String> resultsList = retriever.findSearchTerms(searchField.getText(), (String) searchOptions.getSelectedItem(), excludeField.getText(), selectField.getText(), authorField.getText(),  localMenuItem.isSelected());
		
		//table variables
		frame = new JFrame("");
		panel = new JPanel(new GridLayout(2, 0, 20, 20));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
		
		String column[] = {"Results for: " + searchField.getText()};
		DefaultTableModel defaultTableModel = new DefaultTableModel(column, 0);
		  
		defaultTableModel.setRowCount(0);
		for(String result : resultsList) {
			Object[] quote = {result};
			defaultTableModel.addRow(quote);
		}
		/*
		
		String[] searchOutPutArray = new String[10]; 
		
		//loop (searchOutPutArray.length-1) times to create elements of the table 
		for (int i = 0; i <= searchOutPutArray.length-1; i++) 
		{
			searchResultTable[i][0] = results[i];
		} 
 */
		//table title
		String[] tableTitle = {
				"Search result for: " + searchField.getText()};

		// initializing the GUI interface to display the search result in a table
//		table = new JTable(searchResultTable, tableTitle);
		table = new JTable(defaultTableModel);
		table.setFillsViewportHeight(true);
		 
		scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoscrolls(true);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		panel = new JPanel(new GridLayout(1,0));
		panel.add(scrollPane);    
		panel.setOpaque(true);

		frame = new JFrame("Search Result");		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		frame.pack();
		frame.setSize(500, 310);
        frame.setLocation( // Center window on screen.
                (screen.width - 500)/2, 
                (screen.height - 500)/2 );
  
		//frame.setSize (screen.width, screen.height-40);
		frame.setVisible(true);  
		
		//message dialog
		JOptionPane.showMessageDialog(frame,
		    "Maximize to see result in full screen\nMinimize to enter another search",
		    "Message",
		    JOptionPane.PLAIN_MESSAGE);
	}
	
	//main method
	public static void main (String [] args) 
	{
		GUI main = new GUI ();	
		
//specialized_ops so = new specialized_ops();
		/*
		titlesAvailable = so.mongo_retrieve_parsed_titles();
		for (String title : titlesAvailable) {
			System.out.println(title);
		}
		JTextField setTextField = new JTextField("irnewtextfield");
		main.selectBooks(setTextField);
		*/
		
		/*
		RemoteIndexer ri = new RemoteIndexer();
		String[] remoteSentences = so.mongo_retrieve_sentences("Warlord of Mars");
		for(String s : remoteSentences) {
			System.out.println(s + "<----- retrieved");
		}
		//String[] testSentences = {"sentence 1", "sentence 2", "sentence 3"};
		ri.insert("testTitle","testAuthor", "testunzipped", remoteSentences);
		*/
	} 

}