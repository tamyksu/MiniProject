package application;

import java.io.Serializable;

public class MyFile implements Serializable {
	
	private String Description=null;
	private String fileName=null;	
	private int size=0;
	public  byte[] mybytearray;
	
	/**
	 * Init the Array
	 * @param size
	 */
	public void initArray(int size)
	{
		mybytearray = new byte [size];	
	}
	
	/**
	 * Constructor
	 * @param fileName
	 */
	public MyFile( String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Get file name
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Set file name
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Get the size of the file
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Set the size of the file
	 * @return
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Get the ByteArray
	 * @return
	 */
	public byte[] getMybytearray() {
		return mybytearray;
	}
	/**
	 * Get the Byte by index
	 * @return
	 */
	public byte getMybytearray(int i) {
		return mybytearray[i];
	}

	/**
	 * Set the ByteArray
	 * @return
	 */
	public void setMybytearray(byte[] mybytearray) {
		
		for(int i=0;i<mybytearray.length;i++)
		this.mybytearray[i] = mybytearray[i];
	}

	/**
	 * Get Description
	 * @return
	 */
	public String getDescription() {
		return Description;
	}
	
	/**
	 * Set Description
	 * @return
	 */
	public void setDescription(String description) {
		Description = description;
	}	
}

