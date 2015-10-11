package qs.phan.test;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.VM;
import qs.phan.test.converter.Translator;

public class TestPathListener extends PropertyListenerAdapter {
	
	public TestPathListener(Config config, JPF jpf) {

		jpf.getReporter().getPublishers().clear();
		config.setProperty("symbolic.dp", "no_solver");
	}
	
	public void propertyViolated(Search search) {

		VM vm = search.getVM();
		ChoiceGenerator<?> cg = vm.getChoiceGenerator();
		if (!(cg instanceof PCChoiceGenerator)) {
			ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
			while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
				prev_cg = prev_cg.getPreviousChoiceGenerator();
			}
			cg = prev_cg;
		}
		if ((cg instanceof PCChoiceGenerator)
				&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
			PathCondition pc = ((PCChoiceGenerator) cg).getCurrentPC();
			/*
			TestConverter converter = new TestConverter();
			converter.convertPC(pc);
			//*/
			Translator tr = new Translator();
			pc.header.accept(tr);
			tr.print();
			
			System.out.println("\n" + pc.header.toString() + "\n");
		}
	}
}
