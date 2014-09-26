
package com.example.proto.view;


import android.content.Context;
import android.util.AttributeSet;

public class MyOrderButton extends MenuButton{
	
	private String name;
	private String code;
	private long distintId;
	private int no;

	public MyOrderButton(Context context) {
        super(context);       
    }
   
    public MyOrderButton(Context context, AttributeSet attrs) {
        this(context,attrs,0);        
    }    
   
    public MyOrderButton(Context context, AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);       
    }   
    

	/**
	 * @return the order
	 */
	public int getNo() {
		return no;
	}

	/**
	 * @param order the order to set
	 */
	public void setNo(int no) {
		this.no = no;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the distintId
	 */
	public long getDistintId() {
		return distintId;
	}

	/**
	 * @param distintId the distintId to set
	 */
	public void setDistintId(long distintId) {
		this.distintId = distintId;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	

}
