import java.io.File;

// file filter for opening files with a specified extension
public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter
{
	ExtensionFileFilter(String[] extensions, String description)
	{
		this.extensions = extensions;
		this.description = description;
	}
	
	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;
		
		String name = f.getName().toUpperCase();
		for (int i = 0; i < extensions.length; i++) 
			if (name.endsWith("." + extensions[i]))
				return true;
		return false;
	}
	
	public String getDescription() { return description; }
	
	private String[] extensions;
	private String description;
}