import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.applet.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;
import javax.swing.border.TitledBorder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

final public class ArabicDictionary extends JPanel
{
	protected static String fileSeparator = System.getProperty("file.separator");
	protected static String lineSeparator = System.getProperty("line.separator");
	protected static boolean language = true;// i.e. default is arabic
	protected static boolean searchLanguage = true;// i.e. default is arabic
	protected static boolean searchByTitle = true;
	protected JList wordList;
	protected Vector arabicWordListVector = new Vector();
	protected Vector arabicDescriptionVector = new Vector();
	protected Vector arabicSoundNameVector = new Vector();
	protected Vector englishWordListVector = new Vector();
	protected Vector englishDescriptionVector = new Vector();
	protected Vector englishSoundNameVector = new Vector();
	protected Vector imageName = new Vector();
	protected Vector category = new Vector();
	protected Vector dictionaryCategory = new Vector();
	protected int sectionIndex []= new int [25];
	protected int ageIndex = 5;

	protected MaknoonIslamicEncyclopedia MIE;
	protected int searchChoice = 0;
	protected JPanel englishDetailsPanel;
	protected JPanel imagePanel;
	protected int pathChoice; // i.e. if = 0, default path (in the same directory) or = 1, manual selection of the location
	protected String imagePath;
	protected String soundPath;
	protected JEditorPane englishDetailsPane, arabicDetailsPane;
	protected JTextField arabicWordTextField, englishWordTextField;
	protected JButton addButton, editButton, deleteButton, exportDBButton, importDBButton, authenticationmButton;
	protected AudioClip click;
	protected boolean choosedAutoDetectorLanguage = true; //i.e. arabic
	protected boolean onlineAutoDetector = false;
	protected JTextField autoWordDetectorTextField;
	public ArabicDictionary(final boolean lang, MaknoonIslamicEncyclopedia ME)
	{
		super(new BorderLayout());

		// used to now the owner of JDialogs
		MIE = ME;

		if(lang) language = true;
		else language = false;

		// By default
		orderLanguage = language;
		choosedAutoDetectorLanguage = language;

		int fixedCounter = 0;
		final String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(47, ".."+fileSeparator+"language"+fileSeparator+((language)?"ArabicDictionaryArabicFixed.txt":"ArabicDictionaryEnglishFixed.txt"));
		final String variable[] = MaknoonIslamicEncyclopedia.StreamConverter(15, ".."+fileSeparator+"language"+fileSeparator+((language)?"ArabicDictionaryArabicVariable.txt":"ArabicDictionaryEnglishVariable.txt"));

		try
		{
			final BufferedReader in = new BufferedReader(new InputStreamReader (new FileInputStream(".."+fileSeparator+"setting"+fileSeparator+"arabicDictionaryPath.txt")));
			imagePath = in.readLine();
			if(imagePath.equals("null")) imagePath = System.getProperty("user.home")+fileSeparator+"Maknoon Arabic Dictionary image files";

			soundPath = in.readLine();
			if(soundPath.equals("null")) soundPath = System.getProperty("user.home")+fileSeparator+"Maknoon Arabic Dictionary sound files";

			pathChoice = Integer.parseInt(in.readLine());
			in.close();
		}
		catch(IOException ae){ae.printStackTrace();}

		// Initilise the section array to cover all section by default
		for(int i=0; i<sectionIndex.length; i++)
			sectionIndex[i] = 1;

		try
		{
			Class.forName("com.ihost.cs.jdbc.CloudscapeDriver").newInstance();
			final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
			final Statement stmt = con.createStatement();
			final ResultSet rs = stmt.executeQuery("select sectionName from dictionarySection");

			int index = 0;
			while(rs.next())
			{
				if(sectionIndex[index]==1)
					dictionaryCategory.addElement(rs.getString("sectionName"));

				index++;
			}
		}
		catch(Throwable ex)
		{
			System.out.println("exception thrown:");
			if(ex instanceof SQLException)
				printSQLError((SQLException)ex);
			else
				ex.printStackTrace();

			// To see the Warning frame above the splash screen, this is only when starting the program
			if(!MIE.windows)MIE.splash.toBack();
			MIE.splash.setAlwaysOnTop(false);

			final Object[] options = {variable[11]};
			JOptionPane.showOptionDialog(ArabicDictionary.this, variable[13], variable[14], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			System.exit(0);

		}

		// This variable is used to label JOptionPane in arabic
		final Object[] OptionPaneYesLabel = {fixed[fixedCounter]};
		final Object[] OptionPaneYesNoLabel = {fixed[fixedCounter++], fixed[fixedCounter++]};

		// Search area
		final JTextField searchTextField = new JTextField();
		final JButton searchButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"search_m.png"));
		searchButton.setToolTipText(fixed[fixedCounter++]);

		final ActionListener searchListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String searchText = new String(searchTextField.getText().trim());
				if(searchText.equals(""))
					JOptionPane.showOptionDialog(MIE, variable[0], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
				else
					searchEngine(searchText);
			}
		};

		searchTextField.addActionListener(searchListener);
		searchButton.addActionListener(searchListener);

		final JRadioButtonMenuItem wholeWordsOnlyButton = new JRadioButtonMenuItem(fixed[fixedCounter++],true);
		final JRadioButtonMenuItem matchCaseButton = new JRadioButtonMenuItem(fixed[fixedCounter++]);
		final JRadioButtonMenuItem matchCaseSeparateButton = new JRadioButtonMenuItem(fixed[fixedCounter++]);
		final JRadioButtonMenuItem wholeWordsOnlySeparateButton = new JRadioButtonMenuItem(fixed[fixedCounter++]);
		final ActionListener searchGroupListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if (ae.getSource() == wholeWordsOnlyButton){searchChoice=0;}
				if (ae.getSource() == matchCaseButton){searchChoice=1;}
				if (ae.getSource() == matchCaseSeparateButton){searchChoice=3;}
				if (ae.getSource() == wholeWordsOnlySeparateButton){searchChoice=2;}
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

		final JMenuBar searchMenuBar = new JMenuBar();
		final JMenu searchMenu = new JMenu(fixed[fixedCounter++]);
		searchMenuBar.add(searchMenu);

		searchMenu.add(wholeWordsOnlyButton);
		searchMenu.add(matchCaseButton);
		searchMenu.add(wholeWordsOnlySeparateButton);
		searchMenu.add(matchCaseSeparateButton);

		final JMenu settingMenu = new JMenu(fixed[fixedCounter++]);
		searchMenuBar.add(settingMenu);

		final JRadioButtonMenuItem englishButton = new JRadioButtonMenuItem(fixed[fixedCounter++]);
		final JRadioButtonMenuItem arabicButton = new JRadioButtonMenuItem(fixed[fixedCounter++]);

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
				if (ae.getSource() == englishButton)
				{
					searchLanguage = false;
					searchTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					//searchTextField.selectAll();
					searchTextField.setText("");
				}

				if (ae.getSource() == arabicButton)
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

		final JMenu languageMenu = new JMenu(fixed[fixedCounter++]);
		settingMenu.add(languageMenu);

		languageMenu.add(englishButton);
		languageMenu.add(arabicButton);

		final JRadioButtonMenuItem searchByTitleButton = new JRadioButtonMenuItem(fixed[fixedCounter++], true);
		final JRadioButtonMenuItem searchByContentButton = new JRadioButtonMenuItem(fixed[fixedCounter++]);
		final ActionListener searchTypeGroupListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource() == searchByTitleButton){searchByTitle = true;}
				if(ae.getSource() == searchByContentButton){searchByTitle = false;}
			}
		};
		searchByTitleButton.addActionListener(searchTypeGroupListener);
		searchByContentButton.addActionListener(searchTypeGroupListener);

		final ButtonGroup searchTypeGroup = new ButtonGroup();
		searchTypeGroup.add(searchByTitleButton);
		searchTypeGroup.add(searchByContentButton);

		final JMenu searchTypeMenu = new JMenu(fixed[fixedCounter++]);
		settingMenu.add(searchTypeMenu);

		searchTypeMenu.add(searchByTitleButton);
		searchTypeMenu.add(searchByContentButton);

		final JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),fixed[fixedCounter++],0,0,null,Color.red));
		searchPanel.add(searchTextField, BorderLayout.CENTER);

		if(language)
		{
			searchPanel.add(searchButton, BorderLayout.WEST);
			searchPanel.add(searchMenuBar, BorderLayout.EAST);
		}
		else
		{
			searchPanel.add(searchButton, BorderLayout.EAST);
			searchPanel.add(searchMenuBar, BorderLayout.WEST);
		}

		final JPanel wordPanel = new JPanel(new BorderLayout());
		wordPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], TitledBorder.CENTER, 0, null, Color.red));

		wordList = new JList();
		wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		wordPanel.add(new JScrollPane(wordList), BorderLayout.CENTER);

		final ListSelectionModel listSelectionModel = wordList.getSelectionModel();
		listSelectionModel.addListSelectionListener(new ListSelectionHandler());

		final JPanel autoWordDetectorPanel = new JPanel(new BorderLayout());
		wordPanel.add(autoWordDetectorPanel, BorderLayout.NORTH);

		autoWordDetectorTextField = new JTextField();
		if(language)autoWordDetectorTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		autoWordDetectorPanel.add(autoWordDetectorTextField, BorderLayout.CENTER);
		autoWordDetectorTextField.addKeyListener(new KeyListener()
		{
			/** Handle the key typed event from the text field. */
		    public void keyTyped(KeyEvent e){}

		    /** Handle the key pressed event from the text field. */
		    public void keyPressed(KeyEvent e){}

		    /** Handle the key released event from the text field. */
		    public void keyReleased(KeyEvent e)
		    {
		    	if(!onlineAutoDetector)
		    	{
		    		if(choosedAutoDetectorLanguage != orderLanguage)
					{
						orderLanguage = choosedAutoDetectorLanguage;
						changeOrderLanguage();
					}

		    		final DefaultListModel listModel = new DefaultListModel();
		    		if(choosedAutoDetectorLanguage)
			    	{
			    		for(int i=0; i<arabicWordTemp.size(); i++)
							listModel.addElement(arabicWordTemp.elementAt(i));
			    	}
			    	else
			    	{
						for(int i=0; i<englishWordTemp.size(); i++)
							listModel.addElement(englishWordTemp.elementAt(i));
			    	}

		    		arabicWordListVector.removeAllElements();
					arabicDescriptionVector.removeAllElements();
					englishWordListVector.removeAllElements();
					englishDescriptionVector.removeAllElements();
					imageName.removeAllElements();
					arabicSoundNameVector.removeAllElements();
					englishSoundNameVector.removeAllElements();
					category.removeAllElements();

		    		for(int i=0; i<arabicWordTemp.size(); i++)
					{
						arabicWordListVector.addElement(arabicWordTemp.elementAt(i));
						arabicDescriptionVector.addElement(arabicDescriptionTemp.elementAt(i));
						englishWordListVector.addElement(englishWordTemp.elementAt(i));
						englishDescriptionVector.addElement(englishDescriptionTemp.elementAt(i));
						imageName.addElement(imageNameTemp.elementAt(i));
						arabicSoundNameVector.addElement(arabicSoundNameTemp.elementAt(i));
						englishSoundNameVector.addElement(englishSoundNameTemp.elementAt(i));
						category.addElement(categoryTemp.elementAt(i));
					}

					wordList.setModel(listModel);
					onlineAutoDetector = true;
		    	}

				final String passedWord = autoWordDetectorTextField.getText();
				imagePanel.removeAll();
				imagePanel.updateUI();
				arabicDetailsPane.setText("");
				englishDetailsPane.setText("");
				arabicWordTextField.setText("");
				englishWordTextField.setText("");
				wordList.clearSelection();

				boolean found = false;
				if(!passedWord.equals(""))
				{
			    	if(choosedAutoDetectorLanguage)
			    	{
			    		wordList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			    		for(int i=0; i<arabicWordTemp.size(); i++)
						{
							if(((String)arabicWordTemp.elementAt(i)).startsWith(passedWord))
							{
								wordList.setSelectedIndex(i);
								found = true;
								break;
							}
						}

						if(!found)
						{
							onlineAutoDetector = false;
							wordList.setModel(new DefaultListModel());
						}
			    	}
			    	else
			    	{
			    		wordList.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
						for(int i=0; i<englishWordTemp.size(); i++)
						{
							if(((String)englishWordTemp.elementAt(i)).startsWith(passedWord))
							{
								wordList.setSelectedIndex(i);
								found = true;
								break;
							}
						}

						if(!found)
						{
							onlineAutoDetector = false;
							wordList.setModel(new DefaultListModel());
						}
			    	}
			    }
			    else
			    {
			    	onlineAutoDetector = false;
					wordList.setModel(new DefaultListModel());
			    }
		    }
		});

		final JButton autoWordDetectorButton = new JButton(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+((language)?"a_letter_m.png":"e_letter_m.png")));
		autoWordDetectorButton.setToolTipText(fixed[fixedCounter++]);
		autoWordDetectorPanel.add(autoWordDetectorButton, BorderLayout.WEST);
		autoWordDetectorButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				imagePanel.removeAll();
				imagePanel.updateUI();
				arabicDetailsPane.setText("");
				englishDetailsPane.setText("");
				arabicWordTextField.setText("");
				englishWordTextField.setText("");
				autoWordDetectorTextField.setText("");
				wordList.setModel(new DefaultListModel());

				if(choosedAutoDetectorLanguage)
				{
					autoWordDetectorButton.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"e_letter_m.png"));
					choosedAutoDetectorLanguage = false;
					autoWordDetectorTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				}
				else
				{
					autoWordDetectorButton.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"a_letter_m.png"));
					choosedAutoDetectorLanguage = true;
					autoWordDetectorTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				}
				//orderLanguage = choosedAutoDetectorLanguage;
				//changeOrderLanguage();
			}
		});

		final JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(searchPanel, BorderLayout.NORTH);

		final JPanel displayPanel = new JPanel(new GridLayout(1,3));
		mainPanel.add(displayPanel, BorderLayout.CENTER);

		final JPanel arabicDetailsPanel = new JPanel(new BorderLayout());
		arabicDetailsPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		arabicDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));

		arabicDetailsPane = new JEditorPane();
		arabicDetailsPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		arabicDetailsPane.setEditable(false);
		arabicDetailsPane.setContentType("text/html");

		final JPanel arabicWordPanel = new JPanel(new BorderLayout());
		arabicWordTextField = new JTextField();
		arabicWordTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		arabicWordTextField.setEditable(false);
		arabicWordPanel.add(arabicWordTextField);

		final JButton arabicSoundButton = new JButton(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"a_audioCataloger_m.png"));
		arabicSoundButton.setToolTipText(fixed[fixedCounter++]);
		arabicWordPanel.add(arabicSoundButton, BorderLayout.WEST);
		arabicSoundButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(selectionIndex!=-1)
				{
					if(!(arabicSoundNameVector.elementAt(selectionIndex).equals("null")))
					{
						String prefix = "file:" + System.getProperty("user.dir")+ fileSeparator;

						try
						{
							URL clickURL = new URL(((pathChoice==0)?(prefix + ".."+fileSeparator+"soundDictionary"):("file:"+soundPath))+fileSeparator+(String)arabicSoundNameVector.elementAt(selectionIndex));
							click = Applet.newAudioClip(clickURL);
							click.play();
						}
						catch (MalformedURLException mue){}
					}
					else
						JOptionPane.showOptionDialog(MIE, variable[7], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
				}
				else
					JOptionPane.showOptionDialog(MIE, variable[6], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
			}
		});

		arabicDetailsPanel.add(new JScrollPane(arabicDetailsPane), BorderLayout.CENTER);
		arabicDetailsPanel.add(arabicWordPanel, BorderLayout.NORTH);

		englishDetailsPanel = new JPanel(new BorderLayout());
		englishDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));

		englishDetailsPane = new JEditorPane();
		englishDetailsPane.setEditable(false);
		englishDetailsPane.setContentType("text/html");

		final JPanel englishWordPanel = new JPanel(new BorderLayout());
		englishWordTextField = new JTextField();
		englishWordTextField.setEditable(false);
		englishWordPanel.add(englishWordTextField);

		final JButton englishSoundButton = new JButton(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"audioCataloger_m.png"));
		englishSoundButton.setToolTipText(fixed[fixedCounter++]);
		englishWordPanel.add(englishSoundButton, BorderLayout.EAST);
		englishSoundButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(selectionIndex!=-1)
				{
					if(!(englishSoundNameVector.elementAt(selectionIndex).equals("null")))
					{
						String prefix = "file:" + System.getProperty("user.dir")+ fileSeparator;

						try
						{
							URL clickURL = new URL(((pathChoice==0)?(prefix + ".."+fileSeparator+"soundDictionary"):("file:"+soundPath))+fileSeparator+(String)englishSoundNameVector.elementAt(selectionIndex));
							click = Applet.newAudioClip(clickURL);
							click.play();
						}
						catch (MalformedURLException mue){}
					}
					else
						JOptionPane.showOptionDialog(MIE, variable[7], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
				}
				else
					JOptionPane.showOptionDialog(MIE, variable[6], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
			}
		});

		englishDetailsPanel.add(new JScrollPane(englishDetailsPane), BorderLayout.CENTER);
		englishDetailsPanel.add(englishWordPanel, BorderLayout.NORTH);

		imagePanel = new JPanel(new FlowLayout());
		imagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));

		final JPanel mainControlPanel = new JPanel(new BorderLayout());
		mainControlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));

		final JButton propertyButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"preferences_m.png"));
		propertyButton.setToolTipText(fixed[fixedCounter++]);
		mainControlPanel.add(propertyButton, BorderLayout.SOUTH);
		propertyButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new ArabicDictionaryPathDeterministic(MIE, ArabicDictionary.this);}});

		final JPanel controlPanel = new JPanel(new GridLayout(5,2));
		final JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(mainControlPanel, BorderLayout.NORTH);
		eastPanel.add(wordPanel, BorderLayout.CENTER);
		mainControlPanel.add(controlPanel, BorderLayout.CENTER);

		addButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"add_m.png"));
		addButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(addButton);
		addButton.setEnabled(false);
		addButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new AddToDictionary(language, MIE, ArabicDictionary.this, arabicWordTemp, englishWordTemp);}});

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
					new EditDictionary(language, MIE, ArabicDictionary.this, (String)imageName.elementAt(selectionIndex),
						(String)arabicDescriptionVector.elementAt(selectionIndex), (String)englishDescriptionVector.elementAt(selectionIndex),
						(String)arabicWordListVector.elementAt(selectionIndex), (String)englishWordListVector.elementAt(selectionIndex),
						(String)arabicSoundNameVector.elementAt(selectionIndex), (String)englishSoundNameVector.elementAt(selectionIndex),
						arabicWordTemp, englishWordTemp, (String)categoryTemp.elementAt(selectionIndex));
				}
				else
					JOptionPane.showOptionDialog(MIE, variable[2], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
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
					int choice = JOptionPane.showOptionDialog(MIE, variable[8]+"("+(language?(String)arabicWordListVector.elementAt(selectionIndex):(String)englishWordListVector.elementAt(selectionIndex))+")", variable[4], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
					if(choice == JOptionPane.YES_OPTION)
					{
						try
						{
							Class.forName("com.ihost.cs.jdbc.CloudscapeDriver").newInstance();
							final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
							final Statement stmt = con.createStatement();
							stmt.executeUpdate("delete from arabicDictionary WHERE arabicWord ='"+(String)arabicWordListVector.elementAt(selectionIndex)+"'");
							stmt.close();
							con.close();

							if(!(imageName.elementAt(selectionIndex).equals("null")))
							{
								choice = JOptionPane.showOptionDialog(MIE, variable[3], variable[4], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
								if(choice == JOptionPane.YES_OPTION)
									new DeleteFile(((pathChoice==0)?(".."+fileSeparator+"imageDictionary"):imagePath)+fileSeparator+(String)imageName.elementAt(selectionIndex));
							}

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
						catch(Throwable ex)
						{
							System.out.println("exception thrown:");
							if(ex instanceof SQLException)
								printSQLError((SQLException)ex);
							else
								ex.printStackTrace();
						}
					}
				}
				else
					JOptionPane.showOptionDialog(MIE, variable[5], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
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
        		javax.swing.filechooser.FileFilter filter = new ExtensionFileFilter(new String[] {"awd", "AWD"}, "AWD files");
				fc.addChoosableFileFilter(filter);
				fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());

				int returnVal = fc.showSaveDialog(ArabicDictionary.this);
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
						final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
						final Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
						final ResultSet rs = stmt.executeQuery("select * from arabicDictionary");

						Writer out;
						if(extensionName.equalsIgnoreCase("awd"))
							out = new OutputStreamWriter(new FileOutputStream(PathName),"UTF8");
						else
							out = new OutputStreamWriter(new FileOutputStream(PathName+".awd"),"UTF8");

						while(rs.next())
						{
							out.write(rs.getString("arabicWord")+"\u00F6");
							out.write(rs.getString("englishWord")+"\u00F6");
							out.write(rs.getString("arabicDescription")+"\u00F6");
							out.write(rs.getString("englishDescription")+"\u00F6");
							out.write(rs.getString("imageName")+"\u00F6");
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
				javax.swing.filechooser.FileFilter filter = new ExtensionFileFilter(new String[] {"awd", "AWD"}, "AWD files");
				fc.addChoosableFileFilter(filter);
				fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
				final int returnVal = fc.showOpenDialog(ArabicDictionary.this);

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
						stmt.execute("drop table arabicDictionary");
						stmt.close();
						con.close();

						con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase;create=true","root","secret");
						stmt = con.createStatement();
						stmt.execute("create table arabicDictionary(arabicWord VARCHAR(100),englishWord VARCHAR(100),arabicDescription LONG VARCHAR,englishDescription LONG VARCHAR,imageName VARCHAR(50),arabicSoundName VARCHAR(50),englishSoundName VARCHAR(50),category VARCHAR(100))");

						final FileInputStream fis = new FileInputStream(PathName);
						final InputStreamReader isr = new InputStreamReader(fis, "UTF8");
						final Reader in = new BufferedReader(isr);
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

								stmt.execute("insert into arabicDictionary values ('"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"')");
								counter++;
								buffer = new StringBuffer();
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

							stmt.execute("insert into arabicDictionary values ('"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"','"+tokens.nextToken()+"')");
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
						imagePanel.removeAll();
						imagePanel.updateUI();
						arabicDetailsPane.setText("");
						englishDetailsPane.setText("");
						arabicWordTextField.setText("");
						englishWordTextField.setText("");

						wordList.setModel(new DefaultListModel());
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
		sectionButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new DictionarySection(MIE, ArabicDictionary.this);}});

		final JButton sentenceButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"sentence_m.png"));
		sentenceButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(sentenceButton);
		sentenceButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new SentenceDictionary(language, MIE, authenticationmButton.isEnabled(), pathChoice, imagePath, soundPath);}});

		final JButton printButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"printer_m.png"));
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

				final int returnVal = fc.showSaveDialog(ArabicDictionary.this);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                    final File file = fc.getSelectedFile();
                    final String fileName = file.getName();
                    final String PathName = String.valueOf(file);

                    String extensionName = "";
					final StringTokenizer tokens = new StringTokenizer(fileName, ".");
					if(tokens.countTokens()>1)
						while(tokens.hasMoreTokens())
							extensionName = tokens.nextToken();

					try
					{
						String folderName = null;
						OutputStreamWriter out = null;
						if(extensionName.equalsIgnoreCase("html"))
						{
							out = new OutputStreamWriter(new FileOutputStream(PathName),"Cp1256");
							folderName = PathName.substring(0, PathName.length()-5)+"_files";
						}
						else
						{
							out = new OutputStreamWriter(new FileOutputStream(PathName+".html"),"Cp1256");
							folderName = PathName+"_files";
						}

						//will not work with linux
						Runtime.getRuntime().exec("cmd.exe /c mkdir \""+folderName+"\"");
						Thread.sleep(500);

						out.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1256\"><title>Arabian Dictionary</title></head><body><table width=\"595.28\" style=\"border-collapse: collapse; border-style: dotted; border-width: 1px\" cellpadding=\"0\">");
						out.write("<tr><td valign=\"middle\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; \" height=\"23\" colspan=\"3\" bgcolor=\"#F4F4F4\"><p align=\"center\" dir=\"rtl\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#0000FF\">\u0627\u0644\u0642\u0627\u0645\u0648\u0633 \u0627\u0644\u0639\u0631\u0628\u064A \u0627\u0644\u0645\u0635\u0648\u0631 (\u0639\u0631\u0628\u064A - \u0639\u0631\u0628\u064A\u060C \u0639\u0631\u0628\u064A - \u0625\u0646\u062C\u0644\u064A\u0632\u064A\u060C \u0625\u0646\u062C\u0644\u064A\u0632\u064A - \u0639\u0631\u0628\u064A\u060C \u0625\u0646\u062C\u0644\u064A\u0632\u064A - \u0625\u0646\u062C\u0644\u064A\u0632\u064A)</font></td></tr><tr><td valign=\"center\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\" height=\"22\" colspan=\"3\" bgcolor=\"#F4F4F4\"><p align=\"center\" dir=\"ltr\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#0000FF\"><span lang=\"en-us\">Arabic Dictionary (Arabic-Arabic, English-Arabic, Arabic-English, English-English)</span></font></td></tr>");
						out.write("<tr><td valign=\"center\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><font face=\"Tahoma\" style=\"font-size: 9pt\"color=\"#FF0000\">Sections </font></td><td valign=\"center\" height=\"19\" width=\"501\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><p align=\"center\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#808000\">");

						for(int i=0; i<dictionaryCategory.size()-1; i++)
							out.write((String)dictionaryCategory.elementAt(i)+", ");

						out.write((String)dictionaryCategory.lastElement());
						out.write("</font></td><td valign=\"center\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><p align=\"right\"><font face=\"Tahoma\" style=\"font-size: 9pt\"><font color=\"#FF0000\">\u0627\u0644\u0623\u0642\u0633\u0627\u0645</font> </font> </td></tr>");
						out.write("<tr><td valign=\"top\" width=\"100%\" colspan=\"3\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td valign=\"top\" height=\"19\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#FF0000\">Age Range</font></td><td><p align=\"center\"><font color=\"#808000\" face=\"Tahoma\" style=\"font-size: 9pt\">\u0623\u0642\u0644 \u0645\u0646 "+ageIndex+" \u0633\u0646\u0629</font></td><td><p align=\"right\"><font face=\"Tahoma\" style=\"font-size: 9pt\" color=\"#FF0000\">\u0627\u0644\u0641\u0626\u0629 \u0627\u0644\u0639\u0645\u0631\u064A\u0629</font></td></tr></table></td></tr>");
						out.write("<tr><td valign=\"top\" width=\"100%\" colspan=\"3\">");

						for(int i=0; i<arabicWordTemp.size(); i++)
						{
							out.write("<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-width: 1px; border-bottom-style: dotted; border-bottom-width: 1px\"><tr><td style=\"border-left-width: 1px; border-right-style: dotted; border-right-width: 1px; border-top-width: 1px; border-bottom-width: 1px\" width=\"50%\" height=\"100%\">");
							if(imageNameTemp.elementAt(i).equals("null"))
								out.write("<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" height=\"100%\"><tr><td style=\"border-left-width: 1px; border-right-style: dotted; border-right-width: 1px; border-top-width: 1px; border-bottom-width: 1px\" width=\"50%\" valign=\"top\"><font face=\"Tahoma\" style=\"font-size: 9pt\">"+(String)englishWordTemp.elementAt(i)+"<br>"+englishDescriptionTemp.elementAt(i)+"</font></td><td width=\"50%\" valign=\"top\"><p align=\"right\"><font face=\"Tahoma\" style=\"font-size: 9pt\">"+arabicWordTemp.elementAt(i)+"<br>"+arabicDescriptionTemp.elementAt(i)+"</font></td></tr></table>");
							else
							{
								out.write("<table border=\"0\" width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"border-left-width: 1px; border-right-style: dotted; border-right-width: 1px; border-top-width: 1px; border-bottom-width: 1px\" width=\"50%\" valign=\"top\"><font face=\"Tahoma\" style=\"font-size: 9pt\">"+(String)englishWordTemp.elementAt(i)+"<br>"+englishDescriptionTemp.elementAt(i)+"</font></td><td width=\"50%\" valign=\"top\"><p align=\"right\"><font face=\"Tahoma\" style=\"font-size: 9pt\">"+arabicWordTemp.elementAt(i)+"<br>"+arabicDescriptionTemp.elementAt(i)+"</font></td></tr><tr><td colspan=\"2\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-style: dotted; border-top-width: 1px; border-bottom-width: 1px\" valign=\"middle\"><p align=\"center\"><img border=\"0\" src=\""+folderName+fileSeparator+imageNameTemp.elementAt(i)+"\"></td></tr></table>");
								new CopyFile(((pathChoice==0)?(".."+fileSeparator+"imageDictionary"):imagePath)+fileSeparator+imageNameTemp.elementAt(i), folderName+fileSeparator+imageNameTemp.elementAt(i), MIE);
							}
							out.write("</td><td width=\"50%\">");

							i++;
							if(i<arabicWordTemp.size())
							{
								if(imageNameTemp.elementAt(i).equals("null"))
									out.write("<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" height=\"100%\"><tr><td style=\"border-left-width: 1px; border-right-style: dotted; border-right-width: 1px; border-top-width: 1px; border-bottom-width: 1px\" width=\"50%\" valign=\"top\"><font face=\"Tahoma\" style=\"font-size: 9pt\">"+(String)englishWordTemp.elementAt(i)+"<br>"+englishDescriptionTemp.elementAt(i)+"</font></td><td width=\"50%\" valign=\"top\"><p align=\"right\"><font face=\"Tahoma\" style=\"font-size: 9pt\">"+arabicWordTemp.elementAt(i)+"<br>"+arabicDescriptionTemp.elementAt(i)+"</font></td></tr></table>");
								else
								{
									out.write("<table border=\"0\" width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td style=\"border-left-width: 1px; border-right-style: dotted; border-right-width: 1px; border-top-width: 1px; border-bottom-width: 1px\" width=\"50%\" valign=\"top\"><font face=\"Tahoma\" style=\"font-size: 9pt\">"+(String)englishWordTemp.elementAt(i)+"<br>"+englishDescriptionTemp.elementAt(i)+"</font></td><td width=\"50%\" valign=\"top\"><p align=\"right\"><font face=\"Tahoma\" style=\"font-size: 9pt\">"+arabicWordTemp.elementAt(i)+"<br>"+arabicDescriptionTemp.elementAt(i)+"</font></td></tr><tr><td colspan=\"2\" style=\"border-left-width: 1px; border-right-width: 1px; border-top-style: dotted; border-top-width: 1px; border-bottom-width: 1px\" valign=\"middle\"><p align=\"center\"><img border=\"0\" src=\""+folderName+fileSeparator+imageNameTemp.elementAt(i)+"\"></td></tr></table>");
									new CopyFile(((pathChoice==0)?(".."+fileSeparator+"imageDictionary"):imagePath)+fileSeparator+imageNameTemp.elementAt(i), folderName+fileSeparator+imageNameTemp.elementAt(i), MIE);
								}
							}
							out.write("</td></tr></table>");
						}

						out.write("</td></tr><tr><td valign=\"top\" width=\"100%\" colspan=\"3\" height=\"14\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"111\"><font color=\"#808000\" face=\"Tahoma\" style=\"font-size: 9pt\">www.maknoon.com</font></td><td width=\"464\"><p align=\"center\"><font color=\"#808000\" face=\"Tahoma\" style=\"font-size: 9pt\">Maknoon Islamic Encyclopedia \u0645\u0648\u0633\u0648\u0639\u0629 \u0645\u0643\u0646\u0648\u0646 \u0627\u0644\u0625\u0633\u0644\u0627\u0645\u064A\u0629</span></font></td><td width=\"1\"><font color=\"#808000\" face=\"Tahoma\" style=\"font-size: 9pt\">"+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"/"+Calendar.getInstance().get(Calendar.YEAR)+"</font></td></tr></table></td></tr></table></body></html>");
						out.close();

						if(extensionName.equalsIgnoreCase("html"))
							new BrowserControl(PathName);
						else
							new BrowserControl(PathName+".html");
					}
					catch(IOException ioe){ioe.printStackTrace();}
					catch(InterruptedException ie){ie.printStackTrace();}
                }
                else
                    System.out.println("print command cancelled by user.");
			}
		});

		final JButton writeInArabicButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"path_m.png"));
		writeInArabicButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(writeInArabicButton);
		writeInArabicButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new ArabicLearning(language, MIE, arabicWordTemp);}});

		authenticationmButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"lock_m.png"));
		authenticationmButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(authenticationmButton);
		authenticationmButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new AuthenticationSystem(language, ArabicDictionary.this, MIE);}});

		if(language)
		{
			displayPanel.add(arabicDetailsPanel);
			displayPanel.add(imagePanel);
			displayPanel.add(englishDetailsPanel);
		}
		else
		{
			displayPanel.add(englishDetailsPanel);
			displayPanel.add(imagePanel);
			displayPanel.add(arabicDetailsPanel);
		}

		add(mainPanel, BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);

		/*
		 * Update version 1.3
		 *
		 * Initilize and store the database in arrays to be fast.
		 *
		final SwingWorker loadWorker = new SwingWorker()
		{
			public Object construct()
			{
				storeDatabaseInMemory();
				return null;
			}
		};
		loadWorker.start();
		*/

		// Initilize and store the database in arrays to be fast.
		storeDatabaseInMemory();

		final JInternalFrame frame = new JInternalFrame();
		frame.getContentPane().add(this);

		Container contentPane = frame.getContentPane();
		if(language) contentPane.applyComponentOrientation(ComponentOrientation.getOrientation(contentPane.getLocale()));

		// Not yet
		//setAllOpaque(contentPane);
		setForEnglishOrientation();
	}

	public static void setAllOpaque(Container cont)
	{
		Component[] components = cont.getComponents();
		for(int i=0; i<components.length; i++)
		{
			if(components[i] instanceof JPanel)
				((JPanel)components[i]).setOpaque(false);

			if(components[i] instanceof JRadioButton)
				((JRadioButton)components[i]).setOpaque(false);

			if(components[i] instanceof Container)
				setAllOpaque((Container)components[i]);
		}
	}

	public void enableControlTools()
	{
		addButton.setEnabled(true);
		editButton.setEnabled(true);
		deleteButton.setEnabled(true);
		exportDBButton.setEnabled(true);
		importDBButton.setEnabled(true);
		authenticationmButton.setEnabled(false);
	}

	public void setForEnglishOrientation()
	{
		englishDetailsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		englishDetailsPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		englishWordTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
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

		arabicWordListVector.removeAllElements();
		arabicDescriptionVector.removeAllElements();
		englishWordListVector.removeAllElements();
		englishDescriptionVector.removeAllElements();
		imageName.removeAllElements();
		arabicSoundNameVector.removeAllElements();
		englishSoundNameVector.removeAllElements();
		category.removeAllElements();

		Vector materialToSearch = new Vector(1000);
		Vector wordTemp = new Vector(1000);
		if(searchLanguage)
		{
			wordTemp = arabicWordTemp;
			if(searchByTitle)
				materialToSearch = wordTemp;
			else
				materialToSearch = arabicDescriptionTemp;
		}
		else
		{
			wordTemp = englishWordTemp;
			if(searchByTitle)
				materialToSearch = wordTemp;
			else
				materialToSearch = englishDescriptionTemp;
		}

		if(searchLanguage != orderLanguage)
		{
			orderLanguage = searchLanguage;
			changeOrderLanguage();
		}

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
					listModel.addElement(wordTemp.elementAt(i));
					arabicWordListVector.addElement(arabicWordTemp.elementAt(i));
					arabicDescriptionVector.addElement(arabicDescriptionTemp.elementAt(i));
					englishWordListVector.addElement(englishWordTemp.elementAt(i));
					englishDescriptionVector.addElement(englishDescriptionTemp.elementAt(i));
					imageName.addElement(imageNameTemp.elementAt(i));
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
					listModel.addElement(wordTemp.elementAt(i));
					arabicWordListVector.addElement(arabicWordTemp.elementAt(i));
					arabicDescriptionVector.addElement(arabicDescriptionTemp.elementAt(i));
					englishWordListVector.addElement(englishWordTemp.elementAt(i));
					englishDescriptionVector.addElement(englishDescriptionTemp.elementAt(i));
					imageName.addElement(imageNameTemp.elementAt(i));
					arabicSoundNameVector.addElement(arabicSoundNameTemp.elementAt(i));
					englishSoundNameVector.addElement(englishSoundNameTemp.elementAt(i));
					category.addElement(categoryTemp.elementAt(i));
				}
			}
		}

		if(searchChoice==2)
		{
			StringTokenizer searchTokens = new StringTokenizer(Search, " ?,:'");
			String [] searchWords = new String[searchTokens.countTokens()];
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
					listModel.addElement(wordTemp.elementAt(i));
					arabicWordListVector.addElement(arabicWordTemp.elementAt(i));
					arabicDescriptionVector.addElement(arabicDescriptionTemp.elementAt(i));
					englishWordListVector.addElement(englishWordTemp.elementAt(i));
					englishDescriptionVector.addElement(englishDescriptionTemp.elementAt(i));
					imageName.addElement(imageNameTemp.elementAt(i));
					arabicSoundNameVector.addElement(arabicSoundNameTemp.elementAt(i));
					englishSoundNameVector.addElement(englishSoundNameTemp.elementAt(i));
					category.addElement(categoryTemp.elementAt(i));
				}
			}
		}

		if(searchChoice==3)
		{
			StringTokenizer searchTokens = new StringTokenizer(Search, " ?,:'");
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
					listModel.addElement(wordTemp.elementAt(i));
					arabicWordListVector.addElement(arabicWordTemp.elementAt(i));
					arabicDescriptionVector.addElement(arabicDescriptionTemp.elementAt(i));
					englishWordListVector.addElement(englishWordTemp.elementAt(i));
					englishDescriptionVector.addElement(englishDescriptionTemp.elementAt(i));
					imageName.addElement(imageNameTemp.elementAt(i));
					arabicSoundNameVector.addElement(arabicSoundNameTemp.elementAt(i));
					englishSoundNameVector.addElement(englishSoundNameTemp.elementAt(i));
					category.addElement(categoryTemp.elementAt(i));
				}
			}
		}

		if(searchLanguage)
			wordList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		else
			wordList.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		imagePanel.removeAll();
		imagePanel.updateUI();
		arabicDetailsPane.setText("");
		englishDetailsPane.setText("");
		arabicWordTextField.setText("");
		englishWordTextField.setText("");

		onlineAutoDetector = false;
		autoWordDetectorTextField.setText("");
		wordList.setModel(listModel);
	}

	protected Vector arabicWordTemp = new Vector(1000);
	protected Vector arabicDescriptionTemp = new Vector(1000);
	protected Vector englishWordTemp = new Vector(1000);
	protected Vector englishDescriptionTemp = new Vector(1000);
	protected Vector imageNameTemp = new Vector(1000);
	protected Vector arabicSoundNameTemp = new Vector(1000);
	protected Vector englishSoundNameTemp = new Vector(1000);
	protected Vector categoryTemp = new Vector(1000);

	protected Vector arabicOrder_arabicWordTemp = new Vector(1000);
	protected Vector arabicOrder_arabicDescriptionTemp = new Vector(1000);
	protected Vector arabicOrder_englishWordTemp = new Vector(1000);
	protected Vector arabicOrder_englishDescriptionTemp = new Vector(1000);
	protected Vector arabicOrder_imageNameTemp = new Vector(1000);
	protected Vector arabicOrder_arabicSoundNameTemp = new Vector(1000);
	protected Vector arabicOrder_englishSoundNameTemp = new Vector(1000);
	protected Vector arabicOrder_categoryTemp = new Vector(1000);

	protected Vector englishOrder_arabicWordTemp = new Vector(1000);
	protected Vector englishOrder_arabicDescriptionTemp = new Vector(1000);
	protected Vector englishOrder_englishWordTemp = new Vector(1000);
	protected Vector englishOrder_englishDescriptionTemp = new Vector(1000);
	protected Vector englishOrder_imageNameTemp = new Vector(1000);
	protected Vector englishOrder_arabicSoundNameTemp = new Vector(1000);
	protected Vector englishOrder_englishSoundNameTemp = new Vector(1000);
	protected Vector englishOrder_categoryTemp = new Vector(1000);

	protected boolean orderLanguage;
	public void storeDatabaseInMemory()
	{
		arabicWordTemp.removeAllElements();
		arabicDescriptionTemp.removeAllElements();
		englishWordTemp.removeAllElements();
		englishDescriptionTemp.removeAllElements();
		arabicSoundNameTemp.removeAllElements();
		englishSoundNameTemp.removeAllElements();
		imageNameTemp.removeAllElements();
		categoryTemp.removeAllElements();

		try
		{
			Class.forName("com.ihost.cs.jdbc.CloudscapeDriver").newInstance();
			final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
			final Statement stmt = con.createStatement();

			// This do it only once at the begingin since it is fixed already unless you add or update some items
			final ResultSet rs = stmt.executeQuery("select * from arabicDictionary");

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
						arabicWordTemp.addElement(new String(rs.getString("arabicWord")));
						arabicDescriptionTemp.addElement(new String(rs.getString("arabicDescription")));
						englishWordTemp.addElement(new String(rs.getString("englishWord")));
						englishDescriptionTemp.addElement(new String(rs.getString("englishDescription")));
						imageNameTemp.addElement(new String(rs.getString("imageName")));
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

		wordList.setModel(new DefaultListModel());
		imagePanel.removeAll();
		imagePanel.updateUI();
		arabicDetailsPane.setText("");
		englishDetailsPane.setText("");
		arabicWordTextField.setText("");
		englishWordTextField.setText("");

		arabicOrder_arabicWordTemp.removeAllElements();
		arabicOrder_arabicDescriptionTemp.removeAllElements();
		arabicOrder_englishWordTemp.removeAllElements();
		arabicOrder_englishDescriptionTemp.removeAllElements();
		arabicOrder_imageNameTemp.removeAllElements();
		arabicOrder_arabicSoundNameTemp.removeAllElements();
		arabicOrder_englishSoundNameTemp.removeAllElements();
		arabicOrder_categoryTemp.removeAllElements();

		englishOrder_arabicWordTemp.removeAllElements();
		englishOrder_arabicDescriptionTemp.removeAllElements();
		englishOrder_englishWordTemp.removeAllElements();
		englishOrder_englishDescriptionTemp.removeAllElements();
		englishOrder_imageNameTemp.removeAllElements();
		englishOrder_arabicSoundNameTemp.removeAllElements();
		englishOrder_englishSoundNameTemp.removeAllElements();
		englishOrder_categoryTemp.removeAllElements();

		arabicOrder_arabicWordTemp.addAll(arabicWordTemp);
		arabicOrder_arabicDescriptionTemp.addAll(arabicDescriptionTemp);
		arabicOrder_englishWordTemp.addAll(englishWordTemp);
		arabicOrder_englishDescriptionTemp.addAll(englishDescriptionTemp);
		arabicOrder_imageNameTemp.addAll(imageNameTemp);
		arabicOrder_arabicSoundNameTemp.addAll(arabicSoundNameTemp);
		arabicOrder_englishSoundNameTemp.addAll(englishSoundNameTemp);
		arabicOrder_categoryTemp.addAll(categoryTemp);

		englishOrder_arabicWordTemp.addAll(arabicWordTemp);
		englishOrder_arabicDescriptionTemp.addAll(arabicDescriptionTemp);
		englishOrder_englishWordTemp.addAll(englishWordTemp);
		englishOrder_englishDescriptionTemp.addAll(englishDescriptionTemp);
		englishOrder_imageNameTemp.addAll(imageNameTemp);
		englishOrder_arabicSoundNameTemp.addAll(arabicSoundNameTemp);
		englishOrder_englishSoundNameTemp.addAll(englishSoundNameTemp);
		englishOrder_categoryTemp.addAll(categoryTemp);

		/*
		arabicOrder_arabicWordTemp = arabicWordTemp;
		arabicOrder_arabicDescriptionTemp = arabicDescriptionTemp;
		arabicOrder_englishWordTemp = englishWordTemp;
		arabicOrder_englishDescriptionTemp = englishDescriptionTemp;
		arabicOrder_imageNameTemp = imageNameTemp;
		arabicOrder_arabicSoundNameTemp = arabicSoundNameTemp;
		arabicOrder_englishSoundNameTemp = englishSoundNameTemp;

		englishOrder_arabicWordTemp = arabicWordTemp;
		englishOrder_arabicDescriptionTemp = arabicDescriptionTemp;
		englishOrder_englishWordTemp = englishWordTemp;
		englishOrder_englishDescriptionTemp = englishDescriptionTemp;
		englishOrder_imageNameTemp = imageNameTemp;
		englishOrder_arabicSoundNameTemp = arabicSoundNameTemp;
		englishOrder_englishSoundNameTemp = englishSoundNameTemp;
		*/

		// Ordering, using bubblesort algorithm, try to do quicksort Algorithm.
		String firstOrderName;
		String dataTypeTemp;
		boolean Sorted = false;
		for(int last = arabicOrder_arabicWordTemp.size()-1; (last >= 1) && (!Sorted); last--)
		{
			Sorted = true;
			for(int index=0; index<last; index++)
			{
				if(((String)arabicOrder_arabicWordTemp.elementAt(index)).compareTo((String)arabicOrder_arabicWordTemp.elementAt(index+1)) > 0)
				{
					// Swap
					dataTypeTemp = (String)arabicOrder_arabicWordTemp.elementAt(index);
					arabicOrder_arabicWordTemp.setElementAt(arabicOrder_arabicWordTemp.elementAt(index+1), index);
					arabicOrder_arabicWordTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)arabicOrder_arabicDescriptionTemp.elementAt(index);
					arabicOrder_arabicDescriptionTemp.setElementAt(arabicOrder_arabicDescriptionTemp.elementAt(index+1), index);
					arabicOrder_arabicDescriptionTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)arabicOrder_englishWordTemp.elementAt(index);
					arabicOrder_englishWordTemp.setElementAt(arabicOrder_englishWordTemp.elementAt(index+1), index);
					arabicOrder_englishWordTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)arabicOrder_englishDescriptionTemp.elementAt(index);
					arabicOrder_englishDescriptionTemp.setElementAt(arabicOrder_englishDescriptionTemp.elementAt(index+1), index);
					arabicOrder_englishDescriptionTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)arabicOrder_imageNameTemp.elementAt(index);
					arabicOrder_imageNameTemp.setElementAt(arabicOrder_imageNameTemp.elementAt(index+1), index);
					arabicOrder_imageNameTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)arabicOrder_arabicSoundNameTemp.elementAt(index);
					arabicOrder_arabicSoundNameTemp.setElementAt(arabicOrder_arabicSoundNameTemp.elementAt(index+1), index);
					arabicOrder_arabicSoundNameTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)arabicOrder_englishSoundNameTemp.elementAt(index);
					arabicOrder_englishSoundNameTemp.setElementAt(arabicOrder_englishSoundNameTemp.elementAt(index+1), index);
					arabicOrder_englishSoundNameTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)arabicOrder_categoryTemp.elementAt(index);
					arabicOrder_categoryTemp.setElementAt(arabicOrder_categoryTemp.elementAt(index+1), index);
					arabicOrder_categoryTemp.setElementAt(dataTypeTemp, index+1);

					Sorted = false;
				}
			}
		}

		Sorted = false;
		for(int last = englishOrder_englishWordTemp.size()-1; (last >= 1) && (!Sorted); last--)
		{
			Sorted = true;
			for(int index = 0; index < last; index++)
			{
				if(((String)englishOrder_englishWordTemp.elementAt(index)).compareTo((String)englishOrder_englishWordTemp.elementAt(index+1)) > 0)
				{
					// Swap
					dataTypeTemp = (String)englishOrder_arabicWordTemp.elementAt(index);
					englishOrder_arabicWordTemp.setElementAt(englishOrder_arabicWordTemp.elementAt(index+1), index);
					englishOrder_arabicWordTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)englishOrder_arabicDescriptionTemp.elementAt(index);
					englishOrder_arabicDescriptionTemp.setElementAt(englishOrder_arabicDescriptionTemp.elementAt(index+1), index);
					englishOrder_arabicDescriptionTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)englishOrder_englishWordTemp.elementAt(index);
					englishOrder_englishWordTemp.setElementAt(englishOrder_englishWordTemp.elementAt(index+1), index);
					englishOrder_englishWordTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)englishOrder_englishDescriptionTemp.elementAt(index);
					englishOrder_englishDescriptionTemp.setElementAt(englishOrder_englishDescriptionTemp.elementAt(index+1), index);
					englishOrder_englishDescriptionTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)englishOrder_imageNameTemp.elementAt(index);
					englishOrder_imageNameTemp.setElementAt(englishOrder_imageNameTemp.elementAt(index+1), index);
					englishOrder_imageNameTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)englishOrder_arabicSoundNameTemp.elementAt(index);
					englishOrder_arabicSoundNameTemp.setElementAt(englishOrder_arabicSoundNameTemp.elementAt(index+1), index);
					englishOrder_arabicSoundNameTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)englishOrder_englishSoundNameTemp.elementAt(index);
					englishOrder_englishSoundNameTemp.setElementAt(englishOrder_englishSoundNameTemp.elementAt(index+1), index);
					englishOrder_englishSoundNameTemp.setElementAt(dataTypeTemp, index+1);

					dataTypeTemp = (String)englishOrder_categoryTemp.elementAt(index);
					englishOrder_categoryTemp.setElementAt(englishOrder_categoryTemp.elementAt(index+1), index);
					englishOrder_categoryTemp.setElementAt(dataTypeTemp, index+1);

					Sorted = false;
				}
			}
		}
		changeOrderLanguage();
	}

	public void changeOrderLanguage()
	{
		arabicWordTemp.removeAllElements();
		arabicDescriptionTemp.removeAllElements();
		englishWordTemp.removeAllElements();
		englishDescriptionTemp.removeAllElements();
		arabicSoundNameTemp.removeAllElements();
		englishSoundNameTemp.removeAllElements();
		imageNameTemp.removeAllElements();
		categoryTemp.removeAllElements();

		if(orderLanguage)
		{
			arabicWordTemp.addAll(arabicOrder_arabicWordTemp);
			arabicDescriptionTemp.addAll(arabicOrder_arabicDescriptionTemp);
			englishWordTemp.addAll(arabicOrder_englishWordTemp);
			englishDescriptionTemp.addAll(arabicOrder_englishDescriptionTemp);
			imageNameTemp.addAll(arabicOrder_imageNameTemp);
			arabicSoundNameTemp.addAll(arabicOrder_arabicSoundNameTemp);
			englishSoundNameTemp.addAll(arabicOrder_englishSoundNameTemp);
			categoryTemp.addAll(arabicOrder_categoryTemp);
		}
		else
		{
			arabicWordTemp.addAll(englishOrder_arabicWordTemp);
			arabicDescriptionTemp.addAll(englishOrder_arabicDescriptionTemp);
			englishWordTemp.addAll(englishOrder_englishWordTemp);
			englishDescriptionTemp.addAll(englishOrder_englishDescriptionTemp);
			imageNameTemp.addAll(englishOrder_imageNameTemp);
			arabicSoundNameTemp.addAll(englishOrder_arabicSoundNameTemp);
			englishSoundNameTemp.addAll(englishOrder_englishSoundNameTemp);
			categoryTemp.addAll(englishOrder_categoryTemp);
		}
	}

	public static String HTMLFreeText(String HTMLText)
	{
		HTMLText.replaceAll("<wbr>", "");

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
				/*
				File file = new File(((pathChoice==0)?(".."+fileSeparator+"imageDictionary"):imagePath)+fileSeparator+imageName.elementAt(selectionIndex));
				Image image = null;
				try{image = Toolkit.getDefaultToolkit().getImage(file.toURL());}
				catch(java.net.MalformedURLException me){System.err.println("SQLException: " + me.getMessage());}
				*/
				// To know the status of the image (i.e. exist or not)
				final ImageIcon image = new ImageIcon(((pathChoice==0)?(".."+fileSeparator+"imageDictionary"):imagePath)+fileSeparator+imageName.elementAt(selectionIndex));
				imagePanel.removeAll();

				//if(image != null)
				if(image.getImageLoadStatus() != MediaTracker.ERRORED)
				{
					//final DisplayImage displayImage = new DisplayImage(image);
					//imagePanel.add(new JScrollPane(DisplayImage));

					final JLabel imageLabel = new JLabel(image);

					// To make the scrollPane work
					//imageLabel.setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
					imagePanel.add(new JScrollPane(imageLabel));
				}
				imagePanel.updateUI();

				arabicDetailsPane.setText((String)arabicDescriptionVector.elementAt(selectionIndex));
				englishDetailsPane.setText((String)englishDescriptionVector.elementAt(selectionIndex));
				arabicWordTextField.setText((String)arabicWordListVector.elementAt(selectionIndex));
				englishWordTextField.setText((String)englishWordListVector.elementAt(selectionIndex));
			}
		}
	}
}