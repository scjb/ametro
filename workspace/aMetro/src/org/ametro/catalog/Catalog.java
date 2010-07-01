/*
 * http://code.google.com/p/ametro/
 * Transport map viewer for Android platform
 * Copyright (C) 2009-2010 Roman.Golovanov@gmail.com and other
 * respective project committers (see project home page)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.ametro.catalog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.ametro.Constants;
import org.ametro.model.Model;

public class Catalog {

	/*package*/ long mTimestamp;
	/*package*/ String mBaseUrl;
	/*package*/ ArrayList<CatalogMap> mMaps;
	/*package*/ boolean mIsCorrupted;

	public Catalog(){
		mIsCorrupted = false;
	}

	public boolean isCorrupted(){
		return mIsCorrupted;
	}
	
	public void setCorrupted(boolean isCorrupted){
		mIsCorrupted = isCorrupted;
	}

	public void setTimestamp(long timestamp){
		mTimestamp = timestamp;
	}

	public long getTimestamp() {
		return mTimestamp;
	}
	
	public String getBaseUrl() {
		return mBaseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		mBaseUrl = baseUrl;
	}
	
	public void setMaps(ArrayList<CatalogMap> maps){
		mMaps = maps;
	}
	
	public ArrayList<CatalogMap> getMaps() {
		return mMaps;
	}

	public Catalog(long timestamp, String baseUrl, ArrayList<CatalogMap> maps) {
		mTimestamp = timestamp;
		mBaseUrl = baseUrl;
		mMaps = maps;
		mIsCorrupted = false;
	}
	
	public String toString() {
		return "[TIME:" + getTimestamp() + ";URL:" + getBaseUrl() + ";COUNT:" + (getMaps()!=null ? getMaps().size() : "null") + "]";
	}
	
	/* VOLATILE FIELDS */
	private HashMap<String, CatalogMap> mMapIndex;
	private long mLoadingTimestamp;
	
	public CatalogMap getMap(String systemName){
		mLoadingTimestamp = System.currentTimeMillis();
		if(mMapIndex == null){
			final HashMap<String, CatalogMap> index = new HashMap<String, CatalogMap>();
			for(CatalogMap map : mMaps){
				index.put(map.getSystemName(), map);
			}
			mMapIndex = index;
		}
		return mMapIndex.get(systemName);
	}

	public static boolean equals(Catalog left, Catalog right) {
		return left!=null && right!=null && left.equals(right);
	}
	
	public boolean equals(Object o) {
		Catalog obj = (Catalog)o;
		return mTimestamp == obj.mTimestamp && mBaseUrl.equals(obj.mBaseUrl) && mMaps.size() == obj.mMaps.size();
	}

	public Catalog deleteMap(CatalogMap map) {
		mMapIndex.remove(map.getSystemName());
		mMaps.remove(map);
		mTimestamp = System.currentTimeMillis();
		return this;
	}

	public void appendMap(CatalogMap map) {
		final String systemName = map.getSystemName(); 
		if(mMapIndex.containsKey(systemName)){
			mMapIndex.remove(map.getSystemName());
			mMaps.remove(map);
		}
		mMapIndex.put(systemName, map);
		mMaps.add(map);
		mTimestamp = System.currentTimeMillis();
	}

	public long getLoadingTimestamp() {
		return mLoadingTimestamp;
	}
	
	public static CatalogMap makeBadCatalogMap(Catalog catalog, File file, final String fileName) {
		
		final String suggestedMapName = fileName.substring(0, fileName.indexOf('.'));
		
		final String[] locales = new String[]{"en","ru"};
		final String[] country = new String[]{UNKNOWN_EN,UNKNOWN_RU};
		final String[] city = new String[]{suggestedMapName,suggestedMapName};
		final String[] description = new String[]{"",""};
		final String[] changeLog = new String[]{"",""};
		
		String systemName = fileName;
		if(fileName.endsWith(Constants.PMETRO_EXTENSION)){
			systemName += Constants.AMETRO_EXTENSION;
		}
		
		CatalogMap map = new CatalogMap(
				 catalog,
				 systemName,
				 fileName,
				 0,
				 0,
				 Model.VERSION,
				 0,
				 Model.COMPATIBILITY_VERSION,
				 locales,
				 country,
				 city,
				 description,
				 changeLog,
				 true
				 );
		return map;
	}
	
	public static CatalogMap extractCatalogMap(Catalog catalog, File file, final String fileName, Model model) {
		final String[] locales = model.locales;
		final int len = locales.length;
		final int countryId = model.countryName;
		final int cityId = model.cityName;
		final String[][] texts = model.localeTexts;
		
		final TreeSet<ModelDescription> modelLocales = new TreeSet<ModelDescription>();
		
		for(int i=0; i<len;i++){
			modelLocales.add( new ModelDescription(locales[i], texts[i][cityId], texts[i][countryId], "Not supported yet.") );
		}

		int index = 0;
		final String[] country = new String[len];
		final String[] city = new String[len];
		final String[] description = new String[len];
		final String[] changeLog = new String[len];
		for(ModelDescription m : modelLocales){
			locales[index] = m.locale;
			city[index] = m.city;
			country[index] = m.country;
			description[index] = m.description;
			changeLog[index] = "";
			index++;
		}
		
		String systemName = fileName;
		if(fileName.endsWith(Constants.PMETRO_EXTENSION)){
			systemName += Constants.AMETRO_EXTENSION;
		}
		
		CatalogMap map = new CatalogMap(
				 catalog,
				 systemName,
				 fileName,
				 model.timestamp,
				 model.transportTypes,
				 Model.VERSION,
				 file.length(),
				 Model.COMPATIBILITY_VERSION,
				 locales,
				 country,
				 city,
				 description,
				 changeLog,
				 false
				 );
		return map;
	}
	
	private static class ModelDescription implements Comparable<ModelDescription>
	{
		String locale;
		String city;
		String country;
		String description;
		
		public int compareTo(ModelDescription another) {
			return locale.compareTo(another.locale);
		}

		public ModelDescription(String locale, String city, String country, String description) {
			super();
			this.locale = locale;
			this.city = city;
			this.country = country;
			this.description = description;
		}
	}
	
	private static final String UNKNOWN_EN = "Unknown";
	private static final String UNKNOWN_RU = "Неизвестно";	
}
