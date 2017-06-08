package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeartherDB {
	public static final String DB_NAME="cool_weather";
	public static int VERSION=1;
	private static CoolWeartherDB coolWeatherDB;
	private SQLiteDatabase db;

	public CoolWeartherDB(Context context) {
		// TODO Auto-generated constructor stub
		CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME, null,VERSION);
	    db=dbHelper.getWritableDatabase();
	}
	public synchronized static CoolWeartherDB getInstance(Context context)
	{
		if(coolWeatherDB==null)
		{
			coolWeatherDB=new CoolWeartherDB(context);
		}
		return coolWeatherDB;
	}
	public void saveProvince(Province province)
	{
		if(province!=null)
		{
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	public List<Province> loadProvinces()
	{
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province",null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do
			{
			  Province province=new Province();
			  province.setId(cursor.getInt(cursor.getColumnIndex("id")));
			  province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
			  province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
			  list.add(province);
			 }while(cursor.moveToNext());
		}
		if(cursor!=null)
		{
			cursor.close();
		}
		return list;
	}
	public void saveCity(City city)
	{
		if(city!=null)
		{
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	public List<City> loadCity(int provinceId)
	{
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City",null, "povince_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst())
		{
			do
			{
			  City city=new City();
			  city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			  city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
			  city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			  city.setProvinceId(provinceId);
			  list.add(city);
			 }while(cursor.moveToNext());
		}
		if(cursor!=null)
		{
			cursor.close();
		}
		return list;
	}
	public void saveCountry(Country country)
	{
		if(country!=null)
		{
			ContentValues values=new ContentValues();
			values.put("country_name", country.getCountryeName());
			values.put("countrycode", country.getCountryCode());
			values.put("city_id", country.getCityId());
			db.insert("Country", null, values);
		}
	}
	public List<Country> loadCountry(int cityId)
	{
		List<Country> list=new ArrayList<Country>();
		Cursor cursor=db.query("Country",null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null,null);
		if(cursor.moveToFirst())
		{
			do
			{
				Country country=new Country();
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCityId(cityId);
				list.add(country);
			 }while(cursor.moveToNext());
		}
		if(cursor!=null)
		{
			cursor.close();
		}
		return list;
	}

}
