import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

final class AddSentenceDictionarySection extends JDialog
{
	protected String fileSeparator = System.getProperty("file.separator");
	protected int sectionNumber, ageNumber;
	protected int sectionIndexTemp []= new int [25];
	protected int ageIndexTemp;
	protected JRadioButton sectionRadioButton [];
	protected JRadioButton ageRadioButton [];
	protected AddToSentenceDictionary ATSD;
	public AddSentenceDictionarySection(AddToSentenceDictionary ATSDTemp, final boolean language)
	{
		super(ATSDTemp, true);
		
		ATSD = ATSDTemp;
		int fixedCounter = 0;
		final String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(6, ".."+fileSeparator+"language"+fileSeparator+((language)?"DictionarySectionArabicFixed.txt":"DictionarySectionEnglishFixed.txt"));
		final String variable[] = MaknoonIslamicEncyclopedia.StreamConverter(2, ".."+fileSeparator+"language"+fileSeparator+((language)?"DictionarySectionArabicVariable.txt":"DictionarySectionEnglishVariable.txt"));
		
		// This variable is used to label JOptionPane in arabic
		final Object[] OptionPaneYesLabel = {fixed[fixedCounter++]};
		
		setTitle(fixed[fixedCounter++]);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		try
		{
			final Connection con = DriverManager.getConnection("jdbc:cloudscape:.."+fileSeparator+"arabicDictionaryDatabase","root","secret");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from sentenceDictionarySection");
			
			rs.next();
			sectionNumber = rs.getInt(1);
			
			sectionRadioButton = new JRadioButton [sectionNumber];
			
			stmt = con.createStatement();
			rs = stmt.executeQuery("select sectionName from sentenceDictionarySection");
			
			for(int i=0; rs.next(); i++)
				sectionRadioButton [i] = new JRadioButton(rs.getString("sectionName"));
			
			stmt = con.createStatement();
			rs = stmt.executeQuery("select count(*) from dictionaryAges");
			
			rs.next();
			ageNumber = rs.getInt(1);
			
			ageRadioButton = new JRadioButton [ageNumber];
			
			stmt = con.createStatement();
			rs = stmt.executeQuery("select age from dictionaryAges");
			
			final ButtonGroup ageRadioGroup = new ButtonGroup();
			for(int i=0; rs.next(); i++)
			{
				ageRadioButton [i] = new JRadioButton(rs.getString("age"));
				ageRadioGroup.add(ageRadioButton[i]);
			}
			
			stmt.close();
			con.close();
		}
		catch(SQLException ex){System.err.println("SQLException: " + ex.getMessage());}
		
		final JPanel dictionarySectionPanel = new JPanel(new GridLayout((sectionNumber%3)==0?sectionNumber/3:(sectionNumber/3)+1,3));
		dictionarySectionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),fixed[fixedCounter++],0,0,null,Color.red));
		add(dictionarySectionPanel, BorderLayout.CENTER);
		
		for(int i=0; i<ATSD.sectionIndex.length; i++)
			sectionIndexTemp[i] = ATSD.sectionIndex[i];
		
		ActionListener tablesButtonGroupListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				for(int i=0; i<sectionNumber; i++)
					if(ae.getSource() == sectionRadioButton[i])
					{
						if(sectionIndexTemp[i]==1)
							sectionIndexTemp[i]=0;
						else
							sectionIndexTemp[i]=1;
					}
			}
		};
		
		for(int i=0; i<sectionNumber; i++)
		{
			sectionRadioButton[i].addActionListener(tablesButtonGroupListener);
			dictionarySectionPanel.add(sectionRadioButton[i]);
			if(ATSD.sectionIndex[i]==1)
				sectionRadioButton[i].setSelected(true);
		}
		
		final JPanel dictionaryAgePanel = new JPanel(new GridLayout((ageNumber%3)==0 ? ageNumber/3 : (ageNumber/3)+1,3));
		dictionaryAgePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),fixed[fixedCounter++],0,0,null,Color.red));
		add(dictionaryAgePanel, BorderLayout.NORTH);
		
		ageIndexTemp = ATSD.ageIndex;
		tablesButtonGroupListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				for(int i=0; i<ageNumber; i++)
					if(ae.getSource() == ageRadioButton[i])
						ageIndexTemp=(i+1)*5;
			}
		};
		
		for(int i=0; i<ageNumber; i++)
		{
			ageRadioButton[i].addActionListener(tablesButtonGroupListener);
			dictionaryAgePanel.add(ageRadioButton[i]);
		}
		
		ageRadioButton[(ATSD.ageIndex/5)-1].setSelected(true);
		
		final JPanel dictionarySectionButtonPanel = new JPanel(new GridLayout(1,2));
		add(dictionarySectionButtonPanel, BorderLayout.SOUTH);
		
		final JButton OKDictionarySectionButton = new JButton (fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"ok_m.png"));
		OKDictionarySectionButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean cont = false;
				for(int i=0; i<sectionNumber; i++)
				{
					if(sectionIndexTemp[i] == 1)
					{
						cont = true;
						break;
					}
				}
				
				if(cont)
				{
					for(int i=0; i<ATSD.sectionIndex.length; i++)
						ATSD.sectionIndex[i] = sectionIndexTemp[i];
					
					ATSD.ageIndex = ageIndexTemp;
					setVisible(false);
					dispose();
				}
				else
					JOptionPane.showOptionDialog(AddSentenceDictionarySection.this, variable[0], variable[1], JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
			}
		});
		dictionarySectionButtonPanel.add(OKDictionarySectionButton);
		
		final JButton cancelDictionarySectionButton = new JButton (fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"cancel_m.png"));
		cancelDictionarySectionButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();
			}
		});
		dictionarySectionButtonPanel.add(cancelDictionarySectionButton);
		
		if(language)
			getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		// To locat the JDialog at the center of the screen
		pack();
		MaknoonIslamicEncyclopedia.centerInScreen(this);
		setVisible(true);
	}
}