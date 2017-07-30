package algorithms.nsga2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleToLongFunction;

import algorithms.nsga2.genes.BigIntegerGene;
import algorithms.nsga2.genes.DoubleGene;
import algorithms.nsga2.genes.Gene;
import algorithms.nsga2.genes.IntegerGene;

public class NSGA2 {

	/**
	 * Taille de la population (défaut: 100)
	 */
	public final static int N = 100;
	/**
	 * Nombre de générations (défaut: 10)
	 */
	public final static int G = 250;
	/**
	 * Taille du tournoi pour la sélection des deux parents
	 * lors de la génération des enfants (doit être une puissance de 2)
	 */
	public final static int TAU = (int) Math.pow(2, 2);
	/**
	 * Probabilité pour le croisement
	 * et indice de distribution pour le SBX
	 */
	public final static double pX = 0.9;
	public final static double etaX = 20;
	/**
	 * Probabilité pour la mutation
	 * et indice de distribution
	 */
	public final static double pM = 0.5;
	public final static double etaM = 20;
	/**
	 * Population parente
	 */
	public List<Individual> P;
	/**
	 * Descendance
	 */
	public List<Individual> Q;
	/**
	 * Le génôme, composé des types de gènes et des fitnesses
	 */
	public Map<String, Map> genome;
	
	public NSGA2(Map<String, Map> genome) {

		this.P = new ArrayList<>();
		this.Q = new ArrayList<>();
		this.genome = new HashMap<>(genome);
	}
	
	/**
	 * Construction de la population P initiale en créant N individus aléatoirement
	 */
	protected void genesis() {
		
		for (int i=0 ; i<N ; i++) {
			Individual individual = new Individual(genome);
			individual.generateRandomGenes();
			
			P.add(individual);
		}
	}
	
	public void run() {
		
		this.genesis();
		
		for (int i=0 ; i<G ; i++) {
			System.out.println("ITERATION N°"+i);
			System.out.println("P");
			System.out.println(P.toString());
			System.out.println("Q");
			System.out.println(Q.toString());
			System.out.println();
			
			// création de R = P U Q
			List<Individual> R = new ArrayList<>();
			R.addAll(P);
			R.addAll(Q);
			
			// suppression des doublons dans R tant que R.size >= N
			Map<String, Gene> genes = genome.get("GENES");
			for (int a=0 ; a<R.size() && R.size()>N ; a++) {
				Individual A = R.get(a);
				
				for (int b=a+1 ; b<R.size() && R.size()>N ; b++) {
					Individual B = R.get(b);
					
					if ( ! A.equals(B)) {
						boolean isClone = true;
						for (Map.Entry<String, Gene> gene : genes.entrySet()) {
							String K = gene.getKey();
							Gene gA = A.genes.get(K);
							Gene gB = B.genes.get(K);
							
							if ( ! gA.isEqualTo(gB)) {
								isClone = false;
								break;
							}
						}
						
						if (isClone) {
							R.remove(b);
							b--;
						}
					}
				}
			}
			
			// clear des deux populations
			P.clear();
			Q.clear();
			
			// création des fronts de Pareto
			Map<Integer, List> F = getFronts(R);
			
			// ajout d'autant de front entier que possible
			int f = 0;
			while (P.size()+F.get(f).size() < N) {
				P.addAll(F.get(f));
				f++;
			}
			
			// si un front doit être divisé
			if (P.size() < N) {
				P.addAll(crowdingDistanceSelection(F.get(f), (N-P.size())));
			}
			
			// création de la descendance
			Q.addAll(getOffspring(P));
			
			// calcul de leurs fitnesses
			for (Individual q : Q) {
				q.evaluate();
			}
		}
	}
	
	/**
	 * à partir d'une population R, range les individus des meilleurs au moins performants (selon leur fitness)
	 */
	protected Map<Integer, List> getFronts(List<Individual> R) {
		Map<Integer, List> F = new HashMap<>();
		int i = 0;

		// jusqu'à ce que R soit vide
		while ( ! R.isEmpty()) {
			List<Individual> L = new ArrayList<>();
			
			// comparaison de chaque individu entre eux
			for (int a=0 ; a<R.size() ; a++) {
				Individual A = R.get(a);
				
				// ajout de A dans la liste L
				L.add(A);
				for (int b=0 ; b<R.size() ; b++) {
					Individual B = R.get(b);
					if ( ! A.equals(B)) {						
						// si B est meilleur ou égal à A, on retire A de la liste L
						if (B.compareTo(A) > 0) {
							L.remove(A);
						}
					}
				}
			}
			
			if (L.isEmpty()) {
				L.addAll(R);
			}
			
			// on retire les individus de L de la liste R et on ajoute L à uin nouveau front
			R.removeAll(L);
			F.put(i, L);
			i++;
		}
		
		return F;
	}
	
	/**
	 * @return la liste des individus de L les plus isolés
	 */
	protected List<Individual> crowdingDistanceSelection(List<Individual> L, int n) {
		// calcule du coefficient de surpeuplement
		for (Individual individual : L) {
			individual.computeCrowdingDistance(L);
		}
		
		Collections.sort(L, new Comparator<Individual>() {
			@Override
			public int compare(Individual bob, Individual roger) {
				// Bob et Roger sont aussi isolés
				if (bob.crowdingDistance - roger.crowdingDistance <= Double.MIN_NORMAL) {
					return 0;
				}
				// Bob est le plus isolé
				else if (bob.crowdingDistance - roger.crowdingDistance > Double.MIN_NORMAL) {
					return 1;
				}
				// sinon c'est Roger le plus isolé
				else {
					return -1;
				}
			}
		});
		
		// puis on renvoie les premiers
		 return L.subList(0, n);
	}
	
	protected List<Individual> getOffspring(List<Individual> P) {
		Random R = new Random();
		List<Individual> Q = new ArrayList<>();
		
		while (Q.size() < N) {
			// sélection de 2 parents
			Individual[] parents = getTwoParents(P);

			Individual A = parents[0];
			Individual B = parents[1];
			Individual C = new Individual(genome);
			Individual D = new Individual(genome);

			Map<String, Gene> genes = genome.get("GENES");
			for (Map.Entry<String, Gene> gene : genes.entrySet()) {
				String K = gene.getKey();
				Gene gA = A.genes.get(K);
				Gene gB = B.genes.get(K);
				Gene[] twoGenes = gene.getValue().crossover(gA, gB);

				Gene gC = twoGenes[0];
				if (R.nextDouble() <= pM) {
					gC.mutate();
				}
				Gene gD = twoGenes[1];
				if (R.nextDouble() <= pM) {
					gD.mutate();
				}

				C.genes.put(K, gC);
				D.genes.put(K, gD);
			}
			
			Q.add(C);
			Q.add(D);
		}
		
		return Q;
	}
	
	protected Individual[] getTwoParents(List<Individual> P) {
		List<Individual> L = new ArrayList<>();
		List<Individual> pool = new ArrayList<>();
		Random rand = new Random();
		
		// copie de P
		L.addAll(P);
		
		// première chose, sélection de T individus aléatoirement dans L
		while (pool.size() < TAU) {
			int r = rand.nextInt(L.size());
			pool.add(L.remove(r));
		}
		
		// puis confrontation des individus 2 par 2 jusqu'à ce qu'il n'en reste que 2
		while (pool.size() > 2) {
			for (int a=0, b=1 ; a<pool.size() ; a++, b=a+1) {
				Individual A = pool.get(a);
				Individual B = pool.get(b);
				
				if (A.compareTo(B) > 0) {
					pool.remove(B);
				}
				else if (A.compareTo(B) < 0) {
					pool.remove(A);
				}
				else {
					if (rand.nextBoolean()) {
						pool.remove(A);
					}
					else {
						pool.remove(B);
					}
				}
			}
		}
		
		return pool.toArray(new Individual[0]);
	}
}
