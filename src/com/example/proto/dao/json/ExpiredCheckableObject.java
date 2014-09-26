
package com.example.proto.dao.json;

public class ExpiredCheckableObject {

	private static final long PASS_LIMITED_TIME = 1000 * 60 * 60;//1시간
	private long createTime;

	/**
	 * @return the createTime
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
	public boolean isExpiredObject(long currentTime){
		if(currentTime - createTime > PASS_LIMITED_TIME){
			return true;
		}else{
			return false;
		}
	}
}
