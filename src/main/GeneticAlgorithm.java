package main;

import static main.SchedulePrinter.scheduleToString;

public class GeneticAlgorithm {
	ScheduleRequirements requirements;
	int generationCount=0;
	int populationSize=50;
	Population population;
	Population offsprings;

	public GeneticAlgorithm(ScheduleRequirements requirements) {
		this.requirements = requirements;
		population = Population.randomPopulation(requirements, populationSize);
	}

	boolean makeStep(){
		generationCount++;
		if(population.selectByFitness(1)) return true;

		// crossover possibly has bugs
		offsprings = population.doCrossover(1,0.8);
		// mutation empty
		offsprings.doMutation(1, 0.05);
		population.newGeneration(offsprings);
		return false;
	}
}

