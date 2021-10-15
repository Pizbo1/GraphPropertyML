package csc426526.visitor.aggregation;

import csc426526.csvparser.Folder;
import csc426526.csvparser.Parameters;

public interface AggregatorVisitor {
	public void visit(Folder f, Parameters p);
}