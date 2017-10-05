package com.ict.txtprocess.bean;

public class Subwords {
	//originWord:原词，subWords:分词列表
	String originWord;
	String[] subWords;
	public String getOriginWord() {
		return originWord;
	}
	public void setOriginWord(String originWord) {
		this.originWord = originWord;
	}
	public String[] getSubWords() {
		return subWords;
	}
	public void setSubWords(String[] subWords) {
		this.subWords = subWords;
	}
	public Subwords(String originWord, String[] subWords) {
		super();
		this.originWord = originWord;
		this.subWords = subWords;
	}

}
