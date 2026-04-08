import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

final class AuthenticationSystem extends JDialog
{
	protected String fileSeparator = System.getProperty("file.separator");
	protected Container component;
	public AuthenticationSystem(boolean language, Container componentTemp, final JFrame mainFrame)
	{
		super(mainFrame, true);
		
		component = componentTemp;
		int fixedCounter = 0;
		String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(7, ".."+fileSeparator+"language"+fileSeparator+((language)?"AuthenticationSystemArabicFixed.txt":"AuthenticationSystemEnglishFixed.txt"));
		
		// This variable is used to label JOptionPane in arabic
		final Object[] OptionPaneYesLabel = {fixed[fixedCounter++]};
		
		final String dialogMessage = fixed[fixedCounter++];
		final String dialogMessageTitle = fixed[fixedCounter++];
		setTitle(fixed[fixedCounter++]);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		final JPasswordField passwordField = new JPasswordField(16);
		final JPanel choicePanel = new JPanel();
		final JPanel passwordPanel = new JPanel();
		
		passwordPanel.add(new JLabel(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"password.png"),SwingConstants.LEFT));
		passwordPanel.add(passwordField);
		
		add(passwordPanel, BorderLayout.CENTER);
		add(choicePanel, BorderLayout.SOUTH);
		
		final JButton OKButton = new JButton (fixed[fixedCounter++]);
		final JButton cancelButton = new JButton (fixed[fixedCounter++]);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
				dispose();
			}
		});
		choicePanel.add(OKButton);
		choicePanel.add(cancelButton);
		
		final ActionListener choiceListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(String.valueOf(passwordField.getPassword()).equals("maknoon"))
				{
					setVisible(false);
					dispose();
					((ArabicDictionary)component).enableControlTools();
				}
				else
				{
					JOptionPane.showOptionDialog(mainFrame, dialogMessage, dialogMessageTitle, JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE, null, OptionPaneYesLabel, OptionPaneYesLabel[0]);
					passwordField.selectAll();
				}
			}
		};
		
		OKButton.addActionListener(choiceListener);
		passwordField.addActionListener(choiceListener);
		
		if(language)
			getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		// To locat the JDialog at the center of the screen
		pack();
		MaknoonIslamicEncyclopedia.centerInScreen(this);
		/* 
		 * put the frames in the left top of the screen
		 * setLocationByPlatform(true);
		 * pack()
		 */
		setVisible(true);
	}
}