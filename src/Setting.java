import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

final class Setting extends JDialog
{
	protected boolean arabicLanguage;
	protected boolean defaultLanguageSelected = false;
	protected String fileSeparator = System.getProperty("file.separator");
	protected String lineSeparator = System.getProperty("line.separator");
	protected MaknoonIslamicEncyclopedia MIE;
	public Setting(MaknoonIslamicEncyclopedia ME)
	{
		super(ME, true);
		
		MIE = ME;
		arabicLanguage = MIE.language;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		int fixedCounter=0;
		final String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(8,".."+fileSeparator+"language"+fileSeparator+((MIE.language)?"SettingArabicFixed.txt":"SettingEnglishFixed.txt"));
		
		setTitle(fixed[fixedCounter++]);
		final JPanel languageChoicePanel = new JPanel(new BorderLayout());
		languageChoicePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++],0,0,null,Color.red));
		add(languageChoicePanel, BorderLayout.NORTH);
		
		final JRadioButton arabicLanguageRadioButton = new JRadioButton(fixed[fixedCounter++]);
		final JRadioButton englishLanguageRadioButton = new JRadioButton(fixed[fixedCounter++]);
		final ActionListener languageGroupListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource() == arabicLanguageRadioButton){arabicLanguage = true;}
				if(ae.getSource() == englishLanguageRadioButton){arabicLanguage = false;}
			}
		};
		arabicLanguageRadioButton.addActionListener(languageGroupListener);
		englishLanguageRadioButton.addActionListener(languageGroupListener);
	
		final ButtonGroup languageGroup = new ButtonGroup();
		languageGroup.add(arabicLanguageRadioButton);
		languageGroup.add(englishLanguageRadioButton);
		
		final JPanel northLanguageChoicePanel = new JPanel();
		languageChoicePanel.add(northLanguageChoicePanel, BorderLayout.NORTH);
		northLanguageChoicePanel.add(arabicLanguageRadioButton);
		northLanguageChoicePanel.add(englishLanguageRadioButton);
		
		final JCheckBox languageShowStartUpCheckBox = new JCheckBox(fixed[fixedCounter++]);
		if(MaknoonIslamicEncyclopedia.autoRun) languageShowStartUpCheckBox.setEnabled(false);
		
		final JLabel languageShowStartUpButton = new JLabel(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"lightbulb_m.png"));
		languageShowStartUpCheckBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange()==ItemEvent.SELECTED)
				{
					defaultLanguageSelected=true;
					languageShowStartUpButton.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"lightbulb_on_m.png"));
				}
				else
				{
					defaultLanguageSelected = false;
					languageShowStartUpButton.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"lightbulb_m.png"));
				}
			}
		});
		
		if(MIE.language)
		{
			languageChoicePanel.add(languageShowStartUpCheckBox, BorderLayout.EAST);
			languageChoicePanel.add(languageShowStartUpButton, BorderLayout.WEST);
		}
		else
		{
			languageChoicePanel.add(languageShowStartUpCheckBox, BorderLayout.WEST);
			languageChoicePanel.add(languageShowStartUpButton, BorderLayout.EAST);
		}
		
		final JPanel closePanel = new JPanel();
		closePanel.setLayout(new BorderLayout());
		add(closePanel, BorderLayout.CENTER);
		
		final JButton helpButton = new JButton (fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"help_m.png"));
		helpButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String prefix = "file:" + System.getProperty("user.dir")+ fileSeparator+".."+fileSeparator+"userGuide"+fileSeparator+"MaknoonIslamicEncyclopedia.htm";
				new BrowserControl(prefix);
			}
		});
		
		final JPanel helpPanel= new JPanel();
		helpPanel.add(helpButton);
		closePanel.add(helpPanel, BorderLayout.EAST);
		
		final JButton cancelButton = new JButton (fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"delete_m.png"));
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();
			}
		});
		
		final JButton closeButton = new JButton (fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"preferences_m.png"));
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if(!MaknoonIslamicEncyclopedia.autoRun)
					{
						PrintWriter out = new PrintWriter(new FileWriter(".."+fileSeparator+"setting"+fileSeparator+"setting.txt",false));
						if(defaultLanguageSelected)
							out.print(arabicLanguage);
						else
							out.print("nothing");
						out.close();
					}
					else
						JOptionPane.showMessageDialog(Setting.this,"\u064A\u0631\u062C\u0649 \u0627\u0644\u062A\u0646\u0628\u0647 \u0645\u0646 \u0623\u0646 \u0647\u0630\u0647 \u0627\u0644\u0646\u0633\u062E\u0629 \u062A\u0639\u0645\u0644 \u062A\u0644\u0642\u0627\u0626\u064A\u0627 \u0645\u0646 DVD \u0648\u0644\u0630\u0644\u0643 \u0644\u0627 \u064A\u062A\u0645 \u062D\u0641\u0638 \u0627\u0644\u062A\u063A\u064A\u0631\u0627\u062A \u0628\u0634\u0643\u0644 \u062F\u0627\u0626\u0645 \u0648\u0625\u0646\u0645\u0627 \u062A\u0639\u0645\u0644 \u0641\u0642\u0637 \u0623\u062B\u0646\u0627\u0621 \u062A\u0634\u063A\u064A\u0644\u0643 \u0644\u0644\u0628\u0631\u0646\u0627\u0645\u062C \u0648\u062A\u0646\u062A\u0647\u064A \u0628\u0625\u063A\u0644\u0627\u0642\u0647.","\u062A\u0646\u0628\u064A\u0647!",JOptionPane.WARNING_MESSAGE);
				}
				catch(IOException ae){ae.printStackTrace();}
				
				/*
				 * Make it last to not shown the dialog of autorun configuration
				 * after doing the switching in the node or the language
				 */
				
				// if there is a change in the setting
				if(MIE.language != arabicLanguage)
				{
					MIE.language = arabicLanguage;
					MIE.languageSetting();
				}
				
				MIE.language = arabicLanguage;
				
				setVisible(false);
				dispose();
			}
		});
		
		final JPanel closePanelWest = new JPanel();
		closePanelWest.add(closeButton);
		closePanelWest.add(cancelButton);
		closePanel.add(closePanelWest, BorderLayout.WEST);
		
		try
		{
			final BufferedReader in = new BufferedReader(new InputStreamReader (new FileInputStream(".."+fileSeparator+"setting"+fileSeparator+"setting.txt")));
			final String defaultLanguage = in.readLine();
			in.close();
			if(defaultLanguage.equals("nothing"))
			{
				if(arabicLanguage)arabicLanguageRadioButton.setSelected(true);
				else englishLanguageRadioButton.setSelected(true);
			}
			else
			{
				if(defaultLanguage.equals("true"))
				{
					arabicLanguage = true;
					arabicLanguageRadioButton.setSelected(true);
				}
				else
				{
					arabicLanguage = false;
					englishLanguageRadioButton.setSelected(true);
				}
				languageShowStartUpCheckBox.setSelected(true);
				defaultLanguageSelected = true;
			}
		}
		catch(IOException ae){ae.printStackTrace();}
		
		// To locat the JDialog at the center of the screen
		pack();
		MaknoonIslamicEncyclopedia.centerInScreen(this);
		
		if(MIE.language)
			getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		setVisible(true);
	}
}