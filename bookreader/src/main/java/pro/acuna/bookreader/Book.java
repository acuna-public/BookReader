	package pro.acuna.bookreader;
	/*
	 Created by Acuna on 23.07.2018
	*/
	
	import org.jsoup.nodes.Element;
	
	import java.io.InputStream;
	import java.util.List;
	
	import pro.acuna.archiver.Archiver;
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	
	public abstract class Book {
		
		protected Archiver archiver;
		protected BookReader reader;
		
		protected Book () {}
		
		public Book (BookReader reader) {
			
			this.reader = reader;
			archiver = new Archiver ();
			
		}
		
		protected String getEntry (String... file) throws BookReaderException {
			
			try {
				return archiver.getEntry (file);
			} catch (Archiver.DecompressException | OutOfMemoryException e) {
				throw new BookReaderException (e);
			}
			
		}
		
		public abstract Book getInstance (BookReader reader) throws BookReaderException;
		public abstract String[] setFormats ();
		public abstract Book open (InputStream stream, String type) throws BookReaderException;
		public abstract String getMimeType () throws BookReaderException;
		
		public abstract Metadata getMetadata () throws BookReaderException;
		public abstract TOC getTOC () throws BookReaderException;
		public abstract List<Chapter> getChapters () throws BookReaderException;
		public abstract Chapter getSection (String entry) throws BookReaderException;
		
		public abstract class Metadata {
			
			public abstract String getTitle () throws BookReaderException;
			public abstract List<String> getAuthors () throws BookReaderException;
			public abstract List<String> getPublishers () throws BookReaderException;
			public abstract List<String> getContributors () throws BookReaderException;
			public abstract String getSubject () throws BookReaderException;
			public abstract String getDescription () throws BookReaderException;
			public abstract String getDate () throws BookReaderException;
			public abstract List<String> getLangs () throws BookReaderException;
			public abstract List<String> getIdentifiers () throws BookReaderException;
			public abstract String getCover () throws BookReaderException;
			
		}
		
		public abstract class TOC {
			
			public abstract String getTitle () throws BookReaderException;
			public abstract List<Item> getItems () throws BookReaderException;
			
			public abstract class Item {
				
				public abstract String getTitle () throws BookReaderException;
				public abstract int getLevel () throws BookReaderException;
				
			}
			
		}
		
		public abstract class Chapter {
			
			public abstract String getTitle () throws BookReaderException;
			public abstract String getCSS () throws BookReaderException;
			public abstract Element getContent () throws BookReaderException;
			public abstract String getName () throws BookReaderException;
			
		}
		
	}