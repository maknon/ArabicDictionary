import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Locale;
import java.awt.event.*;

final class DefaultLanguage extends JDialog
{
	/*
	 * language = 0 ; default arabic language
	 * language = 1 ; english language
	 */
	protected boolean arabicLanguage = true;
	protected boolean defaultSelected = false;
	protected String screenCaptureFilePath;
	protected String fileSeparator = System.getProperty("file.separator");
	protected String lineSeparator = System.getProperty("line.separator");
	protected MaknoonIslamicEncyclopedia MIE;
	public DefaultLanguage(MaknoonIslamicEncyclopedia ME)
	{
		super(ME, "\u0627\u0644\u0644\u063A\u0629 \u0627\u0644\u0645\u0633\u062A\u062E\u062F\u0645\u0629 (Default Language)", true);
		
		MIE = ME;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		arabicLanguage = MIE.language;
		
		final JPanel languageChoicePanel = new JPanel();
		languageChoicePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"\u0627\u062E\u062A\u0631 \u0627\u0644\u0644\u063A\u0629 \u0627\u0644\u062A\u064A \u0633\u062A\u0633\u062A\u062E\u062F\u0645 \u0641\u064A \u0627\u0644\u0628\u0631\u0646\u0627\u0645\u062C (Choose the language to be used in the program)",0,0,null,Color.red));
		add(languageChoicePanel, BorderLayout.CENTER);
		
		final JRadioButton arabicLanguageRadioButton = new JRadioButton("\u0627\u0644\u0644\u063A\u0629 \u0627\u0644\u0639\u0631\u0628\u064A\u0629", true);
		final JRadioButton englishLanguageRadioButton = new JRadioButton("English Language");
		
		ActionListener languageGroupListener = new ActionListener() 
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
		
		languageChoicePanel.add(arabicLanguageRadioButton);
		languageChoicePanel.add(englishLanguageRadioButton);
		
		final JPanel closePanel = new JPanel();
		add(closePanel, BorderLayout.SOUTH);
		
		final JButton closeButton = new JButton ("\u0625\u063A\u0644\u0627\u0642 (Close)", new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"lightbulb_m.png"));
		closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				MIE.language = arabicLanguage;
				
				if(!MIE.autoRun)
				{
					try
					{
						final PrintWriter out = new PrintWriter(new FileWriter(".."+fileSeparator+"setting"+fileSeparator+"setting.txt",false));
						if(defaultSelected)
							out.print(arabicLanguage);
						else
							out.print("nothing");
						out.close();
					}
					catch(IOException ae){ae.printStackTrace();}
				}
				setVisible(false);
				dispose();
			}
		});
		
		final JCheckBox showStartUpCheckBox = new JCheckBox("\u0627\u062C\u0639\u0644 \u0647\u0630\u0647 \u0627\u0644\u0644\u063A\u0629 \u0647\u064A \u0627\u0644\u0631\u0626\u064A\u0633\u0629 \u062F\u0648\u0645\u0627 \u0648\u0644\u0627 \u062A\u0642\u0645 \u0628\u0633\u0624\u0627\u0644\u064A \u0645\u0631\u0629 \u0623\u062E\u0631\u0649 (Don't ask me again and make this language is the default)");
		
		// To indicate that the user cannot change the setting permanently
		if(MIE.autoRun) showStartUpCheckBox.setEnabled(false);
		
		showStartUpCheckBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange()==ItemEvent.SELECTED)
				{
					defaultSelected=true;
					closeButton.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"lightbulb_on_m.png"));
				}
				else
				{
					defaultSelected=false;
					closeButton.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"lightbulb_m.png"));
				}
			}
		});
		closePanel.add(showStartUpCheckBox);
		closePanel.add(closeButton);
		
		try
		{
			final BufferedReader in = new BufferedReader(new InputStreamReader (new FileInputStream(".."+fileSeparator+"setting"+fileSeparator+"setting.txt")));
			final String defaultLanguage = in.readLine();
			screenCaptureFilePath = in.readLine();
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
				showStartUpCheckBox.setSelected(true);
				defaultSelected = true;
			}
		}
		catch(IOException ae){ae.printStackTrace();}
		
		// To locat the JDialog at the center of the screen
		pack();
		MaknoonIslamicEncyclopedia.centerInScreen(this);
		getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		englishLanguageRadioButton.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		// To disable setAlwaysOnTop(true); for the splash
		setAlwaysOnTop(true);
		setVisible(true);
	}
}