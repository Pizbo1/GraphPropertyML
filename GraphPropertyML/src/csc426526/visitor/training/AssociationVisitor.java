package csc426526.visitor.training;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import csc426526.csvparser.CSVFile;
import csc426526.csvparser.Parameters;

/**
 * @author Brandon Baggett
 * This visitor implements the Apriori algorithm for finding frequent itemsets
 */
public class AssociationVisitor implements Visitor {
	@Override
	public void visit(CSVFile f, Parameters p) {
		FileWriter w = createFileWriter(p.getOutputFolder() + p.getFileSeperator() + f.getFileName() + ".output.txt");
		List<String[]> attributes = new ArrayList<String[]>();
		Map<Integer, List<String[]>> finalFreq = new HashMap<Integer, List<String[]>>();
		int location = 0;
		String row;
		String[] lineData;
		Map<Integer, String[]> data = new HashMap<Integer, String[]>();
		List<String[]> a = new ArrayList<String[]>();
		int rowCount = 0;
		int aSize;
		int dSize;
		double sup;
		List<String> item = null;
		double minSup = 0.4; // this should probably be passed as a parameter, but I need to look into the best way to add parameters that
							// are only used in specific models
		
		attributes = getAttributes(f);
		aSize = attributes.size();
		for(int i = 0; i < aSize; i++) {
			if(attributes.get(i).equals("PD")) {
				location = i;
			}
		}
		if(aSize == 0 | location == 0) {
			System.out.println("Incorrectly formatted csv.");
			return;
		}
		aSize = location;
		try {
			while ((row = f.getReader().readLine()) != null) {
				lineData = row.split(",");
				data.put(rowCount, lineData);
				rowCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * It's possible that when trimming that file has no elements and 
		 * when we go to calc the sup for those files
		 * it crashes, and there is not point in proceeeding so just exit
		 */
		if(rowCount == 0) {
			return; 
		}
		
		dSize = data.size();
	
		// find the frequent 1-itemsets
		/*
		 * loop through each attribute
		 * check sup
		 * if good add to a
		 */
		for(int i = 0; i < aSize; i++) {
			item = new ArrayList<String>();
			item.add(attributes.get(i));
			sup = calcSup(item, rowCount, data, attributes);
			if(sup >= minSup) {
				a.add(test);
			}
		}
		
		
		/*
		 * a is the list of frequent i-itemsets
		 * this probably doesn't need 0 based indexing
		 * 
		 * 
		 * this isn't set in stone, but I think I need my "a" to be the attribute names
		 * and then I will do a lookup for the indexes and then search based on that
		 */
		finalFreq.put(1, a);
		
		for(int i = 2; i < 4/*aSize*/; i++) {
			a = findFrequentISets(i, attributes, a, rowCount, data);
			if(a == null) {
				/*
				 * There are no more frequent itemsets and we can exit
				 */
				break;
			}
			/*
			 * otherwise we add the a (the frequent itemsets of size i to
			 * the final frequent set 
			 */
			finalFreq.put(i, a);
		}
		// write to output
		try {
			for(int i = 1; i < finalFreq.size(); i++) {
				System.out.println(i + " : " + finalFreq.get(i));
			}
			System.out.println(finalFreq.size());
			w.append("size: " + finalFreq);
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * @param a: the rule that are we checking
	 * @param data: the data from the csv file
	 */
	private double calcSup(List<String> a, int total, Map<Integer, String[]> data, List<String> attributes) {
		double sup = 0;
		int aSize = a.size();
		int dSize = data.size();
		int index;
		int count = 0;
		String[] row;
		/*
		 * Iterate through each row 
		 * then each column until we find a math for the 0th
		 * element of the rule
		 */
		for(int i = 0; i < dSize; i++) {
			for(int j = 0; j < aSize; j++) {
				row = data.get(i);
				if(checkRow(a, row, attributes, 0)) {
						count += 1;
				}
			}
		}
		sup = Double.valueOf(count)/Double.valueOf(total);
		return sup;
	}
	
	/*
	 * This assumes that you are checking the attributes in the order they appear in the 
	 * attributes list
	 * 
	 * this still isn't right bc I am assuming that it acts like a normal "database"
	 * but its 1s and 0s in the columns
	 * 
	 * I think I can do this by getting the index
	 */
	private boolean checkRow(List<String> a, String[] row, List<String> attributes, int i) {
		int aSize = a.size();
		int length = row.length;
		int index = attributes.indexOf(a.get(i));
		
		if(Integer.parseInt(row[index].trim()) == 1) {
			// Have we checked and found all the items in the list 
			if(i == aSize-1) {
				return true;
			} else {
				return checkRow(a,row, attributes, i+1);
			}
		}	
		return false;
	}
	
	/*
	 * I probably won't use this, but its here incase someone
	 * wants to calculate strong rules or something
	 */
	private double calcConf(List<String> a, List<String> b, int total, Map<Integer, 
						String[]> data, List<String> attributes) {
		List<String> ab = a;
		ab.addAll(b);
		double aSup = calcSup(a, total, data, attributes);
		double abSup = calcSup(ab, total, data, attributes);
		double conf = abSup/aSup;
		return conf;
	}

	
	/*
	 * @param a: is a list of the frequent i-1 itemsets
	 * 
	 * this builds a list of all i-itemsets from attributes
	 * that appear in a
	 */
	
	// I need to make the returned object a string array 
	private List<String> buildCk(List<String> a, int i) {
		List<String> output = null;
		List<String> temp = null;
		int aSize = a.size();
		
		//System.out.println(a/*.get(0).split(",")*/);
		output = addAttributes(a.get(0).split(","), a, output, i, 0);
		//System.out.println(output);
		if(output == null) {
			/*
			 *  If it can't make a i-itemset on the first iteration then
			 *  no subsequent iterations will produce an i-itemset
			 *  so we are done
			 */
			return null;
		}
		for(int j = 1; j < aSize; j++) {
			temp = null;
			temp = addAttributes(a.get(j).split(","), a, output, i, j);
			output.addAll(temp);
		}
		
		//System.out.println("here: " + output);
		return output;
	}
	
	/*
	 * This could probably use a better name
	 * It takes a i-1 attribute list and then adds any other attributes
	 * that appear in the other frequent i-1 itemsets
	 * 
	 * @param a: an array of 1 set of frequent i-1 itemsets
	 * @param b: the list of all frequent i-1 itemsets
	 * @param current: the list of current i-itemsets build in previous iterations of this method call
	 * @param i: the size itemset we are currently building
	 * @param index: the index of 
	 * 
	 */
	private List<String> addAttributes(String[] a, List<String> b, List<String> current, int i, int index){
		List<String> output = new ArrayList<String>();
		int bSize = b.size();
		String temp;
		String prefix;
		testing new setup2
		/*
		 * Build a prefix string based on the i-1 attributes in a
		 * to make adding the i-itemsets to output easier 
		 */
		prefix = a[0];
		//System.out.println(a[0]);
		for(int j=1; j < i-1; j++) {
			//System.out.println("i: " + i + ": j : " + j);
			//System.out.println(j + " : " + a[i]);
			prefix = prefix + ", " + a[j];
		}
		//System.out.println(prefix);
		//System.out.println(b);
		// I think I need to be checking to make sure I don't return any list larger than i
		for(int j = index+1; j < bSize; j++) {
			for(int k = 0; k < i-1; k++) {
				for(int l = 0; l < i-1; l++) {
					if(a[k] == b.get(j).split(",")[l]) {
						// do nothing
					} else {
						//System.out.println(prefix);
						prefix = prefix + ", " + b.get(j).split(",")[l];
						output.add(prefix);					
					}
				}
			}
		}
		return output;
	}
	
	
	private List<String> trimCk(List<String> a, int i, int rowCount, Map<Integer, String[]> data,
								List<String> attributes) {
		// Remove all from C_i+1 all itemsets that contain i-itemsets that are not in F_i 
		List<String> output = null;
		
		return output;
	}

	/*
	 * @param i: is the size itemset we are currently looking at
	 * @param attributes: is a list of all of the attributes in the csv file
	 * @param a: is the list of frequent i-1 itemsets
	 * @param rowCount: is the number of rows in the csv file
	 * @param data: is a map between the row number and the values of the csv file value
	 *     i.e., (1,0,0,1....) which corresponds to the attributes list
	 */
	private List<String> findFrequentISets(int i, List<String> attributes, List<String> a,
									int rowCount, Map<Integer, String[]> data) {
		List<String> output = new ArrayList<String>();
		int size = attributes.size();

		output = buildCk(a, i);
		//output = trimCk(output, i, rowCount, data, attributes);
		/*
		 * from here I need to calculate which of the remaining itemsets are frequent
		 * and add them to the output
		 */

		return output;
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
}
