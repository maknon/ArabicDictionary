import java.io.*;
import java.sql.*;
import javax.swing.*;

public class LoadImage extends JFrame
{
	private static final long serialVersionUID = 1L;
	static String fileSeparator = System.getProperty("file.separator");
	public LoadImage()
	{
		JLabel s = new JLabel();
		try
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			final Connection con = DriverManager.getConnection("jdbc:derby:.."+fileSeparator+"imageDatabase;create=true", "root", "secret");
			final Statement stmt = con.createStatement();
			stmt.execute("CREATE TABLE image(picture BLOB(15000), filename VARCHAR(50))");
			
			final PreparedStatement prepStmt = con.prepareStatement("INSERT INTO image values (?, ?)");
		    prepStmt.setString(2, "TestImage");
		    
		    final File photoFile = new File("plugin.png");
		    final InputStream fileIn = new FileInputStream(photoFile);
		    prepStmt.setBinaryStream(1, fileIn, (int) photoFile.length());
		    prepStmt.executeUpdate();
		    fileIn.close();
		    prepStmt.close();
		    final ResultSet rs = stmt.executeQuery("SELECT * FROM image");
		    rs.next();
		    s = new JLabel(new ImageIcon(rs.getBytes("picture")));
			con.close();
		}
		catch(java.lang.ClassNotFoundException e){System.err.println(e.getMessage());}
		catch(SQLException e) {e.printStackTrace();}
		catch(java.lang.Exception e){System.err.println(e.getMessage());}
		
		add(s);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args){new LoadImage();}
}