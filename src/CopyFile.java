import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.Window;
import javax.swing.JOptionPane;

public class CopyFile
{
	protected Window MIE;
	public CopyFile(String from, String to, Window ME)
	{
		MIE = ME;
		try{copy(from, to);}
		catch (IOException e){System.err.println(e.getMessage());}
	}

	public void copy(String fromFileName, String toFileName) throws IOException 
	{
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		if (!fromFile.exists())
			throw new IOException("CopyFile: " + "no such source file: "+ fromFileName);

		if (!fromFile.isFile())
			throw new IOException("CopyFile: " + "can't copy directory: "+ fromFileName);

		if (!fromFile.canRead())
			throw new IOException("CopyFile: " + "source file is unreadable: "+ fromFileName);

		if (toFile.isDirectory())
			toFile = new File(toFile, fromFile.getName());

		if (toFile.exists()) 
		{
			if (!toFile.canWrite())
				throw new IOException("CopyFile: "+ "destination file is unwriteable: " + toFileName);
			
			int choice = JOptionPane.showConfirmDialog(MIE,"Overwrite existing file " + toFile.getName()+"?","Warning!",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
			if(choice == JOptionPane.NO_OPTION)
				throw new IOException("CopyFile: "+ "existing file was not overwritten.");
		}
		else
		{
			String parent = toFile.getParent();

			if (parent == null)
				parent = System.getProperty("user.dir");

			File dir = new File(parent);

			if (!dir.exists())
				throw new IOException("CopyFile: "+ "destination directory doesn't exist: " + parent);

			if (dir.isFile())
				throw new IOException("CopyFile: "+ "destination is not a directory: " + parent);

			if (!dir.canWrite())
				throw new IOException("CopyFile: "+ "destination directory is unwriteable: " + parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;

		try
		{
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1) to.write(buffer, 0, bytesRead); // write
		} 
		finally 
		{
			if (from != null)
			{
				try{from.close();} 
				catch(IOException e){System.err.println(e.getMessage());}
			}
			
			if (to != null)
			{
				try{to.close();} 
				catch (IOException e){System.err.println(e.getMessage());}
			}
		}
	}
}