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
		List<String> attributes = new ArrayList<String>();
		Map<Integer, List<String>> finalFreq = new HashMap<Integer, List<String>>();
		int location = -1;
		String row;
		String[] lineData;
		Map<Integer, String[]> data = new HashMap<Integer, String[]>();
		List<String> a;
		int rowCount = 0;
		
		
		attributes = getAttributes(f);
		for(int i = 0; i < attributes.size(); i++) { 
			if(attributes.get(i).equals("PD")) {
				location = i;
			}
		}
		if(attributes.size() == 0 | location == -1) {
			System.out.println("Incorrectly formatted csv.");
			return;
		}

		try {
			while ((row = f.getReader().readLine()) != null) {
				lineData = row.split(",");
				data.put(rowCount, lineData);
				rowCount++;
			}
		} catch (IOException e) {
			System.out.println("Error: IOException in AssociationVisitor.visit on parsing a csv file");
			e.printStackTrace();
		}
		
		for(int i = 0; i < 1; i++) {
			a = null; // I don't think this is needed, but I want to ensure it is reset between each run
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
		
		/*
		 * Iterate through each attribute in the rule, then through each row of the data map
		 * and check the column that corresponds to the attribute we are considering
		 * and sum of the values 
		 */
		for(int i = 0; i < aSize; i++) {
			/*
			 * Get the location in the map that corresponds to the attribute we are currently
			 * looking at ("i" is for finding the column the attribute is in
			 * and "j" is for iterating through the data file by row)
			 * 
			 * note, this only works because we are looking at a single file
			 * if we did this while aggregating, then the indexes might not line up
			 */
			index = attributes.indexOf(a.get(i));
			for(int j = 0; j < dSize; j++) {
				count += Integer.parseInt(data.get(j)[i]);
			}
		}
		
		sup = count/total;
		return sup;
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

	private boolean keep() {
		boolean status = false;
		
		return status;
	}
	
	private List<String> buildCk(List<String> a, int i) {
		// place into C_i+1 all combinations of i+1 itemsets that can be formed from F_i
		List<String> output = null;
		List<String> singles = null;
		List<String> temp = null;
		int aSize = a.size();
		int singlesSize;
		String[] line;
		
		/*
		 * Iterate through each frequent i-1 set in a
		 * Split each frequent set into its i-1 individual attributes
		 * if the singles list is empty or the attribute isn't in the list yet
		 * then add it to the list, otherwise do nothing
		 * 
		 * I don't think I am going to use this method any more
		 */
		for(int j = 0; j < aSize; j++) {
			line = a.get(j).split(",");
			for(int k = 0; k < i-1; k++) {
				if(singles == null) {
					singles.add(line[k]);
				} else if(singles.indexOf(line[k]) == -1) {
					singles.add(line[k]);
				}
				// else do nothing because the attribute is already in the list
			}
		}
		
		
		/*
		 * Build the output list of all i-itemsets from attributes that appear
		 * in the list "a"
		 */
		singlesSize = singles.size();
		for(int j = 0; j < singlesSize; j++) {
			
		}
		
		
		//-----------------------------------------------------------------------------------
		// second attempt
		/*
		 * This method is based on taking each of the i-1 itemsets and then adding each individual element from
		 * the other itemsets if they i itemset doesn't already exist
		 */
		output = addAttributes(a.get(0).split(","), a, output, i);
		if(output == null) {
			/*
			 *  If it can't make a i-itemset on the first iteration then
			 *  no subsequent iterations will produce an i-itemset
			 *  so we are done
			 */
			
			return null;
		}
		for(int j = 0; j < a.size(); j++) {
			temp = null;
			temp = addAttributes(a.get(j).split(","), a, output, i);
			output.addAll(temp);
		}
		
		return output;
	}
	
	/*
	 * This could probably use a better name
	 * It takes a i-1 attribute list and then adds any other attributes
	 */
	private List<String> addAttributes(String[] a, List<String> b, List<String> current, int i){
		List<String> output = null;
		int bSize = b.size();
		String temp;
		String prefix;
		
		/*
		 * Build a prefix string based on the i-1 attributes in a
		 * to make adding the i-itemsets to output easier 
		 */
		prefix = a[0];
		for(int j=1; j < i; j++) {
			prefix = prefix + ", " + a[j];
		}
		
		for(int j = 0; j < bSize; j++) {
			for(int k = 0; k < i; k++) {
				for(int l = 0; l < i; l++) {
					if(a[k] == b.get(j)[k]) {
						
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

	private List<String> findFrequentISets(int i, List<String> attributes, List<String> a,
									int rowCount, Map<Integer, String[]> data) {
		List<String> output = null;
		int size = attributes.size();

		/*
		 * I need to be doing something with j here
		 * probably using it to iterate through the data map
		 * but idk rn
		 */
		for(int j = 0; i < size; i++) {
			output = buildCk(a, i);
			output = trimCk(output, i, rowCount, data, attributes);
			/*
			 * from here I need to calculate which of the remaining itemsets are frequent
			 * and add them to the output
			 */
		}
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
