import montecarlo.Experiment;

import java.util.Random;

/**
 * Implémentation générique de l'expérience du paradoxe des anniversaires.
 * Cette classe simule la distribution d'anniversaires dans un groupe de personnes
 * et vérifie si un certain nombre de personnes partagent le même anniversaire.
 * <p>
 * @author : Junod Arthur
 * @author : Häffner Edwin
 */
public class BirthdayParadoxGenericExperiment implements Experiment {
    private final int sizeOfGroup;
    private final int nbDays;
    private final int threshold;

    /**
     * Constructeur de la classe
     * @param sizeOfGroup La taille du groupe
     * @param nbDays Nombre de jours dans l'année
     * @param threshold Seuil recherché de l'experience
     */
    BirthdayParadoxGenericExperiment(int sizeOfGroup, int nbDays, int threshold){
        this.sizeOfGroup = sizeOfGroup;
        this.nbDays = nbDays;
        this.threshold = threshold;
    }

    /**
     * Execute l'experience du paradoxe des anniversaires
     * @param rnd random source to be used to simulate the experiment
     * @return true si on est >= au seuil, false sinon.
     */
    @Override
    public double execute(Random rnd) {
        int[] dates = new int[nbDays];

        for(int i = 0; i < sizeOfGroup; ++i){
            int rdm = rnd.nextInt(nbDays);
            ++dates[rdm];

            if (dates[rdm] >= threshold) return 1;
        }
        return 0;
    }
}
