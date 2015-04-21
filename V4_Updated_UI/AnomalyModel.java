package com.test15;

import java.util.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.awt.*;

public class AnomalyModel extends AbstractTableModel{

	
	public static Vector<Vector<String>> rowData;
	public static Vector<String> columnNames;
	
	public AnomalyModel(){
		rowData = new Vector<Vector<String>>();
		columnNames = new Vector<String>();
		columnNames.add("Source Prefix");
		columnNames.add("Destination Prefix");
		columnNames.add("Flow Size");
//		columnNames.add("Times");
	}
	
	public AnomalyModel(Vector<Vector<String>> anomalySet){
		
		rowData = anomalySet;
		
		columnNames = new Vector<String>();
		columnNames.add("Source Prefix");
		columnNames.add("Destination Prefix");
		columnNames.add("Flow Size");
//		columnNames.add("Times");
		
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return this.columnNames.size();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return this.rowData.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		
		return (this.rowData.get(row)).get(col);
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return this.columnNames.get(column);
	}

}
