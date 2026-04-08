import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;

final class ArabicDictionaryPathDeterministic extends JDialog
{
	protected String fileSeparator = System.getProperty("file.separator");
	protected int pathChoice = 0;
	protected ArabicDictionary AD;
	public ArabicDictionaryPathDeterministic(final MaknoonIslamicEncyclopedia MIE, ArabicDictionary ADTemp)
	{
		super(MIE, true);
		
		/*
		 * This is done because we want to accsess AD inside classes and final ArabicDictionary will not change 
		 * the real one, it will take a copy.
		 */
		AD = ADTemp;
		
		int fixedCounter = 0;
		final String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(11, ".."+fileSeparator+"language"+fileSeparator+((AD.language)?"ArabicDictionaryPathDeterministicArabicFixed.txt":"ArabicDictionaryPathDeterministicEnglishFixed.txt"));
		
		setTitle(fixed[fixedCounter++]);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		final JPanel noticePanel = new JPanel(new BorderLayout());
		noticePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),fixed[fixedCounter++],0,0,null,Color.red));
		add(noticePanel, BorderLayout.NORTH);
		noticePanel.setPreferredSize(new Dimension(0, 100));
		
		final JTextArea noticeTextArea = new JTextArea();
		noticeTextArea.setLineWrap(true);
		noticeTextArea.setWrapStyleWord(true);
		noticeTextArea.setEditable(false);
		noticePanel.add(new JScrollPane(noticeTextArea),BorderLayout.CENTER);
		
		try
		{
			Reader in;
			if(AD.language) in = new BufferedReader(new InputStreamReader(new FileInputStream(".."+fileSeparator+"language"+fileSeparator+"ArabicDictionaryArabicNoticeTextArea.txt"), "UTF8"));
			else in = new BufferedReader(new InputStreamReader(new FileInputStream(".."+fileSeparator+"language"+fileSeparator+"ArabicDictionaryEnglishNoticeTextArea.txt"), "UTF8"));
			
			final StringBuffer buffer = new StringBuffer();
			int ch;
			int counter=0;
			while((ch = in.read()) > -1)
			{
				if(counter>0)
					buffer.append((char)ch);
				else
					counter++;
			}
			noticeTextArea.setText(buffer.toString());
			in.close();
		}
		catch(FileNotFoundException e){JOptionPane.showMessageDialog(MIE,"! \u0645\u0644\u0641\u0627\u062A \u0627\u0644\u062A\u0631\u062C\u0645\u0629 \u0645\u0641\u0642\u0648\u062F\u0629","! \u062E\u0637\u0623",JOptionPane.ERROR_MESSAGE);}
		catch (IOException e){e.printStackTrace();}
		
		final JPanel pathDeterministicPanel = new JPanel(new BorderLayout());
		pathDeterministicPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),fixed[fixedCounter++],0,0,null,Color.red));
		add(pathDeterministicPanel, BorderLayout.CENTER);
		
		final JPanel browserPathPanel = new JPanel(new GridLayout(2,1));
		if(AD.language)pathDeterministicPanel.add(browserPathPanel, BorderLayout.EAST);
		else pathDeterministicPanel.add(browserPathPanel, BorderLayout.WEST);
		
		final JButton browserImagePathButton = new JButton(fixed[fixedCounter++]);
		browserImagePathButton.setToolTipText(fixed[fixedCounter++]);
		
		final JButton browserSoundPathButton = new JButton(fixed[fixedCounter++]);
		browserSoundPathButton.setToolTipText(fixed[fixedCounter++]);
		
		browserPathPanel.add(browserImagePathButton);
		browserPathPanel.add(browserSoundPathButton);
		
		final JPanel textFieldPathPanel = new JPanel(new GridLayout(2,1));
		final JTextField browserImagePathTextField = new JTextField();
		final JTextField browserSoundPathTextField = new JTextField();
		pathDeterministicPanel.add(textFieldPathPanel, BorderLayout.CENTER);
		
		textFieldPathPanel.add(browserImagePathTextField);
		textFieldPathPanel.add(browserSoundPathTextField);
		
		ActionListener browserListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//fc.setApproveButtonText("Open");
				
				int returnVal = fc.showOpenDialog(MIE);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{ 
					File file = fc.getSelectedFile();
					String PathName = String.valueOf(file);
				
					if(e.getSource() == browserImagePathButton)
						browserImagePathTextField.setText(PathName);
				
					if(e.getSource() == browserSoundPathButton)
						browserSoundPathTextField.setText(PathName);
				}
				else{System.out.println("\nAttachment cancelled by user.");}
			}
		};
		browserImagePathButton.addActionListener(browserListener);
		browserSoundPathButton.addActionListener(browserListener);
		
		final JRadioButton defaultPathRadioButton = new JRadioButton(fixed[fixedCounter++]);
		final JRadioButton soundImagePathRadioButton = new JRadioButton(fixed[fixedCounter++]);
    
		final ActionListener pathListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(ae.getSource() == defaultPathRadioButton)
				{
					pathChoice=0;
					browserImagePathTextField.setEnabled(false);
					browserImagePathButton.setEnabled(false);
					browserSoundPathTextField.setEnabled(false);
					browserSoundPathButton.setEnabled(false);
				}
			
				if(ae.getSource() == soundImagePathRadioButton)
				{
					pathChoice=1;
					browserImagePathTextField.setEnabled(true);
					browserImagePathButton.setEnabled(true);
					browserSoundPathTextField.setEnabled(true);
					browserSoundPathButton.setEnabled(true);
				}
			}
		};
		defaultPathRadioButton.addActionListener(pathListener);
		soundImagePathRadioButton.addActionListener(pathListener);
		
		final ButtonGroup pathGroup = new ButtonGroup();
		pathGroup.add(defaultPathRadioButton);
		pathGroup.add(soundImagePathRadioButton);
		
		try
		{
			final BufferedReader in = new BufferedReader(new InputStreamReader (new FileInputStream(".."+fileSeparator+"setting"+fileSeparator+"arabicDictionaryPath.txt")));
			
			final String imagePathTemp = in.readLine();
			if(imagePathTemp.equals("null")) browserImagePathTextField.setText(System.getProperty("user.home")+fileSeparator+"Maknoon Arabic Dictionary image files");
			else browserImagePathTextField.setText(imagePathTemp);
			
			final String soundPathTemp = in.readLine();
			if(soundPathTemp.equals("null")) browserSoundPathTextField.setText(System.getProperty("user.home")+fileSeparator+"Maknoon Arabic Dictionary sound files");
			else browserSoundPathTextField.setText(soundPathTemp);
			
			pathChoice = Integer.valueOf(in.readLine());
			in.close();
			
			if(pathChoice==0)
			{
				browserSoundPathTextField.setEnabled(false);
				browserSoundPathButton.setEnabled(false);
				browserImagePathTextField.setEnabled(false);
				browserImagePathButton.setEnabled(false);
				defaultPathRadioButton.setSelected(true);
			}
			else // i.e. pathChoice==1
				soundImagePathRadioButton.setSelected(true);
		}
		catch(IOException ae){ae.printStackTrace();}
	
		final JPanel pathPanel = new JPanel();
		pathDeterministicPanel.add(pathPanel, BorderLayout.NORTH);
		
		pathPanel.add(defaultPathRadioButton);
		pathPanel.add(soundImagePathRadioButton);
		
		final JPanel choicePanel = new JPanel(new GridLayout(1,2));
		add(choicePanel, BorderLayout.SOUTH);
	
		final JButton okButton = new JButton(fixed[fixedCounter]);
		okButton.setToolTipText(fixed[fixedCounter++]);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(pathChoice==1)
				{
					AD.soundPath=browserSoundPathTextField.getText().trim();
					AD.imagePath=browserImagePathTextField.getText().trim();
				}
				
				try
				{
					PrintWriter out = new PrintWriter(new FileWriter(".."+fileSeparator+"setting"+fileSeparator+"arabicDictionaryPath.txt",false));
					out.println(AD.imagePath);
					out.println(AD.soundPath);
					out.print(pathChoice);
					out.close();
				}
				catch(IOException ae){ae.printStackTrace();}
				
				AD.pathChoice=pathChoice;
				setVisible(false);
				dispose();
			}
		});
		choicePanel.add(okButton);
	
		final JButton cancelButton = new JButton(fixed[fixedCounter]);
		cancelButton.setToolTipText(fixed[fixedCounter++]);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setVisible(false);
				dispose();
			}
		});
		choicePanel.add(cancelButton);
		
		if(AD.language)
			getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		// Put at the last to have LEFT TO RIGHT Orientation befoer Container Orientation since it will override all Orientations previously
		browserImagePathTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		browserSoundPathTextField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		// To locat the JDialog at the center of the screen
		pack();
		MaknoonIslamicEncyclopedia.centerInScreen(this);
		setVisible(true);
	}
}