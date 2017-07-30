package algorithms.nsga2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithms.nsga2.genes.Gene;

public class Individual implements Comparable<Individual> {
	
	public int birthday;
	public double crowdingDistance;
	protected Map<String, Map> genome;
	protected Map<String, Gene> genes;
	protected Map<String, Double> fitnesses;
	protected Map<String, Boolean> constraints;
	
	public Individual(Map<String, Map> genome) {
		this.genome = new HashMap<>(genome);
		this.birthday = 0;
		this.crowdingDistance = 0;
		this.genes = new HashMap<>();
		this.fitnesses = new HashMap<>();
		this.constraints = new HashMap<>();
		
		Map<String, Gene> genes = genome.get("GENES");
		for (Map.Entry<String, Gene> gene : genes.entrySet()) {
			this.genes.put(gene.getKey(), gene.getValue().copy());
		}
		
		Map<String, Fitness> fitnesses = genome.get("FITNESSES");
		for (Map.Entry<String, Fitness> fitness : fitnesses.entrySet()) {
			this.fitnesses.put(fitness.getKey(), -1d);
		}
		
		Map<String, Constraint> constraints = genome.get("CONSTRAINTS");
		for (Map.Entry<String, Constraint> constraint : constraints.entrySet()) {
			this.constraints.put(constraint.getKey(), new Boolean(false));
		}
	}
	
	/**
	 * Calcule les fitnesses et évalue les contraintes
	 */
	protected void evaluate() {
		Map<String, Fitness> allFitnesses = genome.get("FITNESSES");
		for (Map.Entry<String, Double> fitness : fitnesses.entrySet()) {
			fitness.setValue(allFitnesses.get(fitness.getKey()).compute(genes));
		}

		Map<String, Constraint> allConstraints = genome.get("CONSTRAINTS");
		for (Map.Entry<String, Boolean> constraint : constraints.entrySet()) {
			constraint.setValue(allConstraints.get(constraint.getKey()).isTriggered(genes));
		}
	}
	
	/**
	 * Calcule sa distance d'isolement
	 */
	protected void computeCrowdingDistance(List<Individual> L) {
		// pour chaque gene du genome
		for (Map.Entry<String, Gene> gene : genes.entrySet()) {
			List<Gene> pool = new ArrayList<>();
			// récupération de tous les genes correspondant des autres gus
			for (Individual individual : L) {
				// seulement des autres individus
				if ( ! individual.equals(this)) {
					pool.add(individual.genes.get(gene.getKey()));	
				}
			}
			// puis pour chaque gene, on calcule la crowdingDistance
			Double ahead = 0d;
			Double behind = 0d;
			for (Gene oGene : pool) {
				Double distance = gene.getValue().getDistanceFrom(oGene);
				// GENE > oGENE
				if (distance > 0) {
					if (distance < ahead) {
						ahead = distance;
					}
				}
				// GENE <= oGENE
				else if (distance <= 0) {
					if (distance < behind) {
						behind = distance;
					}
				}
			}
			
			crowdingDistance += ahead - behind;
		}
	}
	
	/**
	 * création des genes aléatoirement et calcule des fitnesses
	 */
	protected void generateRandomGenes() {
		
		for (Map.Entry<String, Gene> gene : genes.entrySet()) {
			gene.getValue().randomize();
		}
		
		evaluate();
	}
	
	/**
	 * création des genes à partir de ceux de deux individus et calcule des fitnesses
	 */
	protected void generateGenesFromParents(Individual A, Individual B) {
		boolean chooseA = true;
		
		for (Map.Entry<String, Gene> gene : genes.entrySet()) {
			Gene gA = A.genes.get(gene.getKey());
			Gene gB = B.genes.get(gene.getKey());
			
			if (chooseA) {
				genes.put(gene.getKey(), gA);
			}
			else {
				genes.put(gene.getKey(), gB);
			}
			
			chooseA = !chooseA;
		}
		
		evaluate();
	}
	
	/**
	 * @return le nombre de contraintes violées
	 */
	protected int countTrigeringConstraints() {
		int counter = 0;
		
		for (Map.Entry<String, Boolean> contraint: constraints.entrySet()) {
			if (contraint.getValue()) {
				counter++;
			}
		}
		
		return counter;
	}
	
	/**
	 * @return un vecteur contenant -1, 0 ou 1 suivant si THIS est moins, égal ou meilleur sur chaque fitness
	 */
	protected Map<String, Integer> getDominanceVector(Individual I) {
		Map<String, Integer> V = new HashMap<>();
		
		for (Map.Entry<String, Double> fitness : fitnesses.entrySet()) {
			String key = fitness.getKey();
			Double myFitness = fitness.getValue();
			Double itsFitness = I.fitnesses.get(key);
			if (itsFitness != null) {
				// si "ma" fitness est contrainte
				if (myFitness < 0) {
					// si la "sienne" l'est aussi
					if (itsFitness < 0) {
						V.put(key, 0);
					}
					else {
						V.put(key, -1);
					}
				}
				// si "j'ai" une fitness non contrainte
				else {
					// si la "sienne" est contrainte
					if (itsFitness < 0d) {
						V.put(key, 1);
					}
					// si la "sienne" est meilleure
					else if (itsFitness < myFitness) {
						V.put(key, -1);
					}
					// si la "sienne" est égale à la "mienne"
					else if (Math.abs(myFitness-itsFitness) <= Double.MIN_VALUE) {
						V.put(key, 0);
					}
					// sinon la "mienne" est meilleure
					else {
						V.put(key, 1);
					}
				}
			}
		}
		
		return V;
	}
	
	/**
	 * @return directement liée à getDominanceVector, cette méthode renvoie le trio [better, equal, worst]
	 */
	protected Map<String, Boolean> getDominance(Map<String, Integer> V) {
		boolean better = false;
		boolean equal = false;
		boolean worst = false;
		
		for (Map.Entry<String, Integer> v: V.entrySet()) {
			switch (v.getValue()) {
			case 1:
				better = true;
				break;

			case -1:
				worst = true;
				break;

			default:
				equal = true;
				break;
			}
		}
		
		Map<String, Boolean> tri = new HashMap<>();
		tri.put("BETTER", better);
		tri.put("EQUAL", equal);
		tri.put("WORST", worst);
		
		return tri;
	}
	
	@Override
	public int compareTo(Individual I) {
		Map<String, Integer> V = getDominanceVector(I);
		Map<String, Boolean> T = getDominance(V);
		
		/*	B	E	W	dominance
		 * 	0	0	1 	=> -2
		 * 	0	1	1 	=> -1
		 * 	1	0	1 	=>  0
		 * 	0	1	0 	=>  0
		 * 	1	1	0 	=>  1
		 * 	1	0	0 	=>  2
		 */
		int C = 0;
		// I domine strictement THIS
		if ( ! T.get("BETTER") && ! T.get("EQUAL") && T.get("WORST")) {
			C = -2;
		}
		// I domine THIS
		else if ( ! T.get("BETTER") && T.get("EQUAL") && T.get("WORST")) {
			C = -1;
		}
		// THIS ne domine pas I, I ne domine pas THIS
		else if ( T.get("BETTER") && ! T.get("EQUAL") && T.get("WORST")) {
			C = 0;
		}
		// THIS ne domine pas I, I ne domine pas THIS
		else if ( ! T.get("BETTER") && T.get("EQUAL") && ! T.get("WORST")) {
			C = 0;
		}
		// THIS domine I
		else if ( T.get("BETTER") && T.get("EQUAL") && ! T.get("WORST")) {
			C = 1;
		}
		// THIS domine strictement I
		else if ( T.get("BETTER") && ! T.get("EQUAL") && ! T.get("WORST")) {
			C = 2;
		}
		
		// I est contraint
		if (I.countTrigeringConstraints() > 0) {
			// THIS aussi
			if (this.countTrigeringConstraints() > 0) {
				C = 0;
			}
			// mais pas THIS
			else {
				C = 2;
			}
		}
		// THIS est contraint
		else if (this.countTrigeringConstraints() > 0) {
			// I aussi
			if (I.countTrigeringConstraints() > 0) {
				C = 0;
			}
			// mais pas I
			else {
				C = -2;
			}
		}
		
		return C;
	}
	
	@Override
	public String toString() {
		ArrayList<String> G = new ArrayList<>();
		ArrayList<String> F = new ArrayList<>();
		
		for (Map.Entry<String, Gene> gene : genes.entrySet()) {
//			G.add(gene.getKey() + ": " + gene.getValue().toString());
			G.add("\t" + gene.getValue().toString());
		}
		
		for (Map.Entry<String, Double> fitness : fitnesses.entrySet()) {
//			F.add(fitness.getKey() + ": " + fitness.getValue());
			F.add("\t" + fitness.getValue());
		}

//		return "Birthday: " + birthday + ", GENES: " + G.toString() + ", FITNESSES: " + F.toString();
//		return "\nG: " + G.toString() + ", F: " + F.toString();
		return "\n" + G.toString() + "\t" + F.toString();
	}
}
