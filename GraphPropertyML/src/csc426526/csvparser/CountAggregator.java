/**
 * 
 */
package csc426526.csvparser;

import java.io.BufferedReader;
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

		/*
		 * Iterate through each file in the file list and make sure there aren't any new attributes
		 * and then update the attribute counts
		 */
		try {
			for(int i = 0; i < size; i++) {
				br = new BufferedReader(new FileReader(p.getOutputFolder() + p.getFileSeperator() + fileList.get(i)));
				attributes = updateAttributes(attributes, br);
				br.close();
				/*
				 * There is probably a more elegant way to do this, but I am not sure how atm
				 * maybe look into RandomAccessFiles?
				*/
				br = new BufferedReader(new FileReader(p.getOutputFolder() + p.getFileSeperator() + fileList.get(i)));
				counts = updateCounts(attributes, counts, br);
				br.close();
			}
		} catch (FileNotFoundException e) {
			System.out.print("Error: File not found, in CountAggregator.visit");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error: IOException in CountAggregator.visit when iterating through individual summary files");
			e.printStackTrace();
		}
		
		/*
		 * Make the new file and write the results
		 */
		FileWriter w = createFileWriter(p.getOutputFolder() + p.getFileSeperator() + "CountAggregationSummary.txt");
		size = attributes.size();
		try {
			w.append("Aggregate Summary\n"); // this is just here so that it crashes if you
											// don't delete the summary file between runs
											// I will fix it later
			w.append("attribute : attrCount: totalCount : percent\n");
			for(int i = 0; i < size; i++) {
				percent = counts.get(i).floatValue()/counts.get(size-1).floatValue();
				w.append(attributes.get(i) + ": " + counts.get(i) + ": "
										+ counts.get(size-1) + ": " + percent + "\n");
			}
			w.close();
		} catch (IOException e) {
			System.out.println("Error: IOException in CountAggregator.visit when summarizing results");
			e.printStackTrace();
		}
	}
	
	/*
	 * Iterate through a file and make add any new attributes to the attributes list
	 */
	private List<String> updateAttributes(List<String> attributes, BufferedReader file){
		 String row;
		 String[] data; 
		 List<String> l = attributes;
		 try {
			 while ((row = file.readLine()) != null) {
				 data = row.split(":");
				 if((checkAttribute(data[0], attributes)) >= 0) {
					 // Do nothing, the attribute is already in the list
				 } else {
					 l.add(data[0]);
				 }
			}
		} catch (IOException e) {
			System.out.println("Error: Failed to close the file in CountAggregator.updateAttributes");
			e.printStackTrace();
		}
		return l;
	}
	
	/*
	 * Takes a file updates the count list for each attribute that it finds. 
	 * TODO make sure this is actually checking the attributes correctly and not assuming based on
	 * position
	 */
	private List<Integer> updateCounts(List<String> attributes, List<Integer> counts, BufferedReader file){
		List<Integer> c = pad(attributes, counts);
		String row;
		String[] data; 
		int index = -1;
		Integer value;
		
		try {
			while ((row = file.readLine()) != null) {
				data = row.split(":");
				if((index = checkAttribute(data[0], attributes)) >= 0) {
					value = c.get(index);
					value = value + Integer.parseInt(data[1].trim());
					c.set(index, value);
				} else {
					// This shouldn't be reachable
					System.out.println("Error parsing the summary text files in CountAggregator.updateCounts");
				}
			}
		} catch (IOException e) {
			System.out.println("Erro: IOException in CountAggregator.updateCounts");
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
	 * 
	 * This currently isn't being used, but it could be a useful helper
	 * function
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
			System.out.println("Failed to parse the aggregate file in CountAggregator.getCounts");
			e.printStackTrace();
		}
		return l;
	}


	/*
	 * Sort through the txt file and return a list of each of the names
	 * of the different attributes
	 * 
	 * This currently isn't being used, but it could be a useful helper
	 * function
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
			System.out.println("Failed to parse the aggregate file in CountAggregator.getAttributes");
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