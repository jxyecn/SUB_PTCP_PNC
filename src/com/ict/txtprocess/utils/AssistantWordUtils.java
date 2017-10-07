package com.ict.txtprocess.utils;

import java.util.ArrayList;

import com.ict.txtprocess.bean.AssistantLine;

public class AssistantWordUtils {
	static AssistantWordUtils _instance;
	
	//单例对象用于操作后续Util方法
	public static AssistantWordUtils getInstanceDC() {
        if (_instance == null) {
            synchronized (AssistantWordUtils.class) {
                if (_instance == null) {
                    _instance = new AssistantWordUtils();
                }
            }
        }
        return _instance;
    }
	
	public AssistantLine generateLine(String src){
		AssistantLine result = null;
		String[] formattedSrc = src.split("	");
		int length = Integer.parseInt(formattedSrc[0]);
		ArrayList<AssistantLine.IndependentWord> independentWords = new ArrayList<>();
		result = new AssistantLine(length, independentWords);
		
		if(isChinese(formattedSrc[1].charAt(0)))
			return null;
		
		for(int i=1;i<formattedSrc.length;i++){
			String[] independentPart = formattedSrc[i].split(" ");
			ArrayList<String> prouns = new ArrayList<>();
			ArrayList<String> words = new ArrayList<>();
			//构造拼音序列
			for(int j=0;j<length;j++)
				prouns.add(independentPart[j]);
			//构造原词序列
			for(int j=length+1;j<independentPart.length;j++)
				words.add(independentPart[j]);
			
			result.getIndependentWords().add(result.new IndependentWord(prouns, words));
			
		}
		
		return result;
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
