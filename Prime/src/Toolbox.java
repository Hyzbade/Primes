import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Toolbox {

	public Toolbox() {
		
	}
	
	/**
	 * @return la décomposition de N en une multiplication de nombres premiers (exemple : 12 => [2, 2, 3])
	 */
	public static List<Integer> getPrimeFactorization(int n) {
		List<Integer> factors = new ArrayList<>();
		int f = 2;
		
		while (n>1) {
			// tant que F est un facteur de N
			while (n%f==0) {
				n = n/f;
				factors.add(f);
			}

			f = getNextPrime(f);
		}
		
		return factors;
	}
	
	/**
	 * @return le nombre premier juste après le nombre N
	 */
	public static int getNextPrime(int n) {
		int i = (n%2==0) ? (n+1) : (n+2);
		
		while ( ! isPrime(i)) {
			i += 2;
		}
		
		return i;
	}
	
	/**
	 * @return TRUE si le nombre N est premier
	 */
	public static boolean isPrime(int n) {
		
		if (n%2==0) {
			return false;
		}
		
		for (int i=3 ; i<=Math.sqrt(n)+1 ; i+=2) {
			if (n%i==0) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @return pour les nombres de 1 à N, récupère le plus petits facteurs premiers
	 */
	public static List<Integer> getTheLowestFactors(int n) {
		List<Integer> lowestFactor = new ArrayList<>();
		
		for (int i=2 ; i<=n ; i++) {
			lowestFactor.add(getPrimeFactorization(i).get(0));
		}
		
		return lowestFactor;
	}
	
	public static List<Integer> translateTheLowestFactors(List<Integer> unnamed, int number) {
		List<Integer> translation = new ArrayList<>();
		int counter = 0;
		boolean isZero = (number==2) ? false : true;
		
		for (int i=0 ; i<unnamed.size() ; i++) {
			// ça compte 1
			if (unnamed.get(i)==number) {
				// si on était sur une suite de 0
				if (isZero) {
					translation.add(counter);
					isZero = false;
					counter = 0;
				}
//				translation.add(1);
			}
			// ça compte 0
			else {
				// si on était sur une suite de 1
				if ( ! isZero) {
					translation.add(counter);
					isZero = true;
					counter = 0;
				}
//				translation.add(0);
			}

			counter++;
		}

		translation.add(counter);
		return translation;
	}
	
	/**
	 * @return la liste des nombres de 1 à N trié par rang
	 */
	public static void getRankedList(int n) {
		List<List<String>> rankedList = new ArrayList<>();
		int p = 0;
		
		while (Math.pow(2, p)<n) {
			rankedList.add(new ArrayList<String>());
			p++;
		}

		for (int i=1 ; i<=n ; i++) {
			List<Integer> f = getPrimeFactorization(i);
			String factorization = f.toString();
			rankedList.get(f.size()).add(factorization);
		}
		
		System.out.println(rankedList);
	}
}
