import montecarlo.Experiment;

import java.util.Random;

public class BirthdayParadoxGenericExperiment implements Experiment {
    private final int nbDates;
    private final int nbJours;
    private final int seuil;


    BirthdayParadoxGenericExperiment(int nbDates, int nbJours, int seuil){
        this.nbDates = nbDates;
        this.nbJours = nbJours;
        this.seuil = seuil;
    }

    @Override
    public double execute(Random rnd) {
        int[] dates = new int[nbJours];

        for(int i = 0; i < nbDates; ++i){
            int rdm = rnd.nextInt(nbJours);
            ++dates[rdm];

            if (dates[rdm] >= seuil) return 1;
        }

        return 0;
    }
}
