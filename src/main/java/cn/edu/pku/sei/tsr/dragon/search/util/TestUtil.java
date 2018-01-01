package cn.edu.pku.sei.tsr.dragon.search.util;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TestUtil {
	private static Analyzer analyzer;
	private static File file;
	public static Directory directory;
	private static IndexWriterConfig config;
	static {
		analyzer = new StandardAnalyzer();
		file = new File(Config.getIndexDir());
		try {
			directory = FSDirectory.open(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateContentToLucene(String content){
		try {
			config = new IndexWriterConfig(analyzer);
			IndexWriter iwriter = new IndexWriter(directory, config);
		    Document doc = new Document();
		    doc.add(new Field("text", content, Field.Store.YES, Field.Index.ANALYZED));
		    iwriter.addDocument(doc);
		    iwriter.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 	

	public static String search(String queryStr){
		try {
			Query query;
		    DirectoryReader ireader = DirectoryReader.open(directory);
		    IndexSearcher isearcher = new IndexSearcher(ireader);
		    QueryParser parser = new QueryParser("text", analyzer);
			query = parser.parse(queryStr);
			ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;
			// Iterate through the results:
		    for (int i = 0; i < hits.length; i++) {
		    	Document hitDoc = isearcher.doc(hits[i].doc);
		    	System.out.println(hits[i].score + "\n" + hitDoc.get("text"));
		    	
		    }
		    Document doc = isearcher.doc(hits[0].doc); 
		    return doc.get("text");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static void main(String args[]){
//		String text = "readfile + openfile + output the index";
//		updateContentToLucene(text);
		String q = "weka";
		System.out.println(search(q));
	}


}
