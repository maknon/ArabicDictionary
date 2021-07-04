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

final public class AddToSentenceDictionary extends JDialog
{
	protected static String fileSeparator = System.getProperty("file.separator");
	protected static String lineSeparator = System.getProperty("line.separator");
	protected String arabicSoundName = null;
	protected String arabicSoundPath = null;
	protected String englishSoundName = null;
	protected String englishSoundPath = null;
	protected JTextArea arabicTextArea, englishTextArea;
	protected int sectionIndex []= new int [25];
	protected int ageIndex = 5;//i.e. it will select the section of the smallest age
	protected SentenceDictionary SD;
	public AddToSentenceDictionary(final boolean language, SentenceDictionary SDTemp)
	{
		super(SDTemp, true);
		
		SD = SDTemp;
		int fixedCounter = 0;
		final String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(16, ".."+fileSeparator+"language"+fileSeparator+((language)?"AddToSentenceDictionaryArabicFixed.txt":"AddToSentenceDictionaryEnglishFixed.txt"));
		final String variable[] = MaknoonIslamicEncyclopedia.StreamConverter(9, ".."+fileSeparator+"language"+fileSeparator+((language)?"AddToSentenceDictionaryArabicVariable.txt":"AddToSentenceDictionaryEnglishVariable.txt"));
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle(fixed[fixedCounter++]);
		
		// This variable is used to label JOptionPane in arabic
		final Object[] OptionPaneYesLabel = {fixed[fixedCounter]};
		final Object[] OptionPaneYesNoLabel = {fixed[fixedCounter++], fixed[fixedCounter++]};
		
		// Initilise the section array to cover all section by default
		for(int i=0; i<sectionIndex.length; i++)
			sectionIndex[i] = 1;
		
		final JPanel arabicPanel = new JPanel(new BorderLayout());
		arabicPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		arabicTextArea = new JTextArea();
		arabicPanel.add(new JScrollPane(arabicTextArea));
		arabicPanel.setPreferredSize(new Dimension(300, 200));
		
		final JButton arabicSoundButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"a_audioCataloger_m.png"));
		arabicSoundButton.setToolTipText(fixed[fixedCounter++]);
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
				filter = new ExtensionFileFilter(new String[] {"wav", "WAV","aiff", "AIFF","au", "AU","midi", "MIDI","rmf", "RMF",}, "WAV, AIFF, AU, MIDI and RMF sound files");
				chooser.addChoosableFileFilter(filter);
				chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
				
				final int returnVal = chooser.showOpenDialog(AddToSentenceDictionary.this);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					arabicSoundName = chooser.getSelectedFile().getName();
					arabicSoundPath = chooser.getSelectedFile().getPath();
					
					if(!new File(arabicSoundPath).exists())
					{
						arabicSoundName = null;
						arabicSoundPath = null;
					}
				}
				else
					System.out.println("Image selection is canceled");
			}
		});
		
		final JPanel englishPanel = new JPanel(new BorderLayout());
		englishPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		englishTextArea = new JTextArea();
		englishPanel.add(new JScrollPane(englishTextArea));
		
		final JButton englishSoundButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"audioCataloger_m.png"));
		englishSoundButton.setToolTipText(fixed[fixedCounter++]);
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
				
				final int returnVal = chooser.showOpenDialog(AddToSentenceDictionary.this);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					englishSoundName = chooser.getSelectedFile().getName();
					englishSoundPath = chooser.getSelectedFile().getPath();
					
					if(!new File(englishSoundPath).exists())
					{
						englishSoundName = null;
						englishSoundPath = null;
					}
				}
				else
					System.out.println("Image selection is canceled");
			}
		});
		
		final JPanel controlPanel = new JPanel(new GridLayout(1,5));
		controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		controlPanel.add(arabicSoundButton);
		controlPanel.add(englishSoundButton);
		
		final JPanel mainPanel = new JPanel(new GridLayout(1,2));
		mainPanel.add(arabicPanel);
		mainPanel.add(englishPanel);
		
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
        
		final JButton sectionButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"section_m.png"));
		sectionButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(sectionButton);
		sectionButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new AddSentenceDictionarySection(AddToSentenceDictionary.this, language);}});
		
		final JButton OKButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"ok_m.png"));
		OKButton.setToolTipText(fixed[fixedCounter++]);
		controlPanel.add(OKButton);
		OKButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(arabicTextArea.getText().equals(""))
					JOptionPane.showOptionDialog(AddToSentenceDictionary.this, variable[3], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
				else
					if(englishTextArea.getText().equals(""))
						JOptionPane.showOptionDialog(AddToSentenceDictionary.this, variable[4], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
					else
					{
						boolean order = true;
						int choice = JOptionPane.YES_OPTION;
						
						if(arabicSoundName == null && order)
						{
							choice = JOptionPane.showOptionDialog(AddToSentenceDictionary.this, variable[7], variable[6], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
							if(choice == JOptionPane.NO_OPTION) order = false;
						}
						
						if(englishSoundName == null && order)
							choice = JOptionPane.showOptionDialog(AddToSentenceDictionary.this, variable[8], variable[6], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, OptionPaneYesNoLabel, OptionPaneYesNoLabel[0]);
						
						if(choice == JOptionPane.YES_OPTION)
						{
							if(arabicSoundName != null)
								new CopyFile(arabicSoundPath, ((SD.pathChoice==0)?(".."+fileSeparator+"soundDictionary"):SD.soundPath)+fileSeparator+arabicSoundName, SD);
							
							if(englishSoundName != null)
								new CopyFile(englishSoundPath, ((SD.pathChoice==0)?(".."+fileSeparator+"soundDictionary"):SD.soundPath)+fileSeparator+englishSoundName, SD);
							
							try
							{
								final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase", "root","secret");
								final Statement stmt = con.createStatement();
								final ResultSet rs = stmt.executeQuery("select sectionName from sentenceDictionarySection");
								
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
								stmt.execute("insert into arabicSentenceDictionary values ('"+arabicTextArea.getText()+"','"+englishTextArea.getText()+"','"+arabicSoundName+"','"+englishSoundName+"','"+category+"')");
								stmt.close();
								con.close();
								
								SD.storeDatabaseInMemory();
								setVisible(false);
								dispose();
							}
							catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());}
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
		
		englishPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		englishTextArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		pack();
        MaknoonIslamicEncyclopedia.centerInScreen(AddToSentenceDictionary.this);
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