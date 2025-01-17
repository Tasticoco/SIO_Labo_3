import montecarlo.*;
import statistics.*;

import java.util.Random;

// Juste pour l'exemple
class FairCoinTossExperiment implements Experiment {
	public double execute(Random rnd) {
		return rnd.nextDouble() < 0.5 ? 1.0 : 0.0;
	}
}



public class Main {

	static long SEED = 0x134D6EE;

	public static void main(String[] args) {
		System.out.println("SIO - LABO 3 - MONTE CARLO");
		//System.out.println("======= DEBUT DE L'EXPERIENCE 1 ==========");
		//experience1();
		System.out.println("======= DEBUT DE L'EXPERIENCE 2 ==========");
		experience2();
	}

	private static void experience1(){
		StatCollector stat = new StatCollector();

		Random rnd = new Random(SEED);

		Experiment birthdayExp = new BirthdayParadoxGenericExperiment(23, 365, 2);


		long tempsDeDepart = System.nanoTime();
		MonteCarloSimulation.simulateTillGivenCIHalfWidth(birthdayExp, 0.95, 0.0001, 1000000, 100000, rnd, stat);
		double tempsDExecution = (System.nanoTime() - tempsDeDepart) / 1000000000.;

		System.out.println("=================BIRTHDAY EXP=================");
		System.out.println("Première simulation avec 10^-4 comme valeur d'intervalle de confiance");
		displayStat(stat, tempsDExecution);

		rnd = new Random(SEED);
		stat.init();

		tempsDeDepart = System.nanoTime();
		MonteCarloSimulation.simulateTillGivenCIHalfWidth(birthdayExp,
				0.95,
				0.00005,
				1000000,
				100000,
				rnd,
				stat);
		tempsDExecution = (System.nanoTime() - tempsDeDepart) / 1000000000.;

		System.out.println();
		System.out.println("Seconde simulation avec 5*10^-5 comme valeur d'intervalle de confiance");
		displayStat(stat, tempsDExecution);

		rnd = new Random(SEED);
		stat.init();

		tempsDeDepart = System.nanoTime();
		MonteCarloSimulation.simulateTillGivenCIHalfWidth(birthdayExp,
				0.95,
				0.000025,
				1000000,
				100000,
				rnd,
				stat);
		tempsDExecution = (System.nanoTime() - tempsDeDepart) / 1000000000.;

		System.out.println();
		System.out.println("Seconde simulation avec 2.5*10^-5 comme valeur d'intervalle de confiance");
		displayStat(stat, tempsDExecution);
	}

	private static void experience2(){

		double vraiValeur = 0.5072972343;
		long nombreSimulation = 1000;

		StatCollector stat = new StatCollector();
		int nombreDeSimulationNonSatisfaisante = 0;

		double intervalleDeConfiance = 0.;

		Experiment birthdayExp = new BirthdayParadoxGenericExperiment(23, 365, 2);
		Random rnd = new Random(SEED);

		for(int i = 0; i < nombreSimulation; ++i){
			MonteCarloSimulation.simulateNRuns(birthdayExp, 1000000, rnd, stat);

			intervalleDeConfiance = stat.getConfidenceIntervalHalfWidth(0.95);
			double moyenne = stat.getAverage();
			if(!(vraiValeur >= moyenne - intervalleDeConfiance && vraiValeur <= moyenne + intervalleDeConfiance )){
				++nombreDeSimulationNonSatisfaisante;
			}
			stat.init();
		}


		System.out.printf("Pourcentage de valeur ok : %.4f\n", (nombreSimulation - nombreDeSimulationNonSatisfaisante)/ (double)nombreSimulation);
	}

	private static void displayStat(StatCollector stat, double tempsDExecution){
		System.out.printf("Nombre d'observations : %,d\n", stat.getNumberOfObs());
		System.out.printf("Variance : %.6f\n", stat.getVariance());
		System.out.printf("Moyenne : %.6f\n", stat.getAverage());
		System.out.printf("Déviation standard : %.6f\n", stat.getStandardDeviation());
		System.out.printf("Durée de calcul : %,.6f secondes\n", tempsDExecution);
	}
}
