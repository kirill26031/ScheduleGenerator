import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GeneticAlgorithm {
	ScheduleRequirements requirements;
	int generationCount=0;
	int populationSize=50;
	Population population;
	
	public GeneticAlgorithm(ScheduleRequirements requirements) {
		this.requirements = requirements;
		population = Population.randomPopulation(requirements, populationSize);
		System.out.println("Hi");
	}

	boolean makeStep(){
		if(population.selectByFitness(1)) return true;
		population.doCrossover(0.8);
		return false;
	}
}
