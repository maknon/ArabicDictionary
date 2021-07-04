import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.plaf.FontUIResource;

public class MaknoonIslamicEncyclopedia extends JFrame implements ActionListener
{
	protected JPopupMenu popup;
	protected static boolean language = true;// i.e. default is arabic
	protected static String fileSeparator = System.getProperty("file.separator");
	protected static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	protected static boolean windows = BrowserControl.isWindowsPlatform();
	protected static boolean windowsXP = BrowserControl.isWindowsXPPlatform();
	protected static boolean autoRun;
	protected static JWindow splash;
	
	public MaknoonIslamicEncyclopedia()
	{
		super("Arabic Dictionary");
		
		Font defaultFont = new Font( "Tahoma", Font.PLAIN, 11 );
		UIManager.put( "CheckBox.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "Menu.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "MenuBar.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "MenuItem.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "ToolTip.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "CheckBoxMenuItem.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "RadioButtonMenuItem.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "OptionPane.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "PopupMenu.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "TabbedPane.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "ColorChooser.font", new FontUIResource ( defaultFont ) );
		
		defaultFont = new Font( "Tahoma", Font.PLAIN, 12 );
		UIManager.put( "List.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "Tree.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "Label.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "Button.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "TextField.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "Table.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "ComboBox.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "ToolBar.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "RadioButton.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "TextArea.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "Panel.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "ToggleButton.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "TextPane.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "TitledBorder.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "TableHeader.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "Text.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "EditorPane.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "ScrollPane.font", new FontUIResource ( defaultFont ) );
		UIManager.put( "PasswordField.font", new FontUIResource ( defaultFont ) );
		
		splash = new JWindow(MaknoonIslamicEncyclopedia.this);
		final JLabel splashBackground = new JLabel(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"MaknoonAudioDictionary.png"));
		splash.getContentPane().add(splashBackground);
		
		splash.pack();
		centerInScreen(splash);
		splash.setAlwaysOnTop(true);
		splash.setVisible(true);
		
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader (new FileInputStream(".."+fileSeparator+"setting"+fileSeparator+"autoRun.txt")));
			if(in.readLine().equals("Yes")) autoRun = true;
			else autoRun = false;
			in.close();
			
			// Determine the default language
			in = new BufferedReader(new InputStreamReader (new FileInputStream(".."+fileSeparator+"setting"+fileSeparator+"setting.txt")));
			final String defaultLanguage = in.readLine();
			
			if(defaultLanguage.equals("nothing"))
			{
				// This is for Linux since ordering setAlwaysOnTop(true) is unspecified.
				if(!windows) splash.toBack();
				//loadingSplash.setAlwaysOnTop(false);
				
				new DefaultLanguage(MaknoonIslamicEncyclopedia.this);
				
				if(!windows) splash.toFront();
				//loadingSplash.setAlwaysOnTop(true);
			}
			else
			{
				if(defaultLanguage.equals("true")) language = true;
				else language = false;
			}
		}
		catch(IOException ae){ae.printStackTrace();}
		
		setBounds(0,0,screenSize.width,screenSize.height-25);
		setIconImage(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"icon.png").getImage());
		setJMenuBar(createMenuBar());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				final Object [] options = {variable[1], variable[2]};
				final int choice = JOptionPane.showOptionDialog(MaknoonIslamicEncyclopedia.this, variable[0], variable[3], JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if(choice == JOptionPane.YES_OPTION){System.exit(0);}
			}
		});
		setContentPane(new ArabicDictionary(language, MaknoonIslamicEncyclopedia.this));
		
		if(language)
			getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		if(windows)
		{
			try
			{
				/*
				 * To indicates if the platform support MAXIMIZED_BOTH like windows or not like Linux
				 * This will not work check please !
				 *
				 * if(Toolkit.isFrameStateSupported(this.MAXIMIZED_BOTH))
				 */
				 setExtendedState(MAXIMIZED_BOTH);
			}
			catch(HeadlessException he){System.err.println("HeadlessException: "+he);}
		}
		setVisible(true);
		splash.setVisible(false);
		splash.dispose();
	}
	
	protected String[] variable;
	protected JMenuBar createMenuBar()
	{
		String[] fixed;
		int fixedCounter = 0;
		
		if(language) fixed = StreamConverter(12, ".."+fileSeparator+"language"+fileSeparator+"createMenuBarArabicFixed.txt");
		else fixed = StreamConverter(12, ".."+fileSeparator+"language"+fileSeparator+"createMenuBarEnglishFixed.txt");
		
		if(language) variable = StreamConverter(4, ".."+fileSeparator+"language"+fileSeparator+"createMenuBarArabicVariable.txt");
		else variable = StreamConverter(4, ".."+fileSeparator+"language"+fileSeparator+"createMenuBarEnglishVariable.txt");
		
		final JMenuBar menuBar = new JMenuBar();
		final JMenu mFile = new JMenu(fixed[fixedCounter++]);
		
		JMenuItem item = new JMenuItem(fixed[fixedCounter++],KeyEvent.VK_S);
		item.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"preferences_m.png"));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		item.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new Setting(MaknoonIslamicEncyclopedia.this);}});
		mFile.add(item);
		
		item = new JMenuItem(fixed[fixedCounter++],KeyEvent.VK_X);
		item.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"exit_m.png"));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object[] options = {variable[1],variable[2]};
				int choice = JOptionPane.showOptionDialog(MaknoonIslamicEncyclopedia.this,variable[0], variable[3], JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if(choice == JOptionPane.YES_OPTION){System.exit(0);}
			}
		});
		mFile.addSeparator();
		mFile.add(item);
		menuBar.add(mFile);
		
		final JMenu mLanguage = new JMenu(fixed[fixedCounter++]);
		final JCheckBoxMenuItem arabic = new JCheckBoxMenuItem(fixed[fixedCounter++]);
		final JCheckBoxMenuItem english = new JCheckBoxMenuItem(fixed[fixedCounter++]);
		arabic.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(arabic.getState())
				{
					if(!language)/* Update version 1.3 */
					{
						language=true;
						languageSetting();
					}
				}
			}
		});
		mLanguage.add(arabic);
		
		if(language)arabic.setState(true);
		else english.setState(true);
		english.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(english.getState())
				{
					if(language)/* Update version 1.3 */
					{
						language=false;
						languageSetting();
					}
				}
			}
		});
		menuBar.add(mLanguage);
		mLanguage.add(english);
		
		final JMenu mHelp = new JMenu(fixed[fixedCounter++]);
		menuBar.add(mHelp);
		
		item = new JMenuItem(fixed[fixedCounter++],KeyEvent.VK_H);
		item.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"help_m.png"));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,InputEvent.CTRL_MASK));
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String prefix = "file:" + System.getProperty("user.dir")+ fileSeparator +".."+fileSeparator+"UserGuide"+fileSeparator+"MaknoonIslamicEncyclopedia.htm";
				new BrowserControl(prefix);
			}
		});
		mHelp.add(item);
		
		item = new JMenuItem(fixed[fixedCounter++],KeyEvent.VK_P);
		item.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"site_m.png"));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_MASK));
		item.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){new BrowserControl("http://www.maknoon.com/");}});
		mHelp.add(item);
		
		item = new JMenuItem(fixed[fixedCounter++],KeyEvent.VK_T);
		item.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"contact_m.png"));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,InputEvent.CTRL_MASK));
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(windows)
				{
					try{Runtime.getRuntime().exec("cmd.exe /c start mailto:webmaster@maknoon.com");}
					catch(Exception ae){ae.printStackTrace();}
				}
				else
				{
					try{Runtime.getRuntime().exec("kmail webmaster@maknoon.com");}
					catch(Exception ae){ae.printStackTrace();}
				}
			}
		});
		mHelp.add(item);
		mHelp.addSeparator();
		
		item = new JMenuItem(fixed[fixedCounter++],KeyEvent.VK_A);
		item.setIcon(new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"about_m.png"));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK));
		
		ActionListener lst = new ActionListener(){public void actionPerformed(ActionEvent e){new About(MaknoonIslamicEncyclopedia.this);}};
		item.addActionListener(lst);
		mHelp.add(item);
		menuBar.add(mHelp);
		
		//Create the popup menu.
		popup = new JPopupMenu();
		final JMenuItem popupMenuItem = new JMenuItem(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"lifebelt_m.png"));
		if(language)popupMenuItem.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		popupMenuItem.addActionListener(this);
		popup.add(popupMenuItem);
		
		// Add listener to components that can bring up popup menus.
		final MouseListener popupListener = new PopupListener();
		addMouseListener(popupListener);
		
		if(language)menuBar.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		else menuBar.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		return menuBar;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		final JMenuItem source = (JMenuItem)(e.getSource());
		new BrowserControl("file:" + System.getProperty("user.dir")+ fileSeparator +".."+fileSeparator+"UserGuide"+fileSeparator+"MaknoonIslamicEncyclopedia.htm");
	}
	
	final class PopupListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e){maybeShowPopup(e);}
		public void mouseReleased(MouseEvent e){maybeShowPopup(e);}
		private void maybeShowPopup(MouseEvent e){if(e.isPopupTrigger()){popup.show(e.getComponent(),e.getX(), e.getY());}}
	}
	
	protected void languageSetting()
	{
		setJMenuBar(createMenuBar());
		setContentPane(new ArabicDictionary(language, MaknoonIslamicEncyclopedia.this));
		SwingUtilities.updateComponentTreeUI(MaknoonIslamicEncyclopedia.this);
	}
	
	/*
	 * Function to read arabic translation files.
	 */
	public static String [] StreamConverter(int arraySize, String filePath)
	{
		final String[] array = new String [arraySize];
		StringBuffer buffer = new StringBuffer();
		
		try
		{
			final FileInputStream fis = new FileInputStream(filePath);
			final InputStreamReader isr = new InputStreamReader(fis, "UTF8");
			final Reader in = new BufferedReader(isr);
			int ch;
			int counter=0;
			boolean emptyFile = true;
			while ((ch = in.read()) > -1)
			{
				emptyFile = false;
				if('\n'==(char)ch)
				{
					array[counter]=buffer.toString();
					
					/*
					 * Update version 1.1
					 *
					 * For the strange first & last charactors for the first line 
					 * and the last charactor for the rest of lines
					 */
					if(counter == 0)
						array[counter]=array[counter].substring(1,array[counter].length()-1);
					else
						array[counter]=array[counter].substring(0,array[counter].length()-1);
					
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
					array[counter] = buffer.toString().substring(1,buffer.toString().length());
				else
					array[counter] = buffer.toString();
			}
			
			in.close();
			return array;
		}
		catch(FileNotFoundException e)
		{
			/*
			 * Update version 1.1
			 */
			// To see the Warning frame above the splash screen, this is only when starting the program
			if(!windows) splash.toBack();
			splash.setAlwaysOnTop(false);
			
			/*
			 * Update version 1.3
			 */
			if(language)
			{
				final Object[] options = {"\u062E\u0631\u0648\u062C", "\u0645\u062A\u0627\u0628\u0639\u0629"};
				final int choice = JOptionPane.showOptionDialog(null,"\u0623\u062D\u062F \u0645\u0644\u0641\u0627\u062A \u0627\u0644\u062A\u0631\u062C\u0645\u0629 \u0645\u0641\u0642\u0648\u062F\u060C \u0644\u0625\u0635\u0644\u0627\u062D \u0627\u0644\u0639\u0637\u0644 \u0642\u0645 \u0628\u0625\u0639\u0627\u062F\u0629 \u062A\u0646\u0635\u064A\u0628 \u0627\u0644\u0628\u0631\u0646\u0627\u0645\u062C.", "\u062E\u0637\u0623",JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
				if(choice == JOptionPane.YES_OPTION){System.exit(0);}
			}
			else
			{
				final Object[] options = {"Exit", "Continue"};
				final int choice = JOptionPane.showOptionDialog(null,"One of the language files is not found, please re-install the program again.", "Error!",JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
				if(choice == JOptionPane.YES_OPTION){System.exit(0);}
			}
			
			// To return back the splash screen to the front of all running programs
			if(!windows) splash.toFront();
			splash.setAlwaysOnTop(true);
			return null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
    
    /*
     * Making the parameter is Component to cast all types of window 
     * e.g. JWindow, JFrame, JDialog, ... to work with all of them
     */
    public static void centerInScreen(Component component)
	{
	    final Dimension dim = component.getToolkit().getScreenSize();
	    final Rectangle bounds = component.getBounds();
	    //System.out.println("dim.width: "+dim.width+" bounds.width: "+bounds.width+" dim.height: "+dim.height+" bounds.height: "+bounds.height);
	    component.setLocation((dim.width - bounds.width) / 2, (dim.height - bounds.height) / 2);
	    component.requestFocus();
	}
	
	public static void main(String[] args)
	{
		JComponent.setDefaultLocale(new Locale("ar"));
		new MaknoonIslamicEncyclopedia();
	}
}