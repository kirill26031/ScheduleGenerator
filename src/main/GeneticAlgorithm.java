package main;

import java.lang.reflect.Array;
import java.util.Arrays;

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
		Arrays.sort(population.chromosomes);
		System.out.println("Average "+generationCount+" is "+population.averageFitness());
		System.out.println("Max "+generationCount+" is "+population.chromosomes[0].getFitness());
		System.out.println("Amount of unique "+population.amountOfUnique());
	}

	boolean makeStep(){
		generationCount++;
		if(generationCount>=10000 || Population.isFinished(population)) return true;
		offsprings = population.evolve(1, 0.8, 0.05);
		System.out.println("Average "+generationCount+" is "+offsprings.averageFitness());
		System.out.println("Max "+generationCount+" is "+offsprings.chromosomes[0].getFitness());
		System.out.println("Amount of unique "+offsprings.amountOfUnique());
//		// crossover possibly has bugs
//		offsprings = population.doCrossover(1,0.8);
//		// mutation empty
//		offsprings.doMutation(1, 0.05);
//		population.newGeneration(offsprings);
		population=offsprings;
		return false;
	}
}

