package com.asu.plp.visualizer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.JSONObject;

public class JsonReader {
	public static void main(String[] args) {
		String conf_file = "conf/graph_blue_print.json";
		BufferedReader reader;
		String line = null;
		String jsonString = "";
		try {
			reader = new BufferedReader(new FileReader (conf_file));
			while((line = reader.readLine()) != null) {
				jsonString += line;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject conf = new JSONObject(jsonString);
		System.out.print(conf.get("vertices"));
		JSONObject vertices = conf.getJSONObject("vertices");
		Iterator<?> json_keys = vertices.keys();
		
		while( json_keys.hasNext() ){
			String json_key = (String)json_keys.next();
			JSONObject node = vertices.getJSONObject(json_key);
			System.out.println(node.getString("name"));
		}
	}
}
