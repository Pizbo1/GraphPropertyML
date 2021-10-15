/**
 * 
 */
package csc426526.csvparser;

import csc426526.visitor.training.Visitor;

/**
 * @author Brandon Baggett
 * This is just a structure object to make holding
 * and passing the program's parameters easier
 */
public class Parameters {
	
	// The input folder for un-trimmed CSV files
	private String inputFolder;  
	// The folder that the un-trimmed CSV files go in
	private String trimFolder;
	// The folder for the output results
	private String outputFolder;
	// The threshold that PD values greater than or equal to
    // will be ignored
	private double threshold;    
	// If true then the program will skip the initial trimming phase
    // this assumes that the csv files are in the TRIMFOLDER and properly formated
	private boolean skipTrim; 
	// If not multi-threading then set to 1
	private int threadCount;     
	// The training model to be used which must be an implementation of the Visitor interface
	private Visitor trainModel;
	// The trimming model to be used which must be an implementation of the Visitor interface
	private Visitor trimModel;
	// the Separator is for Linux vs windows machines to make sure the structure is preserved
	private String fileSep = "/";
	
	public Parameters(String input, String trimmed, String output, double a, boolean skip, 
			int thread, Visitor train, Visitor trim, String slash) {
		inputFolder = input;
		trimFolder = trimmed;
		outputFolder = output;
		threshold = a;
		skipTrim = skip;
		threadCount = thread;
		trainModel = train;
		trimModel = trim;
		fileSep = slash;
	}
	
	public String getInputFolder() {
		return inputFolder;
	}
	
	public String getTrimmedFolder() {
		return trimFolder;
	}
	
	public String getOutputFolder() {
		return outputFolder;
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	public boolean getSkipTrim() {
		return skipTrim;
	}
	
	public int getThreadCount() {
		return threadCount;
	}
	public Visitor getTrainingModel() {
		return trainModel;
	}
	
	public Visitor getTrimModel() {
		return trimModel;
	}
	
	public String getFileSeperator() {
		return fileSep;
	}
}
