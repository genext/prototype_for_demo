
package com.example.proto.ftp;

public class FileEntity {	
	
	private boolean checked;

    /** File name */
    private String name;

    /** Absolute file absolutePath */
    private String absolutePath;

    /** Absolute parent parentPath */
    private String parentPath;

    /** File size in bytes */
    private long size;
    
    public FileEntity(){};
    
    public FileEntity(String name, String absolutePath, String parentPath, long size){
    	this.name = name;
    	this.absolutePath = absolutePath;
    	this.parentPath = parentPath;
    	this.size = size;
    }

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the absolutePath
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}

	/**
	 * @param absolutePath the absolutePath to set
	 */
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	/**
	 * @return the parentPath
	 */
	public String getParentPath() {
		return parentPath;
	}

	/**
	 * @param parentPath the parentPath to set
	 */
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}
    
    
}
