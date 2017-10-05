package com.ict.txtprocess.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileIO {
	
	public static void readByBufferedReader(String fileName,ArrayList sourceArray) {  
        try {  
            File file = new File(fileName);  
            BufferedReader bufread;  
            String read;  
            bufread = new BufferedReader(new FileReader(file));  
            while ((read = bufread.readLine()) != null) {  
                //System.out.println(read);  
            	sourceArray.add(read);
            }  
            bufread.close();  
        } catch (FileNotFoundException ex) {  
            ex.printStackTrace();  
        } catch (IOException ex) {  
            ex.printStackTrace();  
        }  
    }  
	
	public void writeByFileOutputStream(String outputContent,String fileName) {
		FileOutputStream fop = null;
		File file;
		String content = outputContent;
		try {
			file = new File(fileName);
			fop = new FileOutputStream(file);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//@args:appendFileFlag-true:append file
	public static void writeByFileReader(String outputContent,String fileName,boolean appendFileFlag) {
		try {
			String data = outputContent;

			File file = new File(fileName);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			FileWriter fileWritter = new FileWriter(file.getName(), appendFileFlag);
			fileWritter.write(data);
			fileWritter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeByBufferedReader(String outputContent,String fileName,boolean appendFileFlag) {
		try {

			String content = outputContent;
			File file = new File(fileName);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, appendFileFlag);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//用来写数组，避免多次IO降低处理时间
	public static void writeArrayByBufferedReader(ArrayList<String> outputContent,String fileName,boolean appendFileFlag) {
		try {
			File file = new File(fileName);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, appendFileFlag);
			BufferedWriter bw = new BufferedWriter(fw);
			System.out.println("--------targetOutputArray-------" );
			System.out.println("source name:\t"+outputContent.getClass().getCanonicalName());
			System.out.println("output size:\t"+ outputContent.size());
			int numberOfLines = 0;
			for(String str:outputContent){
				numberOfLines++;
				bw.write("\n"+str);
				bw.flush();
//				FileIO.writeByBufferedReader("\n"+str, tempURL, true);
			}
			System.out.println("practical size:\t"+ numberOfLines);
			System.out.println("------targetOutputArray end-------");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
