package cn.edu.pku.sei.tsr.dragon.wordscluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public  class Program
    {
       public static void main(String[] args) throws IOException
        {
            //1����ȡ�ĵ�����
    	    List<String> phrases = new ArrayList<>();
    	    //.readDocuments();
            //String[] docs = getInputDocs("D:\\test\\test\\1.txt");
            String[] docs=new String[phrases.size()];
            docs = phrases.toArray(docs);
            System.out.print(docs.length);
            if (docs.length < 1)
            {
                System.out.println("û���ĵ�����");
                System.in.read();
              //  System.exit(0);
                return;
            }
            /*else{
            	for(String s:docs){
            		System.out.println(s);
            	}
            }*/

            //2����ʼ��TFIDF����������������ÿ���ĵ���TFIDFȨ��
            TFIDFMeasure tf = new TFIDFMeasure(docs, new Tokeniser());
            //System.out.println(tf.get_numTerms());
            System.out.println("terms num ====  "+tf.get_numTerms());

            int K = 200; //�۳�3������

            //3������k-means���������ݣ���һ���������飬��һά��ʾ�ĵ�������
            //�ڶ�ά��ʾ�����ĵ��ֳ��������д�
            double[][] data = new double[docs.length][];
            int docCount = docs.length; //�ĵ�����
            int dimension = tf.get_numTerms();//���дʵ���Ŀ
            for (int i = 0; i < docCount; i++)
            {
//                for (int j = 0; j < dimension; j++)
//                {
                    data[i] = tf.GetTermVector2(i); //��ȡ��i���ĵ���TFIDFȨ������
//                }
            }
            System.out.println("done!!!!!");

            //4����ʼ��k-means�㷨����һ��������ʾ�������ݣ��ڶ���������ʾҪ�۳ɼ�����
            WawaKMeans kmeans = new WawaKMeans(data, K);
            System.out.println("init finished!!!!!!!!!");
            
//            PrintStream myout = new PrintStream(new FileOutputStream(new File("result50___.txt")));       
//            System.setOut(myout);        
//            System.setErr(myout);
            //5����ʼ����
            System.out.println("begin to kmeans!!!!");
            kmeans.Start();
            System.out.println(docCount+"   "+dimension);
//
            //6����ȡ�����������
            WawaCluster[] clusters = kmeans.getClusters();
            int clusterNum=0;
            for(WawaCluster cluster : clusters){
                List<Integer> members = cluster.CurrentMembership;
                PrintStream myout = new PrintStream(new FileOutputStream(new File("result200/result"+clusterNum+".txt")));       
                System.setOut(myout);        
                System.setErr(myout);
                System.out.println("===============");
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println("===============");
                for (int i : members)
                {
                	System.out.println(docs[i]);
                }      
                clusterNum++;
            }
            
            
            
//            for (WawaCluster cluster : clusters)
//            {
//                List<Integer> members = cluster.CurrentMembership;
//                System.out.println("-----------------");
////                for (WawaCluster wawaCluster : clusters) {
////                	System.out.println(docs[i]);
////				}
//                for (int i = 0; i < clusters.length; i++) {
//                	System.out.println(i+"------"+docs[i]);
//				}
//            }
        }

        /// <summary>
        /// ��ȡ�ĵ�����
        /// </summary>
        /// <returns></returns>
        private static String[] getInputDocs(String file)
        {
            List<String> ret = new ArrayList<String>();
            
            try
            {
                BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                {
                    String temp;
                    while ((temp = br.readLine()) != null)
                    {
                        ret.add(temp);
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            String[] fileString=new String[ret.size()];
            return ret.toArray(fileString);
        }
    }
