package algorithms;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Cet algorithme permet de récupérer tous les nombres premiers dans un ensemble donné.
 * Son fonctionnement originel en quelques étapes :
 * 		1/ initialisation des variables
 * 			a/ soit la liste L constituée des entiers allant de 2 à N par ordre croissant
 * 			b/ soit la liste P, vide, qui sera constituée d'entiers (les nombres premiers)
 * 		2/ jusqu'à ce que la liste soit vide, faire :
 * 			a/ soit I, le premier élément de L (donc 2 à la première itération par exemple)
 * 			b/ ajout de I dans P
 * 			c/ retrait, de la liste L, des éléments multiples de I
 * 		3/ retourner P
 * 
 * @author Alan
 *
 */
public class SetShrinkingAlgorithm {
	
	private final int N;
	/** l'ensemble contenant les nombres */
	private List<Integer> set = new ArrayList<>();
	/** la liste des nombres premiers */
	private List<Integer> primes = new ArrayList<>();
	/** l'évolution de la taille de SET au fil des itérations */
	private List<Integer> sizes = new ArrayList<>();

	public SetShrinkingAlgorithm(int N) {
		
		this.N = N;
		
		// inutile d'insérer les modulos 2 et 3, gagnons du temps
		for (int i=5 ; i<=N ; i+=2) {
			if (i%3>0) set.add(i);
		}

		// initialisation
		primes.add(1);
		sizes.add(N);

		// première itération
		primes.add(2);
		sizes.add((int)Math.ceil(N/2));
		
		// seconde itération
		primes.add(3);
		sizes.add(set.size()+1);
	}
	
	public void run() {
		Instant start = Instant.now();
		
		this.core();
		
		Instant end = Instant.now();

		System.out.println("Temps: " + Duration.between(start, end).toMillis() + " ms");
		System.out.println("Ensemble = [1, " + N + "]");
		System.out.println("Nombres premiers (" + primes.size() + ") : " + primes.toString());
		System.out.println("Evolution de la taille (" + sizes.size() + ") : " + sizes.toString());
	}
	
	/**
	 * L'algorithme : retire itérativement tous les multiples du premier de la liste SET
	 */
	protected void core() {
		while (set.size()>0) {
			int modulo = set.get(0);
			int oldSize = set.size()+1;
			
			set = this.filter(set, modulo);
			
			int newSize = set.size()+1;
			
			if (oldSize-newSize==1) {
				break;
			}
			else {
				primes.add(modulo);
				sizes.add(newSize);
			}
		}
		
		primes.addAll(set);
	}
	
	/**
	 * @return l'ensemble NUMBERS sans les multiples de MODULO
	 */
	protected ArrayList<Integer> filter(List<Integer> numbers, int modulo) {
		
		ArrayList<Integer> filteredNumbers = new ArrayList<>();
		for (int i=0 ; i<numbers.size() ; i++) {
			int number = numbers.get(i);
			
			if (numbers.get(i)%modulo>0) {
				filteredNumbers.add(number);
			}
		}
		
		return filteredNumbers;
	}
}
