package com.ict.txtprocess.bean;

public class Word {
	//原词
	String word;
	//读音串
	String proun;
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getProun() {
		return proun;
	}
	public void setProun(String proun) {
		this.proun = proun;
	}
	public Word(String word, String proun) {
		super();
		this.word = word;
		this.proun = proun;
	}

}
