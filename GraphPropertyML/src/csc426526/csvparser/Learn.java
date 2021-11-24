/**
 * 
 */
package csc426526.csvparser;

import java.io.IOException;

import csc426526.visitor.aggregation.AggregatorVisitor;
import csc426526.visitor.aggregation.CountAggregator;
import csc426526.visitor.training.AssociationVisitor;
import csc426526.visitor.training.AssociationVisitor2;
import csc426526.visitor.training.CountVisitor;
import csc426526.visitor.training.TrimVisitor;
import csc426526.visitor.training.Visitor;


/**
 * @author Brandon Baggett
 * This file is the driver file, the user shouldn't have to change anything except for the parameter 
 * definitions for this to run correctly. (Unless, they are implementing a new visitor class.)
 */
public class Learn {
	// Program Parameters
	/*
	 * TODO change this to be a generic path
	 * i.e., use the fileseperator, and the a <USERPATH> for everything up to /Documents/...
	 * 
	 * TODO Some of these don't need to be in the parameter object since they are 
	 * never used outside of this file (i.e., the SKIP  booleans, and maybe the 
	 * visitor models)
	 */
	// System level parameters, these shouldn't change
	// ------------------------------------------------------------------------------------------------------------------
	/*
	 *  For specifying how files are separated
	 */
	private static final String FILESEPARATOR = System.getProperty("file.separator");
	/*
	 * For specifying the systems newline symbol
	 */
	private static final String NEWLINE = System.getProperty("line.separator"); 
	// ------------------------------------------------------------------------------------------------------------------
	
	// Working directories 
	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * The input folder for un-trimmed CSV files
	 */
	private static final String INPUTFOLDER = "/home/brandon/Documents/temp/input";  
	/*
	 *  The folder that the trimmed CSV files go in
	 */
	private static final String TRIMFOLDER = "/home/brandon/Documents/temp/trimmed";
	/*
	 *  The folder for the output results
	 */
	private static final String OUTPUTFOLDER = "/home/brandon/Documents/temp/output";
	// ------------------------------------------------------------------------------------------------------------------
	
	// Misc Parameters
	// ------------------------------------------------------------------------------------------------------------------
	/*
	 * The threshold that PD values greater than or equal to
	 * will be ignored
	 */
	private static final double THRESHOLD = .2;    
	/*
	 *  If not multi-threading then set to 1
	 *  This isn't currently implemented
	 */
	private static final int THREADCOUNT = 1;     
	// ------------------------------------------------------------------------------------------------------------------
	
	// Visitors
	// ------------------------------------------------------------------------------------------------------------------
	/*
	 *  The training model to be used which must be an implementation of the Visitor interface
	 */
	private static final Visitor TRAINMODEL = new AssociationVisitor2();
	/*
	 * The training model to be used which must be an implementation of the Visitor interface
	 */
	private static final Visitor TRIMMODEL = new TrimVisitor();
	/*
	 *  The aggregation model to be used which must be an implementation of the Visitor interface
	 */
	private static final AggregatorVisitor AGGREGATIONMODEL = new CountAggregator();
	// ------------------------------------------------------------------------------------------------------------------

	
	// Model Select
	// ------------------------------------------------------------------------------------------------------------------
	/*
	 *  If true then the program will skip the initial trimming phase
	 *  this assumes that the CSV files are in the TRIMFOLDER and properly formated
     */
	private static final boolean SKIPTRIM = true; 
	/*
	 * If true then the program will skip the training phase for individual files
	 */
	private static final boolean SKIPTRAIN = false;
	/*
	 *  If true then the program will not aggregate the results in the 
	 *  OUTPUTFOLDER, you need a separate visitor to handle the aggregation
	 */
	private static final boolean SKIPAGGREGATE = true;
	// ------------------------------------------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		// Make the parameter object
		Parameters p = new Parameters(INPUTFOLDER, TRIMFOLDER, OUTPUTFOLDER, THRESHOLD, 
									SKIPTRIM, THREADCOUNT, TRAINMODEL, TRIMMODEL, FILESEPARATOR);
		
		Folder f;
		
		/*
		 * Take the input csv's and trim them based on the threshold parameter
		 * This assumes that all of the csv's are properly formated
		 * i.e., only the -dmanalysis csv files that list which attributes were used and their
		 * pd values, TP,TN, etc...
		 */
		try {
			if(!SKIPTRIM) {
				f = new Folder(p, INPUTFOLDER, "csv");
				f.accept(TRIMMODEL, p);
			}
		} catch (IOException e) {
			System.out.println("Trimming Failure");
			e.printStackTrace();
		}
		
		/*
		 * Take the trimmed csv files and use the training model
		 * This has the same assumptions as the trimming model for
		 * the csv format, except the csv's need to be in the TRIMFOLDER
		 * instead of the INPUTFOLDER
		 */
		try {
			if(!SKIPTRAIN) {
				f = new Folder(p, TRIMFOLDER, "csv");
				f.accept(TRAINMODEL, p);
			}
		} catch (IOException e) {
			System.out.println("Training failure");
			e.printStackTrace();
		}
		
		/*
		 * This aggregates the results and use the OUTPUTFOLDER
		 * for both its input and output.
		 * You need to make sure that the only files in the OUTPUTFOLDER
		 * are from the appropriate training model, or you might get 
		 * weird results or a crash
		 */
		try {
			if(!SKIPAGGREGATE) {
				f = new Folder(p, OUTPUTFOLDER, "txt");
				f.accept(AGGREGATIONMODEL, p);
			}
		} catch (IOException e) {
			System.out.println("Aggregation Failure.");
			e.printStackTrace();
		}
	
		System.out.println("done\n");
		return;
	}
}
