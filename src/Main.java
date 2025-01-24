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
		//System.out.println("======= DEBUT DE L'EXPERIENCE 3 ==========");
		//experience3();
	}

	private static void experience1(){
		StatCollector stat = new StatCollector();

		Random rnd = new Random(SEED);

		Experiment birthdayExp = new BirthdayParadoxGenericExperiment(23, 365, 2);


		long tempsDeDepart = System.nanoTime();
		MonteCarloSimulation.simulateTillGivenCIHalfWidth(birthdayExp, 0.95, 0.0001, 1000000, 100000, rnd, stat);
		double tempsDExecution = (System.nanoTime() - tempsDeDepart) / 1000000000.;

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

		// TODO: A regarder on a 100,000 réalisations de moins qu'Eva et Rachel, bizarre...
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
		double nombreSimulation = 1000.;

		StatCollector stat = new StatCollector();
		// Le stat collector pour les résultat des intervalles m'a été conseillé par le Big boss
		StatCollector statFin = new StatCollector();
		int nombreDeSimulationNonSatisfaisante = 0;

		double moyenne = 0.0;
		double moitieIntervalleDeConfiance = 0.0;

		Experiment birthdayExp = new BirthdayParadoxGenericExperiment(23, 365, 2);
		Random rnd = new Random(SEED);

		for(int i = 0; i < nombreSimulation; ++i){
			stat.init();
			MonteCarloSimulation.simulateNRuns(birthdayExp, 1000000, rnd, stat);

			moitieIntervalleDeConfiance = stat.getConfidenceIntervalHalfWidth(0.95);
			moyenne = stat.getAverage();
			// On fait une nouvelle expérience avec les 1 et 0 donc on peut utiliser le statCollector, indiqué par le Big boss
			if(vraiValeur >= moyenne - moitieIntervalleDeConfiance && vraiValeur <= moyenne + moitieIntervalleDeConfiance){
				statFin.add(1);
			}else{
				statFin.add(0);
			}
		}

		//System.out.printf("Pourcentage de valeur ok : %.4f\n", (nombreSimulation - nombreDeSimulationNonSatisfaisante)/ nombreSimulation);
		// Ca donnait 0.9540
		System.out.printf("Pourcentage de valeur ok : %.4f\n", statFin .getAverage());
		// Peut être à demander à eva et rachel pas sur de l'intervalle
		System.out.printf("Intervalle de confiance : [%.4f, %.4f]\n", statFin.getAverage() - statFin.getConfidenceIntervalHalfWidth(0.95), statFin.getAverage() + statFin.getConfidenceIntervalHalfWidth(0.95));
	}

	private static void experience3(){

		// Grand N donné par JFH le GOAT aka le "Big boss" de son dire
		int nombreSimulation = 1000000;

		StatCollector stat = new StatCollector();
		int tailleGroupetrouvee = 0;

		for(int i = 80; i <= 100; i++){
			Random rnd = new Random(SEED);
			stat.init();
			Experiment birthdayExp = new BirthdayParadoxGenericExperiment(i, 365, 3);
			MonteCarloSimulation.simulateNRuns(birthdayExp, nombreSimulation, rnd, stat);
			if (stat.getAverage() >= 0.5) {
				tailleGroupetrouvee = i;
				break;
			}
		}

		System.out.printf("Taille de groupe trouvée : %d\n", tailleGroupetrouvee);
		// Si 88 c'est le même résultat que Rachel et Eva
	}

	private static void displayStat(StatCollector stat, double tempsDExecution){
		System.out.printf("Nombre d'observations : %,d\n", stat.getNumberOfObs());
		System.out.printf("Variance : %.6f\n", stat.getVariance());
		System.out.printf("Moyenne : %.6f\n", stat.getAverage());
		System.out.printf("Déviation standard : %.6f\n", stat.getStandardDeviation());
		System.out.printf("Durée de calcul : %,.6f secondes\n", tempsDExecution);
		System.out.printf("Intervalle de confiance : [%.4f, %.4f]\n",
				stat.getAverage() - stat.getConfidenceIntervalHalfWidth(0.4),
				stat.getAverage() + stat.getConfidenceIntervalHalfWidth(0.4));
	}
}
