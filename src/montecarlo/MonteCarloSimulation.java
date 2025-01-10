package montecarlo;

import statistics.StatCollector;

import java.util.Random;

/**
 * This class provides methods for simple Monte Carlo simulations.
 */
public class MonteCarloSimulation {
	/**
	 * Private constructor. Makes it impossible to instantiate.
	 */
	private MonteCarloSimulation() {
	}

	/**
	 * Simulates experiment exp n times, using rnd as a source of pseudo-random numbers and collect
	 * the results in stat.
	 *
	 * @param exp  experiment to be run each time
	 * @param n    number of runs to be performed
	 * @param rnd  random source to be used to simulate the experiment
	 * @param stat collector to be used to collect the results of each experiment
	 */
	public static void simulateNRuns(Experiment exp,
									 long n,
									 Random rnd,
									 StatCollector stat) {
		for (long run = 0; run < n; ++run) {
			stat.add(exp.execute(rnd));
		}
	}

	/**
	 * First simulates experiment exp initialNumberOfRuns times, then estimates the number of runs
	 * needed for a 95% confidence interval half width no more than maxHalfWidth. If final C.I. is
	 * too wide, simulates additionalNumberOfRuns before recalculating the C.I. and repeats the process
	 * as many times as needed.
	 * <p>
	 * Uses rnd as a source of pseudo-random numbers and collects the results in stat.
	 *
	 * @param exp                    experiment to be run each time
	 * @param level                  confidence level of the confidence interval
	 * @param maxHalfWidth           maximal half width of the confidence interval
	 * @param initialNumberOfRuns    initial number of runs to be performed
	 * @param additionalNumberOfRuns additional number of runs to be performed if C.I. is too wide
	 * @param rnd                    random source to be used to simulate the experiment
	 * @param stat                   collector to be used to collect the results of each experiment
	 */
	public static void simulateTillGivenCIHalfWidth(Experiment exp,
													double level,
													double maxHalfWidth,
													long initialNumberOfRuns,
													long additionalNumberOfRuns,
													Random rnd,
													StatCollector stat) {
		//Write your code here
		//On cherche N tel que Z_(1-alpha/2) * S/(N)^1/2 <= DeltaMax

		//Runs initiaux :
		simulateNRuns(exp, initialNumberOfRuns, rnd, stat);

		//Estimer le nombre de réalisations supplémentaire :

		//Calculer la demi-largeur actuelle
		double demiLargeur = stat.getConfidenceIntervalHalfWidth(level);

		if(demiLargeur <= maxHalfWidth){ //C'est ok on se barre
			return;
		}

		System.out.println(level);

		//Estimation du nombre de réalisations
		double nbRealNecessaire = Math.pow(stat.getStandardDeviation() * (level + (1-level)/2) / maxHalfWidth, 2);



	}

}
