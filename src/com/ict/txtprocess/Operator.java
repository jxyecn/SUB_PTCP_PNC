package com.ict.txtprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.ict.txtprocess.bean.Subwords;
import com.ict.txtprocess.bean.Word;
import com.ict.txtprocess.utils.FileIO;
import com.ict.txtprocess.utils.WordUtils;


public class Operator {
	//存“待处理”
	ArrayList<String> wordSourceArray;
	//存“读音”
	ArrayList<String> targetSourceArray;
	//存储将在读音最后加上的内容
	ArrayList<String> targetOutputArray;
	//词典，用Hashmap增加读取速度，一个字允许有多个读音
	//Key为原语，ArrayList<String>中存储每一个读音
	HashMap<String, ArrayList<String>> lexiconHashmap;
	//存储原词与分词相对应的Hashmap
	HashMap<String, String[]> wordsHashmap;
	//需要存储部分失败的词
	ArrayList<String> failedOutputArray;
	//需要存储存在多音字的词
	Set<String> variousOutputSet;
	ArrayList<String> variousOutputArray;
	
	WordUtils wordOperator;
	FBSegment fbSegment;
	
	String sourceURL = "source/lexicon_待处理（仅有原词）.txt";
	String targetURL = "output/lexicon_读音（增补后_原词_读音）.txt";
	
	
	public void pncProcess(){
		//1. 生成HashMap，原词——读音 对应，从读音生成
		generateDict();
		//2. 对于每一行source中的文本，取得其原词、分词,也用HashMap来做
		generateSourceMap();
		//3. 对于每一项原词，看Dict里是否已经存在
		//这里可以选择用递归，处理一个string，匹配失败就分开写分词
		Iterator<Entry<String, String[]>> iter = wordsHashmap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String[]> entry = (Entry<String, String[]>) iter.next();
			String originString = entry.getKey();
			String[] subStrings = entry.getValue();
			
			//已包含原词，直接跳过
			if(lexiconHashmap.containsKey(originString))
				continue;
			//原词未包含，提取分词，构造String输出到目标位置
			else
				processSingleWord(subStrings,originString);
		}
		
		//4.将最后生成出的内容写入文本中
		String tempURL1 = new String("output/TEST.txt");
		FileIO.writeArrayByBufferedReader(targetOutputArray,tempURL1,true);
		String tempURL2 = new String("output/ERROR.txt");
		FileIO.writeArrayByBufferedReader(failedOutputArray,tempURL2,true);
		
		variousOutputArray = wordOperator.set2ArrayAdapter(variousOutputSet);
		
		String tempURL3 = new String("output/VARIOUS.txt");
		FileIO.writeArrayByBufferedReader(variousOutputArray,tempURL3,true);
	}
	
	//不是已经存在的，那么按分词方式来做
	public void processSingleWord(String[] subStrings,String originString){
//		if(originString.equals("机械厂"))
//			System.out.println("处理词组'机械厂'");
		
		StringBuilder outputStringBuilder = new StringBuilder();
		StringBuilder tempProunStringBuilder = new StringBuilder();
		
		for(String subString:subStrings){
			ArrayList<String> prouns = null;
			//依次取出每个分词，先加到输出序列里
			if(lexiconHashmap.containsKey(subString)){
				prouns = lexiconHashmap.get(subString);
//				outputStringBuilder.append(subString).append(" ");
				if(prouns.size() == 1)
					tempProunStringBuilder.append(" ").append(prouns.get(0));
				else{
					//将原始词加入
//					variousOutputArray.add(originString);
					variousOutputSet.add(originString);
					
					tempProunStringBuilder.append(" ");
					for(int i=0;i<prouns.size()-1;i++)
						tempProunStringBuilder.append(prouns.get(i)).append("|");
					
					tempProunStringBuilder.append(prouns.get(prouns.size()-1));
				}
			}else{
				ArrayList<String> finalSubString = new ArrayList<>();
				processUnexistWord(subString,finalSubString);
				try{
					for(String str:finalSubString){
						prouns = lexiconHashmap.get(str);
//						outputStringBuilder.append(str).append(" ");
						//说明此分词只有单一读音
						if(prouns.size() == 1)
							tempProunStringBuilder.append(" ").append(prouns.get(0));
						else{
							
							tempProunStringBuilder.append(" ");
							for(int i=0;i<prouns.size()-1;i++)
								tempProunStringBuilder.append(prouns.get(i)).append("|");
							tempProunStringBuilder.append(prouns.get(prouns.size()-1));
						}
						
					}
				}catch(Exception e){
					System.out.println("--------ERROR INFO---------");
					System.out.println("FUNCTION NAME:processSingleWord");
					System.out.println("ERROR on string:" + subString);
					System.out.println("-------ERROR INFO END--------");
					e.printStackTrace();
				}
				
			}
			
		}
		
		//分词序列输出完毕，直接加上原词、读音串输出
		outputStringBuilder.append(originString);
		outputStringBuilder.append(" ").append(tempProunStringBuilder);
		
		targetOutputArray.add(outputStringBuilder.toString());
	}
	
	public void processUnexistWord(String sourceString,ArrayList<String> target){
		//目标存储位置为空，说明是被人为置空的，可能发生了匹配失败的异常状况，所以直接返回
		if(target == null)
			return;
		String[] subWords;
		if(lexiconHashmap.containsKey(sourceString)){
			target.add(sourceString);
		}
		else{
			try{
				subWords = wordsHashmap.get(sourceString);
				//根本不存在，那么直接拆成单字
				if(subWords == null){
					
					/***
					 * 2017年10月5日更改   使用双向最大匹配方式来进行分词
					 */
					
					Vector<String> tempSegResult = fbSegment.segment(sourceString);
					subWords = new String[tempSegResult.size()];
					for(int i=0;i<subWords.length;i++)
						subWords[i] = tempSegResult.get(i);
					
//					subWords = new String[sourceString.length()];
//					for(int i=0;i<sourceString.length();i++)
//						subWords[i] = sourceString.charAt(i) + "";
					
				}else{
					//如果前后取得的内容完全相同，那么必须完全拆分成单字
					if(subWords.length == 1){
						/***
						 * 2017年10月5日更改   使用双向最大匹配方式来进行分词
						 */
						Vector<String> tempSegResult = fbSegment.segment(sourceString);
						subWords = new String[tempSegResult.size()];
						for(int i=0;i<subWords.length;i++)
							subWords[i] = tempSegResult.get(i);
						
//						subWords = new String[sourceString.length()];
//						for(int i=0;i<sourceString.length();i++)
//							subWords[i] = sourceString.charAt(i) + "";
					}
				}
				
				for(String str:subWords){
					//TODO:当单字也不存在于发音词典中时，直接返回，后续再加处理
					if(str.length() == 1 && !lexiconHashmap.containsKey(str)){
						failedOutputArray.add(sourceString);
						target = null;
						return;
					}
						
					
					if(lexiconHashmap.containsKey(str))
						target.add(str);
					else{
						try{
							processUnexistWord(str, target);
						}catch(StackOverflowError e){
							System.out.println("-----StackOverflowError------");
							System.out.println("source word:" + str);
						}
					}
				}
			}catch(Exception e){
				System.out.println("--------ERROR INFO---------");
				System.out.println("FUNCTION-NAME:processUnexistWord");
				System.out.println("ERROR on line:" + sourceString);
				System.out.println("-------ERROR INFO END--------");
				e.printStackTrace();
			}
		}
	}
	
	//得到待处理原词、分词
	public void generateSourceMap(){
		System.out.println("start generateSourceMap");
		wordsHashmap = new HashMap<>();
		Iterator<String> iter = wordSourceArray.iterator();
		while(iter.hasNext()){
			Subwords subwords = wordOperator.getSubdByLine(iter.next());
			
			String[] subWordsByDict;
			/***
			 * 2017年10月5日更改   使用双向最大匹配方式来进行分词
			 */
			Vector<String> tempSegResult = fbSegment.segment(subwords.getOriginWord());
			subWordsByDict = new String[tempSegResult.size()];
			for(int i=0;i<subWordsByDict.length;i++)
				subWordsByDict[i] = tempSegResult.get(i);
			subwords.setSubWords(subWordsByDict);
			
			wordsHashmap.put(subwords.getOriginWord(), subwords.getSubWords());
		}
		System.out.println("generateSourceMap succeeded");
	}
	
	//得到参考词典原词、读音
	public void generateDict(){
		System.out.println("start generateDict");
		lexiconHashmap = new HashMap<>();
		Set<String> dictSet = new HashSet<>();
		Iterator<String> iter = targetSourceArray.iterator();
		while(iter.hasNext()){
			//从下一行中拿到一个word的bean
			Word word = wordOperator.getWordByLine(iter.next());
			dictSet.add(word.getWord());
			//已经包含了一个Key
			if(lexiconHashmap.containsKey(word.getWord())){
				lexiconHashmap.get(word.getWord()).add(word.getProun());
			}else{
				ArrayList<String> temp = new ArrayList<>();
				temp.add(word.getProun());
				lexiconHashmap.put(word.getWord(),temp);
			}
		}
		fbSegment.setSegDict(dictSet);
		System.out.println("generateDict succeeded");
	}
	
	public void handleInput(){
		FileIO.readByBufferedReader(this.sourceURL, this.wordSourceArray);
		FileIO.readByBufferedReader(this.targetURL, this.targetSourceArray);
		this.wordOperator = WordUtils.getInstanceDC();
		System.out.println("Input succeed;");
		System.out.println("size of wordSourceArray:" + wordSourceArray.size());
		System.out.println("size of targetSourceArray:" + targetSourceArray.size());
	}
	
	public void init(){
		wordSourceArray = new ArrayList<>();
		targetSourceArray = new ArrayList<>();
		targetOutputArray = new ArrayList<>();
		failedOutputArray = new ArrayList<>();
		variousOutputArray = new ArrayList<>();
		variousOutputSet = new HashSet<>();
		fbSegment = new FBSegment();
		handleInput();
		pncProcess();
	}
	
	public static void main(String[] args) {
		new Operator().init();
	}

}
