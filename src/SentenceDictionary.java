import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.applet.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.event.*;
import java.applet.*;
import java.net.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

final public class SentenceDictionary extends JDialog
{
	protected static String fileSeparator = System.getProperty("file.separator");
	protected static String lineSeparator = System.getProperty("line.separator");
	protected static boolean language = true;// i.e. default is arabic
	protected static boolean searchLanguage = true;// i.e. default is arabic
	protected JList sentenceList;
	protected Vector arabicListVector = new Vector();
	protected Vector arabicSoundNameVector = new Vector();
	protected Vector englishListVector = new Vector();
	protected Vector englishSoundNameVector = new Vector();
	protected Vector category = new Vector();
	protected Vector dictionaryCategory = new Vector();
	protected int sectionIndex []= new int [25];
	protected int ageIndex = 5;
	protected int pathChoice; // i.e. if = 0, default path (in the same directory) or = 1, manual selection of the location
	protected String imagePath;
	protected String soundPath;
	protected int searchChoice = 0;
	protected JPanel englishPanel;
	protected JEditorPane englishSentencesPane, arabicSentencesPane;
	protected JButton addButton, editButton, deleteButton, exportDBButton, importDBButton, englishSoundButton;
	protected AudioClip click;
	public SentenceDictionary(boolean lang, final MaknoonIslamicEncyclopedia MIE, boolean disableControlTools, int pathChoiceTemp, String imagePathTemp, String soundPathTemp)
	{
		super(MIE, true);
		
		pathChoice = pathChoiceTemp;
		imagePath = imagePathTemp;
		soundPath = soundPathTemp;
		
		if(lang) language = true;
		else language = false;
		
		int fixedCounter = 0;
		final String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(38, ".."+fileSeparator+"language"+fileSeparator+((language)?"SentenceDictionaryArabicFixed.txt":"SentenceDictionaryEnglishFixed.txt"));
		final String variable[] = MaknoonIslamicEncyclopedia.StreamConverter(11, ".."+fileSeparator+"language"+fileSeparator+((language)?"SentenceDictionaryArabicVariable.txt":"SentenceDictionaryEnglishVariable.txt"));
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle(fixed[fixedCounter++]);
		
		// Initilise the section array to cover all section by default
		for(int i=0; i<sectionIndex.length; i++)
			sectionIndex[i] = 1;
		
		try
		{
			final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
			final Statement stmt = con.createStatement();
			final ResultSet rs = stmt.executeQuery("select sectionName from sentenceDictionarySection");
			
			int index = 0;
			while(rs.next())
			{
				if(sectionIndex[index]==1)
					dictionaryCategory.addElement(rs.getString("sectionName"));
				
				index++;
			}
		}
		catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());}
		
		// This variable is used to label JOptionPane in arabic
		final Object[] OptionPaneYesLabel = {fixed[fixedCounter]};
		final Object[] OptionPaneYesNoLabel = {fixed[fixedCounter++], fixed[fixedCounter++]};
		
		// Search area
		final JTextField searchTextField = new JTextField(20);
		final JButton searchButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"sentenceSearch_m.png"));
		searchButton.setToolTipText(fixed[fixedCounter++]);
		
		final ActionListener searchListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String searchText = new String(searchTextField.getText());
				if(searchText.equals(""))
					JOptionPane.showOptionDialog(SentenceDictionary.this, variable[0], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
				else
					searchEngine(searchText);
			}
		};
		searchTextField.addActionListener(searchListener);
		searchButton.addActionListener(searchListener);
		
		final JRadioButton wholeWordsOnlyButton = new JRadioButton(fixed[fixedCounter++], true);
		final JRadioButton matchCaseButton = new JRadioButton(fixed[fixedCounter++]);
		final JRadioButton matchCaseSeparateButton = new JRadioButton(fixed[fixedCounter++]);
		final JRadioButton wholeWordsOnlySeparateButton = new JRadioButton(fixed[fixedCounter++]);
		
		final ActionListener searchGroupListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource() == wholeWordsOnlyButton){searchChoice=0;}
				if(ae.getSource() == matchCaseButton){searchChoice=1;}
				if(ae.getSource() == matchCaseSeparateButton){searchChoice=3;}
				if(ae.getSource() == wholeWordsOnlySeparateButton){searchChoice=2;}
			}
		};
		
		wholeWordsOnlyButton.addActionListener(searchGroupListener);
		matchCaseButton.addActionListener(searchGroupListener);
		matchCaseSeparateButton.addActionListener(searchGroupListener);
		wholeWordsOnlySeparateButton.addActionListener(searchGroupListener);
		
		final ButtonGroup searchGroup = new ButtonGroup();
		searchGroup.add(wholeWordsOnlyButton);
		searchGroup.add(matchCaseButton);
		searchGroup.add(matchCaseSeparateButton);
		searchGroup.add(wholeWordsOnlySeparateButton);
		
		final JPanel choiceSearchPanel = new JPanel(new GridLayout(2,2));
		choiceSearchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++],0,0,null,Color.red));
		
		choiceSearchPanel.add(wholeWordsOnlyButton);
		choiceSearchPanel.add(matchCaseButton);
		choiceSearchPanel.add(wholeWordsOnlySeparateButton);
		choiceSearchPanel.add(matchCaseSeparateButton);
		
		final JPanel settingPanel = new JPanel(new GridLayout(1,2));
		settingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++],0,0,null,Color.red));
		
		final JRadioButton englishButton = new JRadioButton(fixed[fixedCounter++]);
		final JRadioButton arabicButton = new JRadioButton(fixed[fixedCounter++]);
		
		if(language)
		{
			arabicButton.setSelected(true);
			searchLanguage = true;
		}
		else
		{
			englishButton.setSelected(true);
			searchLanguage = false;
		}
		
		final ActionListener languageGroupListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource() == englishButton)
				{
					searchLanguage = false;
					searchTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					//searchTextField.selectAll();
					searchTextField.setText("");
				}
				
				if(ae.getSource() == arabicButton)
				{
					searchLanguage = true;
					searchTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					searchTextField.setText("");
				}
			}
		};
		englishButton.addActionListener(languageGroupListener);
		arabicButton.addActionListener(languageGroupListener);
		
		final ButtonGroup languageGroup = new ButtonGroup();
		languageGroup.add(englishButton);
		languageGroup.add(arabicButton);
		
		settingPanel.add(englishButton);
		settingPanel.add(arabicButton);
		
		JPanel searchTextFieldPanel = new JPanel(new BorderLayout());
		searchTextFieldPanel.add(searchTextField, BorderLayout.CENTER);
		
		if(language) searchTextFieldPanel.add(searchButton, BorderLayout.WEST);
		else searchTextFieldPanel.add(searchButton, BorderLayout.EAST);
		
		final JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
		searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),fixed[fixedCounter++],0,0,null,Color.red));
		
		searchPanel.add(searchTextFieldPanel);
		searchPanel.add(choiceSearchPanel);
		searchPanel.add(settingPanel);
		
		final JPanel sentencePanel = new JPanel(new BorderLayout());
		sentencePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], TitledBorder.CENTER, 0, null, Color.red));
		
		sentenceList = new JList();
		sentenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sentencePanel.add(new JScrollPane(sentenceList), BorderLayout.CENTER);
		
		final ListSelectionModel listSelectionModel = sentenceList.getSelectionModel();
		listSelectionModel.addListSelectionListener(new ListSelectionHandler());
		
		final JPanel searchAndControlPanel = new JPanel(new BorderLayout());
		searchAndControlPanel.add(searchPanel, BorderLayout.CENTER);
		
		final JPanel arabicPanel = new JPanel(new BorderLayout());
		arabicPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		arabicPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		arabicSentencesPane = new JEditorPane();
		arabicSentencesPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		arabicSentencesPane.setEditable(false);
		arabicSentencesPane.setContentType("text/html");
		
		final JButton arabicSoundButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"a_audioCataloger_m.png"));
		arabicSoundButton.setToolTipText(fixed[fixedCounter++]);
		arabicSoundButton.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		arabicSoundButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(selectionIndex!=-1)
				{
					if(!(arabicSoundNameVector.elementAt(selectionIndex).equals("null")))
					{
						try
						{
							final URL clickURL = new URL(((pathChoice==0)?("file:" + System.getProperty("user.dir")+ fileSeparator + ".."+fileSeparator+"soundDictionary"):("file:"+soundPath))+fileSeparator+(String)arabicSoundNameVector.elementAt(selectionIndex));
							click = Applet.newAudioClip(clickURL);
							click.play();
						}
						catch (MalformedURLException mue){}
					}
					else
						JOptionPane.showOptionDialog(SentenceDictionary.this, variable[7], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
				}
				else
					JOptionPane.showOptionDialog(SentenceDictionary.this, variable[6], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
			}
		});
		
		arabicPanel.add(new JScrollPane(arabicSentencesPane), BorderLayout.CENTER);
		englishPanel = new JPanel(new BorderLayout());
		englishPanel.setPreferredSize(new Dimension(0, 230));
		englishPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		englishSentencesPane = new JEditorPane();
		englishSentencesPane.setEditable(false);
		englishSentencesPane.setContentType("text/html");
		
		englishSoundButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"audioCataloger_m.png"));
		englishSoundButton.setToolTipText(fixed[fixedCounter++]);
		englishSoundButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(selectionIndex!=-1)
				{
					if(!(englishSoundNameVector.elementAt(selectionIndex).equals("null")))
					{
						try
						{
							final URL clickURL = new URL(((pathChoice==0)?("file:" + System.getProperty("user.dir")+ fileSeparator + ".."+fileSeparator+"soundDictionary"):("file:"+soundPath))+fileSeparator+(String)englishSoundNameVector.elementAt(selectionIndex));
							click = Applet.newAudioClip(clickURL);
							click.play();
						}
						catch (MalformedURLException mue){}
					}
					else
						JOptionPane.showOptionDialog(SentenceDictionary.this, variable[7], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
				}
				else
					JOptionPane.showOptionDialog(SentenceDictionary.this, variable[6], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
			}
		});
		englishPanel.add(new JScrollPane(englishSentencesPane), BorderLayout.CENTER);
		
		final JPanel controlPanel = new JPanel(new GridLayout(5,2));
		controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		searchAndControlPanel.add(controlPanel, BorderLayout.EAST);
		
		addButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"add_m.png"));
		addButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(addButton);
		addButton.setEnabled(false);
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new AddToSentenceDictionary(language, SentenceDictionary.this);
			}
		});
		
		editButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"update_m.png"));
		editButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(editButton);
		editButton.setEnabled(false);
		editButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(selectionIndex!=-1)
				{
					new EditSentenceDictionary(language, SentenceDictionary.this,
						(String)arabicListVector.elementAt(selectionIndex), (String)englishListVector.elementAt(selectionIndex),
						(String)arabicSoundNameVector.elementAt(selectionIndex), (String)englishSoundNameVector.elementAt(selectionIndex),
						(String)categoryTemp.elementAt(selectionIndex));
				}
				else
					JOptionPane.showOptionDialog(SentenceDictionary.this, variable[2], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
			}
		});
		
		deleteButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"delete_m.png"));
		deleteButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(deleteButton);
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(selectionIndex!=-1)
				{
					int choice = JOptionPane.showOptionDialog(SentenceDictionary.this, variable[8], variable[4], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
					if(choice == JOptionPane.YES_OPTION)
					{
						try
						{
							final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
							final Statement stmt = con.createStatement();
							stmt.executeUpdate("delete from arabicSentenceDictionary WHERE arabicSentence LIKE '"+(String)arabicListVector.elementAt(selectionIndex)+"'");
							stmt.close();
							con.close();
							
							if(!(arabicSoundNameVector.elementAt(selectionIndex).equals("null")))
							{
								choice = JOptionPane.showOptionDialog(MIE, variable[9], variable[4], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
								if(choice == JOptionPane.YES_OPTION)
									new DeleteFile(((pathChoice==0)?(".."+fileSeparator+"soundDictionary"):soundPath)+fileSeparator+(String)arabicSoundNameVector.elementAt(selectionIndex));
							}
							
							if(!(englishSoundNameVector.elementAt(selectionIndex).equals("null")))
							{
								choice = JOptionPane.showOptionDialog(MIE, variable[10], variable[4], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
								if(choice == JOptionPane.YES_OPTION)
									new DeleteFile(((pathChoice==0)?(".."+fileSeparator+"soundDictionary"):soundPath)+fileSeparator+(String)englishSoundNameVector.elementAt(selectionIndex));
							}
							storeDatabaseInMemory();
						}
						catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());}
					}
				}
				else
					JOptionPane.showOptionDialog(SentenceDictionary.this, variable[5], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
			}
		});
		
		exportDBButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"save_m.png"));
		exportDBButton.setToolTipText(fixed[fixedCounter++]);
		exportDBButton.setEnabled(false);
		controlPanel.add(exportDBButton);
		exportDBButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
        		final JFileChooser fc = new JFileChooser();
        		javax.swing.filechooser.FileFilter filter = new ExtensionFileFilter(new String[] {"asd", "ASD"}, "ASD files");
				fc.addChoosableFileFilter(filter);
				fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
				
				final int returnVal = fc.showSaveDialog(SentenceDictionary.this);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                    final File file = fc.getSelectedFile();
                    final String fileName = file.getName();
                    final String PathName = String.valueOf(file);
                    
                    String extensionName="";
					final StringTokenizer tokens = new StringTokenizer(fileName, ".");
					if(tokens.countTokens()>1)
						while(tokens.hasMoreTokens())
							extensionName=tokens.nextToken();
					
                    try
					{
						Class.forName("com.ihost.cs.jdbc.CloudscapeDriver").newInstance();
						Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
						Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
						ResultSet rs = stmt.executeQuery("select * from arabicSentenceDictionary");
						
						Writer out;
						if(extensionName.equalsIgnoreCase("asd"))
							out = new OutputStreamWriter(new FileOutputStream(PathName),"UTF8");
						else
							out = new OutputStreamWriter(new FileOutputStream(PathName+".asd"),"UTF8");
						
						while(rs.next())
						{
							out.write(rs.getString("arabicSentence")+"\u00F6");
							out.write(rs.getString("englishSentence")+"\u00F6");
							out.write(rs.getString("arabicSoundName")+"\u00F6");
							out.write(rs.getString("englishSoundName")+"\u00F6");
							out.write(rs.getString("category"));
							if(!rs.isLast())out.write(lineSeparator);
						}
						
						stmt.close();
						con.close();
						out.close();
					}
					catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());}
					catch(IOException ae){ae.printStackTrace();}
					catch(Throwable ex)
					{
						System.out.println("exception thrown:");
						if(ex instanceof SQLException)
							printSQLError((SQLException)ex);
						else
							ex.printStackTrace();
					}
                }
                else
                    System.out.println("Save command cancelled by user.");
			}
		});
		
		importDBButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"load_m.png"));
		importDBButton.setToolTipText(fixed[fixedCounter++]);
		importDBButton.setEnabled(false);
		controlPanel.add(importDBButton);
		importDBButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JFileChooser fc = new JFileChooser();
				javax.swing.filechooser.FileFilter filter = new ExtensionFileFilter(new String[] {"asd", "ASD"}, "ASD files");
				fc.addChoosableFileFilter(filter);
				fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
				
				final int returnVal = fc.showOpenDialog(SentenceDictionary.this);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                	final File file = fc.getSelectedFile();
                	final String PathName = String.valueOf(file);
                	StringBuffer buffer = new StringBuffer();
                	String word = new String();
                	boolean gotSQLExc = false;
                	try
					{
						Class.forName("com.ihost.cs.jdbc.CloudscapeDriver").newInstance();
						Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
						Statement stmt = con.createStatement();
						stmt.execute("drop table arabicSentenceDictionary");
						stmt.close();
						con.close();
						
						con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase;create=true","root","secret");
						stmt = con.createStatement();
						stmt.execute("create table arabicSentenceDictionary(arabicSentence LONG VARCHAR,englishSentence LONG VARCHAR,arabicSoundName VARCHAR(50),englishSoundName VARCHAR(50),category VARCHAR(100))");
						
						final Reader in = new BufferedReader(new InputStreamReader(new FileInputStream(PathName), "UTF8"));
						int ch;
						int counter = 0;
						boolean emptyFile = true;
						StringTokenizer tokens;
						while((ch = in.read()) > -1)
						{
							emptyFile = false;
							if('\n'==(char)ch)
							{
								word = buffer.toString();
								
								if(counter == 0)
									tokens = new StringTokenizer((word.substring(1,word.length()-1)), "\u00F6");
								else
									tokens = new StringTokenizer((word.substring(0,word.length()-1)), "\u00F6");
								
								stmt.execute("insert into arabicSentenceDictionary values ('"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"')");
								buffer = new StringBuffer();
								counter++;
							}
							else
								buffer.append((char)ch);
						}
						
						// For the last line and when the file has some text
						if(!emptyFile)
						{
							if(counter == 0)
								tokens = new StringTokenizer((buffer.toString().substring(1,buffer.toString().length())), "\u00F6");
							else
								tokens = new StringTokenizer(buffer.toString(), "\u00F6");
							
							stmt.execute("insert into arabicSentenceDictionary values ('"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"')");
						}
						
						in.close();
						DriverManager.getConnection("jdbc:cloudscape:;shutdown=true");
					}
					catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());gotSQLExc = true;}
					catch(FileNotFoundException fnfe){fnfe.printStackTrace();}
					catch(IOException ae){ae.printStackTrace();}
					catch(Throwable ex)
					{
						System.out.println("exception thrown:");
						if(ex instanceof SQLException)
							printSQLError((SQLException)ex);
						else
							ex.printStackTrace();
					}
					
					if (!gotSQLExc) System.out.println("Database did not shut down normally");
					else
					{
						System.out.println("Database shut down normally");
						arabicSentencesPane.setText("");
						englishSentencesPane.setText("");
						
						sentenceList.setModel(new DefaultListModel());
						storeDatabaseInMemory();
					}
                }
                else
                	System.out.println("Open command cancelled by user.");                
			}
		});
		
		final JButton sectionButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"section_m.png"));
		sectionButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(sectionButton);
		sectionButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new SentenceDictionarySection(SentenceDictionary.this);}});
		
		controlPanel.add(arabicSoundButton);
		controlPanel.add(englishSoundButton);
		
		final JButton printButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"printer_m.png"));
		printButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(printButton);
		printButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JFileChooser fc = new JFileChooser();
        		javax.swing.filechooser.FileFilter filter = new ExtensionFileFilter(new String[] {"html", "HTML"}, "HTML files");
				fc.addChoosableFileFilter(filter);
				fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
				
				final int returnVal = fc.showOpenDialog(SentenceDictionary.this);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                    final File file = fc.getSelectedFile();
                    final String fileName = file.getName();
                    final String PathName = String.valueOf(file);
                    
                    String extensionName="";
					StringTokenizer tokens = new StringTokenizer(fileName, ".");
					if(tokens.countTokens()>1)
						while(tokens.hasMoreTokens())
							extensionName=tokens.nextToken();
					
					try
					{
						OutputStreamWriter out = null;
						if(extensionName.equalsIgnoreCase("html"))
							out = new OutputStreamWriter(new FileOutputStream(PathName),"Cp1256");
						else
							out = new OutputStreamWriter(new FileOutputStream(PathName+".html"),"Cp1256");
						
						out.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1256\"><title>Arabian Dictionary</title></head><body><table width=\"595.28\" style=\"border-collapse: collapse; border-style: dotted; border-width: 1px\" cellpadding=\"0\">");
						out.write("<tr><td valign=\"middle\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; \" height=\"23\" colspan=\"3\" bgcolor=\"#F4F4F4\"><p align=\"center\" dir=\"rtl\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#0000FF\">\u0627\u0644\u0642\u0627\u0645\u0648\u0633 \u0627\u0644\u0639\u0631\u0628\u064A \u0627\u0644\u0645\u0635\u0648\u0631 (\u0639\u0631\u0628\u064A - \u0639\u0631\u0628\u064A\u060C \u0639\u0631\u0628\u064A - \u0625\u0646\u062C\u0644\u064A\u0632\u064A\u060C \u0625\u0646\u062C\u0644\u064A\u0632\u064A - \u0639\u0631\u0628\u064A\u060C \u0625\u0646\u062C\u0644\u064A\u0632\u064A - \u0625\u0646\u062C\u0644\u064A\u0632\u064A)</font></td></tr><tr><td valign=\"center\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\" height=\"22\" colspan=\"3\" bgcolor=\"#F4F4F4\"><p align=\"center\" dir=\"ltr\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#0000FF\"><span lang=\"en-us\">Arabic Dictionary (Arabic-Arabic, English-Arabic, Arabic-English, English-English)</span></font></td></tr>");
						out.write("<tr><td valign=\"center\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><font face=\"Tahoma\" style=\"font-size: 9pt\"color=\"#FF0000\">Sections </font></td><td valign=\"center\" height=\"19\" width=\"501\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><p align=\"center\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#808000\">");
						
						for(int i=0; i<dictionaryCategory.size()-1; i++)
							out.write((String)dictionaryCategory.elementAt(i)+", ");
						
						out.write((String)dictionaryCategory.lastElement());
						out.write("</font></td><td valign=\"center\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><p align=\"right\"><font face=\"Tahoma\" style=\"font-size: 9pt\"><font color=\"#FF0000\">\u0627\u0644\u0623\u0642\u0633\u0627\u0645</font> </font> </td></tr>");
						out.write("<tr><td valign=\"top\" width=\"100%\" colspan=\"3\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td valign=\"top\" height=\"19\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#FF0000\">Age Range</font></td><td><p align=\"center\"><font color=\"#808000\" face=\"Tahoma\" style=\"font-size: 9pt\">\u0623\u0642\u0644 \u0645\u0646 "+ageIndex+" \u0633\u0646\u0629</font></td><td><p align=\"right\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#FF0000\">\u0627\u0644\u0641\u0626\u0629 \u0627\u0644\u0639\u0645\u0631\u064A\u0629</font></td></tr></table></td></tr>");
						out.write("<tr><td valign=\"top\" width=\"100%\" colspan=\"3\">");
						
						for(int i=0; i<arabicListTemp.size(); i++)
							out.write("<table width=\"595.28\" cellspacing=\"0\" cellpadding=\"0\" style=\"border-bottom-style: dotted; border-bottom-width: 1px\"><tr><td width=\"50%\" valign=\"top\" style=\"border-left-width: 1px; border-right-style: dotted; border-right-width: 1px; border-top-width: 1px; border-bottom-width: 1px\"><div align=\"left\"><font face=\"Verdana\" style=\"font-size: 9pt\">"+englishListTemp.elementAt(i)+"</div></td><td width=\"50%\" valign=\"top\"><div align=\"right\"><font face=\"Traditional Arabic\" style=\"font-size: 12pt\">"+arabicListTemp.elementAt(i)+"</div></td></tr></table>");
						
						out.write("</td></tr><tr><td valign=\"top\" width=\"100%\" colspan=\"3\" height=\"14\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"111\"><font color=\"#808000\" face=\"Tahoma\" style=\"font-size: 9pt\">www.maknoon.com</font></td><td width=\"464\"><p align=\"center\"><font color=\"#808000\" face=\"Tahoma\" style=\"font-size: 9pt\">Maknoon Islamic Encyclopedia \u0645\u0648\u0633\u0648\u0639\u0629 \u0645\u0643\u0646\u0648\u0646 \u0627\u0644\u0625\u0633\u0644\u0627\u0645\u064A\u0629</span></font></td><td width=\"1\"><font color=\"#808000\" face=\"Tahoma\" style=\"font-size: 9pt\">"+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"/"+Calendar.getInstance().get(Calendar.YEAR)+"</font></td></tr></table></td></tr></table></body></html>");
						out.close();
						
						if(extensionName.equalsIgnoreCase("html"))
							new BrowserControl(PathName);
						else
							new BrowserControl(PathName+".html");
					}
					catch(IOException ioe){ioe.printStackTrace();}
					catch(Exception ae){ae.printStackTrace();}
                }
                else
                    System.out.println("print command cancelled by user.");
			}
		});
		
		final JButton displayButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"screenshot_m.png"));
		displayButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(displayButton);
		displayButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final DefaultListModel listModel = new DefaultListModel();
				
				arabicListVector.removeAllElements();
				englishListVector.removeAllElements();
				arabicSoundNameVector.removeAllElements();
				englishSoundNameVector.removeAllElements();
				category.removeAllElements();
				
				if(searchLanguage)
					for(int i=0; i<arabicListTemp.size(); i++)
						listModel.addElement("<HTML>"+arabicListTemp.elementAt(i));
				else
					for(int i=0; i<englishListTemp.size(); i++)
						listModel.addElement("<HTML>"+englishListTemp.elementAt(i));
				
				arabicListVector.addAll(arabicListTemp);
				englishListVector.addAll(englishListTemp);
				arabicSoundNameVector.addAll(arabicSoundNameTemp);
				englishSoundNameVector.addAll(englishSoundNameTemp);
				category.addAll(categoryTemp);
				
				if(searchLanguage)
					sentenceList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				else
					sentenceList.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				
				arabicSentencesPane.setText("");
				englishSentencesPane.setText("");
				sentenceList.setModel(listModel);
			}
		});
		
		final JPanel discriptionPanel = new JPanel(new GridLayout(1,2));
		discriptionPanel.add(arabicPanel);
		discriptionPanel.add(englishPanel);
		
		add(searchAndControlPanel, BorderLayout.NORTH);
		add(discriptionPanel, BorderLayout.CENTER);
		add(sentencePanel, BorderLayout.SOUTH);
		
		// Initilize and store the database in arrays to be fast
		storeDatabaseInMemory();
		getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		setForEnglishOrientation();
		
		if(!disableControlTools)
		{
			addButton.setEnabled(true);
			editButton.setEnabled(true);
			deleteButton.setEnabled(true);
			exportDBButton.setEnabled(true);
			importDBButton.setEnabled(true);
		}
		
		// To locat the JDialog at the center of the screen
		pack();
		MaknoonIslamicEncyclopedia.centerInScreen(this);
		setVisible(true);
	}
	
	public void setForEnglishOrientation()
	{
		englishPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		englishSentencesPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		englishSoundButton.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	}
	
	public void printSQLError(SQLException e)
	{
		while (e != null) 
		{
			System.out.println(e.toString());
			e = e.getNextException();
		}
	}
	
	/*
	 * Searching the inserted words with 4 options
	 *
	 * Very important point, that is searching in the english description will cause some problems since 
	 * We will use HTML tags within the text.
	 */
	public void searchEngine(String Search)
	{
		final DefaultListModel listModel = new DefaultListModel();
		
		arabicListVector.removeAllElements();
		englishListVector.removeAllElements();
		arabicSoundNameVector.removeAllElements();
		englishSoundNameVector.removeAllElements();
		category.removeAllElements();
		
		Vector materialToSearch = new Vector(1000);
		if(searchLanguage)
			materialToSearch = arabicListTemp;
		else
			materialToSearch = englishListTemp;
		
		StringTokenizer tokens;
		boolean found = false;
		if(searchChoice==0)
		{
			for(int i=0; i<materialToSearch.size(); i++)
			{
				found=false;
				
				// We use here HTMLFreeText function to clear the text from HTML tags
				tokens = new StringTokenizer(HTMLFreeText((String)materialToSearch.elementAt(i)), " ?,:'\".()[]<>-;0123456789\u060C\u061F\u061B}{!");
				while(tokens.hasMoreTokens())
				{
					if(Search.equals(tokens.nextToken()))
					{
						found=true;
						break;
					}
				}
				
				if(found)
				{
					listModel.addElement("<HTML>"+materialToSearch.elementAt(i));
					arabicListVector.addElement(arabicListTemp.elementAt(i));
					englishListVector.addElement(englishListTemp.elementAt(i));
					arabicSoundNameVector.addElement(arabicSoundNameTemp.elementAt(i));
					englishSoundNameVector.addElement(englishSoundNameTemp.elementAt(i));
					category.addElement(categoryTemp.elementAt(i));
				}
			}
		}
		
		if(searchChoice==1)
		{
			for (int i=0; i<materialToSearch.size(); i++)
			{
				// We use here HTMLFreeText function to clear the text from HTML tags
				if((HTMLFreeText((String)materialToSearch.elementAt(i))).indexOf(Search)>-1)
				{
					listModel.addElement("<HTML>"+materialToSearch.elementAt(i));
					arabicListVector.addElement(arabicListTemp.elementAt(i));
					englishListVector.addElement(englishListTemp.elementAt(i));
					arabicSoundNameVector.addElement(arabicSoundNameTemp.elementAt(i));
					englishSoundNameVector.addElement(englishSoundNameTemp.elementAt(i));
					category.addElement(categoryTemp.elementAt(i));
				}
			}
		}
	
		if(searchChoice==2)
		{
			final StringTokenizer searchTokens = new StringTokenizer(Search, " ?,:'");
			final String [] searchWords = new String[searchTokens.countTokens()];
			int searchWordsIndex = 0;
			
			while(searchTokens.hasMoreTokens())
				searchWords[searchWordsIndex++] = searchTokens.nextToken();
			
			for (int i=0; i<materialToSearch.size(); i++)
			{
				// We use here HTMLFreeText function to clear the text from HTML tags
				tokens = new StringTokenizer(HTMLFreeText((String)materialToSearch.elementAt(i)), " ?,:'\".()[]<>-;0123456789\u060C\u061F\u061B}{!");
				
				final String [] lineWords = new String[tokens.countTokens()];
				int lineWordsIndex = 0;
				
				while(tokens.hasMoreTokens())
					lineWords[lineWordsIndex++] = tokens.nextToken();
				
				for(int w=0; w<searchWords.length; w++)
				{
					found=false;
					for(int s=0; s<lineWords.length; s++)
					{
						if(searchWords[w].equals(lineWords[s]))
						{
							found=true;
							break;
						}
					}
					
					if(!found)break;
				}
				
				if(found)
				{
					listModel.addElement("<HTML>"+materialToSearch.elementAt(i));
					arabicListVector.addElement(arabicListTemp.elementAt(i));
					englishListVector.addElement(englishListTemp.elementAt(i));
					arabicSoundNameVector.addElement(arabicSoundNameTemp.elementAt(i));
					englishSoundNameVector.addElement(englishSoundNameTemp.elementAt(i));
					category.addElement(categoryTemp.elementAt(i));
				}
			}
		}
	
		if(searchChoice==3)
		{
			final StringTokenizer searchTokens = new StringTokenizer(Search, " ?,:'");
			final String [] searchWords = new String[searchTokens.countTokens()];
			int searchWordsIndex = 0;
			
			while(searchTokens.hasMoreTokens())
				searchWords[searchWordsIndex++] = searchTokens.nextToken();
			
			for (int i=0; i<materialToSearch.size(); i++)
			{
				for(int w=0; w<searchWords.length; w++)
				{
					found=false;
					
					// We use here HTMLFreeText function to clear the text from HTML tags
					found=(HTMLFreeText((String)materialToSearch.elementAt(i))).indexOf(searchWords[w])>-1;
					if(!found)break;
				}
			
				if(found)
				{
					listModel.addElement("<HTML>"+materialToSearch.elementAt(i));
					arabicListVector.addElement(arabicListTemp.elementAt(i));
					englishListVector.addElement(englishListTemp.elementAt(i));
					arabicSoundNameVector.addElement(arabicSoundNameTemp.elementAt(i));
					englishSoundNameVector.addElement(englishSoundNameTemp.elementAt(i));
					category.addElement(categoryTemp.elementAt(i));
				}
			}
		}
		
		if(searchLanguage)
			sentenceList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		else
			sentenceList.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		arabicSentencesPane.setText("");
		englishSentencesPane.setText("");
		sentenceList.setModel(listModel);
	}
	
	protected Vector arabicListTemp = new Vector(1000);
	protected Vector englishListTemp = new Vector(1000);
	protected Vector arabicSoundNameTemp = new Vector(1000);
	protected Vector englishSoundNameTemp = new Vector(1000);
	protected Vector categoryTemp = new Vector(1000);
	
	public void storeDatabaseInMemory()
	{
		arabicListTemp.removeAllElements();
		englishListTemp.removeAllElements();
		arabicSoundNameTemp.removeAllElements();
		englishSoundNameTemp.removeAllElements();
		categoryTemp.removeAllElements();
		
		try
		{
			Class.forName("com.ihost.cs.jdbc.CloudscapeDriver").newInstance();
			final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
			final Statement stmt = con.createStatement();
			
			// This do it only once at the begingin since it is fixed already unless you add or update some items
			final ResultSet rs = stmt.executeQuery("select * from arabicSentenceDictionary");
			
			StringTokenizer tokens;
			String cat = "";
			String age = "";
			boolean cont = false;
			
			while(rs.next())
			{
				tokens = new StringTokenizer(new String(rs.getString("category")), ":");
				cat = tokens.nextToken();
				age = tokens.nextToken();
				
				if(Integer.valueOf(age)<=ageIndex)
				{
					tokens = new StringTokenizer(cat, ".");
					cont = false;
					String catog = "";
					while(tokens.hasMoreTokens())
					{
						if(dictionaryCategory.indexOf((Object)tokens.nextToken()) > -1)
						{
							cont = true;
							break;
						}
					}
					
					if(cont)
					{
						arabicListTemp.addElement(new String(rs.getString("arabicSentence")));
						englishListTemp.addElement(new String(rs.getString("englishSentence")));
						arabicSoundNameTemp.addElement(new String(rs.getString("arabicSoundName")));
						englishSoundNameTemp.addElement(new String(rs.getString("englishSoundName")));
						categoryTemp.addElement(new String(rs.getString("category")));
					}
				}
			}
			
			stmt.close();
			con.close();
		}
		catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());}
		catch(Throwable ex)
		{
			System.out.println("exception thrown:");
			if(ex instanceof SQLException)
				printSQLError((SQLException)ex);
			else
				ex.printStackTrace();
		}
		
		sentenceList.setModel(new DefaultListModel());
		arabicSentencesPane.setText("");
		englishSentencesPane.setText("");
	}
	
	public static String HTMLFreeText(String HTMLText)
	{
		String HTMLTextUpdate = "";
		boolean contStore = true;
		for(int i=0; i<HTMLText.length(); i++)
		{
			if(HTMLText.charAt(i) == '<')
				contStore = false;
			else
			{
				if(contStore)
					HTMLTextUpdate = HTMLTextUpdate + HTMLText.charAt(i);
				
				if(HTMLText.charAt(i) == '>')
					contStore = true;
			}
		}
		
		return HTMLTextUpdate;
	}
	
	protected int selectionIndex = -1;
	final class ListSelectionHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			final ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			selectionIndex = lsm.getMaxSelectionIndex();
			
			if(selectionIndex!=-1)
			{
				arabicSentencesPane.setText((String)arabicListVector.elementAt(selectionIndex));
				englishSentencesPane.setText((String)englishListVector.elementAt(selectionIndex));
			}
		}
	}
}