import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.applet.*;
import java.util.*;
import java.awt.image.*;
import javax.swing.event.*;
import javax.swing.plaf.FontUIResource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

final public class ArabicLearning extends JDialog
{
	protected static String fileSeparator = System.getProperty("file.separator");
	protected static String lineSeparator = System.getProperty("line.separator");
	protected static boolean language = true;// i.e. default is arabic
	protected JList wordList, recommendList;
	protected DrawArabicWord drawArabicWord;
	protected Vector wordListVector = new Vector();
	protected Vector recommendListVector = new Vector();
	protected Object [] OptionPaneButtonLabel;
	protected String [] arabicLettersString = 
		{
			"\u0623", "\u0628", "\u062A", "\u062B", "\u062C", "\u062D", "\u062E",
			"\u062F", "\u0630", "\u0631", "\u0632", "\u0633", "\u0634", "\u0635", "\u0636", "\u0637", "\u0638", "\u0639",
			"\u063A", "\u0641", "\u0642", "\u0643", "\u0644", "\u0645", "\u0646", "\u0647", "\u0648", "\u064A"
		};
	
	protected char[] arabicLettersChar = 
		{
			'\u0649'/*ì*/, '\u0626'/*Æ*/, '\u0621'/*Á*/, '\u0624'/*Ä*/, '\u0622'/*Â*/, '\u0627'/*Ç*/, '\u0623', '\u0628', '\u062A', '\u062B', '\u062C', '\u062D', '\u062E',
			'\u062F', '\u0630', '\u0631', '\u0632', '\u0633', '\u0634', '\u0635', '\u0636', '\u0637', '\u0638', '\u0639',
			'\u063A', '\u0641', '\u0642', '\u0643', '\u0644', '\u0645', '\u0646', '\u0647', '\u0648', '\u064A'
		};
	
	protected char[] arabicShapChar = { '\u064E', '\u064B', '\u064F', '\u064C', '\u0650', '\u064D', '\u0652' };
	
	protected JPanel displayPanel;
	protected JLayeredPane drawPanel;
	protected Font defaultFont;
	protected Vector arabicWords;
	public ArabicLearning(boolean lang, MaknoonIslamicEncyclopedia MIE, Vector inputArabicWords)
	{
		super(MIE, true);
		
		arabicWords = inputArabicWords;
		if(lang) language = true;
		else language = false;
		
		int fixedCounter = 0;
		final String fixed[] = MaknoonIslamicEncyclopedia.StreamConverter(14, ".."+fileSeparator+"language"+fileSeparator+((language)?"ArabicLearningArabicFixed.txt":"ArabicLearningEnglishFixed.txt"));
		
		setTitle(fixed[fixedCounter++]);
		
		// This variable is used to label JOptionPane in arabic
		final Object[] option = {fixed[fixedCounter++]};
		OptionPaneButtonLabel = option;
		
		final JButton displayButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"screenshot_m.png"));
		displayButton.setToolTipText(fixed[fixedCounter++]);
		displayButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				wordListVector.removeAllElements();
				recommendList.setModel(new DefaultListModel());
				DefaultListModel listModel = new DefaultListModel();
				for(int i = 0; i<arabicWords.size(); i++)					
				{
					listModel.addElement(arabicWords.elementAt(i));
					wordListVector.addElement(arabicWords.elementAt(i));
				}
				wordList.setModel(listModel);
			}
		});
		
		final JButton colourButton = new JButton(fixed[fixedCounter++], new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"colour_m.png"));
		colourButton.setToolTipText(fixed[fixedCounter++]);
		colourButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Color newColor = JColorChooser.showDialog(ArabicLearning.this, "Choose Text Color",drawArabicWord.getColour());
				if(newColor!=null) drawArabicWord.setColour(newColor);
			}
		});
		
		final JButton fontButton = new JButton(fixed[fixedCounter++],new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"font_m.png"));
		fontButton.setToolTipText(fixed[fixedCounter++]);
		fontButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final JPanel fontPanel = new JPanel();
				final JLabel fontLabel = new JLabel("\u0627\u0644\u0633\u0644\u0627\u0645 \u0639\u0644\u064A\u0643\u0645 \u0648\u0631\u062D\u0645\u0629 \u0627\u0644\u0644\u0647 \u0648\u0628\u0631\u0643\u0627\u062A\u0647");
				
				final String[] fontItems = { "Traditional arabic", "Times New Roman"};
				
				final JPanel fontItemsPanel = new JPanel();
				final JComboBox fontComboBox = new JComboBox(fontItems);
				fontItemsPanel.add(fontComboBox);
				
				String fontName = drawArabicWord.getFont().getFontName();
				fontLabel.setFont(new Font(fontName, Font.PLAIN, 20));
				for(int i=0; i<fontItems.length; i++)
				{
					if(fontName.equals(fontItems[i]))
					{
						fontComboBox.setSelectedIndex(i);
						break;
					}
				}
				fontPanel.add(fontLabel);
				
				DefaultListCellRenderer renderer = new DefaultListCellRenderer();
				renderer.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				fontComboBox.setRenderer(renderer);
				fontComboBox.addActionListener(new ActionListener() 
				{
					public void actionPerformed(ActionEvent e) 
					{
						int index = fontComboBox.getSelectedIndex();
						drawArabicWord.setFont(new Font(fontItems[index], Font.PLAIN, 150));
						fontLabel.setFont(new Font(fontItems[index], Font.PLAIN, 20));
					}
				});
				
				final JDialog fontFrame = new JDialog(ArabicLearning.this, "Fonts", true);
		        fontFrame.getContentPane().add(fontItemsPanel, BorderLayout.NORTH);
		        fontFrame.getContentPane().add(fontPanel, BorderLayout.CENTER);
		        
		        final JButton okButton = new JButton("Done");
		        okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						fontFrame.setVisible(false);
						fontFrame.dispose();
					}
				});
		        fontItemsPanel.add(okButton);
		        
		        fontFrame.pack();
		        MaknoonIslamicEncyclopedia.centerInScreen(fontFrame);
        		fontFrame.setVisible(true);
			}
		});
		
		final JPanel wordPanel = new JPanel(new BorderLayout());
		wordPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		wordList = new JList();
		wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		wordPanel.add(new JScrollPane(wordList),BorderLayout.CENTER);
		wordPanel.setPreferredSize(new Dimension(180, 0));
		
		ListSelectionModel listSelectionModel = wordList.getSelectionModel();
		listSelectionModel.addListSelectionListener(new ListSelectionHandler());
		
		final JPanel recommendPanel = new JPanel(new BorderLayout());
		recommendPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		recommendList = new JList();
		recommendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		recommendPanel.add(new JScrollPane(recommendList),BorderLayout.CENTER);
		recommendPanel.setPreferredSize(new Dimension(180, 0));
		
		listSelectionModel = recommendList.getSelectionModel();
		listSelectionModel.addListSelectionListener(new RecommendListSelectionHandler());
		
		final JPanel letterPanel = new JPanel(new BorderLayout());
		letterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		
		// This class is used to sooth the fonts
		//Wrapper.wrap();
		
		final JList letterList = new JList(arabicLettersString);
		letterList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		letterList.setVisibleRowCount(2);
		letterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		defaultFont = new Font("Traditional arabic", Font.PLAIN, 27);
		
		letterList.setFont(defaultFont);
		letterPanel.add(new JScrollPane(letterList),BorderLayout.CENTER);
		letterPanel.setPreferredSize(new Dimension(408, 117));
		
		listSelectionModel = letterList.getSelectionModel();
		listSelectionModel.addListSelectionListener(new LetterListSelectionHandler());
		
		final JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(recommendPanel, BorderLayout.CENTER);
		listPanel.add(letterPanel, BorderLayout.WEST);
		
		final JPanel letterAndControlPanel = new JPanel(new BorderLayout());
		letterAndControlPanel.add(listPanel, BorderLayout.EAST);
		
		final JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(3,1));
		controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		letterAndControlPanel.add(controlPanel, BorderLayout.CENTER);
		
		controlPanel.add(displayButton);
		controlPanel.add(fontButton);
		controlPanel.add(colourButton);
		
		displayPanel = new JPanel(new BorderLayout());
		displayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		displayPanel.setPreferredSize(new Dimension(500, 260));
		
		drawArabicWord = new DrawArabicWord();
		drawArabicWord.init();
		displayPanel.add(drawArabicWord);
		
		final ImageIcon icon = new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"drawLabel_lifted.png");
		final ImageIcon pressedIcon = new ImageIcon(".."+fileSeparator+"images"+fileSeparator+"drawLabel.png");
		
		final JLabel cursorLabel = new JLabel(icon);
		cursorLabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		cursorLabel.setVisible(false);
		cursorLabel.setOpaque(false);
		
		Image image = null;
		try{image = Toolkit.getDefaultToolkit().getImage(new File("null.png").toURL());}
		catch(java.net.MalformedURLException me){System.err.println("SQLException: " + me.getMessage());}
		
	    drawPanel = new JLayeredPane();
		drawPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), fixed[fixedCounter++], 0, 0, null, Color.red));
		drawPanel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(40,40), "salam alicom"));
		drawPanel.setPreferredSize(new Dimension(0, 240));
	    drawPanel.add(cursorLabel, new Integer(5), JLayeredPane.DRAG_LAYER);
	    drawPanel.setOpaque(false);
	    drawPanel.addMouseMotionListener(new MouseMotionAdapter()
		{
			//Graphics g = null;
			final int XFUDGE = 2;
			final int YFUDGE = icon.getIconHeight()/2 +8;
			public void mouseDragged(MouseEvent e)
			{
				//g = getGraphics();
				//g.fillRect(e.getX(), e.getY(), 4, 4);
				cursorLabel.setLocation(e.getX()-XFUDGE, e.getY()-YFUDGE);
			}
			public void mouseMoved(MouseEvent e){cursorLabel.setLocation(e.getX()-XFUDGE, e.getY()-YFUDGE);}
		});
		
		drawPanel.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e){}
			public void mouseExited(MouseEvent e){cursorLabel.setVisible(false);}
			public void mouseEntered(MouseEvent e){cursorLabel.setVisible(true);}
			public void mouseReleased(MouseEvent e){cursorLabel.setIcon(icon);}
			public void mousePressed(MouseEvent e){cursorLabel.setIcon(pressedIcon);}
		});
		
		add(letterAndControlPanel, BorderLayout.NORTH);
		add(displayPanel, BorderLayout.CENTER);
		add(wordPanel, BorderLayout.EAST);
		add(drawPanel, BorderLayout.SOUTH);
		
		getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(getContentPane().getLocale()));
		
		pack();
		MaknoonIslamicEncyclopedia.centerInScreen(this);
		setVisible(true);
	}
	
	public void printSQLError(SQLException e)
	{
		while(e != null) 
		{
			System.out.println(e.toString());
			e = e.getNextException();
		}
	}
	
	protected int selectionIndex;
	final class ListSelectionHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			selectionIndex = lsm.getMaxSelectionIndex();
			
			if(selectionIndex!=-1)
				drawArabicWord.setWord((String)wordListVector.elementAt(selectionIndex));
		}
	}
	
	final class RecommendListSelectionHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			selectionIndex = lsm.getMaxSelectionIndex();
			
			if(selectionIndex!=-1)
				drawArabicWord.setWord((String)recommendListVector.elementAt(selectionIndex));
		}
	}
	
	protected boolean firstFound;
	protected boolean middleFound;
	protected boolean lastFound;
	final class LetterListSelectionHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			final ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			selectionIndex = lsm.getMaxSelectionIndex();
			
			// To avoid the first 5 charactors which are the first letter
			selectionIndex = selectionIndex+6;
			
			if(selectionIndex!=0)
			{
				wordListVector.removeAllElements();
				recommendListVector.removeAllElements();
				
				final DefaultListModel listModel = new DefaultListModel();
				final DefaultListModel recommendListModel = new DefaultListModel();
				
				boolean found, cont;
				String word = null;
				
				firstFound = false;
				middleFound = false;
				lastFound = false;
				
				for(int q = 0; q<arabicWords.size(); q++)
				{
					found = false;
					word = (String)arabicWords.elementAt(q);
					for(int i=0; i<word.length(); i++)
					{
						found = false;
						for(int j=0; j<=selectionIndex; j++)
						{
							if(arabicLettersChar[j]==word.charAt(i))
							{
								found = true;
								break;
							}
						}
						
						if(!found)
						{
							// Check for the shap charactors
							for(int k=0; k<arabicShapChar.length; k++)
							{
								if(arabicShapChar[k]==word.charAt(i))
								{
									found = true;
									break;
								}
							}
							
							if(!found)
							{
								if(selectionIndex>=8)
									if(word.charAt(i)=='\u0629')//É
										found = true;
								if(!found)break;
							}
						}
					}
					
					if(found)
					{
						listModel.addElement(word);
						wordListVector.addElement(word);
						
						// This variable to not repeat the word again
						cont = true;
						if(!firstFound)
						{
							if(word.charAt(0)==arabicLettersChar[selectionIndex])
							{
								firstFound = true;
								recommendListModel.addElement(word);
								recommendListVector.addElement(word);
								cont = false;
							}
						}
						
						if(!middleFound && cont)
						{
							int charIndex=word.indexOf(arabicLettersChar[selectionIndex]);
							if(charIndex>0 && charIndex<word.length())
							{
								middleFound = true;
								recommendListModel.addElement(word);
								recommendListVector.addElement(word);
								cont = false;
							}
						}
						
						if(!lastFound && cont)
						{
							if(word.endsWith(String.valueOf(arabicLettersChar[selectionIndex])))
							{
								lastFound = true;
								recommendListModel.addElement(word);
								recommendListVector.addElement(word);
								cont = false;
							}
						}
					}
				}
				
				wordList.setModel(listModel);
				recommendList.setModel(recommendListModel);
			}
		}
	}
	
	final class MainCanvas extends Canvas
	{
		private String _strText = null;
		private Color colour = Color.blue;
		private Font font = null;
		
		public MainCanvas(String strText){_strText = strText;}
		public void notify(String strFace)
		{
			_strText = strFace;
			repaint();
		}
		
		public void setColour(Color strColour)
		{
			colour = strColour;
			repaint();
		}
		
		public void setFont(Font strFont)
		{
			font = strFont;
			repaint();
		}
		public Color getColour(){return colour;}
		public Font getFont(){return font;}
		
		public void paint(Graphics g)
		{
			// we must manually blank out the background ourselves in order
			// to avoid bugs in some implementations of the AWT...
			
			final Graphics2D g2d = (Graphics2D)g;
			g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON ));
			
			g.setFont(font);
			g.setColor(colour);
			
			final FontMetrics fm = g.getFontMetrics();
			final int w = fm.stringWidth(_strText);
			final int x = (getSize().width - w) / 2;
			
			g.drawString(_strText, x, 150);
		}
	}
	
	final public class DrawArabicWord extends JApplet
	{
		private MainCanvas can = null;
		public void init()
		{
			defaultFont = defaultFont.deriveFont((float)150);
			can = new MainCanvas("");
			can.setFont(defaultFont);
			add(can);
		}
		
		public void setFont(final Font font){can.setFont(font);}
		public Font getFont(){return can.getFont();}
		public void setWord(final String word){can.notify(word);}
		public void setColour(final Color colour){can.setColour(colour);}
		public Color getColour(){return can.getColour();}
		public Dimension getPreferredSize(){return new Dimension(400, 200);}
	}
}