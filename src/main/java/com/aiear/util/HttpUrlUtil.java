package com.aiear.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class HttpUrlUtil {
	public static void main(String args[]) {

		String url = "http://192.168.33.181:8090/status";
//		getJSONFromUrl(url);
		String str = "{\"cpu\":100.0,\"memory\":{\"total\":4189808.0,\"used\":3352812.0,\"usage\":80.023046402126312},\"drives\":[{\"name\":\"C:\\\\\",\"total\":31605125120.0,\"free\":6497267712.0,\"used\":25107857408.0,\"usage\":79.442360416765212},{\"name\":\"D:\\\\\",\"total\":75158777856.0,\"free\":74255872000.0,\"used\":902905856.0,\"usage\":1.2013312107468233}],\"processes\":[{\"name\":\"STAT\",\"cpu\":0.0,\"memory\":0.0,\"status\":\"STOP\",\"version\":\"1.012\"},{\"name\":\"CIM\",\"cpu\":0.0,\"memory\":30236.0,\"status\":\"RUN\",\"version\":\"1.001\"},{\"name\":\"ALARM\",\"cpu\":6.962071418762207,\"memory\":348604.0,\"status\":\"RUN\",\"version\":\"1.102\"},{\"name\":\"MONGO\",\"cpu\":13.333727836608887,\"memory\":371196.0,\"status\":\"RUN\",\"version\":\"1.005\"},{\"name\":\"WEB\",\"cpu\":0.0,\"memory\":113784.0,\"status\":\"RUN\",\"version\":\"1.006\"},{\"name\":\"DB\",\"cpu\":0.30302992463111877,\"memory\":98780.0,\"status\":\"RUN\",\"version\":\"1.0.0\"},{\"name\":\"RABBITMQ\",\"cpu\":54.1450309753418,\"memory\":351064.0,\"status\":\"RUN\",\"version\":\"1.0.0\"}]}";
//		JSONObject obj = JSONObject.fromObject(str);
//		System.out.println(obj);
	}

	/**
	 * url을 이용해 body 데이터를 가지고 온다
	 * 
	 * @param _url
	 *            url
	 * @param _method
	 *            POST, GET
	 * @return
	 */
	public static String getHttpBodyData(String _url, String _method) {
		String result = "";
		try {

			URL url = new URL(_url);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestMethod(_method);
			
			http.setRequestProperty("content-type",	"application/x-www-form-urlencoded");

			StringBuffer buffer = new StringBuffer();

			OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());
			writer.flush();

			InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
			BufferedReader reader = new BufferedReader(tmp);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) {
				builder.append(str + "\n");
			}
			result = builder.toString();
			
			http.disconnect();

		} catch (MalformedURLException e) {

		} catch (IOException e) {
		}

		return result;
	}

	
	//////////////////////////////
	public static org.json.simple.JSONObject getSimpleJSONFromUrl(String url) {
		InputStream is = null;
		org.json.simple.JSONObject jObj = new org.json.simple.JSONObject();
		String json = "";
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			HttpGet httpGet = new HttpGet(url);
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			
			json = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			org.json.simple.JSONArray ja=(org.json.simple.JSONArray) JSONValue.parse(json);
			
			if(ja.size()==0){
				jObj.put("msg", "값이 없습니다.");
				return jObj;
			}
			
			jObj=(org.json.simple.JSONObject) ja.get(0);
			
		} catch (ClassCastException cce) {
			//error 메시지가 뜰땐 배열이 아니라 json이라 캐스팅에러 발생
			jObj.put("msg", "값이 없습니다.");
			return jObj;
		}
		
		

		return jObj;
	}
	
	public static String[] getArrayFromUrl(String url) {
		InputStream is = null;
		String json = "";
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			HttpGet httpGet = new HttpGet(url);
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			
			json = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		json = json.replace("\"", "");
		json = json.replace("[", "");
		json = json.replace("]", "");
		json = json.replace("\n", "").replace("\r", "");
		String[] jsonArray=json.split(",");
		return jsonArray;
	}

	
	
	public static void sendToURL(String url) throws Exception {

		HttpParams httpParams = new BasicHttpParams();
		//ConnectionTimeOut, SocketTimeOut 설정(ms단위)
		HttpConnectionParams.setConnectionTimeout(httpParams, 1800);
		HttpConnectionParams.setSoTimeout(httpParams, 1800);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpGet httpGet = new HttpGet(url);
		httpClient.execute(httpGet);
		httpClient = null; httpGet = null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getGSONFromURL(String url) throws Exception {
		
		Gson gson = new Gson();
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		
		InputStream is = null;
		HttpParams httpParams = new BasicHttpParams();
		//ConnectionTimeOut, SocketTimeOut 설정(ms단위)
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		
		org.json.simple.JSONArray ja=(org.json.simple.JSONArray) JSONValue.parse(sb.toString());
		
		if(sb != null){
			for(int i = 0; i < ja.size(); i++){
				result.add(gson.fromJson(ja.get(i).toString(), map.getClass()));
			}
		}
		
		return result;
	}
	
	/**
	 * url을 이용해 body 데이터를 가지고 온다
	 */
	public static String getHttpBodyFromURL(String _url, String _method, String _errMsg) {
		String result = "";
		try {

			InputStream is = null;
			HttpParams httpParams = new BasicHttpParams();
			//ConnectionTimeOut, SocketTimeOut 설정(ms단위)
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpGet httpGet;
			HttpPost httpPost;
			HttpResponse httpResponse;
			if(_method.equals("POST")) {
				httpPost = new HttpPost(_url);
				httpResponse = httpClient.execute(httpPost);
			}else { // 그 외에는 GET방식 고정
				httpGet = new HttpGet(_url);
				httpResponse = httpClient.execute(httpGet);
			}
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			if(_errMsg != null) {
				result = _errMsg;
			}else {
				result = e.getMessage();
			}
		}

		return result;
	}
	
	
	public static String getHttpBodyFromStatusURL(String _url, String _method, String _errMsg) {
		String result = "";
		try {

			InputStream is = null;
			HttpParams httpParams = new BasicHttpParams();
			//ConnectionTimeOut, SocketTimeOut 설정(ms단위)
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 20000);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpGet httpGet;
			HttpPost httpPost;
			HttpResponse httpResponse;
			if(_method.equals("POST")) {
				httpPost = new HttpPost(_url);
				httpResponse = httpClient.execute(httpPost);
			}else { // 그 외에는 GET방식 고정
				httpGet = new HttpGet(_url);
				httpResponse = httpClient.execute(httpGet);
			}
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			if(_errMsg != null) {
				result = _errMsg;
			}else {
				result = e.getMessage();
			}
		}

		return result;
	}
	
	
	
	
	/**
	 * Description	: HashMap Paramter로 append 기능 추가
	 * @method		: getHttpBodyData
	 * @author		: pcw
	 * @date		: 2022. 5. 10.
	 * @param _url
	 * @param _method
	 * @param hMap
	 * @return
	 */
	public static String getHttpBodyData(String _url, String _method, HashMap<String, Object> hMap) {
		String result = "";
		try {

			URL url = new URL(_url);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestMethod(_method);
			
			http.setRequestProperty("content-type",	"application/x-www-form-urlencoded");

			StringBuffer buffer = new StringBuffer();
			
			//Request Parameters Key, Value Setting
			for(Entry<String, Object> elem : hMap.entrySet()){
				String key = elem.getKey();
				String value = (String) elem.getValue();
				if(!StringUtil.isEmpty(value)){
					buffer.append(key).append("=").append(value).append("&");
				}
			}
			
			OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());
			writer.flush();

			InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
			BufferedReader reader = new BufferedReader(tmp);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) {
				builder.append(str + "\n");
			}
			result = builder.toString();
			
			http.disconnect();

		} catch (MalformedURLException e) {

		} catch (IOException e) {
		}

		return result;
	}
	
	/**
	 * Description	: HashMap Paramter로 append 기능 추가
	 * @method		: getHttpBodyDataToMap
	 * @author		: pcw
	 * @date		: 2022. 9. 29.
	 * @param _url
	 * @param _method
	 * @param hMap
	 * @return
	 */
	public static Map<String, Object> getHttpBodyDataToMap(String _url, String _method, JSONObject jsonObj) {
		String result = "";
		Map<String, Object> rsltMap = new HashMap<String, Object>();
		
		try {
			
			URL url = new URL(_url);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			http.setDefaultUseCaches(false);
			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestMethod(_method);
			
			http.setRequestProperty("content-type",	"application/json");
			
			OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
			PrintWriter writer = new PrintWriter(outStream);
			writer.write(jsonObj.toString());
			writer.flush();

			InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
			BufferedReader reader = new BufferedReader(tmp);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) {
				builder.append(str + "\n");
			}
			result = builder.toString();
			
			ObjectMapper mapper = new ObjectMapper();
			rsltMap = mapper.readValue(result, Map.class);
			
			int rsltCode = http.getResponseCode();
			rsltMap.put("rsltCode", rsltCode);
			
			System.out.println("■■■■■■ HTTP RESULT CODE : " + rsltCode);
			
			http.disconnect();
			
		} catch (MalformedURLException e) {

		} catch (IOException e) {
		}

		return rsltMap;
	}
	
}
