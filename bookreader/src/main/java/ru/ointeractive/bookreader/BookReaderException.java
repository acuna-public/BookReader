	package ru.ointeractive.bookreader;
	/*
	 Created by Acuna on 23.07.2018
	*/
	
  public class BookReaderException extends Exception {
    
    public BookReaderException (Exception msg) {
      super (msg);
    }
    
    public BookReaderException (String mess) {
      super (mess);
    }
    
    @Override
    public Exception getCause () {
      return (Exception) super.getCause ();
    }
    
  }