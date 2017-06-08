package com.example.coolweather;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeartherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_City=1;
	public static final int LEVEL_Country=2;
	private ListView listView;
	private TextView titleText;
	private ArrayAdapter<String> adapter; 
	private int currentLevel;
	private List<String> dataList=new ArrayList<String>();
	private Province selectedProvince;
	private City selectedCity;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<Country> countryList;
    private	CoolWeartherDB coolWeatherDB;
    private ProgressDialog progressDialog;
    private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity", false);
	    SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
	    if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity)
	    {
	    	Intent intent=new Intent(this,WeatherActivity.class);
	    	startActivity(intent);
	    	finish();
	    	return;
	    }
		setContentView(R.layout.choose_area);
		listView=(ListView) findViewById(R.id.list_view);
		titleText=(TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB=CoolWeartherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEL_PROVINCE)
				{
					selectedProvince=provinceList.get(index);
					queryCities();
				}
				else if(currentLevel==LEVEL_City)
				{
					selectedCity=cityList.get(index);
					queryCounties();
				}
				else if(currentLevel==LEVEL_Country)
				{
					String countryCode=countryList.get(index).getCountryCode();
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
				}
				
				
			}
		});
		queryProvince();
		
	}
	protected void queryCounties() {
		// TODO Auto-generated method stub
		cityList=coolWeatherDB.loadCity(selectedProvince.getId());
		if(cityList.size()>0)
		{
			dataList.clear();
			for(City city:cityList)
			{
				dataList.add(city.getCityName());
			}
		
		adapter.notifyDataSetChanged();
		listView.setSelection(0);
		titleText.setText(selectedProvince.getProvinceName());
		currentLevel=LEVEL_City;
		}
		else
		{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
		
	}
	protected void queryCities() {
		// TODO Auto-generated method stub
		countryList=coolWeatherDB.loadCountry(selectedCity.getId());
		if(countryList.size()>0)
		{
			dataList.clear();
			for(Country country:countryList)
			{
				dataList.add(country.getCountryeName());
			}
		
		adapter.notifyDataSetChanged();
		listView.setSelection(0);
		titleText.setText(selectedCity.getCityName());
		currentLevel=LEVEL_Country;
		}
		else
		{
			queryFromServer(selectedCity.getCityCode(),"country");
		}
	}
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code))
		{
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}
		else
		{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequst(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if(type.equals("province"))
				{
					result=Utility.handleProvinceResponse(coolWeatherDB, response);
				}
				else if(type.equals("city"))
				{
					result=Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}
				else if(type.equals("country"))
				{
					result=Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result)
				{
					runOnUiThread(new Runnable() {//更新UI
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if(type.equals("province"))
							{
								queryProvince();
							}
							else if(type.equals("city"))
							{
								queryCities();
							}
							else if(type.equals("country"))
							{
								queryCounties();
							}
						}

						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_LONG).show();
					}
				});
				
			}
		});
	}
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog==null)
		{
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
		
	}
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog!=null)
		{
			progressDialog.dismiss();
		}
	}
	private void queryProvince()
	{
		provinceList=coolWeatherDB.loadProvinces();
		if(provinceList.size()>0)
		{
			dataList.clear();
			for(Province province:provinceList)
			{
				dataList.add(province.getProvinceName());
			}
		
		adapter.notifyDataSetChanged();
		listView.setSelection(0);
		titleText.setText("中国");
		currentLevel=LEVEL_PROVINCE;
		}
		else
		{
			queryFromServer(null,"province");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_area, menu);
		return true;
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stu
		if(currentLevel==LEVEL_Country)
		{
			queryCities();
		}
		else if(currentLevel==LEVEL_Country)
		{
			queryProvince();
		}
		else
		{
			if(isFromWeatherActivity)
			{
				Intent intent=new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}

}
