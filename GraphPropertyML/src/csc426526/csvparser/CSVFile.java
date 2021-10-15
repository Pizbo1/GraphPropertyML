/**
 * 
 */
package csc426526.csvparser;

/**
 * @author Brandon Baggett
 *
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import csc426526.visitor.training.Visitor;

import java.io.BufferedReader;

public class CSVFile {

	private BufferedReader reader;
	private String fileName;
	
	public CSVFile(String name, String folderName, Parameters p) {
		try {
			fileName = name;
			reader = new BufferedReader(new FileReader(folderName + p.getFileSeperator() + fileName));
		} catch (FileNotFoundException e) {
			System.out.println("Error: " + folderName + p.getFileSeperator() +  fileName + " does not exists.");
		}
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public BufferedReader getReader() {
		return reader;	
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
	public void accept(Visitor v, Parameters p) {
		v.visit(this, p);
	}
}