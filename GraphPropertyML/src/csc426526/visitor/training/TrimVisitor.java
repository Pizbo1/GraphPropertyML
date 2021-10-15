/**
 * 
 */
package csc426526.visitor.training;

import java.io.FileWriter;
import java.io.IOException;

import csc426526.csvparser.CSVFile;
import csc426526.csvparser.Parameters;

/**
 * @author Brandon Baggett
 * It has access to the file, so it needs to iterate through it, and then 
 * write the output to the trimmedfolder
 * and then close the new file only
 * 
 * I need to make a new folder object at some point, but i think that needs
 * to be in the lear.java folder after we do the trimming
 * assuming that we are trimming
 */
public class TrimVisitor implements Visitor {
	@Override
	public void visit(CSVFile f, Parameters p) {
		String row;
		int location;
		String[] data;
		
		String outputFileName = p.getTrimmedFolder() + p.getFileSeperator() + f.getFileName() + ".trim.csv";
		//System.out.println("outputFileName: " + outputFileName);
		FileWriter w = createFileWriter(outputFileName);
	
		try {
			location = findPD(f, w);
			/*
			 * Check each line of the CSV file to see if PD <= threshold
			 * if it is write the line to the output CSV file in the
			 * TRIMMEDFOLDER directory 
			 */
			if(location == 0) {
				System.out.println("Incorrectly formatted csv.");
				return;
			}
			while ((row = f.getReader().readLine()) != null) {
				data = row.split(",");
				if(Double.parseDouble(data[location]) <= p.getThreshold()) {
					for(int i = 0; i < data.length - 1; i++) {
						w.append(data[i] + ", "); 
					}
					w.append(data[data.length-1] + "\n");
				}
			}
			w.close();
		} catch (IOException e) {
			System.out.println("IOExeption in reading through the csv: " + f.getFileName() + ".\n");
			e.printStackTrace();
		}
	}

	/* This method finds the index of the column containing the PD measure
	 * so that we only have to check that column when trimming the CSV file
	 */
	private int findPD(CSVFile f, FileWriter w) {
		int location = 0;
		try {
			String row = f.getReader().readLine();
			String[] data = row.split(",");
			for(int i = 0; i < data.length; i++) {
				if(data[i].equals("PD")) {
					location = i;
					break;
				}
			}
			if(location != 0) {
				w.append(row);
				w.append("\n");
			}
		} catch (IOException e) {
			System.out.println("IOExeption in reading through the csv: " + f.getFileName() + ".\n");
			e.printStackTrace();
		}
		return location;
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

