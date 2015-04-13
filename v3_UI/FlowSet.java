/*
 * This class is to store all flowsets in every recursion
 */
package com.test9;

import java.util.Vector;

public class FlowSet {
//	store every timestamp in each flowset
	Vector<String> time = new Vector<String>();
//	store every source IP in each flowset
	Vector<String> src = new Vector<String>();
//	store every destination IP in each flowset
	Vector<String> dst = new Vector<String>();
//	store the length of every packet in each flowset
	Vector<String> length = new Vector<String>();
//	store the protocol of every packet in each flowset
	Vector<String> protocol = new Vector<String>();
}

