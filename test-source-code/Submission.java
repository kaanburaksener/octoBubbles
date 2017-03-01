package com.kaanburaksener;

import java.util.Date;

public class Submission {
	long studentID;
	float grade;
	Date dateSubmitted;
	Student student;
	Day day;
	
	public Submission(long studentID, Date dateSubmitted, float grade){
		this.studentID = studentID;
		this.grade = grade;
		this.dateSubmitted = dateSubmitted;
		this.day = DAY.SUNDAY;
	}
	
	public Submission(){
		this(0, null, 0);
	}
	
	public long getStudentID(){
		return studentID;
	}
	
	public void setStudentID(long studentID){
		this.studentID = studentID;
	}
	
	public float getGrade(){
		return grade;
	}
	
	public void setGrade(float grade){
		this.grade = grade;
	}
	
	public Date getDateSubmitted(){
		return dateSubmitted;
	}
	
	public void setDateSubmitted(Date dateSubmitted){
		this.dateSubmitted = dateSubmitted;
	}
}
