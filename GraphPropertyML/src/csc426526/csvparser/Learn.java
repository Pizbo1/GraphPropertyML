/**
 * 
 */
package csc426526.csvparser;

import java.io.IOException;

/**
 * @author Brandon Baggett
 * This file is to facilitate a block 
 *
 */
public class Learn {
	// Program Parameters
	// The input folder for un-trimmed CSV files
	private static final String INPUTFOLDER = "/home/brandon/Documents/temp";  
	// The folder that the un-trimmed CSV files go in
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
	private static final int THREADCOUNT = 2;     
	// The training model to be used which must be an implementation of the Visitor interface
	private static final Visitor TRAINMODEL = new CountVisitor();
	// The training model to be used which must be an implementation of the Visitor interface
	private static final Visitor TRIMMODEL = new TrimVisitor();
	// The aggregation model
	private static final AggregatorVisitor AGGREGATIONMODEL = new CountAggregator();
	// For specifying how files are separated
	// "/" for Linux, "\" for Windows 
	private static final String FILESEPARATOR = "/";

	public static void main(String[] args) {
		// Make the parameter object
		Parameters p = new Parameters(INPUTFOLDER, TRIMFOLDER, OUTPUTFOLDER, THRESHOLD, 
									SKIPTRIM, THREADCOUNT, TRAINMODEL, TRIMMODEL, FILESEPARATOR);
		
		Folder f = new Folder(p, INPUTFOLDER, "csv");
		
		try {
			if(!SKIPTRIM) {
				TRIMMODEL.visit(f, TRIMMODEL, p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Make a new folder for the trimmed CSVs
		
		f = new Folder(p, TRIMFOLDER, "csv");
		//System.out.println(f.getFileList());
		try {
			TRAINMODEL.visit(f, TRAINMODEL, p);
		} catch (IOException e) {
			System.out.println("training failure");
			e.printStackTrace();
		}
		
		f = new Folder(p, OUTPUTFOLDER, "txt");
		AGGREGATIONMODEL.visit(f, p);
		
		f.close();
		System.out.println("done\n");
		return;
	}
}
