/**
 * 
 */
package csc426526.csvparser;

import java.io.File;
import java.io.IOException;
//import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
//import java.util.Scanner;

/**
 * @author Brandon Baggett
 *
 */
public class Folder {
	// The number of files in the directory
	private int numFiles;
	// We are 
	private File file;
	// A list containing the file names of the CSV files in the FOLDER
	List<String> fileList = new ArrayList<String>();
	// The name of the folder
	private String folderName;
	
	/* The construction of this class should learn how many files there are
	* passing the folder is unnecessary, but it makes for cleaner code than keeping up
	* with a boolean and having a if-else statement
	*/
	public Folder(Parameters p, String folder, String fileType) {
		file = new File(folder);
		fileList = buildFiles(fileType);
		folderName = folder;
	}
	
	private List<String> buildFiles(String fileType){
		String temp;
		numFiles = 0;
		List<String> l = new ArrayList<String>();
		for (final File fileEntry : file.listFiles()) {
			if (fileEntry.isFile()) {
				temp = fileEntry.getName();
				if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals(fileType)) {
					//System.out.println("File = " + file.getAbsolutePath()+ "\\" + fileEntry.getName());
					l.add(fileEntry.getName());
					numFiles++;
				}
			}
		}
		return l;
	}
	
	public String getFolderNamer() {
		return folderName;
	}
	
	// I don't think I need this
	public void updateFiles(String fileType) {
		buildFiles(fileType);
	}
	
	public int getFileCount() {
		return numFiles;
	}
	
	public List<String> getFileList(){
		return fileList;
	}
	
	// I guess I don't this
	// I will probably delete it later
	public void close() {
		//file.close();
	}
	
	public void accept(Visitor v, Parameters p) throws IOException {
		v.visit(this, v, p);
	}
	
	public void accept(AggregatorVisitor v, Parameters p) throws IOException {
		v.visit(this, p);
	}
}