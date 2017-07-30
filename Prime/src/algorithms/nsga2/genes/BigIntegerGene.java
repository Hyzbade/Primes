package algorithms.nsga2.genes;

import java.math.BigInteger;
import java.util.Random;

import algorithms.nsga2.NSGA2;

public class BigIntegerGene implements Gene {

	public BigInteger value;
	public BigInteger upperBound;
	public BigInteger lowerBound;
	
	public BigIntegerGene(BigInteger lowerBound, BigInteger upperBound) {

		this.value = BigInteger.ZERO;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}
	
	public BigIntegerGene(BigInteger value, BigInteger lowerBound, BigInteger upperBound) {

		this.value = value;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}
	
	@Override
	public void randomize() {
		
		BigInteger range = upperBound.subtract(lowerBound);
		value = new BigInteger(range.bitLength(), new Random());
	}
	
	@Override
	public Double getDistanceFrom(Gene gene) {

		return 0d;
	}
	
	@Override
	public Gene[] crossover(Gene A, Gene B) {
		
       	return new Gene[]{A, B};
	}
	
	@Override
	public void mutate() {
		
	}
	
	@Override
	public boolean isEqualTo(Gene A) {

		if (A.getClass()==this.getClass()
			&& ((BigIntegerGene) A).value==this.value
			&& ((BigIntegerGene) A).lowerBound==this.lowerBound
			&& ((BigIntegerGene) A).upperBound==this.upperBound) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public Gene copy() {

		return new BigIntegerGene(value, lowerBound, upperBound); 
	}
	
	@Override
	public String toString() {
		
		return value.toString();
	}
}
