package com.FinalProject.dynaicResultAnalyzer;

public class AutomationParameter {
	//This is the class in which i can store and retrieve data efficiently 
	//The no. variables that i use here will be the no. of parameters that are necessary 
	//to be passed when submitting form
	private String EnrollNo;
	private String SeatNo;
	private int RowNo;
	private String siteUrl;
	private int colNo;
	private int totalRowNo;
	private int headRowNo;
	private String enrollOrSeat;
	
	//For Enroll-SeatNo selection
	public void setSelected(String selected) {
		this.enrollOrSeat = selected;
	}
	public String getSelected() {
		return enrollOrSeat;
	}
	
	//For SeatNo
	public void setSeatNo(String seatNo) {
		this.SeatNo = seatNo;
	}
	public String getSeatNo() {
		return SeatNo;
	}
	
	
	//For Enrollment No.
	public void setEnroll(String enroll) {
		this.EnrollNo = enroll;
	}
	public String getEnroll() {
		return EnrollNo;
	}
	
	//For Website URL
	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
	public String getSiteUrl() {
		return siteUrl;
	}
//	For Row Nos.
	public void setRowNo(int rowNo) {
		this.RowNo = rowNo;
	}
	public int getRowNo() {
		return RowNo;
	}
//For Col No.
	public void setColNo(int colNo) {
		this.colNo = colNo;
	}
	public int getColNo() {
		return colNo;
	}
//Rows For Total section 
	public void setTotalRowNo(int totalRowNo) {
		this.totalRowNo = totalRowNo;
	}
	public int getTotalRowNo() {
		return totalRowNo;
	}
	
	//Rows For Head section 
		public void setHeadRowNo(int headRowNo) {
			this.headRowNo = headRowNo;
		}
		public int getHeadRowNo() {
			return headRowNo;
		}
}
