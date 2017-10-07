package com.ict.txtprocess.bean;

import java.util.ArrayList;

public class AssistantLine {
	int length;
	ArrayList<IndependentWord> independentWords;
	
	public AssistantLine(int length, ArrayList<IndependentWord> independentWords) {
		super();
		this.length = length;
		this.independentWords = independentWords;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public ArrayList<IndependentWord> getIndependentWords() {
		return independentWords;
	}

	public void setIndependentWords(ArrayList<IndependentWord> independentWords) {
		this.independentWords = independentWords;
	}

	public class IndependentWord{
		ArrayList<String> prouns;
		ArrayList<String> words;
		public IndependentWord(ArrayList<String> prouns, ArrayList<String> words) {
			super();
			this.prouns = prouns;
			this.words = words;
		}
		public ArrayList<String> getProuns() {
			return prouns;
		}
		public void setProuns(ArrayList<String> prouns) {
			this.prouns = prouns;
		}
		public ArrayList<String> getWords() {
			return words;
		}
		public void setWords(ArrayList<String> words) {
			this.words = words;
		}
	}
}
