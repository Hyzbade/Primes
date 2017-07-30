package algorithms.nsga2.genes;

public interface Gene {
	
	public void randomize();
	
	public Double getDistanceFrom(Gene gene);
	
	public Gene[] crossover(Gene A, Gene B);
	
	public void mutate();
	
	public Gene copy();
	
	public boolean isEqualTo(Gene A);
}
