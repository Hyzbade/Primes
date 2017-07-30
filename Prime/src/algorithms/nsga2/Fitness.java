package algorithms.nsga2;

import java.util.Map;

import algorithms.nsga2.genes.Gene;

public interface Fitness {
	
	public double compute(Map<String, Gene> genes);
}
