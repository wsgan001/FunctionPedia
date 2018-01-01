package cn.edu.pku.sei.tsr.dragon.stackoverflow;

import java.io.File;

import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

/**
 * Created by maxkibble on 2015/12/24.
 */
public class TrainVector {

	public static void main(String args[]) throws Exception {
		TrainVector trainVector = new TrainVector();
		String corpusDir = Config.getDataObjDir() + File.separator + ObjectIO.SOCORPUS
				+ File.separator + "apache-poi_corpus.txt";
		Word2Vec word2Vec = new Word2Vec();
		word2Vec.trainModel(corpusDir);
	}
}
