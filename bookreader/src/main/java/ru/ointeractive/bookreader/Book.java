	package ru.ointeractive.bookreader;
	/*
	 Created by Acuna on 23.07.2018
	*/
	
	import org.jsoup.nodes.Element;
  
  import java.io.File;
	import java.io.InputStream;
	import java.util.List;
	
	import ru.ointeractive.archiver.Archiver;
  import ru.ointeractive.jabadaba.exceptions.OutOfMemoryException;
	
	public abstract class Book {
		
		protected Archiver archiver;
		protected BookReader reader;
		
		protected Book () {}
		
		public Book (BookReader reader) {
			
			this.reader = reader;
			archiver = new Archiver ();
			
		}
		
		public String getEntry (String... file) throws BookReaderException {
			
			try {
				return archiver.getEntry (file);
			} catch (Archiver.DecompressException e) {
				throw new BookReaderException ("Entry " + e.getEntry () + " not found");
			} catch (OutOfMemoryException e) {
        throw new BookReaderException (e);
      }
			
		}
		
		public String[] contentEntryName (String name) {
			return new String[] { name };
		}
		
		public String getContentEntry (String file) throws BookReaderException {
			return getEntry (contentEntryName (file));
		}
		
		public InputStream getEntryStream (String... file) throws BookReaderException {
			
			try {
				return archiver.getEntryStream (file);
			} catch (Archiver.DecompressException e) {
				throw new BookReaderException ("Entry " + e.getEntry () + " not found");
			}
			
		}
		
		public InputStream getContentEntryStream (String file) throws BookReaderException {
			return getEntryStream (contentEntryName (file));
		}
		
		public abstract Book getInstance (BookReader reader) throws BookReaderException;
		public abstract String[] setFormats ();
  
		public abstract Book open (File file) throws BookReaderException;
		public abstract String getMimeType () throws BookReaderException;
		
		public abstract Metadata getMetadata () throws BookReaderException;
		public abstract TOC getTOC () throws BookReaderException;
		public abstract List<Chapter> getChapters () throws BookReaderException;
		public abstract Chapter getSection (String entry) throws BookReaderException;
		
		public abstract static class Metadata {
			
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
		
		public abstract static class TOC {
			
			public abstract String getTitle () throws BookReaderException;
			public abstract List<Item> getItems () throws BookReaderException;
			
			public abstract static class Item {
				
				public abstract String getTitle () throws BookReaderException;
				public abstract int getLevel () throws BookReaderException;
				
			}
			
		}
		
		public abstract static class Chapter {
			
			public abstract String getTitle () throws BookReaderException;
			public abstract String getCSS () throws BookReaderException;
			public abstract Element getContent () throws BookReaderException;
			public abstract String getName () throws BookReaderException;
			
		}
		
	}