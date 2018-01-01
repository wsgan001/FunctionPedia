package cn.edu.pku.sei.tsr.dragon.taskdocmodel.search;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class SearchEngine {
	List<Doc> docs = new ArrayList<Doc>();
	List<Task> tasks = new ArrayList<Task>();
    private static int NUM_OF_THREAD = 8;
    static Thread[] threads = new Thread[NUM_OF_THREAD];
	private static Analyzer analyzer;
	private static File fileDoc;
	public static Directory directoryDoc;
	private static File fileTask;
	public static Directory directoryTask;	
	private static IndexWriterConfig config;
	static {
		analyzer = new StandardAnalyzer();
		fileDoc = new File("D:\\codes\\index");
		fileTask = new File("D:\\codes\\indexTask");
		try {
			directoryDoc = FSDirectory.open(fileDoc.toPath());
			directoryTask = FSDirectory.open(fileTask.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	public SearchEngine() {
		// TODO Auto-generated constructor stub
	}

	public void LoadDoc(String path){
		File dir = new File(path);
		int no = 0;
		for (int time = 0; time < 1; time++)
		for (File file : dir.listFiles()){
			try {
				Doc doc = new Doc();
				doc.name = file.getName();
				doc.id = no;
				no++;
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				while ((line = br.readLine()) != null){
					Task task = new Task();
					task.settask(line);
					line = br.readLine();
					task.score = Double.valueOf(line);
					task.doc = doc;
					doc.addtask(task);
					tasks.add(task);
				}
				docs.add(doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void calcidf(){
		// get normal factor for each document
		for (Doc doc : docs){
			for (Task task : doc.tasks){
				double score = 0;
				for (Task t : doc.tasks){
					score += task.getSim(t);
				}
				doc.factor+=score;
			}
		}
		// for each task calculate the idf
        for (int i = 0; i< NUM_OF_THREAD; i++) {
            threads[i] = new MyThread(i);
            threads[i].start();
        }
        for (int i = 0; i< NUM_OF_THREAD; i++) {
        	try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		for (Doc doc : docs){
			for (Task task : doc.tasks){
				for (double relevance : task.relevances){
					task.tfidf.add(relevance * Math.log(docs.size()/task.idf));
				}
			}
		}
	}
	
	public void savetfidf(String path) throws IOException{
		File file = new File(path);
		FileWriter fw = new FileWriter(file);
		for (Task task : tasks){
			for (Double d : task.tfidf){
				fw.write(d.toString()+" ");
			}
		}
		fw.close();
	}
	
	public void loadtfidf(String path) throws FileNotFoundException{
		File file = new File(path);
		InputStream is = new FileInputStream(file);
		Scanner sc = new Scanner(is);
		for (Task task : tasks){
			for (int i = 0 ; i < docs.size(); i++)
			task.tfidf.add(sc.nextDouble());
		}
	}
	
	public List<Double> searchMain(String q){
		Task query = new Task();
		query.settask(q);
		List<Double> A = new ArrayList<Double>();
		for (Task task : tasks){
			A.add(query.getSim(task));
		}
		List<Double> B = new ArrayList<Double>();
		for (int i = 0; i < docs.size(); i++){
			double tmp = 0;
			for (int j = 0; j < tasks.size(); j++){
				tmp += A.get(j) * tasks.get(j).tfidf.get(i);
			}
			B.add(tmp);
		}
		return B;
	}
	
	public List<String> searchNaviTask(String query){
		List<String> ret = new ArrayList<String>();
		String[] words = query.replaceAll(" +", " ").trim().split(" ");
		for (Doc doc : docs){
			for (Task task : doc.tasks){
				boolean flag = true;
				for (String word : words){
					if (!task.map.containsKey(word)) {
						flag = false;
						break;
					}
				}
				if (flag){
					/*change the content added to the list*/
					ret.add(task.toString() + " ***** " + doc.name);
				}
			}
		}
		Collections.sort(ret,String.CASE_INSENSITIVE_ORDER);
		return ret;
	}
	
	public void createTaskIndex(){
		for (Task task : tasks){
			String key = task.toString();
			
			/*change the value of value*/
			String value = task.toString() + " " + task.doc.name;
			
			try {
				config = new IndexWriterConfig(analyzer);
				IndexWriter iwriter = new IndexWriter(directoryTask, config);
			    Document doc = new Document();
			    doc.add(new TextField("key", key, Field.Store.YES));
			    doc.add(new TextField("value", value, Field.Store.YES));
			    iwriter.addDocument(doc);
			    iwriter.close();	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createDocIndex(){
		for (Doc d : docs){
			String key = d.toString();
			
			/*change the value of value*/
			String value = ""+d.id;
			
			try {
				config = new IndexWriterConfig(analyzer);
				IndexWriter iwriter = new IndexWriter(directoryDoc, config);
			    Document doc = new Document();
			    doc.add(new TextField("key", key, Field.Store.YES));
			    doc.add(new TextField("value", value, Field.Store.YES));
			    iwriter.addDocument(doc);
			    iwriter.close();	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<String> searchTaskIndex(String queryStr){
		List<String> ret = new ArrayList<String>(); 
		try {
			Query query;
		    DirectoryReader ireader = DirectoryReader.open(directoryTask);
		    IndexSearcher isearcher = new IndexSearcher(ireader);
		    QueryParser parser = new QueryParser("key", analyzer);
			query = parser.parse(queryStr);
			ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;
			// Iterate through the results:
		    for (int i = 0; i < hits.length; i++) {
		    	Document hitDoc = isearcher.doc(hits[i].doc);
		    	System.out.println(hits[i].score + "\n" + hitDoc.get("value"));
		    	ret.add(hitDoc.get("value"));
		    	
		    }
		    //Document doc = isearcher.doc(hits[0].doc); 
		    //return doc.get("value");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public List<String> searchDocIndex(String queryStr){
		List<String> ret = new ArrayList<String>(); 
		try {
			Query query;
		    DirectoryReader ireader = DirectoryReader.open(directoryDoc);
		    IndexSearcher isearcher = new IndexSearcher(ireader);
		    QueryParser parser = new QueryParser("key", analyzer);
			query = parser.parse(queryStr);
			ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;
			// Iterate through the results:
		    for (int i = 0; i < hits.length; i++) {
		    	Document hitDoc = isearcher.doc(hits[i].doc);
		    	//System.out.println(hits[i].score + "\n" + hitDoc.get("value"));
		    	ret.add(hitDoc.get("value"));
		    	
		    }
		    //Document doc = isearcher.doc(hits[0].doc); 
		    //return doc.get("value");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public class MyThread extends Thread
	{
	    private int id;
	    public MyThread(int id)
	    {
	        this.id = id;
	    }
	    public void run()
	    {
	    	for (Doc doc : docs){
    			if (doc.id % NUM_OF_THREAD != id)continue;
    			for (Task task : doc.tasks){
    				for (Doc d : docs){
    					double tf = 0;
    					double relevance = 0;
    					for (Task t : d.tasks){
    						double similaity = task.getSim(t);
    						tf = Math.max(tf, similaity);
    						relevance += similaity * t.score;
    					}
    					task.idf += tf;
    					task.tfs.add(tf);
    					task.relevances.add(relevance);
    				}
    			}			
    		}
	    }
	}
	public static void main(String args[]) throws IOException{
		SearchEngine se = new SearchEngine();
        long start = System.nanoTime();
        se.LoadDoc("D:\\ApiDocs");
        /*************************/
        //lucene example
        
        //run only once
        se.createTaskIndex();
        //search example
		List<String> t = se.searchTaskIndex("take task from index");
		/*************************/
		//Relevance idf example
		
        //run only once		
		se.calcidf();
		se.savetfidf("D:\\codes\\ApiTFIDF.txt");
		
		//search example
		se.loadtfidf("D:\\codes\\ApiTFIDF.txt");
		List<Double> scores = se.searchMain("take/verb task/knn from/prop index/nn");
		for (Doc doc : se.docs){
			System.out.println(doc.name);
		}
		
		/*************************/
		//TaskNavi example
		se.LoadDoc("D:\\ApiDocs");
		List<String> ls = se.searchNaviTask("take task from navi");
		// ls : take task from index ***** 1.txt 
		long end = System.nanoTime();
		System.out.println((double)(end-start)/1000000000);
	}

}
