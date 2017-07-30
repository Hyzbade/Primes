package algorithms.nsga2.genes;

import java.util.Random;

import algorithms.nsga2.NSGA2;

public class DoubleGene implements Gene {

	public Double value;
	public Double upperBound;
	public Double lowerBound;
	
	public DoubleGene(Double lowerBound, Double upperBound) {

		this.value = 0d;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}
	
	public DoubleGene(Double value, Double lowerBound, Double upperBound) {

		this.value = value;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}
	
	@Override
	public void randomize() {
		Double range = upperBound - lowerBound;
		Random rand = new Random();
		
		this.value = (rand.nextDouble() * range) - lowerBound;
	}
	
	@Override
	public Double getDistanceFrom(Gene gene) {
		
		return this.value - ((DoubleGene) gene).value;
	}
	
	@Override
	public Gene[] crossover(Gene A, Gene B) {
		Gene C = new DoubleGene(lowerBound, upperBound);
		Gene D = new DoubleGene(lowerBound, upperBound);
		Random R = new Random();
		Double gA = ((DoubleGene) A).value;
		Double gB = ((DoubleGene) B).value;
		
		if (R.nextDouble() < 0.5) {
			double rand;
            double y1, y2, yl, yu;
            double c1, c2;
            double alpha, beta, betaq;

            if (Math.abs(gA - gB) > Double.MIN_NORMAL) {
                if (gA < gB) {
                    y1 = gA;
                    y2 = gB;
                }
                else {
                    y1 = gB;
                    y2 = gA;
                }
                
                yl = lowerBound;
                yu = upperBound;
                rand = R.nextDouble();
                beta = 1.0 + (2.0 * (y1 - yl) / (y2 - y1));
                alpha = 2.0 - Math.pow(beta, - (NSGA2.etaX + 1.0));
                
                if (rand <= (1.0 / alpha)) {
                    betaq = Math.pow((rand * alpha), (1.0 / (NSGA2.etaX + 1.0)));
                }
                else {
                    betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (NSGA2.etaX + 1.0)));
                }
                
                c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
                beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
                alpha = 2.0 - Math.pow(beta, - (NSGA2.etaX + 1.0));
                
                if (rand <= (1.0 / alpha)) {
                    betaq = Math.pow((rand * alpha), (1.0 / (NSGA2.etaX + 1.0)));
                }
                else {
                    betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (NSGA2.etaX + 1.0)));
                }
                
                c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));
                
                if(c1 < yl) {
                	c1 = yl;
                }
                if(c2 < yl) {
                	c2 = yl;
                }
                if(c1 > yu) {
                	c1 = yu;
                }
                if(c2 > yu) {
                	c2 = yu;
                }
                
                if (R.nextDouble() <= 0.5) {
                	((DoubleGene) C).value = c2;
                	((DoubleGene) D).value = c1;
                	return new Gene[]{C, D};
                }
                else {
                	((DoubleGene) C).value = c1;
                	((DoubleGene) D).value = c2;
                	return new Gene[]{C, D};
                }
            }
            else {
                if (R.nextDouble() <= 0.5) {
                	((DoubleGene) C).value = ((DoubleGene) A).value;
                	((DoubleGene) D).value = ((DoubleGene) B).value;
                	return new Gene[]{C, D};
                }
                else {
                	((DoubleGene) C).value = ((DoubleGene) B).value;
                	((DoubleGene) D).value = ((DoubleGene) A).value;
                	return new Gene[]{C, D};
                }
            }
		}
		else {
        	((DoubleGene) C).value = ((DoubleGene) A).value;
        	((DoubleGene) D).value = ((DoubleGene) B).value;
        	return new Gene[]{C, D};
		}
	}
	
	@Override
	public void mutate() {
		Random R = new Random();
		double y = value, yl = lowerBound, yu = upperBound;
		double delta1 = (y - yl) / (yu - yl), delta2 = (yu - y) / (yu - yl), deltaq;
		double mut_pow = 1 / (NSGA2.etaM + 1);
		double rand = R.nextDouble();
		
        if (rand <= 0.5) {
            double xy = 1 - delta1;
            double val = 2*rand + (1 - 2*rand)*(Math.pow(xy, (NSGA2.etaM + 1)));
            deltaq =  Math.pow(val, mut_pow) - 1;
        }
        else {
            double xy = 1 - delta2;
            double val = 2 * (1 - rand) + 2 * (rand - 0.5) * (Math.pow(xy, (NSGA2.etaM + 1)));
            deltaq = 1 - (Math.pow(val, mut_pow));
        }
        
        y = y + deltaq * (yu - yl);
        
        if( y < yl ) {
            y = yl;
        }
        if( y > yu ) {
            y = yu;
        }

		value = y;
	}
	
	@Override
	public boolean isEqualTo(Gene A) {

		if (A.getClass()==this.getClass()
			&& ((DoubleGene) A).value==this.value
			&& ((DoubleGene) A).lowerBound==this.lowerBound
			&& ((DoubleGene) A).upperBound==this.upperBound) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public Gene copy() {

		return new DoubleGene(value, lowerBound, upperBound); 
	}
	
	@Override
	public String toString() {
		
		return value.toString();
	}
}
