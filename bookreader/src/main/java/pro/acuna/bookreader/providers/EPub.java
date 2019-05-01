	package pro.acuna.bookreader.providers;
	/*
	 Created by Acuna on 23.07.2018.
	*/
	
	import org.jsoup.nodes.Attribute;
	import org.jsoup.nodes.Document;
	import org.jsoup.nodes.Element;
	import org.jsoup.nodes.Node;
	import org.jsoup.select.NodeVisitor;
	
	import java.io.InputStream;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	
	import pro.acuna.archiver.Archiver;
	import pro.acuna.bookreader.Book;
	import pro.acuna.bookreader.BookReader;
	import pro.acuna.bookreader.BookReaderException;
	import pro.acuna.jabadaba.Arrays;
	import pro.acuna.jabadaba.Files;
	import pro.acuna.jabadaba.Int;
  import pro.acuna.jabadaba.Net;
  
	public class EPub extends Book {
		
		private static final String FILE_MIMETYPE = "mimetype";
		private static final String FILE_CONTAINER = "META-INF/container.xml";
		
		private static final String TYPE_TOC = "application/x-dtbncx+xml";
		private static final String TYPE_CSS = "text/css";
		private static final String TYPE_XHTML = "application/xhtml+xml";
		private static final String TYPE_OTHER = "application/octet-stream";
		
		public EPub () {}
		
		private Document xmlReader, contentReader;
		private Element data;
		private String contentPath;
		
		private EPub (BookReader reader) {
			super (reader);
		}
		
		@Override
		public Book getInstance (BookReader reader) throws BookReaderException {
			return new EPub (reader);
		}
		
		@Override
		public String[] setFormats () {
			return new String[] { "epub" };
		}
		
		@Override
		public Book open (InputStream stream, String type) throws BookReaderException {
			
			try {
				
				archiver = archiver.open (stream, type);
				xmlReader = Net.toHTML (getEntry (FILE_CONTAINER));
				
				data = xmlReader.select ("rootfiles").get (0);
				data = data.select ("rootfile").get (0);
				
				contentReader = Net.toHTML (getEntry (data.attr ("full-path"))); // content.opf
				contentPath = Files.getPath (data.attr ("full-path"));
				
			} catch (Archiver.DecompressException e) {
				throw new BookReaderException (e);
			}
			
			return this;
			
		}
		
		@Override
		public List<Chapter> getChapters () throws BookReaderException {
			
			Element data = contentReader.select ("spine").get (0);
			Element data2 = contentReader.select ("manifest").get (0);
			
			List<Chapter> items = new ArrayList<> ();
			
			for (Element elem2 : data.select ("itemref")) {
				
				if (!elem2.attr ("linear").equals ("no")) { // Тега может не быть вообще
					
					elem2 = data2.select ("#" + elem2.attr ("idref")).get (0);
					items.add (getSection (elem2.attr ("href")));
					
				}
				
			}
			
			return items;
			
		}
		
		public String getMimeType () throws BookReaderException {
			return getEntry (FILE_MIMETYPE);
		}
		
		@Override
		public Book.Metadata getMetadata () throws BookReaderException {
			return new Metadata ();
		}
		
		private class Metadata extends Book.Metadata {
			
			private Metadata () throws BookReaderException {
				data = contentReader.select ("metadata").get (0);
			}
			
			@Override
			public String getTitle () throws BookReaderException {
				return getItem (data, BookReader.ITEM_TITLE);
			}
			
			@Override
			public List<String> getAuthors () throws BookReaderException {
				return getList (data, BookReader.ITEM_AUTHOR);
			}
			
			@Override
			public List<String> getPublishers () throws BookReaderException {
				return getList (data, BookReader.ITEM_PUBLISHER);
			}
			
			@Override
			public List<String> getContributors () throws BookReaderException {
				return getList (data, BookReader.ITEM_CONTRIBUTOR);
			}
			
			@Override
			public String getSubject () throws BookReaderException {
				return getItem (data, BookReader.ITEM_SUBJECT);
			}
			
			@Override
			public String getDescription () throws BookReaderException {
				return getItem (data, BookReader.ITEM_DESCRIPTION);
			}
			
			@Override
			public String getDate () throws BookReaderException {
				return getItem (data, BookReader.ITEM_DATE);
			}
			
			@Override
			public List<String> getLangs () throws BookReaderException {
				return getList (data, BookReader.ITEM_LANGUAGE);
			}
			
			@Override
			public List<String> getIdentifiers () throws BookReaderException {
				return getList (data, BookReader.ITEM_IDENTIFIER);
			}
			
			@Override
			public String getCover () throws BookReaderException {
				return getEntry (contentPath, getFiles (getMeta ("cover"), "id").get (0));
			}
			
		}
		
		@Override
		public Chapter getSection (String entry) throws BookReaderException {
			return new Section (entry);
		}
		
		private class Section extends Chapter {
			
			private Element elem, head, content;
			private String entry;
			
			private Section (String entry) throws BookReaderException {
				
				this.entry = entry;
				elem = Net.toHTML (getEntry (contentPath, entry));
				
				head = elem.select ("head").get (0);
				content = elem.select ("body").get (0);
				
			}
			
			@Override
			public String getTitle () throws BookReaderException {
				
				Element data = head.select ("title").get (0);
				return data.text ();
				
			}
			
			@Override
			public String getCSS () throws BookReaderException {
				
				String css = "";
				List<Map<String, String>> tags = getObject (head, "link");
				
				for (Map<String, String> tag : tags) {
					
					if (tag.get ("type").equals ("text/css"))
						css += getEntry (contentPath, tag.get ("href"));
					
				}
				
				return css;
				
			}
			
			@Override
			public Element getContent () throws BookReaderException {
				return content;
			}
			
			@Override
			public String getName () throws BookReaderException {
				return entry;
			}
			
		}
		
		@Override
		public Book.TOC getTOC () throws BookReaderException {
			return new TOC ();
		}
		
		private class TOC extends Book.TOC {
			
			private Element elem;
			
			private TOC () throws BookReaderException {
				
				String file = getFiles (TYPE_TOC).get (0);
				elem = Net.toHTML (getEntry (contentPath, file));
				
			}
			
			@Override
			public String getTitle () throws BookReaderException {
				
				Element data = elem.select ("docTitle").get (0);
				return data.select ("text").get (0).text ();
				
			}
			
			@Override
			public List<Book.TOC.Item> getItems () throws BookReaderException {
				
				final List<Book.TOC.Item> items = new ArrayList<> ();
				
				Element elem = this.elem.select ("navMap").get (0);
				
				elem.traverse (new NodeVisitor () {
					
					@Override
					public void head (Node node, int depth) {
						
						Element elem = Net.toHTML (node.toString ());
						
						if (depth > 0 && Int.size (elem.select ("navPoint")) == 1)
							items.add (new Item (elem, depth));
						
					}
					
					@Override
					public void tail (Node node, int depth) {}
					
				});
				
				return items;
				
			}
			
			private class Item extends Book.TOC.Item {
				
				private Element elem, title;
				private int depth;
				
				private Item (Element elem, int depth) {
					
					this.elem = elem;
					this.depth = depth;
					
				}
				
				@Override
				public String getTitle () throws BookReaderException {
					
					title = elem.select ("navLabel").get (0);
					return title.select ("text").get (0).text ();
					
				}
				
				@Override
				public int getLevel () {
					return depth;
				}
				
			}
			
		}
		
		private List<String> getFiles (String type) throws BookReaderException {
			return getFiles (type, "media-type");
		}
		
		private List<String> getFiles (String type, String col) throws BookReaderException {
			
			Element data = contentReader.select ("manifest").get (0);
			
			List<String> output = new ArrayList<> ();
			List<Map<String, String>> pages = getObject (data, "item");
			
			for (Map<String, String> list : pages) {
				
				if (Arrays.contains (col, list) && list.get (col).equals (type))
					output.add (list.get ("href"));
				
			}
			
			return output;
			
		}
		
		private String getMeta (String type) throws BookReaderException {
			
			List<Map<String, String>> pages = getObject (data, "meta");
			
			for (Map<String, String> list : pages) {
				
				if (Arrays.contains ("name", list) && list.get ("name").equals (type))
					return list.get ("content");
				
			}
			
			return null;
			
		}
		
		//@Override TODO
		public List<Map<String, String>> getSpine () throws BookReaderException {
			
			Element data = contentReader.select ("spine").get (0);
			return getObject (data, "itemref");
			
		}
		
		private String getItem (Element data, String tag) throws BookReaderException {
			return data.select ("*|" + tag).get (0).text ();
		}
		
		private List<String> getList (Element data, String tag) throws BookReaderException {
			
			List<String> output = new ArrayList<> ();
			
			for (Element elem : data.select ("*|" + tag))
				output.add (elem.text ());
			
			return output;
			
		}
		
		private List<Map<String, String>> getObject (Element data, String tag) throws BookReaderException {
			
			List<Map<String, String>> output = new ArrayList<> ();
			
			for (Element elem : data.select (tag)) {
				
				Map<String, String> output2 = new HashMap<> ();
				
				for (Attribute att : elem.attributes ().asList ())
					output2.put (att.getKey (), att.getValue ());
				
				output.add (output2);
				
			}
			
			return output;
			
		}
		
	}