package cn.edu.pku.sei.tsr.dragon.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.CodeParser;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaBaseInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaClassInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaMethodInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaTypeInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.CodeLikeTermInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class CodeTermsParser {

	private JavaProjectInfo project;

	private Map<JavaBaseInfo, Integer> contentCodes;
	private Map<JavaBaseInfo, Integer> paragraphCodes;
	private Map<JavaBaseInfo, Integer> sentenceCodes;
	private Map<JavaBaseInfo, Integer> termCodes;
	private static final int is_for_Package = 0;
	private static final int is_for_Type = 1;
	private int flag = 0;
	private static double threshhold = 0.95;
	private static File out_file;
	public static FileOutputStream fop;
	static {

	try {
			out_file = new File("newfile.txt");
		    if (!out_file.exists()) {
			   out_file.createNewFile();
			}			  
			fop = new FileOutputStream(out_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public CodeTermsParser(){
		super();
	}
	
	public CodeTermsParser(JavaProjectInfo project) {
		super();
		this.project = project;
	}
	
	public void setJavaProject(JavaProjectInfo project){
		this.project = project;
	}
	public JavaProjectInfo getJavaProject(){
		return this.project;
	}
	
	//先找相似的，后判定类，最后判定方法
	public void parseRelativeCodeTerms(ContentInfo content){
		for (ParagraphInfo paragraph : content.getParagraphList()){
			if (paragraph.isCodeFragment()) continue;
			for (SentenceInfo sentence : paragraph.getSentences()){
				for (CodeLikeTermInfo term : sentence.getCodeLikeTerms()){
					parseCodeLikeTerms(term);
					findSimilarTypeCodeElements(term);
					findSimilarMethodCodeElements(term);
				}
			}
		}		
		this.flag = this.is_for_Package;		
		getAllCodes(content);
		for (ParagraphInfo paragraph : content.getParagraphList()){
			if (paragraph.isCodeFragment()) continue;
			getAllCodes(paragraph);
			for (SentenceInfo sentence : paragraph.getSentences()){
				getAllCodes(sentence);
				for (CodeLikeTermInfo term : sentence.getCodeLikeTerms()){
					getAllCodes(term);
					parseTypeContextFilter(term);
				}
			}
		}		
		this.flag = this.is_for_Type;		
		getAllCodes(content);
		for (ParagraphInfo paragraph : content.getParagraphList()){
			if (paragraph.isCodeFragment()) continue;
			getAllCodes(paragraph);
			for (SentenceInfo sentence : paragraph.getSentences()){
				getAllCodes(sentence);
				for (CodeLikeTermInfo term : sentence.getCodeLikeTerms()){
					getAllCodes(term);
					parseMethodContextFilter(term);
				}
			}
		}		
	}
	
	private void getAllCodes(ContentInfo content){
		contentCodes = new HashMap<JavaBaseInfo,Integer>();
		for (ParagraphInfo paragraph: content.getParagraphList()){
			if (paragraph.isCodeFragment()) continue;
			getAllCodes(paragraph);
			for (JavaBaseInfo coder : paragraphCodes.keySet()){
				if (contentCodes.containsKey(coder)){
					contentCodes.put(coder,contentCodes.get(coder) + paragraphCodes.get(coder));
				}else contentCodes.put(coder,paragraphCodes.get(coder));
			}			
		}
	}
	
	private void getAllCodes(ParagraphInfo paragraph){
		paragraphCodes = new HashMap<JavaBaseInfo,Integer>();
		for (SentenceInfo sentence : paragraph.getSentences()){
			getAllCodes(sentence);
			for (JavaBaseInfo coder : sentenceCodes.keySet()){
				if (paragraphCodes.containsKey(coder)){
					paragraphCodes.put(coder,paragraphCodes.get(coder) + sentenceCodes.get(coder));
				}else paragraphCodes.put(coder,sentenceCodes.get(coder));
			}			
		}
	}
	
	private void getAllCodes(SentenceInfo sentence){
		sentenceCodes = new HashMap<JavaBaseInfo,Integer>();
		for (CodeLikeTermInfo term : sentence.getCodeLikeTerms()){
			getAllCodes(term);
			for (JavaBaseInfo coder : termCodes.keySet()){
				if (sentenceCodes.containsKey(coder)){
					sentenceCodes.put(coder,sentenceCodes.get(coder) + termCodes.get(coder));
				}else sentenceCodes.put(coder,termCodes.get(coder));
			}			
		}
	}
	
	private void getAllCodes(CodeLikeTermInfo term){
		termCodes = new HashMap<JavaBaseInfo,Integer>();
		getAllCode(term);
	}
	//找到所有当前上下文的类或包
	private void getAllCode(CodeLikeTermInfo term){
		if (term.isLeaf()){
			if (this.flag == this.is_for_Package){
				for (JavaBaseInfo coder : term.getSimilarTypeCodeElementList().keySet()){
					JavaBaseInfo pack = ((JavaTypeInfo)coder).getJavaPackage();
					if (termCodes.containsKey(pack)){
						termCodes.put(pack,termCodes.get(pack)+1);
					}else termCodes.put(pack,1);
				}
			}else{
				for (JavaBaseInfo coder : term.getSimilarTypeCodeElementList().keySet()){
					if (termCodes.containsKey(coder)){
						termCodes.put(coder,termCodes.get(coder)+1);
					}else termCodes.put(coder,1);
					if (coder instanceof JavaClassInfo){
						JavaClassInfo clazz = ((JavaClassInfo)coder).getSuperClass();
						if (termCodes.containsKey(clazz)){
							termCodes.put(coder,termCodes.get(clazz)+1);
						}else termCodes.put(clazz,1);
					}
				}
			}
		}
		for (CodeLikeTermInfo subTerm : term.getCodeLikeTermList()){
			getAllCode(subTerm);
		}
	}
	//递归分析用'.'，隔开的表达式xxx.xxx.xxx
	private void parseCodeLikeTerms(CodeLikeTermInfo term){
		String text = term.getContent();
		int last = 0;
		List<CodeLikeTermInfo> list = term.getCodeLikeTermList();
		//while (!Character.isJavaIdentifierStart(text.charAt(last))) last++;
		boolean flag = true;
		for (int i = 0; i < text.length(); i++){
			if (text.charAt(i) == '('){
				int cnt = 1;
				while (cnt != 0){
					i++;
					if (i >= text.length()) {
						flag = false;
						break;
					}
					if (text.charAt(i) == ')') cnt--;
					if (text.charAt(i) == '(') cnt++;
				}				
				continue;
			}
			if (text.charAt(i) == '.'){
				if (i - last > 0){
					CodeLikeTermInfo coder = new CodeLikeTermInfo(text.substring(last, i));
					coder.setParent(term.getParent());
					list.add(coder);
					parseCodeLikeTerms(coder);
				}
				last = i + 1;
			}
		}
		if (list.size() == 0){
			term.setLeaf(true);
			String s = term.getContent();
			if (s.indexOf('(') >= 0){
				term.setContent(s.substring(0,s.indexOf('(')));
				if (flag){
					parseBracketTerms(term,s.substring(s.indexOf('(')));
					term.setParameterMethod(true);
					term.setParameterNum(term.getCodeLikeTermList().size());
				}
				term.setMethod(true);
			}
			System.out.println(term.getContent());
			for (CodeLikeTermInfo t : term.getCodeLikeTermList()) t.setContextfather(term);
			if (!term.isMethod()){
				term.setMethod(true);
				term.setType(true);
			}
		}else if (flag){			
			CodeLikeTermInfo coder = new CodeLikeTermInfo(text.substring(last, text.length()));
			coder.setParent(term.getParent());
			list.add(coder);
			parseCodeLikeTerms(coder);
		}
	}
	//将用都好隔开的表达式递归分析xxx,xxx,xxx
	private void parseBracketTerms(CodeLikeTermInfo term, String content){
		String text = content;
		text = text.substring(1,text.length() - 1) + ',';
		int last = 0;
		List<CodeLikeTermInfo> list = term.getCodeLikeTermList();
		//while (!Character.isJavaIdentifierStart(text.charAt(last))) last++;
		boolean flag = true;		
		for (int i = 0; i < text.length(); i++){
			if (text.charAt(i) == '('){
				int cnt = 1;
				while (cnt != 0){
					i++;
					if (i >= text.length()) {
						flag = false;
						break;
					}					
					if (text.charAt(i) == ')') cnt--;
					if (text.charAt(i) == '(') cnt++;
				}				
				continue;
			}
			if (!flag) break;
			if (text.charAt(i) == ','){
				if (i - last > 0){
					CodeLikeTermInfo coder = new CodeLikeTermInfo(text.substring(last, i));
					coder.setParent(term.getParent());
					list.add(coder);
					parseCodeLikeTerms(coder);
				}
				last = i + 1;
			}
		}		
	}
	//寻找名字相似的所有类名
	private void findSimilarTypeCodeElements(CodeLikeTermInfo term){
		if (term.getContent().contains("NumberRecord")){
			System.out.println();
		}
		for (CodeLikeTermInfo subTerm : term.getCodeLikeTermList()){
			findSimilarTypeCodeElements(subTerm);
		}
		if (!term.isLeaf()) return;
		Map<JavaBaseInfo,Double> map = new HashMap<JavaBaseInfo,Double>();
		project.getClasses().forEach(
				clazz->{
					double similar = computeSimilar(term,clazz); 
					if (similar > CodeTermsParser.threshhold){
						map.put(clazz, similar);
					}
				});
		project.getInterfaces().forEach(
				clazz->{
					double similar = computeSimilar(term,clazz); 
					if (similar > CodeTermsParser.threshhold){
						map.put(clazz, similar);
					}
				});
		
		term.setSimilarTypeCodeElementList(map);
	}
	//寻找名字相似的所有方法名
	private void findSimilarMethodCodeElements(CodeLikeTermInfo term){
		for (CodeLikeTermInfo subTerm : term.getCodeLikeTermList()){
			findSimilarMethodCodeElements(subTerm);
		}		
		if (!term.isLeaf()) return;
		Map<JavaBaseInfo,Double> map = new HashMap<JavaBaseInfo,Double>();
		project.getClasses().forEach(clazz->clazz.getMethods().forEach(method->{
					double similar = computeSimilar(term,method);
					if (similar > CodeTermsParser.threshhold){
						if ((term.isParameterMethod())&&(method.getArgs().size() == term.getParameterNum()))
							similar += 0.5;
						map.put(method, similar);
					}
				}));
		term.setSimilarMethodCodeElementList(map);
	}
	//如果有类型全匹配的方法，直接判定
	private void parseMethodContextFilter(CodeLikeTermInfo term){
		if (term.getContent().contains("getLastCellNum")){
			System.out.println();
		}
		for (CodeLikeTermInfo subTerm : term.getCodeLikeTermList()){
			parseMethodContextFilter(subTerm);
		}
		boolean flag = false;
		if (term.isLeaf() && term.isMethod()){
			if (term.isParameterMethod()){
				int max = 0;
				for (JavaBaseInfo coder : term.getSimilarMethodCodeElementList().keySet()){
					int cnt = 0;
					if (((JavaMethodInfo)coder).getArgs().size() != term.getParameterNum()) continue; 
					for (int i = 0; i < term.getCodeLikeTermList().size(); i++){
						CodeLikeTermInfo subTerm = term.getCodeLikeTermList().get(i);
						if ((subTerm.getRelevantCodeElement() != null)
								&&(subTerm.getRelevantCodeElement() instanceof JavaTypeInfo)){
							if  (((JavaMethodInfo)coder).getArgs().get(i).getName() ==
									subTerm.getRelevantCodeElement().getName()){
								cnt++;
							}
						}
						if ((subTerm.getRelevantCodeElement() != null)
								&&(subTerm.getRelevantCodeElement() instanceof JavaMethodInfo)){
							if  (((JavaMethodInfo)coder).getArgs().get(i).getName() ==
									((JavaMethodInfo)subTerm.getRelevantCodeElement()).getBelongType().getName()){
								cnt++;
							}
						}						
					}
					if (cnt > max){
						max = cnt;
						term.setRelevantCodeElement(coder);
					}
				}
			}
			
			if (term.getSimilarMethodCodeElementList().size() > 1) flag = true; 
			if (term.getRelevantCodeElement() == null) getMaxCoderByContext(term,termCodes);
			if (term.getRelevantCodeElement() == null) getMaxCoderByContext(term,sentenceCodes);
			if (term.getRelevantCodeElement() == null) getMaxCoderByContext(term,paragraphCodes);
			if (term.getRelevantCodeElement() == null) getMaxCoderByContext(term,contentCodes);
			if (term.getRelevantCodeElement() == null) getMaxCoderByContextPackage(term,contentCodes);
			if (term.getRelevantCodeElement() == null) {
				if (term.getSimilarMethodCodeElementList().size() > 0){
					term.setRelevantCodeElement((JavaBaseInfo)term.getSimilarMethodCodeElementList().keySet().toArray()[0]);
				}else
				term.setMethod(false);
			}
		}
		if (term.isMethod()){
			System.out.println(term.getContent() + " "+((JavaMethodInfo)term.getRelevantCodeElement()).getFullyQualifiedName());
			String tmp = term.getParent().getContent() + " *** ";
			tmp = tmp + ((JavaMethodInfo)term.getRelevantCodeElement()).getFullyQualifiedName() + "\n";
			if (((JavaMethodInfo)term.getRelevantCodeElement()).getFullyQualifiedName().
			contains("RowRecordsAggregate.getFirstRowNum")){
				System.out.println();
			}
			try {
				if (flag)
				fop.write(tmp.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void parseTypeContextFilter(CodeLikeTermInfo term){
		for (CodeLikeTermInfo subTerm : term.getCodeLikeTermList()){
			parseTypeContextFilter(subTerm);
		}
		boolean flag = false;
		if (term.isLeaf() && term.isType()){
			if (term.getSimilarTypeCodeElementList().size() > 1) flag = true;			
			if (term.getRelevantCodeElement() == null) getMaxCoderByContext(term,termCodes);
			if (term.getRelevantCodeElement() == null) getMaxCoderByContext(term,sentenceCodes);
			if (term.getRelevantCodeElement() == null) getMaxCoderByContext(term,paragraphCodes);
			if (term.getRelevantCodeElement() == null) getMaxCoderByContext(term,contentCodes);
			if (term.getRelevantCodeElement() == null) {
				if (term.getSimilarTypeCodeElementList().size() > 0){
					term.setRelevantCodeElement((JavaBaseInfo)term.getSimilarTypeCodeElementList().keySet().toArray()[0]);
				}else
				term.setType(false);
			}
		}
		if (term.isType()){
			term.setMethod(false);
			System.out.println(term.getContent() + " "+((JavaTypeInfo)term.getRelevantCodeElement()).getFullyQualifiedName());
			String tmp = term.getParent().getContent() + " *** ";
			tmp = tmp + ((JavaTypeInfo)term.getRelevantCodeElement()).getFullyQualifiedName() + "\n";
			try {
				if (flag)
				fop.write(tmp.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	//根据不同的上下文，计算频度，以此确定是哪个，类用包，方法用类
	private void getMaxCoderByContextPackage(CodeLikeTermInfo term, Map<JavaBaseInfo, Integer> codes){
		int max = 0;
		Map<JavaBaseInfo, Integer> packs = new HashMap<JavaBaseInfo, Integer>();
		for  (JavaBaseInfo coder : codes.keySet()){
			if (packs.containsKey(((JavaTypeInfo)coder).getJavaPackage())){
				packs.put(((JavaTypeInfo)coder).getJavaPackage(),
						packs.get(((JavaTypeInfo)coder).getJavaPackage()) + codes.get(coder));
			}else packs.put(((JavaTypeInfo)coder).getJavaPackage(),codes.get(coder));
		}
		for (JavaBaseInfo coder : term.getSimilarMethodCodeElementList().keySet()){
			JavaBaseInfo pack = ((JavaMethodInfo)coder).getBelongType().getJavaPackage();
			if (packs.containsKey(pack) && packs.get(pack) > max){
				max = packs.get(pack);
				term.setRelevantCodeElement(coder);
			}
		}
		Map<JavaBaseInfo,Double> tmp = new HashMap<JavaBaseInfo,Double>();
		for (JavaBaseInfo coder : term.getSimilarMethodCodeElementList().keySet()){
			JavaBaseInfo pack = ((JavaMethodInfo)coder).getBelongType().getJavaPackage();
			
			if ((packs.containsKey(pack))&&(packs.get(pack) == max)){
				tmp.put(coder, 1.0);
			}
			if (tmp.size() > 0) term.setSimilarMethodCodeElementList(tmp);
			if (tmp.size() > 1) term.setRelevantCodeElement(null);
		}			
		
	}
	private void getMaxCoderByContext(CodeLikeTermInfo term, Map<JavaBaseInfo, Integer> codes){
		int max = 0;
		if (this.flag == this.is_for_Package){
			for (JavaBaseInfo coder : term.getSimilarTypeCodeElementList().keySet()){
				JavaBaseInfo pack = ((JavaTypeInfo)coder).getJavaPackage();
				if (codes.containsKey(pack) && codes.get(pack) > max){
					max = codes.get(pack);
					term.setRelevantCodeElement(coder);
				}
			}
			Map<JavaBaseInfo,Double> tmp = new HashMap<JavaBaseInfo,Double>();
			for (JavaBaseInfo coder : term.getSimilarTypeCodeElementList().keySet()){
				JavaBaseInfo pack = ((JavaTypeInfo)coder).getJavaPackage();
				
				if (codes.containsKey(pack)&&(codes.get(pack) == max)){
					tmp.put(coder, 1.0);
				}
				if (tmp.size() > 0) term.setSimilarTypeCodeElementList(tmp);
				if (tmp.size() > 1) term.setRelevantCodeElement(null);
			}
		}else{
			for (JavaBaseInfo coder : term.getSimilarMethodCodeElementList().keySet()){
				JavaBaseInfo clazz = ((JavaMethodInfo)coder).getBelongType();
				if (codes.containsKey(clazz) && codes.get(clazz) > max){
					max = codes.get(clazz);
					term.setRelevantCodeElement(coder);
				}
			}
			Map<JavaBaseInfo,Double> tmp = new HashMap<JavaBaseInfo,Double>();
			for (JavaBaseInfo coder : term.getSimilarMethodCodeElementList().keySet()){
				JavaBaseInfo clazz = ((JavaMethodInfo)coder).getBelongType();
				
				if ((codes.containsKey(clazz))&&(codes.get(clazz) == max)){
					tmp.put(coder, 1.0);
				}
				if (tmp.size() > 0) term.setSimilarMethodCodeElementList(tmp);
				if (tmp.size() > 1) term.setRelevantCodeElement(null);
			}			
		}
	}
	//计算编辑距离
	private double computeSimilar(CodeLikeTermInfo term, JavaBaseInfo element){
		String s1 = element.getName().toString();
		String s2 = term.getContent();
		int l1 = s1.length();
		int l2 = s2.length();		
		if (s1.contains(s2) || s2.contains(s1)) return 1 - Math.abs(l1 - l2)/10.0 ;
		if  (Math.abs(l1 - l2) > 1) return 0;
		//if  (Math.abs(l1 - l2) > Math.min(l1,l2)* 0.2) return 0;
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		int f[][] = new int [l1+1][l2+1];
		for (int i = 0; i < s1.length(); i++) f[i][0] = i;
		for (int i = 0; i < s2.length(); i++) f[0][i] = i;
		for (int i = 1; i <= s1.length(); i++){
			for (int j = 1; j <= s2.length(); j++){
				if (s1.charAt(i-1) != s2.charAt(j-1)) f[i][j] = f[i-1][j-1] + 1; else
					f[i][j] = f[i-1][j-1];
				f[i][j] = Math.min(f[i][j],Math.min(f[i-1][j],f[i][j-1])+1);
			}
		}
		return 1 - 1.0 * f[l1][l2] / Math.max(l1, l2);
	}

	public static void main(String args[]) throws FileNotFoundException{
		JavaProjectInfo project = CodeParser.parse("lucene", "D:\\Codes\\lucene-5.2.1");
		System.out.println(project.getClasses().size());
		System.out.println(project.getInterfaces().size());
		int cnt = 0;
		for (JavaTypeInfo x : project.getClasses())
		{
			for (JavaMethodInfo y : x.getMethods()) if (y.ispublic())
			cnt ++;
		}
		System.out.println(cnt);
		cnt = 0;
		for (JavaTypeInfo x : project.getInterfaces())
		{
			for (JavaMethodInfo y : x.getMethods()) if (y.ispublic())
			cnt ++;
		}		
		System.out.println(cnt);
		//ObjectIO.writeObject(project, String.format("%s.dat", project.getUuid()));
//		File rootDir = new File("D:\\Dragon Project\\data-codesnippet\\" + "5d8e8142-c20e-4f87-97e0-a79b6d81f5b7.dat");
//		JavaProjectInfo project = (JavaProjectInfo)ObjectIO.readObject(rootDir);
//		CodeLikeTermInfo term = new CodeLikeTermInfo("QueryParser");
/*		System.out.println(project.getClasses().size());
		System.out.println(project.getInterfaces().size());
		int cnt = 0;
		int tot = 0;
		long startTime = System.currentTimeMillis();
		CodeTermsParser parser = new CodeTermsParser(project);
		for (int i = 0; i < 1000; i++){
			for (JavaTypeInfo x : project.getClasses())
			{
				cnt += x.getMethods().size();
				parser.computeSimilar(term,x);
				for (JavaMethodInfo y : x.getMethods()){
					parser.computeSimilar(term,y);
				}
			}
			for (JavaTypeInfo x : project.getInterfaces())
			{
				parser.computeSimilar(term,x);
				cnt += x.getMethods().size();
				for (JavaMethodInfo y : x.getMethods()){
					parser.computeSimilar(term,y);
				}			
			}
		}
		System.out.println(cnt);
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");*/
		
		Scanner sc = new Scanner(new File("testdata/test.txt"));
		String sent = "";
		while (sc.hasNext()) {
			sent = sent + sc.nextLine() + "\n";
		}
		String test = sent;
		System.err.println("test:" + test);
		ContentInfo content = new ContentInfo(test);
		content.setHTMLContent(true);
		ContentParser.parseContent(content);
		for (ParagraphInfo paragraph : content.getParagraphList()){
			if (paragraph.isCodeFragment()) continue;
			for (SentenceInfo sentence : paragraph.getSentences()){
				ContentParser.replaceCodeLikeTerms(sentence);
			}
		}
//		File rootDir = new File("testdata/data.data");
//		content = (ContentInfo)ObjectIO.readObject(rootDir);

		CodeTermsParser parser = new CodeTermsParser(project);
		parser.parseRelativeCodeTerms(content);
		//SyntaxParser.extractPhrases(content);
		ObjectIO.writeObject(content, String.format("%s.dat", content.getUuid()));		
//		parseRelativeCodeTerms(content);
//		parser.setJavaProject(project);
//		parser.parseRelativeCodeTerms(content);		
	}
}
