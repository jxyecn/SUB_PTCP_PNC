package com.ict.txtprocess.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import com.ict.txtprocess.bean.Subwords;
import com.ict.txtprocess.bean.Word;

public class WordUtils {
	static WordUtils _instance;
	
	//单例对象用于操作后续Util方法
	public static WordUtils getInstanceDC() {
        if (_instance == null) {
            synchronized (WordUtils.class) {
                if (_instance == null) {
                    _instance = new WordUtils();
                }
            }
        }
        return _instance;
    }
	
	//根据待处理内容生成分词、原词
	public Subwords getSubdByLine(String line){
		String originWord;
		String[] subWords;
		String[] source = line.split("	");
		try{
			//默认最后一个是原词，前面的都是分词
//			originWord = source[source.length-1];
//			subWords = Arrays.copyOfRange(source, 0, source.length-2 +1);
			
			originWord = source[0];
//			subWords = source[0].split("  ");
			subWords = null;
//			
//			for(int i=0;i<subWords.length;i++)
//				subWords[i] = processWordWithAttr(subWords[i]);
			
			return new Subwords(originWord, subWords);
		}catch(Exception e){
			System.out.println("--------ERROR INFO---------");
			System.out.println("ERROR on line:" + line);
			System.out.println("-------ERROR INFO END--------");
			e.printStackTrace();
		}
		return null;
	}
	
	//给带有词性的词做标准化，去除词性方便后面的查找发音递归
	private String processWordWithAttr(String sourceString){
		return sourceString.substring(0, sourceString.indexOf("/"));
	}
	
	public ArrayList<String> set2ArrayAdapter(Set<String> src){
		return new ArrayList<>(src);
	}
	
	//根据一行完整数据抽取原词串、原词对应的发音串
	public Word getWordByLine(String line){
		String origin,proun;
		//TODO:从一行完整文件中构建词典
		String[] source = line.split(" ");
		//首个英文字符开头的String的Index值
//		int originStart,prounStart;
//		int tempCursor = line.length()-1;
		//从后向前检索，检索至第一个中文字符，截取下一个空格后的全部字符串
		//从头检索，检索至第一个空格截取之前的所有
		int tempCursor = 0;
		try{
			while( line.charAt(tempCursor) != ' ')
				tempCursor++;
//					prounStart = tempCursor+2;
//					while(isChinese(line.charAt(tempCursor)))
//						tempCursor--;
//					originStart = tempCursor+1;
//					origin = line.substring(originStart, prounStart-2 +1);
//					proun = line.substring(prounStart);
//					origin = line.substring(0,tempCursor+1);
			//固定包含了一个空格，将其才减掉
			origin = line.substring(0,tempCursor);
			proun = line.substring(tempCursor+1,line.length());
			return new Word(origin, proun);
		}catch(Exception e){
			System.out.println("ERROR on char:" + line);
			e.printStackTrace();
		}
		return null;
	}
	
	// 根据Unicode编码判断中文汉字和符号
    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }


}
