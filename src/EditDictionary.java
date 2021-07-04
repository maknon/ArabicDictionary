import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.filechooser.*;

final public class EditDictionary extends JDialog
{
	protected static String fileSeparator = System.getProperty("file.separator");
	protected static String lineSeparator = System.getProperty("line.separator");
	protected String imageName = null;
	protected String imagePath = null;
	protected String arabicSoundName = null;
	protected String arabicSoundPath = null;
	protected String englishSoundName = null;
	protected String englishSoundPath = null;
	protected JTextArea arabicDetailsTextArea, englishDetailsTextArea;
	protected JTextField arabicWordTextField, englishWordTextField;
	protected int sectionIndex []= new int [25];
	protected int ageIndex = 5;//i.e. it will select the section of the smallest age
	protected ArabicDictionary AD;
	public EditDictionary(final boolean language, final MaknoonIslamicEncyclopedia MIE, ArabicDictionary ADTemp,
		final String inputImageName, final String inputArabicDescription, final String inputEnglishDescription,
		final String inputArabicWordList, final String inputEnglishWordList, final String inputArabicSoundName,
		final String inputEnglishSoundName, final Vector arabicWord, final Vector englishWord, final String inputCategpry)
	{
		super(MIE, true);
		setLayout(new GridLayout(1,3));
		
		AD = ADTemp;
        imageName = inputImageName;
        arabicSoundName = inputArabicSoundName;
        englishSoundName = inputEnglishSoundName;
        
		int fixedCounter = 0;
		final String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(19, ".."+fileSeparator+"language"+fileSeparator+((language)?"EditDictionaryArabicFixed.txt":"EditDictionaryEnglishFixed.txt"));
		final String variable[] = MaknoonIslamicEncyclopedia.StreamConverter(15, ".."+fileSeparator+"language"+fileSeparator+((language)?"EditDictionaryArabicVariable.txt":"EditDictionaryEnglishVariable.txt"));
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle(fixed[fixedCounter++]);
		
		// Initilise the section array to cover all section by default
		try
		{
			final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
			final Statement stmt = con.createStatement();
			final ResultSet rs = stmt.executeQuery("select sectionName from dictionarySection");
			
			StringTokenizer tokens = new StringTokenizer(inputCategpry, ":");
			final String cat = tokens.nextToken();
			ageIndex = Integer.valueOf(tokens.nextToken());
			tokens = new StringTokenizer(cat, ".");
			Vector catog = new Vector();
			
			while(tokens.hasMoreTokens())
				catog.addElement(tokens.nextToken());
			
			// Dont forget to check the array size since we may have alot of .<nothing>.
			int index = 0;
			while(rs.next())
			{
				if(catog.indexOf(rs.getString("sectionName")) > -1)
					sectionIndex[index]=1;
				
				index++;
			}
			stmt.close();
			con.close();
		}
		catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());}
		
		// This variable is used to label JOptionPane in arabic
		final Object[] OptionPaneYesLabel = {fixed[fixedCounter]};
		final Object[] OptionPaneYesNoLabel = {fixed[fixedCounter++], fixed[fixedCounter++]};
		
		final JPanel arabicDetailsPanel = new JPanel(new BorderLayout());
		arabicDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		arabicDetailsTextArea = new JTextArea();
		arabicDetailsTextArea.setText(inputArabicDescription);
		arabicDetailsPanel.add(new JScrollPane(arabicDetailsTextArea));
		
		final JPanel arabicWordPanel = new JPanel(new BorderLayout());
		arabicWordPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		arabicWordTextField = new JTextField();
		arabicWordTextField.setText(inputArabicWordList);
		arabicWordPanel.add(arabicWordTextField);
		
		final JButton arabicSoundButton = new JButton(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"a_audioCataloger_m.png"));
		arabicSoundButton.setToolTipText(fixed[fixedCounter++]);
		arabicWordPanel.add(arabicSoundButton, BorderLayout.WEST);
		arabicSoundButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JFileChooser chooser = new JFileChooser();
				javax.swing.filechooser.FileFilter filter = new ExtensionFileFilter(new String[] {"wav", "WAV"}, "WAV sound files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"aiff", "AIFF"}, "AIFF image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"au", "AU"}, "AU image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"midi", "MIDI"}, "MIDI image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"rmf", "RMF"}, "RMF image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"wav", "WAV","aiff", "AIFF","au", "AU","midi", "MIDI","rmf", "RMF"}, "WAV, AIFF, AU, MIDI and RMF sound files");
				chooser.addChoosableFileFilter(filter);
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				
				final int returnVal = chooser.showOpenDialog(EditDictionary.this);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					arabicSoundName = chooser.getSelectedFile().getName();
					arabicSoundPath = chooser.getSelectedFile().getPath();
					
					if(!new File(arabicSoundPath).exists())
					{
						arabicSoundName = "null";
						arabicSoundPath = "null";
					}
				}
				else
					System.out.println("Image selection is canceled");
			}
		});
		
		final JPanel arabicPanel = new JPanel(new BorderLayout());
		arabicPanel.add(arabicDetailsPanel, BorderLayout.CENTER);
		arabicPanel.add(arabicWordPanel, BorderLayout.NORTH);
		
		final JPanel englishDetailsPanel = new JPanel(new BorderLayout());
		englishDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		englishDetailsTextArea = new JTextArea();
		englishDetailsTextArea.setText(inputEnglishDescription);
		englishDetailsPanel.add(new JScrollPane(englishDetailsTextArea));
		
		final JPanel englishWordPanel = new JPanel(new BorderLayout());
		englishWordPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		englishWordTextField = new JTextField();
		englishWordTextField.setText(inputEnglishWordList);
		englishWordPanel.add(englishWordTextField);
		
		final JButton englishSoundButton = new JButton(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"audioCataloger_m.png"));
		englishSoundButton.setToolTipText(fixed[fixedCounter++]);
		englishWordPanel.add(englishSoundButton, BorderLayout.EAST);
		englishSoundButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JFileChooser chooser = new JFileChooser();
				javax.swing.filechooser.FileFilter filter = new ExtensionFileFilter(new String[] {"wav", "WAV"}, "WAV sound files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"aiff", "AIFF"}, "AIFF image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"au", "AU"}, "AU image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"midi", "MIDI"}, "MIDI image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"rmf", "RMF"}, "RMF image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"wav", "WAV","aiff", "AIFF","au", "AU","midi", "MIDI","rmf", "RMF",}, "WAV, AIFF, AU, MIDI and RMF sound files");
				chooser.addChoosableFileFilter(filter);
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				
				final int returnVal = chooser.showOpenDialog(EditDictionary.this);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					englishSoundName = chooser.getSelectedFile().getName();
					englishSoundPath = chooser.getSelectedFile().getPath();
					
					if(!new File(englishSoundPath).exists())
					{
						englishSoundName = "null";
						englishSoundPath = "null";
					}
				}
				else
					System.out.println("Image selection is canceled");
			}
		});
		
		final JPanel englishPanel = new JPanel(new BorderLayout());
		englishPanel.add(englishDetailsPanel, BorderLayout.CENTER);
		englishPanel.add(englishWordPanel, BorderLayout.NORTH);
		
		final JPanel imageAndControlPanel = new JPanel(new BorderLayout());
		final JPanel imagePanel = new JPanel(new FlowLayout());
		imagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		final ImageIcon image = new ImageIcon(((AD.pathChoice==0)?(".."+fileSeparator+"imageDictionary"):AD.imagePath)+fileSeparator+imageName);
		if(image.getImageLoadStatus() != MediaTracker.ERRORED)
			imagePanel.add(new JScrollPane(new JLabel(image)));
		
		imageAndControlPanel.add(imagePanel, BorderLayout.CENTER);
		
		final JPanel controlPanel = new JPanel(new GridLayout(2,2));
		controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		imageAndControlPanel.add(controlPanel, BorderLayout.SOUTH);
		
		getContentPane().add(arabicPanel);
		getContentPane().add(imageAndControlPanel);
		getContentPane().add(englishPanel);
        
		final JButton browseButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"add_m.png"));
		browseButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(browseButton);
		browseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JFileChooser chooser = new JFileChooser();
				javax.swing.filechooser.FileFilter filter = new ExtensionFileFilter(new String[] {"gif", "GIF"}, "GIF image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"png", "PNG"}, "PNG image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"jpg", "JPG", "jpeg", "JPEG"}, "JPG image files");
				chooser.addChoosableFileFilter(filter);
				filter = new ExtensionFileFilter(new String[] {"gif", "GIF", "jpg", "JPG", "jpeg", "JPEG", "png", "PNG"}, "GIF, PNG and JPG image files");
				chooser.addChoosableFileFilter(filter);
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				
				final int returnVal = chooser.showOpenDialog(EditDictionary.this);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					imageName = chooser.getSelectedFile().getName();
					imagePath = chooser.getSelectedFile().getPath();
					
					final ImageIcon image = new ImageIcon(imagePath);
					if(image.getIconHeight() > 200 || image.getIconWidth() > 200)
					{
						JOptionPane.showOptionDialog(EditDictionary.this, variable[14], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
						imageName = "null";
						imagePath = "null";
					}
					else
					{
						//Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
						if(image.getImageLoadStatus() != MediaTracker.ERRORED)
						{
							imagePanel.removeAll();
							//final DisplayImage displayImage = new DisplayImage(image);
							imagePanel.add(new JScrollPane(new JLabel(image)));
							imagePanel.updateUI();
						}
						else
						{
							imageName = "null";
							imagePath = "null";
						}
					}
				}
				else
					System.out.println("Image selection is canceled");
			}
		});
		
		final JButton sectionButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"section_m.png"));
		sectionButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(sectionButton);
		sectionButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new EditDictionarySection(EditDictionary.this, language);}});
		
		final JButton OKButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"ok_m.png"));
		OKButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(OKButton);
		OKButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(arabicWordTextField.getText().equals(""))
					JOptionPane.showOptionDialog(EditDictionary.this, variable[0], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
				else
					if(englishWordTextField.getText().equals(""))
						JOptionPane.showOptionDialog(EditDictionary.this, variable[2], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
					else
					{
						boolean cont = true;
						String passedWord = arabicWordTextField.getText();
						if(!passedWord.equals(inputArabicWordList))
						{
							for(int i=0; i<arabicWord.size(); i++)
							{
								if(passedWord.equals(arabicWord.elementAt(i)))
								{
									cont = false;
									JOptionPane.showOptionDialog(EditDictionary.this, variable[12], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
									break;
								}
							}
						}
						
						if(cont)
						{
							passedWord = englishWordTextField.getText();
							if(!passedWord.equals(inputEnglishWordList))
							{
								for(int i=0; i<englishWord.size(); i++)
								{
									if(passedWord.equals(englishWord.elementAt(i)))
									{
										cont = false;
										JOptionPane.showOptionDialog(EditDictionary.this, variable[13], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
										break;
									}
								}
							}
						}
						
						if(cont)
						{
							if(arabicDetailsTextArea.getText().equals(""))
								JOptionPane.showOptionDialog(EditDictionary.this, variable[3], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
							else
								if(englishDetailsTextArea.getText().equals(""))
									JOptionPane.showOptionDialog(EditDictionary.this, variable[4], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
								else
								{
									boolean order = true;
									int choice = JOptionPane.YES_OPTION;
									
									if(imageName.equals("null") && order)
									{
										choice = JOptionPane.showOptionDialog(EditDictionary.this, variable[5], variable[6], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
										if(choice == JOptionPane.NO_OPTION) order = false;
									}
									
									if(arabicSoundName.equals("null") && order)
									{
										choice = JOptionPane.showOptionDialog(EditDictionary.this, variable[7], variable[6], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
										if(choice == JOptionPane.NO_OPTION) order = false;
									}
									
									if(englishSoundName.equals("null") && order)
										choice = JOptionPane.showOptionDialog(EditDictionary.this, variable[8], variable[6], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
									
									if(choice == JOptionPane.YES_OPTION)
									{
										choice = JOptionPane.NO_OPTION;
										if(!imageName.equals(inputImageName))
										{
											new CopyFile(imagePath, ((AD.pathChoice==0)?(".."+fileSeparator+"imageDictionary"):AD.imagePath)+fileSeparator+imageName, MIE);
											if(!inputImageName.equals("null")) choice = JOptionPane.showOptionDialog(EditDictionary.this, variable[9], variable[6], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
										}
										
										if(choice == JOptionPane.YES_OPTION)
											new DeleteFile(((AD.pathChoice==0)?(".."+fileSeparator+"soundDictionary"):AD.soundPath)+fileSeparator+inputImageName);
										
										choice = JOptionPane.NO_OPTION;
										if(!arabicSoundName.equals(inputArabicSoundName))
										{
											new CopyFile(arabicSoundPath, ((AD.pathChoice==0)?(".."+fileSeparator+"soundDictionary"):AD.soundPath)+fileSeparator+arabicSoundName, MIE);
											if(!inputArabicSoundName.equals("null")) choice = JOptionPane.showOptionDialog(EditDictionary.this, variable[10], variable[6], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
										}
										
										if(choice == JOptionPane.YES_OPTION)
											new DeleteFile(((AD.pathChoice==0)?(".."+fileSeparator+"soundDictionary"):AD.soundPath)+fileSeparator+inputArabicSoundName);
										
										choice = JOptionPane.NO_OPTION;
										if(!englishSoundName.equals(inputEnglishSoundName))
										{
											new CopyFile(englishSoundPath, ((AD.pathChoice==0)?(".."+fileSeparator+"soundDictionary"):AD.soundPath)+fileSeparator+englishSoundName, MIE);
											if(!inputEnglishSoundName.equals("null")) choice = JOptionPane.showOptionDialog(EditDictionary.this, variable[11], variable[6], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
										}
										
										if(choice == JOptionPane.YES_OPTION)
											new DeleteFile(((AD.pathChoice==0)?(".."+fileSeparator+"soundDictionary"):AD.soundPath)+fileSeparator+inputEnglishSoundName);
										
										try
										{
											final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
											final Statement stmt = con.createStatement();
											final ResultSet rs = stmt.executeQuery("select sectionName from dictionarySection");
											
											int index = 0;
											String category = "";
											while(rs.next())
											{
												if(sectionIndex[index]==1 && index!=0)
													category = category + "." + rs.getString("sectionName");
												else
													if(sectionIndex[index]==1 && index==0)
														category = rs.getString("sectionName");
												
												index++;
											}
											
											category = category + ":" + ageIndex;
											stmt.executeUpdate("UPDATE arabicDictionary SET arabicWord = '"+arabicWordTextField.getText()+"', englishWord = '"+englishWordTextField.getText()+
												"', arabicDescription = '"+arabicDetailsTextArea.getText()+"', englishDescription = '"+englishDetailsTextArea.getText()+"', imageName = '"+imageName+
												"', arabicSoundName = '"+arabicSoundName+"', englishSoundName = '"+englishSoundName+"',category = '"+category+"' WHERE arabicWord = '"+inputArabicWordList+"'");
											stmt.close();
											con.close();
											
											AD.storeDatabaseInMemory();
											setVisible(false);
											dispose();
										}
										catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());}
									}
								}
						}
					}
			}
		});
		
		final JButton cancelButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"cancel_m.png"));
		cancelButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();
			}
		});
		
		if(language)
			getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		englishDetailsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		englishDetailsTextArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		englishWordPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		englishWordTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
        setSize(850,400);
        MaknoonIslamicEncyclopedia.centerInScreen(this);
		setVisible(true);
	}
	
	public static void printSQLError(SQLException e)
	{
		while(e != null) 
		{
			System.out.println(e.toString());
			e = e.getNextException();
		}
	}
}