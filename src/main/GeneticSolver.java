import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticSolver {

    private int maxGenerations; //Количество формируемых популяций
    private int genesQuantity;  //Количество ген в хромосоме
    private int firstGenerationSize; //Количество хромосом в первой популяции
    private int survivingIndividuals; //Количество хромосом которое мы будем отбирать в каждой популяции

    Random random = new Random();

    private int maxLoad; //Максимальная вместимость ранца
    private Item[] items; //Предметы

    private Generation currentGeneration;

    //Инициализируем решатель
    public GeneticSolver(List<Item> items, int maxLoad) {
        this.genesQuantity = items.size();
        this.items = new Item[items.size()];
        this.items = items.toArray(this.items);
        firstGenerationSize = items.size() * 4;
        survivingIndividuals = items.size()/2;
        maxGenerations = 100;
        this.maxLoad = maxLoad;
        this.currentGeneration = new Generation();
    }

    public static Fill fillKnapsackGenetic(int load, List<Item> items) {
        GeneticSolver geneticSolver = new GeneticSolver(items, load);
        return geneticSolver.findSolution();
    }

    private Fill findSolution() {
        for (int i = 1; i <= maxGenerations; i++) {
            currentGeneration.evolute();
        }
        return getBestFill();
    }

    private Fill getBestFill() {
        Individual bestIndividual = getAndDeleteBestIndividual();
        return bestIndividual.convertToFill();
    }

    private Individual getAndDeleteBestIndividual() {

        Individual bestIndividual = null;
        int bestFitnessFound = Integer.MIN_VALUE + 1;

        for (Individual individual : currentGeneration.individuals) {
            int newFitness = individual.getFitness();
            if (newFitness > bestFitnessFound) {
                bestIndividual = individual;
                bestFitnessFound = newFitness;
            }
        }
        currentGeneration.individuals.remove(bestIndividual);
        return bestIndividual;
    }

    private class Generation {

        int generation;
        List<Individual> individuals;

        Generation() {
            this.generation = 0;
            individuals = new ArrayList<>();
            for (int i = 0; i < firstGenerationSize; i++) {
                individuals.add(new Individual());
            }
        }

        public void evolute() {
            List<Individual> newIndividuals = new ArrayList<>();
            int quantity = firstGenerationSize / 2;
            if (survivingIndividuals < quantity)
                quantity = survivingIndividuals;

            Individual[] survivedIndividuals = new Individual[quantity];

            for (int i = 0; i < quantity; i++) {
                Individual nextIndividual = getAndDeleteBestIndividual();
                survivedIndividuals[i] = nextIndividual;
                newIndividuals.add(nextIndividual);
            }

            newIndividuals.addAll(getAllTheChildren(survivedIndividuals));
            for (int i = 0; i < survivingIndividuals; i++) {
                newIndividuals.add(new Individual());
            }

            individuals = newIndividuals;

            generation++;

        }

        private List<Individual> getAllTheChildren(Individual[] individuals) {
            List<Individual> crossedIndividuals = new ArrayList<>();
            for (int i = 0; i < individuals.length - 1; i++) {
                for (int j = i + 1; j < individuals.length; j++) {
                    if (individuals[j] == null) {
                    }
                    crossedIndividuals.add(individuals[i].crossover(individuals[j]));
                }
            }
            return crossedIndividuals;
        }

    }

    private class Individual {
        boolean[] genome;

        Individual() {
            boolean[] randomGenome = new boolean[genesQuantity];
            for (int i = 0; i < randomGenome.length; i++) {
                randomGenome[i] = getRandomBoolean();
            }
            genome = randomGenome;
        }

        Individual(boolean[] genome) {
            this.genome = genome;
        }

        int getFitness() {
            Fill fill = convertToFill();

            if (fill == null)
                return 0;

            int fillLoad = 0;

            for (Item item : fill.getItems()) {
                fillLoad += item.getWeight();
            }

            int fitness = fillLoad > maxLoad
                    ? -1
                    : fill.getCost();

            return fitness;

        }

        Fill convertToFill() {
            Fill fill = null;
            for (int i = 0; i < genome.length; i++) {
                if (genome[i]) {
                    if (fill == null)
                        fill = new Fill(items[i]);
                    else
                        fill = fill.plus(new Fill(items[i]));
                }
            }
            return fill;
        }

        Individual crossover(Individual otherParent) {
            boolean crossedGenome[] = new boolean[genesQuantity];
            for (int i = 0; i < crossedGenome.length; i++) {
                if (this.genome[i] == otherParent.genome[i])
                    crossedGenome[i] = this.genome[i];
                else
                    crossedGenome[i] = getRandomBoolean();

            }
            Individual child = new Individual(crossedGenome);
            return child;
        }

        private boolean getRandomBoolean() {
            return (random.nextInt(100) > 50);
        }
    }
}