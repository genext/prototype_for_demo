
package com.example.proto.dao;

public class ContentsPayAmountDAO {
	private double amount;
	private int result;
	
	public ContentsPayAmountDAO(Double amount, Integer result){
		this.amount = amount;
		this.result = result;
	}	
	
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the result
	 */
	public int getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(int result) {
		this.result = result;
	}	
}
