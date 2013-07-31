package com.komodo.tagin;

import android.graphics.Color;
import android.widget.TextView;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

/*
 * Tag class:
 * For now tags are just cubes. Later they will be replaced by real texts!
 */
public class Tag implements Comparable<Tag> {

	private static final int DEFAULT_POPULARITY = 1;
	
	private String mText;
	private String mURL;
	private int mPopularity;  //this is the importance/popularity of the Tag 
	private int mTextSize;
	private float x, y, z; //the center of the 3D Tag
	private float x2D, y2D;
	private float mScale;
	private int mColor;
    
    private TextView mTextView;
    
	public Tag() {
		this("", 0f, 0f, 0f, 1.0f, 0, "");
	}
	
	public Tag(String text, int popularity) {
		this(text, 0f, 0f, 0f, 1.0f, popularity, "");
	}
	
	public Tag(String text, int popularity, String url) {
		this(text, 0f, 0f, 0f, 1.0f, popularity, url);
	}
	
	public Tag(String text, float x, float y, float z) {
		this(text, x, y, z, 1.0f, DEFAULT_POPULARITY, "");
	}
	
	public Tag(String text, float x, float y, float z, float scale) {
		this(text, x, y, z, scale, DEFAULT_POPULARITY, "");
	}
	
	public Tag(String text, float x, float y, float z, float scale, int popularity, String url) {
		this.mText = text;
    	this.x = x;
    	this.y = y;
    	this.z = z;

    	this.x2D = 0;
    	this.y2D = 0;
    	
    	mColor = Color.argb(255, 128, 128, 128);
    	mScale = scale;
    	mPopularity = popularity;
    	mURL = url;
    }	
	
	@Override
	public int compareTo(Tag another) {
		return (int)(another.z - z);
	}
	
    public float getX() {
		return x;
	}
    
	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public float getScale() {
		return mScale;
	}
	
	public void setScale(float scale) {
		this.mScale = scale;
	}
	
	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		this.mText = text;
	}
	
	public int getColor() {
		return mColor;
	}
	
	public void setColor(int color) {
		mColor = color;
	}
	
	public int getPopularity() {
		return mPopularity;
	}
	
	public void setPopularity(int popularity) {
		this.mPopularity = popularity;
	}
	
	public int getTextSize() {
		return mTextSize;
	}
	
	public void setTextSize(int textSize) {
		this.mTextSize = textSize;
	}
	
	public float getX2D() {
		return x2D;
	}
	
	public void setX2D(float x2D) {
		this.x2D = x2D;
	}
	
	public float getY2D() {
		return y2D;
	}
	
	public void setY2D(float y2D) {
		this.y2D = y2D;
	}
	
	public TextView getTextView() {
		return mTextView;
	}
	
	public void setTextView(TextView textView) {
		mTextView = textView;
	}
	
	public String getUrl() {
		return mURL;
	}
	
	public void setUrl(String url) {
		this.mURL = url;
	}
}
