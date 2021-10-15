/**
 * 
 */
package csc426526.csvparser;

/**
 * @author Brandon Baggett
 *
 */
public class AssociationVisitor implements Visitor {
	@Override
	public void visit(CSVFile f, Parameters p) {
		// TODO Auto-generated method stub

		// maybe make a database and implement 
		// the apriori algorithm
		
		
		// this shouldnt be too hard to convert to an aggregate version... I think....
		
		int i = 1;
		boolean keepGoing = true;
		
		while(keepGoing) {
			
		}
		
	}
	
	private long calcSup() {
		long sup = 0;
		
		return sup;
	}
	
	private long calcConf() {
		long conf = 0;
		
		return conf;
	}

	private boolean keep() {
		boolean status = false;
		
		return status;
	}
	
	/*
	 *  Apriori Algorithm
	 *  Find frequent 1 itemsets
	 *  for k =2 ; k is not empty; k++
	 *  set each count to zero
	 *  ....
	 */
	private void buildCk() {
		// place into C_i+1 all combinations of i+1 itemsets that can be formed from F_i
		return;
	}
	
	private void trimCk() {
		// Remove all from C_i+1 all itemsets that contain i-itemsets that are not in F_i 
		return;
	}

	private void findFrequentISets(int i) {
		// Scan the data base and find supp for each itemset in C_i+1
		// add to F_i
		// add to Final
		return;
	}
}
