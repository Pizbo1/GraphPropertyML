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
public class AssociationVisitor2 implements Visitor {
	@Override
	public void visit(CSVFile f, Parameters p) {
		FileWriter w = createFileWriter(p.getOutputFolder() + p.getFileSeperator() + f.getFileName() + ".output.txt");
		List<String> attributes = new ArrayList<String>();
		int aSize; // the size of the attributes object 
		Map<Integer, String[]> data = new HashMap<Integer, String[]>(); // basically the database
		String row;
		String[] lineData;
		int rowCount = 0;
		List<String[]> F_i = new ArrayList<String[]>();
		List<String[]> F_iold = new ArrayList<String[]>();
		double sup;
		double minSup = .6; // this should probably be a parameter, but I will pass it in later
		String rule[] = new String[1];
		List<List<String[]>> finalFreq = new ArrayList<List<String[]>>();
		int iSize;
		
		attributes = getAttributes(f);
		aSize = attributes.size();
		
		if(aSize == 0) {
			System.out.println("Incorrectly formatted csv.");
			return;
		}
		// Build the "database" object
		try {
			while ((row = f.getReader().readLine()) != null) {
				lineData = row.split(",");
				data.put(rowCount, lineData);
				rowCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// This can happen if nothing in the original data was within the threshold
		if(rowCount == 0) {
			System.out.println("The file is empty");
			try {
				w.append("The input file was empty");
				w.close();
			} catch (IOException e) {
				System.out.println("Failed to write output");
				e.printStackTrace();
			}
			return;
		}
		
		for(int i = 0; i < aSize; i++) {
			 rule[0] = attributes.get(i);
			 sup = calcSup(rule, rowCount, data, attributes);
			 if(sup >= minSup) {
				 F_i.add(rule[0].split(" "));
				 // I also need to do the final here, but I will figure out what that looks like later
				 // actually i think i need to do this below the loop
			 }
		}
		finalFreq.add(F_i);
		// Up to here, I have found the frequent 1-itemsets, now I need to loop through the remaining possibilities
		iSize = 1;
		
		while(true) {
			
	
			iSize++;
			System.out.println(iSize);
			F_iold = F_i;
			F_i = buildCk(F_iold, iSize-1);
//			System.out.println("built list:");
//			printStringList(F_i);
			F_i = trimCk(F_i, rowCount, data, attributes, minSup);
//			System.out.println("trimmed list:");
//			printStringList(F_i);
			// if its empty then we can exit
			if(F_i.size() == 0) {
				break;
			}
			finalFreq.add(F_i);
		}
		
		/*
		 * I should also probably make a helper method for writing off a 
		 * List<String[]> object and then just iterate through the finalFreq list
		 * 
		 * 
		 */
		
		// this is just for testing
		try {
//			for(int i = 0; i < aSize; i++) {
//				w.append(attributes.get(i) +  "\n");
//			}
//			w.append("\n\n\n");
//			w.append(F_i.size() + "\n");
//			for(int i = 0; i < F_i.size(); i++) {
//				w.append(F_i.get(i)[0] + ": i : " + i + "\n");
//			}
			
			w.append("FinalFreq:\n");
			//System.out.println(finalFreq.size());
			for(int i = 0; i < finalFreq.size(); i++) {
				w.append(i+1 + "-itemsets : \n");
				for(int j = 0; j < finalFreq.get(i).size(); j++) {
					for(int k = 0; k < finalFreq.get(i).get(j).length; k++) {
						//System.out.println(finalFreq.get(i).get(i).length);
						w.append(finalFreq.get(i).get(j)[k] + " ");
					}
					w.append("\n");
				}
				
			}
			
			
		} catch (IOException e) {
			System.out.println("Failed to write output");
			e.printStackTrace();
		}
		
		try {
			w.close();
		} catch (IOException e) {
			System.out.println("Failed to close file");
			e.printStackTrace();
		}
		
		return; 
	}
	
	
	private void printStringList(List<String[]> l) {
		
		for(int i = 0; i < l.size(); i++) {
			System.out.println("\n");
			for(int j = 0; j < l.get(i).length; j++) {
				System.out.println(l.get(i)[j] + " ");
			}
		}
		
		
		return;
	}
	
	/*
	 * I could make this not be quite a apriori, and just manually check the sup for each
	 * rule (which ironically makes this not the apriori algorithm at all)
	 * 
	 * I am getting a heap overflow error, so I will probably have to fix this,
	 * but fortunately it looks like it is working, so I think I am almost done
	 * 
	 */
	private List<String[]> trimCk(List<String[]> unTrimmedCk, int totalRows,
			Map<Integer, String[]> data, List<String> attributes, double minSup){
		
		List<String[]> l = new ArrayList<String[]>();
		double sup;
		
		for(int i = 0; i < unTrimmedCk.size(); i++) {
			sup = calcSup(unTrimmedCk.get(i), totalRows, data, attributes);
			if(sup > minSup) {
				l.add(unTrimmedCk.get(i));
			}
		}
		
		return l;
	}
	
	private boolean isSetDuplicate(List<String[]> l, String[] newRule) {
		// iterate through each item in the list and see if it matches the new rule
		// which will involve iterating through the newrule
		// so two for loops 
		
		if(l.size() == 0) {
			return false;
		}
		
		for(int i = 0; i < l.size(); i++) {
			for(int j = 0; j < newRule.length; j++) {
				if(!(l.get(i)[j].equals(newRule[j]))) {
					return false;
				}
			}
		}
		
		// we are assuming they are true
		// if they are different then the loop will detect it and return early
		return true;
	}
	
	private Boolean isItemDuplicate(String[] originalRules, String newItem) {
		// one for loop, iterate through the original rules to see if the new item
		// appears in there
		
		for(int i = 0; i < originalRules.length; i++) {
			if(originalRules[i].equals(newItem)) {
				return true;
			}
		}
		// otherwise the new rule was not found so it is unique
		return false;
	}
	
	private String[] addNewItem(String[] originalRules, String newItem) {
		String[] newString;
		int newStringSize = originalRules.length + 1; 
		
		newString = new String[newStringSize];
		
		for(int i = 0; i < newStringSize - 1; i++) {
			newString[i] = originalRules[i];
		}
		
		newString[newStringSize-1] = newItem;
		
		return newString;
	}
	
	/*
	 * im1Size is the size of the previous iteration 
	 */
	private List<String[]> buildCk(List<String[]> f_im1, int im1Size){
		List<String[]> iItemSets = new ArrayList<String[]>();
		String[] tempRule = null;
		
		/*
		 * I think is where my error is
		 * I think I am making a new List<String[]> for each of the previous rules
		 * instead of adding them to the list as String[] objects
		 * this means I will probably have to change the three helper methods I made
		 * 
		 * 
		 * I actually think this might not need much changing
		 * the helper methods will do the bulk of the work
		 * 
		 * 
		 * nvm, i think this is fixed, I will remove it later
		 * 
		 */
		
		
		
		for(int i = 0; i < f_im1.size(); i++) { // take each i-1 frequent itemset
			for(int j = i+1; j < f_im1.size(); j++) { // check it against the remaining freq sets
				for(int k = 0; k < im1Size; k++) { // check each attribute of the remaining set
					if(!isItemDuplicate(f_im1.get(i), f_im1.get(j)[k])) {
						tempRule = addNewItem(f_im1.get(i), f_im1.get(j)[k]);
						if(!isSetDuplicate(iItemSets, tempRule)) {
							iItemSets.add(tempRule);
							//System.out.println(tempRule[0]);
						}
					}
					
					/*
					 * check if it is already in the "i loop" set
					 * if not, make a new rule, then check to make sure it doesnt already
					 * exist in iItemset (probably make a helper function for this
					*/
				}
			}
		}
		
		return iItemSets;
	}
	
	
	/*
	 * This checks to see if a rule is supported in a particular row of the "database"
	 */
	private boolean checkRow(String rule, List<String> attributes, String[] row) {
		for(int i = 0; i < attributes.size(); i++) {
			if(attributes.get(i).trim().equals(rule) & row[i].trim().equals("1")) {
				//System.out.println("here");
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Given a rule (i.e., a set of attributes) it returns the percentage of columns that
	 * all of the attributes are in
	 * 
	 * @param rules: each rules[i] should be the name of an attribute
	 * 
	 */
	private double calcSup(String[] rules, int totalRows, Map<Integer, String[]> data, List<String> attributes) {
		double support = 0.0;
		int aSize = attributes.size();
		int count = 0;
		String[] row;
		
		/*
		 * Iterate through each row, and then check each row to see if each rule 
		 * is present, if its not break out of the loop
		 * You should only get to the if statement that interates the count
		 * if all of the rules have pased the rowCheck
		 */
		for(int i = 0; i < totalRows; i++) {
			row = data.get(i);
			for(int j = 0; j < rules.length; j++) {
				if(!checkRow(rules[j], attributes, row)) {
					break;
				}
				if(j == rules.length - 1) {
					count++;
				}
			}
		}
		support = (double) count / (double) totalRows;
		//System.out.println(rules[0] + " " + support);
		//System.out.println(support);
		return support;
	}
	
	/* 
	 * This returns all of the attributes up to and including the PD column
	 */
	private List<String> getAttributes(CSVFile f) {
		List<String> l = new ArrayList<String>();
		try {
			String row = f.getReader().readLine();
			String[] data = row.split(",");
			for(int i = 0; i < data.length; i++) {
				if(data[i].equals("PD")){
					break;
				}
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
