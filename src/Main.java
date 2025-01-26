import montecarlo.*;
import statistics.*;

import java.util.Random;

// Juste pour l'exemple
class FairCoinTossExperiment implements Experiment {
	public double execute(Random rnd) {
		return rnd.nextDouble() < 0.5 ? 1.0 : 0.0;
	}
}


/**
 * Ce programme réalise trois simulations distinctes :
 * 1. Calcul d'estimations et d'intervalles de confiance pour la probabilité
 *    d'avoir au moins 2 personnes avec le même anniversaire dans un groupe de 23 personnes,
 *    avec trois niveaux de précision.
 * <p>
 * 2. Étude empirique de la couverture des intervalles de confiance à 95%
 *    à travers 1000 simulations de 1 million de réalisations chacune.
 * <p>
 * 3. Recherche du nombre minimal de personnes nécessaires pour avoir une probabilité
 *    supérieure à 50% que 3 personnes au moins aient leur anniversaire le même jour.
 * <p>
 * @author : Junod Arthur
 * @author : Häffner Edwin
 */
public class Main {

	private static final class Constants {
		static final long 		SEED 					= 0x134D6EE;
		static final double 	CONFIDENCE_INTERVAL 	= 0.95;
		static final double 	MAX_HALF_WIDTH_EXP1 	= 1E-4;
		static final double 	HALF_WIDTH_EXP1_SECOND 	= 5E-5;
		static final double 	HALF_WIDTH_EXP1_THIRD 	= 2.5E-5;
		static final int 		MAX_RUNS 				= 1_000_000;
		static final int 		BATCH_SIZE 				= 100_000;
		static final double 	TRUE_VALUE_EXP2 		= 0.5072972343;
		static final int 		SIMULATIONS_EXP2 		= 1_000;
		static final int 		GROUP_SIZE_MIN 			= 80;
		static final int 		GROUP_SIZE_MAX 			= 100;
		static final int 		NB_DAYS_IN_A_YEAR 		= 365;
		static final int 		BASE_GROUP_SIZE 		= 23;
		static final int 		BASE_THRESHOLD 			= 2;
		static final int 		THRESHOLD_EXP3 			= 3;
		static final double 	TARGET_CHANCE 			= 0.5;

	}

	public static void main(String[] args) {
		System.out.println("SIO - LABO 3 - MONTE CARLO");
		//simulation1();
		//simulation2();
		simulation3();
	}

	/**
	 * Exécute trois simulations successives du paradoxe des anniversaires avec des intervalles
	 * de confiance de précision croissante.
	 * <p>
	 * Cette méthode effectue trois simulations distinctes pour estimer la probabilité d'avoir
	 * au moins deux personnes partageant la même date d'anniversaire dans un groupe de 23 personnes.
	 * Chaque simulation utilise une demi-largeur d'intervalle de confiance différente :
	 * - Première simulation : demi-largeur de 10^-4
	 * - Deuxième simulation : demi-largeur de 5*10^-5
	 * - Troisième simulation : demi-largeur de 2.5*10^-5
	 * <p>
	 * Les paramètres constants utilisés sont :
	 * - Niveau de confiance : 95%
	 * - Taille du groupe : 23 personnes
	 * - Nombre de jours dans l'année : 365
	 * - Seuil minimum de personnes : 2
	 * - Graine aléatoire : 0x134D6EE
	 * <p>
	 * Pour chaque simulation, la méthode affiche :
	 * - L'estimation de la probabilité.
	 * - Le nombre total de réalisations générées.
	 * - La demi-largeur de l'intervalle de confiance.
	 * - L'intervalle de confiance complet.
	 */
	private static void simulation1(){
		System.out.println("======= DEBUT DE LA SIMULATION 1 ==========");
		Experiment birthdayExp = new BirthdayParadoxGenericExperiment(Constants.BASE_GROUP_SIZE, Constants.NB_DAYS_IN_A_YEAR, Constants.BASE_THRESHOLD);

		runSimulation("Première intervalle (10^-4)"		, birthdayExp, Constants.MAX_HALF_WIDTH_EXP1);
		runSimulation("Seconde intervalle (5*10^-5)"		, birthdayExp, Constants.HALF_WIDTH_EXP1_SECOND);
		runSimulation("Troisième intervalle (2.5*10^-5)"	, birthdayExp, Constants.HALF_WIDTH_EXP1_THIRD);
	}

	/**
	 * Permet de run les simulations pour la simulation numéro 1.
	 * @param description 	Le texte de sortie de la simulation.
	 * @param experiment 	L'experience (Ici, on utilise tout le temps celle du paradox de l'anniversaire).
	 * @param halfWidth 	La demi-largeur voulue.
	 */
	private static void runSimulation(String description, Experiment experiment, double halfWidth) {
		//Initialisation du random et du StatCollector
		StatCollector stat = new StatCollector();
		Random rnd = new Random(Constants.SEED);

		long startTime = System.nanoTime();

		//Simulation
		MonteCarloSimulation.simulateTillGivenCIHalfWidth(
				experiment,
				Constants.CONFIDENCE_INTERVAL,
				halfWidth,
				Constants.MAX_RUNS,
				Constants.BATCH_SIZE,
				rnd,
				stat
		);
		double executionTime = (System.nanoTime() - startTime) / 1e9;

		System.out.println("\n" + description);
		displayStats(stat, executionTime, Constants.CONFIDENCE_INTERVAL);
	}

	/**
	 * Étudie le seuil de couverture des intervalles de confiance calculés à partir du théorème central limite.
	 * <p>
	 * Cette méthode effectue une série de 1000 simulations Monte Carlo pour évaluer la fiabilité
	 * des intervalles de confiance à 95%. Pour chaque simulation :
	 * - Génère 1 million de réalisations.
	 * - Calcule un intervalle de confiance à 95%.
	 * - Vérifie si la vraie valeur (p23) est contenue dans l'intervalle.
	 * <p>
	 * Les paramètres utilisés sont :
	 * - Nombre de simulations : 1000
	 * - Taille de chaque simulation : 1 million de réalisations
	 * - Taille du groupe : 23 personnes
	 * - Nombre de jours dans l'année : 365
	 * - Seuil minimum de personnes : 2
	 * - Niveau de confiance : 95%
	 * - Graine aléatoire : 0x134D6EE
	 * <p>
	 * Résultats affichés :
	 * - Le pourcentage d'intervalles contenant la vraie valeur
	 * - L'intervalle de confiance pour ce pourcentage
	 * <p>
	 * Cette simulation permet de vérifier empiriquement si le niveau de confiance théorique
	 * de 95% est effectivement atteint dans la pratique.
	 */
	private static void simulation2(){
		System.out.println("======= DEBUT DE LA SIMULATION 2 ==========");
		StatCollector stat = new StatCollector();
		StatCollector statFinal = new StatCollector();

		double moyenne;
		double moitieIntervalleDeConfiance;

		Experiment birthdayExp = new BirthdayParadoxGenericExperiment(Constants.BASE_GROUP_SIZE, Constants.NB_DAYS_IN_A_YEAR, Constants.BASE_THRESHOLD);
		Random rnd = new Random(Constants.SEED);

		long startTime = System.nanoTime();

		//Boucle des simulations
		for(int i = 0; i < Constants.SIMULATIONS_EXP2; ++i){
			stat.init();
			MonteCarloSimulation.simulateNRuns(birthdayExp, Constants.MAX_RUNS, rnd, stat);

			moitieIntervalleDeConfiance = stat.getConfidenceIntervalHalfWidth(Constants.CONFIDENCE_INTERVAL);
			moyenne = stat.getAverage();
			// Crée une nouvelle expérience en collectant 1 si la vraie valeur est dans l'intervalle, sinon 0
			if(Constants.TRUE_VALUE_EXP2 >= moyenne - moitieIntervalleDeConfiance
					&& Constants.TRUE_VALUE_EXP2 <= moyenne + moitieIntervalleDeConfiance){
				statFinal.add(1);
			}else{
				statFinal.add(0);
			}
		}

		double executionTime = (System.nanoTime() - startTime) / 1e9;

		final int LABEL_WIDTH = 25;
		String formatLabel = "%-" + LABEL_WIDTH + "s : ";

		// Affiche les statistiques
		double halfWidth = statFinal.getConfidenceIntervalHalfWidth(Constants.CONFIDENCE_INTERVAL);
		System.out.printf(formatLabel +"%.4f\n", "Pourcentage de valeur ok", statFinal .getAverage());
		System.out.printf(formatLabel + "[%.6f, %.6f]\n", "Intervalle de confiance",
				stat.getAverage() - halfWidth,
				stat.getAverage() + halfWidth);
		System.out.printf(formatLabel + "%,.6f secondes\n", "Durée de calcul", executionTime);
	}


	/**
	 * Détermine le nombre minimal de personnes nécessaires pour avoir une probabilité supérieure à 50%
	 * que trois personnes au moins aient leur anniversaire le même jour.
	 * <p>
	 * Cette méthode effectue une recherche itérative dans l'intervalle [80, 100] personnes pour trouver
	 * la plus petite taille de groupe K satisfaisant le critère de probabilité.
	 * Pour chaque taille de groupe testée :
	 * - Réinitialise le générateur aléatoire avec la graine spécifiée.
	 * - Effectue une simulation Monte Carlo.
	 * - Compare la probabilité obtenue avec le seuil cible (0.5).
	 * - S'arrête dès que le seuil est atteint.
	 * <p>
	 * Paramètres constants utilisés :
	 * - Seuil de probabilité cible : 0.5 (50%)
	 * - Nombre minimum de personnes avec même anniversaire : 3
	 * - Nombre de jours dans l'année : 365
	 * - Graine aléatoire : 0x134D6EE
	 * - Plage de recherche : [80, 100] personnes
	 * <p>
	 * Affiche la taille minimale du groupe satisfaisant le critère
	 */
	private static void simulation3(){
		System.out.println("======= DEBUT DE LA SIMULATION 3 ==========");
		StatCollector stat = new StatCollector();
		int minNumberOfPeople = 0;

		long startTime = System.nanoTime();

		for(int i = Constants.GROUP_SIZE_MIN; i <= Constants.GROUP_SIZE_MAX; i++){
			Random rnd = new Random(Constants.SEED);
			stat.init();
			Experiment birthdayExp = new BirthdayParadoxGenericExperiment(i, Constants.NB_DAYS_IN_A_YEAR, Constants.THRESHOLD_EXP3);
			MonteCarloSimulation.simulateNRuns(birthdayExp, Constants.MAX_RUNS, rnd, stat);
			if (stat.getAverage() >= Constants.TARGET_CHANCE) {
				minNumberOfPeople = i;
				break;
			}
		}

		double executionTime = (System.nanoTime() - startTime) / 1e9;

		final int LABEL_WIDTH = 35;
		String formatLabel = "%-" + LABEL_WIDTH + "s : ";

		System.out.printf(formatLabel + "%d\n", "Taille de groupe minimale trouvée", minNumberOfPeople);
		System.out.printf(formatLabel + "%,.6f secondes\n", "Durée de calcul", executionTime);
	}

	/**
	 * Permet d'afficher les différentes statistiques après une simulation
	 * @param stat le statCollector duquel on va extraire les données à afficher
	 * @param executionTime le temps d'exécution de la simulation
	 * @param level le seuil de l'intervalle de confiance
	 */
	private static void displayStats(StatCollector stat, double executionTime, double level) {
		final int LABEL_WIDTH = 25;
		String formatLabel = "%-" + LABEL_WIDTH + "s : ";

		double halfWidth = stat.getConfidenceIntervalHalfWidth(level);
		System.out.printf(formatLabel + "%,d\n", "Nombre d'observations", stat.getNumberOfObs());
		System.out.printf(formatLabel + "%.6f\n", "Moyenne", stat.getAverage());
		System.out.printf(formatLabel + "[%.6f, %.6f]\n", "Intervalle de confiance",
				stat.getAverage() - halfWidth,
				stat.getAverage() + halfWidth);
		System.out.printf(formatLabel + "%.6f\n", "Variance", stat.getVariance());
		System.out.printf(formatLabel + "%.6f\n", "Déviation standard", stat.getStandardDeviation());
		System.out.printf(formatLabel + "%,.6f secondes\n", "Durée de calcul", executionTime);
	}
}
