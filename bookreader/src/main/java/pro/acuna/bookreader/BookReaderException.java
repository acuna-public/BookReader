	package pro.acuna.bookreader;
	/*
	 Created by Acuna on 23.07.2018.
	 */
	
	public class BookReaderException extends Exception {
		
		public BookReaderException (Exception e) {
			super (e);
		}
		
		public BookReaderException (String msg) {
			super (msg);
		}
		
		@Override
		public Exception getCause () {
			return (Exception) super.getCause ();
		}
		
	}