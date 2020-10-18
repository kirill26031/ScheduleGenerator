public class GeneticAlgorithm {
	ScheduleRequirements requirements;
	int generationCount=0;
	int populationSize=50;
	Population population;
	
	public GeneticAlgorithm(ScheduleRequirements requirements) {
		this.requirements = requirements;
		population = randomPopulation(requirements);
		System.out.println("Hi");
	}

	private Population randomPopulation(ScheduleRequirements requirements) {
		Schedule[] chromosomes = new Schedule[populationSize];
		for(int i=0; i<chromosomes.length; ++i){
			chromosomes[i] = Schedule.getRandomSchedule(requirements);
		}
		return new Population(chromosomes);
	}
}
