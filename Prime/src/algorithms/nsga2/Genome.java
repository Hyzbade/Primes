package algorithms.nsga2;

import java.util.HashMap;
import java.util.Map;

import algorithms.nsga2.genes.DoubleGene;
import algorithms.nsga2.genes.Gene;
import algorithms.nsga2.genes.IntegerGene;

public class Genome {
	
	public Genome() {

	}
	
	/**
	 * Problème :<br>
	 * - soit deux gènes A et B<br>
	 * - soit un objectif T = X*Y (X et Y a priori inconnus par l'algorithme)<br>
	 * <br>
	 * > trouver A et B tel que A*B = T<br>
	 * <br>
	 * <i>TPNF = Two Prime Numbers Factorization</i>
	 */
	public static Map<String, Map> integerTPNF() {
		Double target = 4583d * 2971d;
		Integer lowerBound = 2;
		Integer upperBound = target.intValue();
		
		/*
		 * Définition des gènes
		 */
		Map<String, Gene> genes = new HashMap<>();
		
		genes.put("A", new IntegerGene(lowerBound, upperBound));
		genes.put("B", new IntegerGene(lowerBound, upperBound));
		
		/*
		 * Définition des fonctions d'évaluation à minimiser
		 */
		Map<String, Fitness> fitnesses = new HashMap<>();
		
		fitnesses.put("N-(A*B)", new Fitness() {
			public double compute(Map<String, Gene> genes) {
				Integer A = ((IntegerGene) genes.get("A")).value;
				Integer B = ((IntegerGene) genes.get("B")).value;

				return Math.abs(target - (A*B));
			}
		});
		
		/*
		 * Définition des contraintes
		 */
		Map<String, Constraint> constraints = new HashMap<>();
		
		/*
		 * Création du génôme
		 */
		Map<String, Map> genome = new HashMap<>();
		
		genome.put("GENES", genes);
		genome.put("FITNESSES", fitnesses);
		genome.put("CONSTRAINTS", constraints);
		
		return genome;
	}
	
	/**
	 * Problème :<br>
	 * - soit deux gènes A et B<br>
	 * - soit un objectif T = X*Y (X et Y a priori inconnus par l'algorithme)<br>
	 * <br>
	 * > trouver A et B tel que A*B = T<br>
	 * <br>
	 * <i>TPNF = Two Prime Numbers Factorization</i>
	 */
	public static Map<String, Map> doubleTPNF() {
		Double target = 4583d * 2971d;
		Double lowerBound = 2d;
		Double upperBound = target;
		
		/*
		 * Définition des gènes
		 */
		Map<String, Gene> genes = new HashMap<>();
		
		genes.put("A", new DoubleGene(lowerBound, upperBound));
		genes.put("B", new DoubleGene(lowerBound, upperBound));
		
		/*
		 * Définition des fonctions d'évaluation à minimiser
		 */
		Map<String, Fitness> fitnesses = new HashMap<>();
		
		fitnesses.put("N-(A*B)", new Fitness() {
			public double compute(Map<String, Gene> genes) {
				Double A = ((DoubleGene) genes.get("A")).value;
				Double B = ((DoubleGene) genes.get("B")).value;

				return Math.abs(target - (A*B));
			}
		});
		
		/*
		 * Définition des contraintes
		 */
		Map<String, Constraint> constraints = new HashMap<>();
		
		/*
		 * Création du génôme
		 */
		Map<String, Map> genome = new HashMap<>();
		
		genome.put("GENES", genes);
		genome.put("FITNESSES", fitnesses);
		genome.put("CONSTRAINTS", constraints);
		
		return genome;
	}
	
	/**
	 * <u>Référence :</u><br>
	 * Binh, T., and Korn, U. Mobes : A multiobjective evolution strategy for constrained optimization problems.<br>
	 * <i>The Third International Conference on Genetic Algorithms (Mendel 97) 1</i> (1997), 27.<br>
	 */
	public static Map<String, Map> BNH() {
		
		/*
		 * Définition des gènes
		 */
		Map<String, Gene> genes = new HashMap<>();
		
		genes.put("x1", new DoubleGene(0d, 5d));
		genes.put("x2", new DoubleGene(0d, 3d));
		
		/*
		 * Définition des fonctions d'évaluation à minimiser
		 */
		Map<String, Fitness> fitnesses = new HashMap<>();

		fitnesses.put("f1", new Fitness() {
			public double compute(Map<String, Gene> genes) {
				Double x1 = ((DoubleGene) genes.get("x1")).value;
				Double x2 = ((DoubleGene) genes.get("x2")).value;

				return (4 * Math.pow(x1, 2)) + (4 * Math.pow(x2, 2));
			}
		});
		fitnesses.put("f2", new Fitness() {
			public double compute(Map<String, Gene> genes) {
				Double x1 = ((DoubleGene) genes.get("x1")).value;
				Double x2 = ((DoubleGene) genes.get("x2")).value;

				return Math.pow((x1 - 5), 2) + Math.pow((x2 - 5), 2);
			}
		});
		
		/*
		 * Définition des contraintes
		 */
		Map<String, Constraint> constraints = new HashMap<>();

		constraints.put("c1", new Constraint() {
			public boolean isTriggered(Map<String, Gene> genes) {
				Double x1 = ((DoubleGene) genes.get("x1")).value;
				Double x2 = ((DoubleGene) genes.get("x2")).value;
				
				Double value = Math.pow((x1 - 5), 2) + Math.pow(x2, 2);
				
				return ! (value <= 25);
			}
		});
		constraints.put("c2", new Constraint() {
			public boolean isTriggered(Map<String, Gene> genes) {
				Double x1 = ((DoubleGene) genes.get("x1")).value;
				Double x2 = ((DoubleGene) genes.get("x2")).value;
				
				Double value = Math.pow((x1 - 8), 2) + Math.pow((x2 + 3), 2);
				
				return ! (value >= 7.7);
			}
		});
		
		/*
		 * Création du génôme
		 */
		Map<String, Map> genome = new HashMap<>();
		
		genome.put("GENES", genes);
		genome.put("FITNESSES", fitnesses);
		genome.put("CONSTRAINTS", constraints);
		
		return genome;
	}
}
