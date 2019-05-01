	package pro.acuna.bookreader;
	/*
	 Created by Acuna on 23.07.2018
	*/
	
	import java.io.File;
	import java.io.IOException;
	import java.io.InputStream;
	import java.util.ArrayList;
	import java.util.List;
	
	import pro.acuna.bookreader.providers.EPub;
	import pro.acuna.jabadaba.Arrays;
	import pro.acuna.jabadaba.Files;
	import pro.acuna.jabadaba.Streams;
	
	public class BookReader {
		
		private Book plugin;
		private List<Book> plugins = new ArrayList<> ();
		
		public static final String ITEM_TITLE = "title";
		public static final String ITEM_LANGUAGE = "language";
		public static final String ITEM_IDENTIFIER = "identifier";
		public static final String ITEM_TERMS = "terms";
		
		public static final String ITEM_AUTHOR = "creator";
		public static final String ITEM_CONTRIBUTOR = "contributor";
		public static final String ITEM_PUBLISHER = "publisher";
		public static final String ITEM_SUBJECT = "subject";
		public static final String ITEM_DESCRIPTION = "description";
		public static final String ITEM_DATE = "date";
		public static final String ITEM_TYPE = "type";
		public static final String ITEM_FORMAT = "format";
		public static final String ITEM_SOURCE = "source";
		public static final String ITEM_RELATION = "relation";
		public static final String ITEM_COVERAGE = "coverage";
		public static final String ITEM_RIGHTS = "rights";
		
		public BookReader () {
			
			addPlugin (new EPub ());
			
		}
		
		public BookReader addPlugin (Book plugin) {
			
			plugins.add (plugin);
			return this;
			
		}
		
		public BookReader setPlugin (Book plugin) {
			
			this.plugin = plugin;
			return this;
			
		}
		
		private Book getPlugin (String type) {
			
			for (Book plugin : plugins)
				if (Arrays.contains (type, plugin.setFormats ()))
					return plugin;
			
			return null;
			
		}
		
		public Book open (String file) throws BookReaderException {
			return open (new File (file));
		}
		
		public Book open (File file) throws BookReaderException {
			
			try {
				return open (Streams.toInputStream (file), Files.getExtension (file));
			} catch (IOException e) {
				throw new BookReaderException (e);
			}
			
		}
		
		public Book open (InputStream stream, String type) throws BookReaderException {
			
			if (plugin == null) plugin = getPlugin (type);
			
			plugin = plugin.getInstance (this);
			
			return plugin.open (stream, type);
			
		}
		
	}