package algorithms.nsga2;

import java.util.Map;

import algorithms.nsga2.genes.Gene;

public interface Constraint {
	
	public boolean isTriggered(Map<String, Gene> genes);
}
