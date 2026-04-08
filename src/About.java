import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

final class About extends JWindow implements MouseListener
{
	public About(final JFrame AC)
	{
		super(AC);
		final JLabel splashBackground = new JLabel(new ImageIcon(".."+System.getProperty("file.separator")+"images"+System.getProperty("file.separator")+"MaknoonAudioDictionary.png"));
		getContentPane().add(splashBackground);
		
		// To locat the JDialog at the center of the screen
		pack();
		MaknoonIslamicEncyclopedia.centerInScreen(this);
		setVisible(true);
		addMouseListener(this);
	}
	
	public void mouseClicked(MouseEvent m)
	{
		setVisible(false);
		dispose();
	}
	public void mouseExited(MouseEvent m){}
	public void mouseEntered(MouseEvent m){}
	public void mouseReleased(MouseEvent m){}
	public void mousePressed(MouseEvent m){}
}