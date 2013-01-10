package com.noobs2d.businessappointer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

/**
 * Store house for license agreement.
 * 
 * @author MrUseL3tter
 */
public class Settings {

    public static boolean licenseAccepted = false;

    public static void load() {
	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	    try {
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".ba-settings");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		licenseAccepted = Boolean.parseBoolean(reader.readLine());
		reader.close();
	    } catch (IOException e) {

	    }
    }

    public static void save() {
	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	    try {
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".ba-settings");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(licenseAccepted + "");
		writer.close();
	    } catch (IOException e) {

	    }
    }
}
