import algorithms.nsga2.Genome;
import algorithms.nsga2.NSGA2;

public class Engine {

	/**
	 * Liens utiles :
	 * - https://en.wikipedia.org/wiki/Prime-counting_function#Table_of_.CF.80.28x.29.2C_x_.2F_ln_x.2C_and_li.28x.29
	 * - https://plot.ly/create/
	 */
	public static void main(String[] args) {
		NSGA2 nsga2 = new NSGA2(Genome.BNH());
		
		nsga2.run();
		
		System.out.println("\n\n\n ==========");
		System.out.println(nsga2.P.toString());
	}
}
