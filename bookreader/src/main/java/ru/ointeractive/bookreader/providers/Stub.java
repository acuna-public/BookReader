	package ru.ointeractive.bookreader.providers;
	/*
	 Created by Acuna on 28.07.2018
	*/
	
	import java.io.File;
	import java.util.List;
	
	import ru.ointeractive.bookreader.Book;
	import ru.ointeractive.bookreader.BookReader;
	import ru.ointeractive.bookreader.BookReaderException;
	
	public class Stub extends Book {
		
		private Stub (BookReader reader) {
			super (reader);
		}
		
		@Override
		public Book getInstance (BookReader reader) throws BookReaderException {
			return new Stub (reader);
		}
		
		@Override
		public String[] setFormats () {
			return new String[0];
		}
		
		@Override
		public Book open (File file) throws BookReaderException {
			return null;
		}
		
		@Override
		public String getMimeType () throws BookReaderException {
			return "";
		}
		
		@Override
		public Book.Metadata getMetadata () throws BookReaderException {
			return null;
		}
		
		@Override
		public Book.TOC getTOC () throws BookReaderException {
			return null;
		}
		
		@Override
		public List<Chapter> getChapters () throws BookReaderException {
			return null;
		}
		
		@Override
		public Chapter getSection (String entry) throws BookReaderException {
			return null;
		}
		
	}