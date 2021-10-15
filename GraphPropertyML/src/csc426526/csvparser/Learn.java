/**
 * 
 */
package csc426526.csvparser;

import java.io.IOException;

/**
 * @author Brandon Baggett
 * This file is to facilitate a block 
 * ^ what was I trying to say????
 */
public class Learn {
	// Program Parameters
	// The input folder for un-trimmed CSV files
	private static final String INPUTFOLDER = "/home/brandon/Documents/temp/input";  
	// The folder that the trimmed CSV files go in
	private static final String TRIMFOLDER = "/home/brandon/Documents/temp/trimmed";
	// The folder for the output results
	private static final String OUTPUTFOLDER = "/home/brandon/Documents/temp/output";
	// The threshold that PD values greater than or equal to
    // will be ignored
	private static final double THRESHOLD = .2;    
	// If true then the program will skip the initial trimming phase
    // this assumes that the CSV files are in the TRIMFOLDER and properly formated
	private static final boolean SKIPTRIM = false; 
	// If true then the program will not aggregate the results in the
	// OUTPUTFOLDER, you need a separate visitor to handle the aggregation
	private static final boolean SKIPAGGREGATE = false;
	// If not multi-threading then set to 1
	// This doesn't work at the moment
	private static final int THREADCOUNT = 1;     
	// The training model to be used which must be an implementation of the Visitor interface
	private static final Visitor TRAINMODEL = new CountVisitor();
	// The training model to be used which must be an implementation of the Visitor interface
	private static final Visitor TRIMMODEL = new TrimVisitor();
	// The aggregation model to be used which must be an implementation of the Visitor interface
	private static final AggregatorVisitor AGGREGATIONMODEL = new CountAggregator();
	// For specifying how files are separated
	private static final String FILESEPARATOR = System.getProperty("file.separator");
	// For specifying the systems newline symbol
	private static final String NEWLINE = System.getProperty("line.separator"); // this still needs to be
																	// added to the parameters object
																	// and update throughout the files

	public static void main(String[] args) {
		// Make the parameter object
		Parameters p = new Parameters(INPUTFOLDER, TRIMFOLDER, OUTPUTFOLDER, THRESHOLD, 
									SKIPTRIM, THREADCOUNT, TRAINMODEL, TRIMMODEL, FILESEPARATOR);
		
		Folder f = new Folder(p, INPUTFOLDER, "csv");
		
		try {
			if(!SKIPTRIM) {
				/*
				 *  I probably need to change these signatures
				 *  bc I am calling the visit function for a particular model
				 *  and then passing the moddel
				 *  which seems unnessary 
				 */
				TRIMMODEL.visit(f, TRIMMODEL, p);
			}
		} catch (IOException e) {
			System.out.println("Trimming Failure");
			e.printStackTrace();
		}
		f.close();
		
		f = new Folder(p, TRIMFOLDER, "csv");
		try {
			TRAINMODEL.visit(f, TRAINMODEL, p);
		} catch (IOException e) {
			System.out.println("Training failure");
			e.printStackTrace();
		}
		
		f = new Folder(p, OUTPUTFOLDER, "txt");
		AGGREGATIONMODEL.visit(f, p);
		
		f.close();
		System.out.println("done\n");
		return;
	}
}
