package cn.edu.pku.sei.tsr.dragon.email.mbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaBaseInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaMethodInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaTypeInfo;
import cn.edu.pku.sei.tsr.dragon.content.CodeTermsParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.CodeLikeTermInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.email.content.MessageProcess;
/*import cn.edu.pku.sei.tsr.dragon.email.dao.MessageDao;
import cn.edu.pku.sei.tsr.dragon.email.dao.SessionContentDao;
import cn.edu.pku.sei.tsr.dragon.email.dao.SessionDao;*/
import cn.edu.pku.sei.tsr.dragon.email.entity.Email;
import cn.edu.pku.sei.tsr.dragon.email.entity.Project;
import cn.edu.pku.sei.tsr.dragon.email.entity.Segment;
import cn.edu.pku.sei.tsr.dragon.email.entity.Session;
import cn.edu.pku.sei.tsr.dragon.email.entity.SessionContent;
//import cn.edu.pku.sei.tsr.dragon.email.utils.StringUtils;
//import cn.edu.pku.sei.tsr.dragon.email.utils.WriteFile;
import cn.edu.pku.sei.tsr.dragon.email.mbox.crawler.ApacheMboxCrawler;
//import cn.edu.pku.sei.tsr.dragon.email.mbox.parser.EmailParser;
import cn.edu.pku.sei.tsr.dragon.email.mbox.parser.MessageParser;
import cn.edu.pku.sei.tsr.dragon.email.mbox.parser.SessionBuilder;
import cn.edu.pku.sei.tsr.dragon.email.utils.Checker;
import cn.edu.pku.sei.tsr.dragon.email.utils.Config;
import cn.edu.pku.sei.tsr.dragon.email.utils.MBoxSpliter;

public class MboxToSession {

	private static final String		luceneUrl					= Config.getLuceneMboxUrl();

	//private static final String		luceneMboxPath				= Config.getLuceneMboxFilePath();

	private static final String luceneMboxPath = "lab/lucene/";

	private static final String		luceneSessionContentPath	= Config.getLuceneSessionContentPath();

	private static final String		tomcatUrl					= Config.getTomcatMboxUrl();

	private static final String		tomcatMboxPath				= Config.getTomcatMboxFilePath();

	private static final String		tomcatSessionContentPath	= Config.getTomcatSessionContentPath();

	protected static final Logger	logger						= Logger.getLogger(MboxToSession.class
																		.getName());
	
	public static Map<JavaTypeInfo, Integer> CodeTypeCnt = new HashMap<JavaTypeInfo, Integer>();
	public static Map<JavaMethodInfo, Integer> CodeMethodCnt = new HashMap<JavaMethodInfo, Integer>();
	public static int codeLines = 0;
	public static int codeBlocks = 0;
	public static int sessionCnt = 0;
	public static int emailCnt = 0;
	
	public static void getMboxFiles() {
		logger.info("start crawl the project Lucene!");
		Project project = new Project();
		project.setProjectID(1);
		project.setProjectName("Lucene");
		project.setProjectUrl("lucene.apache.org");
		project.setMboxPath(luceneMboxPath);
		ArrayList<String> urlList = new ArrayList<String>();
		urlList.add(luceneUrl);
		ApacheMboxCrawler crawler = new ApacheMboxCrawler(project, urlList);
		crawler.Crawl();
		logger.info("lucene mbox crawled end");

		// logger.info("start crawl the project tomcat!");
		// project = new Project();
		// project.setProjectID(2);
		// project.setProjectName("tomcat");
		// project.setProjectUrl("tomcat.apache.org");
		// project.setMboxPath(tomcatMboxPath);
		//
		// urlList = new ArrayList<String>();
		// urlList.add(tomcatUrl);
		// crawler = new ApacheMboxCrawler(project, urlList);
		// crawler.Crawl();
		// logger.info("tomcat mbox crawled end");
	}

	public static void saveMboxToDisk() {
		logger.info("start save poi mbox file to database!");
//		File fileDir = new File(luceneMboxPath);

		File fileDir = new File("D:\\Dragon Project\\mbox_file\\lucene");

		if (fileDir.isDirectory()) {
			File files[] = fileDir.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					parseMboxFile(file, 11);
					System.gc();
				}
			}
		}
		logger.info("lucene poi save to database finished!");
		System.out.println(("CodeLines : " + codeLines));
		System.out.println(("CodeBlocks : " + codeBlocks));
		System.out.println(("sessionS : " + sessionCnt));
		System.out.println(("emailS : " + emailCnt));
		System.out.println(("TypeCode : " + CodeTypeCnt.size()));
		System.out.println(("MethodCode : " + CodeMethodCnt.size()));
		
		File codelist = new File("CodeListFileLucene.txt");
	    if (!codelist.exists()) {
	    	try {
				codelist.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			  
	    try {
			FileOutputStream fop = new FileOutputStream(codelist);
			fop.write(("CodeLines : " + codeLines + "\n").getBytes());
			fop.write(("CodeBlocks : " + codeBlocks + "\n").getBytes());
			fop.write(("sessionS : " + sessionCnt + "\n").getBytes());
			fop.write(("emailS : " + emailCnt + "\n").getBytes());
			fop.write(("TypeCode : " + CodeTypeCnt.size() + "\n").getBytes());
			fop.write(("MethodCode : " + CodeMethodCnt.size() + "\n").getBytes());			
			for (JavaTypeInfo x : CodeTypeCnt.keySet()){
				fop.write((x.getFullyQualifiedName() + "  times : " + CodeTypeCnt.get(x) + "\n").getBytes());
			}
			for (JavaMethodInfo x : CodeMethodCnt.keySet())if (x.ispublic()){
				fop.write((x.getFullyQualifiedName() + "  times : " + CodeMethodCnt.get(x) + "\n").getBytes());
			}			
			fop.flush();
			fop.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// logger.info("start save tomcat mbox file to database");
		// fileDir = new File(tomcatMboxPath);
		// if (fileDir.isDirectory()) {
		// File files[] = fileDir.listFiles();
		// if (files != null && files.length > 0) {
		// for (File file : files) {
		// parseMboxFile(file, 2);
		// }
		// }
		// }
		// logger.info("save tomcat mbox file to database finished!");
	}

	private static void parseMboxFile(File file, int projectID) {
		if (!file.exists()) {
			logger.info("File does not exist!");
			return;
		}

		logger.info("start parse mbox file " + file.getAbsolutePath());
		long start = System.currentTimeMillis();
		ArrayList<String> emails = MBoxSpliter.splitMBox(file);
		ArrayList<Email> emailList = new ArrayList<Email>();
		MessageProcess mp = new MessageProcess();
		try {		
		for (String email : emails) {
			// EmailParser ep = new EmailParser();
			// Email e = ep.parse(email);
			Email e = MessageParser.buildMessage(email);
			if (e == null)
				continue;
			e.setUuid(UUID.randomUUID().toString());
			e.setProjectID(projectID);
			// TODO tmp set the keywords subject
			e.setKeyWords(e.getSubject());
			mp.process(e);
			if (Checker.isGoodMessage(e))
				emailList.add(e);
		}
		File out_file;
		FileOutputStream fop;


		ArrayList<Session> sessionList = new ArrayList<Session>();
		SessionBuilder sessionBuilder = new SessionBuilder(emailList);
		ArrayList<SessionContent> sessionContentList = new ArrayList<SessionContent>();
		sessionBuilder.buildSessionAndContent();
		sessionList = sessionBuilder.getSessionList();
		emailList = sessionBuilder.getEmailList();
		sessionContentList = sessionBuilder.getSessionContentList();
		for (Email e : emailList){
			String path = "Email2" + File.separator + file.getName() + File.separator + e.getUuid() + ".dat";
			//ObjectIO.writeObject(e, path);
			setCodeCnt(e);
		}
		out_file = new File("D:\\Dragon Project\\mbox_file\\luceneCodeReal\\"+file.getName());
	    if (!out_file.exists()) {
		   out_file.createNewFile();
		}			  
		fop = new FileOutputStream(out_file);
		for (Email e : emailList){
			for (Segment seg : e.getEmailContent().getSegments())
				if (seg.getContentType() == Segment.CODE_CONTENT){
					codeLines += seg.getSentences().size();
					codeBlocks ++;
					fop.write((seg.getContentText()+"\n*********\n\n").getBytes());
				}
		}		
		fop.write((sessionList.size() + " "+ emailList.size()).getBytes());
		sessionCnt += sessionList.size() ;
		emailCnt += emailList.size();
		fop.flush();
		fop.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
/*		MessageDao md = new MessageDao();
		for (Email e : emailList) {
			// if(Checker.isGoodMessage(e)) {
			// md.insertMessage(e);
			// // System.out.println("insert a message : " + e.getMessageID());
			// }
			md.insertMessage(e);
		}

		SessionDao sd = new SessionDao();
		for (Session s : sessionList) {
			// s.setProjectID("test project lucene");
			// if(Checker.isGoodSession(s)) {
			// sd.insertSession(s);
			// // System.out.println("insert a session : " + s.getSessionID());
			// }
			if (s.getParticipants().split(",").length == 1)
				continue;
			s.setProjectID(projectID);
			sd.insertSession(s);
		}

		SessionContentDao scd = new SessionContentDao();
		for (SessionContent sc : sessionContentList) {
			// the session length should be longer than 1
			if (sc.getParticipants().split(",").length == 1)
				continue;
			sc.setProjectID(projectID);
			scd.insertSessionContent(sc);
			// write the session content to file

			String subject = StringUtils.legalFileName(sc.getSubject());
//			String filePath = (projectID == 1 ? luceneSessionContentPath : tomcatSessionContentPath)
//					+ "/" + subject + ".txt";
			String filePath = subject + ".txt";
			WriteFile.writeStringToFile(sc.getContent(), filePath);
		}*/
		long end = System.currentTimeMillis();
		logger.info("process mbox file " + file.getAbsolutePath() + " end!");
		System.out.println("process mbox file " + file.getAbsolutePath() + " cost time "
				+ (end - start) + " mils");

	}
	private static void setCodeCnt(Email e){
		for (ParagraphInfo para : e.getContentInfo().getParagraphList()){
			for (SentenceInfo sent : para.getSentences()){
				for (CodeLikeTermInfo term : sent.getCodeLikeTerms()){
					dfs(term);
				}
			}
		} 
	}
	
	private static void dfs(CodeLikeTermInfo term){
		if (term.isLeaf()){
			JavaBaseInfo coder = term.getRelevantCodeElement();
			if (coder != null){
				if (coder instanceof JavaTypeInfo){
					if (CodeTypeCnt.containsKey(coder)){
						CodeTypeCnt.put((JavaTypeInfo)coder, CodeTypeCnt.get(coder)+1);
					}else CodeTypeCnt.put((JavaTypeInfo)coder, 1);
				}
				if (coder instanceof JavaMethodInfo){
					if (CodeMethodCnt.containsKey(coder)){
						CodeMethodCnt.put((JavaMethodInfo)coder, CodeMethodCnt.get(coder)+1);
					}else CodeMethodCnt.put((JavaMethodInfo)coder, 1);
				}				
			}
		}
		for (CodeLikeTermInfo subterm : term.getCodeLikeTermList()){
			dfs(subterm);
		}
	}
	
	public static void main(String args[]) {
		// getMboxFiles();
		saveMboxToDisk();
		try {
			CodeTermsParser.fop.flush();
			CodeTermsParser.fop.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
