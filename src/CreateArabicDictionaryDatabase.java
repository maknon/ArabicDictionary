import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

final public class CreateArabicDictionaryDatabase
{
	public CreateArabicDictionaryDatabase()
	{
		try
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").getDeclaredConstructor().newInstance();
			Connection con = DriverManager.getConnection("jdbc:derby:.."+MaknoonIslamicEncyclopedia.fileSeparator+"arabicDictionaryDatabase;create=true","root","secret");
			Statement s = con.createStatement();
			
			s.execute("create table arabicDictionary(arabicWord VARCHAR(100),englishWord VARCHAR(100),arabicDescription LONG VARCHAR,englishDescription LONG VARCHAR,imageName VARCHAR(50),arabicSoundName VARCHAR(50),englishSoundName VARCHAR(50),category VARCHAR(500))");
			s.execute("create table arabicSentenceDictionary(arabicSentence LONG VARCHAR,englishSentence LONG VARCHAR,arabicSoundName VARCHAR(50),englishSoundName VARCHAR(50),category VARCHAR(500))");
			s.execute("create table dictionarySection(sectionName VARCHAR(100))");
			s.execute("create table sentenceDictionarySection(sectionName VARCHAR(100))");
			s.execute("create table dictionaryAges(age VARCHAR(100))");
			
			s.execute("insert into dictionarySection values ('\u062B\u0642\u0627\u0641\u0629 \u0625\u0633\u0644\u0627\u0645\u064A\u0629 (Islamic Culture)')");
			s.execute("insert into dictionarySection values ('\u0627\u0644\u0623\u062F\u0628 \u0627\u0644\u0639\u0631\u0628\u064A (Arabic art)')");
			s.execute("insert into dictionarySection values ('\u0645\u0635\u0637\u0644\u062D\u0627\u062A \u0639\u0627\u0645\u0629 (General Words)')");
			s.execute("insert into dictionarySection values ('\u0645\u0635\u0637\u0644\u062D\u0627\u062A \u0639\u0644\u0645\u064A\u0629 (Science Words)')");
			s.execute("insert into dictionarySection values ('\u0645\u0635\u0637\u0644\u062D\u0627\u062A \u062A\u0627\u0631\u064A\u062E\u064A\u0629 (Historical Culture)')");
			s.execute("insert into dictionarySection values ('\u0631\u064A\u0627\u0636\u0629 (Sport)')");
			
			s.execute("insert into sentenceDictionarySection values ('Not decided yet 1')");
			s.execute("insert into sentenceDictionarySection values ('Not decided yet 2')");
			s.execute("insert into sentenceDictionarySection values ('Not decided yet 3')");
			
			s.execute("insert into dictionaryAges values ('0-5')");
			s.execute("insert into dictionaryAges values ('0-10')");
			s.execute("insert into dictionaryAges values ('0-15')");
			s.execute("insert into dictionaryAges values ('0-20')");
			s.execute("insert into dictionaryAges values ('0-25')");
			s.execute("insert into dictionaryAges values ('0-30')");
			
			boolean gotSQLExc = false;
			try{DriverManager.getConnection("jdbc:cloudscape:;shutdown=true");} 
			catch (SQLException se){gotSQLExc = true;}
			
			if (!gotSQLExc)
				System.out.println("Database did not shut down normally");
			else
				System.out.println("Database shut down normally");
		}
		catch (Throwable te)
		{
			System.out.println("exception thrown:");
			
			if (te instanceof SQLException)
				printSQLError((SQLException)te);
			else
				te.printStackTrace();
		}
	}
	
	static void printSQLError(SQLException e)
	{
		while (e != null)
		{
			System.out.println(e.toString());
			e = e.getNextException();
		}
	}
	
	public static void main(String args[]){new CreateArabicDictionaryDatabase();}
}