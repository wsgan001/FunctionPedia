package cn.edu.pku.sei.tsr.dragon.search.util;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.email.entity.Email;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;

public class LuceneUtil {
	private static Analyzer				analyzer;
	private static File					file;
	public static Directory				directory;
	private static IndexWriterConfig	config;
	static {
		analyzer = new StandardAnalyzer();
		file = new File(Config.getIndexDir());
		try {
			directory = FSDirectory.open(file.toPath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void saveContentToLucene(ContentInfo content) {
		try {
			config = new IndexWriterConfig(analyzer);
			IndexWriter iwriter = new IndexWriter(directory, config);
			Document doc = new Document();
			doc.add(new Field("text", content.getContent(), TextField.TYPE_STORED));
			doc.add(new Field("uuid", content.getUuid(), TextField.TYPE_STORED));
			iwriter.addDocument(doc);
			iwriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void updateThreadToLucene(OldThreadInfo thread) {
		try {
			config = new IndexWriterConfig(analyzer);
			IndexWriter iwriter = new IndexWriter(directory, config);
			Document doc = new Document();
			String content = "";
			content = content + thread.getTitle() + "\n";
			content = content + thread.getQuestion().getContent().getContent() + "\n";
			for (PostInfo post : thread.getAnswers()) {
				content = content + post.getContent().getContent();
			}
			doc.add(new Field("text", content, TextField.TYPE_STORED));
			doc.add(new Field("uuid", thread.getUuid(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			Term term = new Term("uuid", doc.get("uuid"));
			iwriter.updateDocument(term, doc);
			iwriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateContentToLucene(ContentInfo content) {
		try {
			config = new IndexWriterConfig(analyzer);
			IndexWriter iwriter = new IndexWriter(directory, config);
			Document doc = new Document();
			doc.add(new Field("text", content.getContent(), TextField.TYPE_STORED));
			doc.add(new Field("uuid", content.getUuid(), Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			Term term = new Term("uuid", doc.get("uuid"));
			iwriter.updateDocument(term, doc);
			iwriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateMailToLucene(Email mail, String name) {
		try {
			config = new IndexWriterConfig(analyzer);
			IndexWriter iwriter = new IndexWriter(directory, config);
			Document doc = new Document();
			doc.add(new Field("text", mail.getContent(), TextField.TYPE_STORED));
			doc.add(new Field("uuid", name + mail.getUuid(), Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			Term term = new Term("uuid", doc.get("uuid"));
			iwriter.updateDocument(term, doc);
			iwriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void updateSessionToLucene(String content, String name) {
		try {
			config = new IndexWriterConfig(analyzer);
			IndexWriter iwriter = new IndexWriter(directory, config);
			Document doc = new Document();
			doc.add(new Field("text", content, TextField.TYPE_STORED));
			doc.add(new Field("uuid", name, Field.Store.YES, Field.Index.NOT_ANALYZED));
			Term term = new Term("uuid", doc.get("uuid"));
			iwriter.updateDocument(term, doc);
			iwriter.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static String search(String queryStr) {
		try {
			Query query;
			DirectoryReader ireader = DirectoryReader.open(directory);
			IndexSearcher isearcher = new IndexSearcher(ireader);
			QueryParser parser = new QueryParser("text", analyzer);
			query = parser.parse(queryStr);
			ScoreDoc[] hits = isearcher.search(query, 50).scoreDocs;
			// Iterate through the results:
			for (int i = 0; i < hits.length; i++) {
				Document hitDoc = isearcher.doc(hits[i].doc);

				System.out.println(hitDoc.get("uuid") + "\t" + hitDoc.get("text"));
				System.out.println("=============================================");
			}
			Document doc = isearcher.doc(hits[0].doc);
			return doc.get("uuid") + doc.get("text");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Vector<String> searchs(String queryStr){
		try {
			Query query;
		    DirectoryReader ireader = DirectoryReader.open(directory);
		    IndexSearcher isearcher = new IndexSearcher(ireader);
		    QueryParser parser = new QueryParser("text", analyzer);
			query = parser.parse(queryStr);
			ScoreDoc[] hits = isearcher.search(query, 20).scoreDocs;
			Vector<String> v = new Vector<String>();
		    for (int i = 0; i < hits.length; i++) {
		    	Document hitDoc = isearcher.doc(hits[i].doc);
		    	v.add(hitDoc.get("text"));
		    }
		    return v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	

	public static void main(String args[]) {
		// File file = new File(Config.getIndexDir());
		// Analyzer analyzer = new StandardAnalyzer();
		// Directory directory;
		// try {
		// directory = FSDirectory.open(file.toPath());
		// IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// IndexWriter iwriter = new IndexWriter(directory, config);
		// Document doc = new Document();
		// String text = "This is the text to be indexed.";
		// doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
		// iwriter.addDocument(doc);
		// iwriter.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		String q = "NOT_ANALYZED not working with uppercase characters";
		System.out.println(search(q));
	}

}
