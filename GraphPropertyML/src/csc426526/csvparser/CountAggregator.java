/**
 * 
 */
package csc426526.csvparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Brandon Baggett
 * This crashes if you have the summary file already in the directory
 * TODO fix that
 */
public class CountAggregator implements AggregatorVisitor {
	@Override
	public void visit(Folder f, Parameters p){
		List<String> fileList = new ArrayList<String>();
		BufferedReader br = null;
		fileList = f.getFileList();
		int size = fileList.size();
		List<String> attributes = new ArrayList<String>();
		List<Integer> counts = new ArrayList<Integer>();
		float percent;

		//System.out.println(size);
		try {
			for(int i = 0; i < size; i++) {
				br = new BufferedReader(new FileReader(p.getOutputFolder() + p.getFileSeperator() + fileList.get(i)));
				attributes = updateAttributes(attributes, br);
				// Theres probably a more elegant way to do this, but im tired and need a way to reset to pointer to the 
				// beginning of the file
				br = new BufferedReader(new FileReader(p.getOutputFolder() + p.getFileSeperator() + fileList.get(i)));
				counts = updateCounts(attributes, counts, br);
				//ystem.out.println("here");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block	
			e.printStackTrace();
		}
		
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		System.out.println(attributes);
		System.out.println(counts);
		
		// make a filewrite and do the things
		FileWriter w = createFileWriter(p.getOutputFolder() + p.getFileSeperator() + "CountAggregationSummary.txt");
		size = attributes.size();
		try {
			w.append("Aggregate Summary\n"); // this is just here so that it crashes if you
											// dont delete the summary file between runs
											// I will fix it later
			w.append("attribute : attrCount: totalCount : percent\n");
			for(int i = 0; i < size; i++) {
				percent = counts.get(i).floatValue()/counts.get(size-1).floatValue();
				w.append(attributes.get(i) + ": " + counts.get(i) + ": "
										+ counts.get(size-1) + ": " + percent + "\n");
				//System.out.println(attributes.get(i) + ": " + counts.get(i));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<String> updateAttributes(List<String> attributes, BufferedReader file){
		 String row;
		 String[] data; 
		 List<String> l = attributes;
		 int index = -1;
		 try {
			 while ((row = file.readLine()) != null) {
				 data = row.split(":");
				 if((index = checkAttribute(data[0], attributes)) >= 0) {
					 // Do nothing, the attribute is already in the list
				 } else {
					 l.add(data[0]);
				 }
			}
		} catch (IOException e) {
			System.out.println("Failed to parse the file");
			e.printStackTrace();
		}
		return l;
	}
	
	private List<Integer> updateCounts(List<String> attributes, List<Integer> counts, BufferedReader file){
		List<Integer> c = pad(attributes, counts);
		String row;
		String[] data; 
		int index = -1;
		Integer value;
		
		//System.out.println(c);
		try {
			while ((row = file.readLine()) != null) {
				data = row.split(":");
				if((index = checkAttribute(data[0], attributes)) >= 0) {
					value = c.get(index);
					value = value + Integer.parseInt(data[1].trim());
					c.set(index, value);
				} else {
					// This shouldn't be reachable
					System.out.println("Error parsing the summary text files");
				}
			}
		} catch (IOException e) {
			System.out.println("Failed to parse the file");
			e.printStackTrace();
		}
		return c;
	}
	
	private int checkAttribute(String attribute, List<String> attributeList) {
		int index = -1;
		for(int i = 0; i < attributeList.size(); i++) {
			if(attribute.equals(attributeList.get(i))) {
				index = i;
				return index;
			}
		}
		return index;
	}
	
	private List<Integer> pad(List<String> attributes, List<Integer> counts){
		List<Integer> c = null;
		int asize = attributes.size();
		int csize = counts.size();
		c = counts;
		if(asize == csize) {
			// The two list are synced and nothing needs to be done 
			return c;
		} else {
			for(int i = csize; i < asize; i++) {
				// pad with 0's to prevent index errors
				c.add(0);
			}
		}
		return c;
	}
	
	/*
	 * Sort the text file again and get the counts for each attribute
	 * make sure that each count has an attribute to go to
	 */
	private List<Integer> getCounts(List<String> attributes, BufferedReader aggregateFile) {
		String row;
		String[] data; 
		List<Integer> l = new ArrayList<Integer>();
		
		try {
			while ((row = aggregateFile.readLine()) != null) {
				data = row.split(":");
				l.add(Integer.parseInt(data[1].trim()));
			}
		} catch (IOException e) {
			System.out.println("Failed to parse the aggregate file");
			e.printStackTrace();
		}
		return l;
	}


	/*
	 * Sort through the txt file and return a list of each of the names
	 * of the different attributes
	 */
	private List<String> getAttributes(BufferedReader aggregateFile) {
		String row;
		String[] data; 
		List<String> l = new ArrayList<String>();
		
		try {
			while ((row = aggregateFile.readLine()) != null) {
				data = row.split(":");
				l.add(data[0]);
			}
		} catch (IOException e) {
			System.out.println("Failed to parse the aggregate file");
			e.printStackTrace();
		}
		return l;
	}


	private FileWriter createFileWriter(String fileName) {
		FileWriter f = null;
		
		try {
			f = new FileWriter(fileName);
		} catch (IOException e) {
			System.out.println("IOExeption in creating the csv: " + fileName + "\n");
			e.printStackTrace();
		}
		return f;
	}
	
	
}