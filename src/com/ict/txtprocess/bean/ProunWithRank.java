package com.ict.txtprocess.bean;

public class ProunWithRank {
	String proun;
	int rank;
	public ProunWithRank(String proun, int rank) {
		super();
		this.proun = proun;
		this.rank = rank;
	}
	public String getProun() {
		return proun;
	}
	public void setProun(String proun) {
		this.proun = proun;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public void setRankIncrease() {
		this.rank++;
	}

}
