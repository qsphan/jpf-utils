package qs.phan.spf.count;

import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/*
 * Count concrete paths using latte
 */
public class ConcreteCounter {

	protected String dir; // directory to store latte's result
	protected int dimension;
	
	protected HashMap<String,Domain> domains;
	protected ArrayList<Hyperplane> lstDomainHP;	
	
	public ConcreteCounter(HashMap<String,Domain> d) {
		domains = d;
		dimension = domains.size();
		computeHyperplanesOfDomains();
	}
	
	/*
	 * set working directory for latte
	 */
	public void setDirectory(String tmpDir) throws FileNotFoundException {
		File f = new File(dir = tmpDir);
		if(!f.exists() || !f.isDirectory()) {
			throw new FileNotFoundException();
		}
	}
	
	/*
	 * compute Hyperplanes of domain constraints
	 */
	private void computeHyperplanesOfDomains() {
		
		lstDomainHP = new ArrayList<Hyperplane>();
		
		for (Map.Entry<String, Domain> entry : domains.entrySet()) {
		    Domain d = entry.getValue();
		    
		    Hyperplane h = d.lowerBoundToHyperplane(dimension);
		    if(h != null) {
		    	lstDomainHP.add(h);
		    }
		    
		    h = d.upperBoundToHyperplane(dimension);
		    if(h != null) {
		    	lstDomainHP.add(h);
		    }
		}
	}
	
	protected String findLatte(){
		try {
			Process p = Runtime.getRuntime().exec("command -v count");
			p.waitFor();
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			// TODO: handle the case when latte is not installed
			br.close();
			return line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public long count(PathCondition pc) {
		
		String filename = dir + "/pc.latte";
		String latte = findLatte();
		if(latte != null){
			String cmd = latte + " " + filename;
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				BufferedReader br = new BufferedReader(new FileReader(new File(dir + "/numOfLatticePoints")));
				long count = Long.parseLong(br.readLine());
				br.close();
				return count;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	protected void createInputForLatte(PathCondition pc){
		
		ArrayList<Hyperplane> lstHP = computeHyperplaneFromPC(pc);
		// Generate input for latte from 2 lists of Hyperplanes
		// The first line is: m d+1
		StringBuilder sb = new StringBuilder();
		sb.append(lstDomainHP.size() + lstHP.size() + " " + (dimension + 1) + "\n");
		
		for (Hyperplane h: lstDomainHP){
			sb.append(h.toString());
		}
		
		for (Hyperplane h: lstDomainHP){
			sb.append(h.toString());
		}
		
		String filename = dir + "/pc.latte";
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			bw.write(sb.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected ArrayList<Hyperplane> computeHyperplaneFromPC(PathCondition pc){
		// TODO
		Constraint c = pc.header;
		
		return null;
	}
	
	public void clean() {
		try {
			FileUtils.cleanDirectory(new File(dir));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
