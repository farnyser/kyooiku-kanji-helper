package org.dunno.kkh.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.dunno.kkh.models.Kanji;
import org.dunno.kkh.models.KanjiSet;

import android.content.Context;
import android.util.Log;

public class ReadCSV {
	static public KanjiSet getKanjiSet(Context ctx, int resId) {
		KanjiSet result = new KanjiSet();
		String lines[] = readFile(ctx, resId).split("\n");
		
		int grade = 1;
		int grades[] = {80, 240, 440, 640, 640+185, 640+185+181};
		
		for ( String line : lines ) {
			String item[] = line.split(",");
			int number = Integer.parseInt(item[0]);
			if ( number > grades[grade] )
				grade++;
			
			result.addKanji(new Kanji(number, grade, item[1], item[2], item[3], item[4]));
		}
		
		return result;
	}

	static private String readFile(Context ctx, int resId) {
		InputStream inputStream = ctx.getResources().openRawResource(resId);

		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();

		try {
			while ((line = buffreader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} 
		catch (IOException e) {
			Log.e("ReadCSV", "Read from file failed", e);
			return "";
		}

		return text.toString();
	}
}
