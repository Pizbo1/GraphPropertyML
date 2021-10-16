/**
 * 
 */
package csc426526.visitor.training;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import csc426526.csvparser.CSVFile;
import csc426526.csvparser.Parameters;

/**
 * @author Brandon Baggett
 * This is a simple analysis to get the counts for how often
 * each graph property appears in the trimmed csv file
 * 
 * Now that we're smarter we can call it absolute support
 */
public class CountVisitor implements Visitor {
	
	
	@Override
	public void visit(CSVFile f, Parameters p) {
		int rowCount = 0;
		String row;
		String[] data;
		String outputFileName = p.getOutputFolder() + p.getFileSeperator() + f.getFileName() + ".txt";
		List<Integer> count = new ArrayList<Integer>();
		List<String> attributes = new ArrayList<String>();
		Integer value; 
		int location = 0; 
		FileWriter w = createFileWriter(p.getOutputFolder() + p.getFileSeperator() + f.getFileName() + ".output.txt");
	
		attributes = getAttributes(f);
		for(int i = 0; i < attributes.size(); i++) {
			count.add(0); 
			if(attributes.get(i).equals("PD")) {
				location = i;
			}
		}
		if(attributes.size() == 0 | location == 0) {
			System.out.println("Incorrectly formatted csv.");
			return;
		}
		
		try {
			while ((row = f.getReader().readLine()) != null) {
				data = row.split(",");
				for(int i = 0; i < location; i++) {
					value = count.get(i);
					value = value + Integer.parseInt(data[i].trim());
					count.set(i, value);
				}
				rowCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			for(int i = 0; i < location; i++) {
				//System.out.println(attributes.get(i) + ": " + count.get(i));
				w.append(attributes.get(i) + ": " + count.get(i) + "\n");	
			}
			w.append("Count: " + rowCount + "\n");
		} catch (IOException e) {
			System.out.println("Failed to write output");
			e.printStackTrace();
		}
			//System.out.println(attributes.get(i) + ": " + count.get(i));
			//w.append(attributes.get(i) + ": " + count.get(i) + "\n");
		try {
			w.close();
		} catch (IOException e) {
			System.out.println("Failed to close filewriter");
			e.printStackTrace();
		}
		return;
	}

	/* This method finds the index of the column containing the PD measure
	 * so that we only have to check that column when trimming the CSV file
	 */
	private List<String> getAttributes(CSVFile f) {
		List<String> l = new ArrayList<String>();
		try {
			String row = f.getReader().readLine();
			String[] data = row.split(",");
			for(int i = 0; i < data.length; i++) {
				l.add(data[i]);
			}
		} catch (IOException e) {
			System.out.println("IOExeption in reading through the csv: " + f.getFileName() + ".\n");
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
