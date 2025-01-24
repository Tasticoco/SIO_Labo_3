package montecarlo;

import statistics.InverseStdNormalCDF;
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

		//Runs initiaux :
		simulateNRuns(exp, initialNumberOfRuns, rnd, stat);

		//Calculer la demi-largeur actuelle
		double halfWidth = stat.getConfidenceIntervalHalfWidth(level);

		// Vérification si la précision souhaitée est déjà atteinte
		if(halfWidth <= maxHalfWidth){
			return;
		}

		// Calcul du nombre de réalisations nécessaires
		// Utilisation de la formule N = (z_(1-α/2) * S / ∆max)²
		double quantile = InverseStdNormalCDF.getQuantile((level + (1-level)/2));
		//Estimation du nombre de réalisations
		long nbRealNecessary = Math.round(Math.pow(stat.getStandardDeviation() * quantile / maxHalfWidth, 2));

		// Calcul du nombre de réalisations supplémentaires arrondi au multiple supérieur tout en enlèvant les
		// réalisations initiales.
		long nbRealRoundUp = Math.ceilDiv(nbRealNecessary,additionalNumberOfRuns) * additionalNumberOfRuns - initialNumberOfRuns;

		// Exécution des réalisations supplémentaires calculées
		simulateNRuns(exp, nbRealRoundUp, rnd, stat);

		// Boucle de vérification et ajouts si nécessaire
		// Continue tant que la précision souhaitée n'est pas atteinte
		while(!(stat.getConfidenceIntervalHalfWidth(level) <= maxHalfWidth)){
			simulateNRuns(exp, additionalNumberOfRuns, rnd, stat);
		}

	}

}
