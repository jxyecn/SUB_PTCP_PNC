package com.ict.txtprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ict.txtprocess.bean.AssistantLine;
import com.ict.txtprocess.bean.AssistantLine.IndependentWord;
import com.ict.txtprocess.utils.AssistantWordUtils;
import com.ict.txtprocess.utils.FileIO;

public class AssistantOperator {
	
	//存“待处理”
	ArrayList<String> assistantLineSourceArray;
	//存“读音”
	ArrayList<String> targetOutputArray;
	//存整理过的原始读音素材
	ArrayList<AssistantLine> assistantLines;
	//原词对应读音的数组
	HashMap<String,HashSet<String[]>> wordProunHashmap;
	//为避免同音同字多次加入，另申请一块HashSet用来判等。加入wordProunHashmap前先合并字符串，然后在uniqueProunHashmap里判
	HashMap<String,HashSet<String>> uniqueProunHashmap;
	
	AssistantWordUtils wordOperator;
	
//	String sourceURL = "source/辅助lexicon/拼音加加中的词汇.txt";
	String targetURL = "source/辅助lexicon/同音+近音词表（搜狗词典）.txt";
	String outputURL = "output/辅助lexicon/原词_读音_读音分字.txt";
	
	
	public void pncProcess(){
		//1. 生成HashMap，原词——读音 对应，从读音生成
		generateAssistantLines();
		//2. 对于每一行source中的文本，取得其原词、分词,也用HashMap来做
		generateProunMap();
		//3. 对于每一个词组，处理其输出请求
		handleOutputRequest();
	}
	
	private void handleOutputRequest() {
		// TODO 按行输出 原词,读音1 读音2 读音3，读音分离1|读音分离2|读音分离3
		Iterator<Entry<String, HashSet<String[]>>> iter = wordProunHashmap.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, HashSet<String[]>> entry = (Entry<String, HashSet<String[]>>) iter.next();
			String originString = entry.getKey();
			HashSet<String[]> prouns= entry.getValue();

			if(originString.equals("紫仙露")){
				System.out.println("size of Set:" + prouns.size());
				System.out.println("紫仙露");
			}
			
			ArrayList<String[]> prounsArray = new ArrayList<>(prouns);
			StringBuilder seg2,seg3;
			String tempResult = originString + ',';
			seg2 = new StringBuilder();
			seg3 = new StringBuilder();
			//提前加上第一个，保证没有冗余'|'项
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2;
			sb1.append(getSingleWordProun(prounsArray.get(0)));
			//sb2指的是第三段里，需要把String[]拆成分字拼音
			sb2 = getSingleWordProun(prounsArray.get(0),originString);
			
			seg2.append(sb1);
			seg3.append(sb2);
			
			for(int i=1;i<prounsArray.size();i++){
				sb1 = new StringBuilder();
				sb2 = getSingleWordProun(prounsArray.get(i),originString);
				sb1.append(getSingleWordProun(prounsArray.get(i)));
				seg2.append('|');
				seg2.append(sb1);
				seg3.append('|');
				seg3.append(sb2);
			}
			
			seg2.append(',');
			
			tempResult += seg2.toString();
			tempResult += seg3.toString();
			
			targetOutputArray.add(tempResult);
		}
		
		FileIO.writeArrayByBufferedReader(targetOutputArray, outputURL, false);
		
	}

	private StringBuilder getSingleWordProun(String[] strings){
		// TODO 根据拼音构成的String[]返回一个StringBuilder
		StringBuilder result = new StringBuilder();
		result.append(strings[0]);
		for(int i=1;i<strings.length;i++)
			result.append(' ').append(strings[i]);
		return result;
	}
	
	private StringBuilder getSingleWordProun(String[] strings,String originalString) {
		// TODO 根据拼音构成的String[]返回一个StringBuilder
		StringBuilder result = new StringBuilder();
		result.append(originalString.charAt(0)).append('~');
		result.append(strings[0]);
		for(int i=1;i<strings.length;i++)
			result.append(' ').append(originalString.charAt(i)).append('~').append(strings[i]);
		return result;
	}

	private void generateProunMap() {
		// TODO 生成   原词————拼音数组
		Iterator<AssistantLine> iter = assistantLines.iterator();
		while(iter.hasNext()){
			AssistantLine assistantLine = iter.next();
			ArrayList<AssistantLine.IndependentWord> words = assistantLine.getIndependentWords();
			for(AssistantLine.IndependentWord word:words){
				//ni hao
				ArrayList<String> iddwProuns = word.getProuns();
				//你好 拟好 ...
				ArrayList<String> iddwWords = word.getWords();
				
				for(String iddwWord:iddwWords){
					
					//当前词 iddwWord是首次出现，需构造容器
					if(!wordProunHashmap.containsKey(iddwWord)){
						HashSet<String[]> temp = new HashSet();
						String[] tempStringArray = new String[iddwProuns.size()];
						for(int i=0;i<iddwProuns.size();i++)
							tempStringArray[i] = iddwProuns.get(i);
//						temp.add((String[])iddwProuns.toArray());
						temp.add(tempStringArray);
						wordProunHashmap.put(iddwWord, temp);
						
						HashSet<String> tempHashSet = new HashSet();
						tempHashSet.add(Arrays.toString(tempStringArray));
						
						uniqueProunHashmap.put(iddwWord,tempHashSet);
					}else{
						//当前词在之前已经出现过，需在容器里为读音项增加一条数据
						String[] tempStringArray = new String[iddwProuns.size()];
						for(int i=0;i<iddwProuns.size();i++)
							tempStringArray[i] = iddwProuns.get(i);
						try{
							//说明此读音尚不存在
							if(!uniqueProunHashmap.get(iddwWord).contains(Arrays.toString(tempStringArray))){
								wordProunHashmap.get(iddwWord).add(tempStringArray);
								uniqueProunHashmap.get(iddwWord).add(Arrays.toString(tempStringArray));
							}
						}catch(Exception e){
							System.out.println("ERROR on:" + iddwWord);
							e.printStackTrace();
						}
					}
						
				}
				
			}
			
		}
		
	}

	private void generateAssistantLines() {
		// TODO 生成一行行原始文本
		FileIO.readByBufferedReader(targetURL, assistantLineSourceArray);
		Iterator<String> iter = assistantLineSourceArray.iterator();
		while(iter.hasNext()){
			AssistantLine assistantLine = wordOperator.generateLine(iter.next());
			if(assistantLine != null)
				this.assistantLines.add(assistantLine);
		}
	}

	public void init(){
		assistantLineSourceArray = new ArrayList<>();
		targetOutputArray = new ArrayList<>();
		assistantLines = new ArrayList<>();
		wordProunHashmap = new HashMap<>();
		uniqueProunHashmap = new HashMap<>();
		wordOperator = new AssistantWordUtils();
		pncProcess();
	}
	
	public static void main(String[] args) {
		new AssistantOperator().init();
//		System.out.println("hello");
	}
}
