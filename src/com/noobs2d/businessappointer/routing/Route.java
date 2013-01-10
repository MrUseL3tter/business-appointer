package com.noobs2d.businessappointer.routing;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

public class Route {
	
	private String name;
	private final List<GeoPoint> points;
	private List<Segment> segments;
	private String copyright;
	private String warning;
	private String country;
	private int length;
	private String polyline;
	
	public Route() {
		points = new ArrayList<GeoPoint>();
		segments = new ArrayList<Segment>();
	}
	
	public void addPoint(final GeoPoint p) {
		points.add(p);
	}
	
	public void addPoints(final List<GeoPoint> points) {
		this.points.addAll(points);
	}
	
	public void addSegment(final Segment s) {
		segments.add(s);
	}
	
	/**
	 * @return the copyright
	 */
	public String getCopyright() {
		return copyright;
	}
	
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public List<GeoPoint> getPoints() {
		return points;
	}
	
	/**
	 * @return the polyline
	 */
	public String getPolyline() {
		return polyline;
	}
	
	public List<Segment> getSegments() {
		return segments;
	}
	
	/**
	 * @return the warning
	 */
	public String getWarning() {
		return warning;
	}
	
	/**
	 * @param copyright
	 *            the copyright to set
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * @param polyline
	 *            the polyline to set
	 */
	public void setPolyline(String polyline) {
		this.polyline = polyline;
	}
	
	/**
	 * @param warning
	 *            the warning to set
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}
	
}
