package com.ict.txtprocess.bean;

public class Word {
	
	//原词
	String word;
	//读音串
//	String proun;
	//带权重的读音串
	ProunWithRank prounWithRank;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public ProunWithRank getProunWithRank() {
		return prounWithRank;
	}
	public void setProunWithRank(ProunWithRank prounWithRank) {
		this.prounWithRank = prounWithRank;
	}
	public Word(String word, ProunWithRank prounWithRank) {
		super();
		this.word = word;
		this.prounWithRank = prounWithRank;
	}
	

}
