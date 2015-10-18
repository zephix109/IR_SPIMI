package spimi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The Class DocumentSplitter.
 */
public class DocumentSplitter { 
	
	/** The instance. */
	public static DocumentSplitter instance = new DocumentSplitter();
	
	/**
	 * Instantiates a new document splitter.
	 */
	private DocumentSplitter() {}
	
	/**
	 * Gets the single instance of DocumentSplitter.
	 *
	 * @return single instance of DocumentSplitter
	 */
	public static DocumentSplitter getInstance() {
		if(instance == null) {
			instance = new DocumentSplitter();
		}
		return instance;
	}
	
	
	/**
	 * Parses the sgm file.
	 *
	 * @param file the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void parseSgmFile (File file) throws IOException {
		
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		NewsFile currentFile = new NewsFile();
		
		String line;
		
		
		while ((line = reader.readLine()) != null) {
			
			if (line.matches("<REUTERS [^>]*>")) {
				
				
				currentFile = new NewsFile();
				currentFile.setNewsID(line.substring(line.lastIndexOf("NEWID")).replaceAll("\\D+",""));
				
			}else if (line.matches("</REUTERS>")) {
				
				this.saveNewsFiles(currentFile, file.getName().replaceFirst("[.][^.]+$", ""));
				
			}else {
				
				if(currentFile.getNewsContent() == null) {
					
					currentFile.setNewsContent(line);
				}else {
					currentFile.setNewsContent(currentFile.getNewsContent() + System.getProperty("line.separator") + line);
				}
			}
			
		}
		
		reader.close();
	}
	
	/**
	 * Save news files.
	 *
	 * @param file the file
	 * @param blockName the block name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void saveNewsFiles(NewsFile file, String blockName) throws IOException  {
		
		String savePath = System.getProperty("user.dir") + File.separator + "reuters" + File.separator + blockName + File.separator;
		
		new File(savePath).mkdirs();

		Files.write(Paths.get(savePath + file.getNewsID() + ".news"), file.getNewsContent().getBytes(), StandardOpenOption.CREATE);
	}
	
	
	/**
	 * The Class NewsFile.
	 */
	public class NewsFile {
		
		/** The news id. */
		private String newsID;
		
		/** The news content. */
		private String newsContent;
		
		/**
		 * Gets the news id.
		 *
		 * @return the news id
		 */
		public String getNewsID() {
			return newsID;
		}
		
		/**
		 * Sets the news id.
		 *
		 * @param newsID the new news id
		 */
		public void setNewsID(String newsID) {
			this.newsID = newsID;
		}
		
		/**
		 * Gets the news content.
		 *
		 * @return the news content
		 */
		public String getNewsContent() {
			return newsContent;
		}
		
		/**
		 * Sets the news content.
		 *
		 * @param newsContent the new news content
		 */
		public void setNewsContent(String newsContent) {
			this.newsContent = newsContent;
		}
		
	}
}
