package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static main.SchedulePrinter.scheduleToString;

public class Population {
	Schedule[] chromosomes;
	static ScheduleRequirements static_requirements;
	static int static_populationSize;

	public Population(Schedule[] chromosomes){
		this.chromosomes = chromosomes;
	}

	public boolean selectByFitness(int elitism_offset){
		System.out.println(scheduleToString(chromosomes[0]));
		Arrays.sort(chromosomes, new Comparator<Schedule>() {
			public int compare(Schedule o1, Schedule o2) {
				return o2.getFitness() - o1.getFitness();
			}
		});
		System.out.println(chromosomes[0].getFitness());
		if(isFinished(chromosomes)) return true;
		Schedule[] new_schedules = new Schedule[chromosomes.length];
		for(int i=0; i<elitism_offset && i<new_schedules.length; ++i){
			new_schedules[i] = chromosomes[i];
		}
		int sum_fitness=0;
		int min_fitness=0;
		for(Schedule schedule: chromosomes) {
			sum_fitness+=schedule.getFitness();
			min_fitness = Math.min(min_fitness, schedule.getFitness());
		}
		for(int i=elitism_offset; i<new_schedules.length; ++i){
			new_schedules[i] = getRandomSchedule(Math.abs(sum_fitness), chromosomes, Math.abs(min_fitness));
		}
		return false;
	}

	private Schedule getRandomSchedule(int sum_fitness, Schedule[] schedules, int min_fitness) {
		sum_fitness = min_fitness*schedules.length-sum_fitness;
		double random = sum_fitness*Math.random();
		double sum=0;
		for(int i=0; i<schedules.length; ++i){
			sum+=(min_fitness-schedules[i].getFitness());
			if(sum>=random) return schedules[i].clone();
		}
		return schedules[0].clone();
	}

	public boolean isFinished(Schedule[] chromosomes) {
		return chromosomes[0].fitness==0;
	}

	public static Population randomPopulation(ScheduleRequirements requirements, int populationSize) {
		static_requirements = requirements;
		static_populationSize = populationSize;


		Schedule[] chromosomes = new Schedule[populationSize];
		for(int i=0; i<chromosomes.length; ++i){
			chromosomes[i] = Schedule.getRandomSchedule(requirements);
		}
		return new Population(chromosomes);
	}

	public Population doCrossover(int elitism_offset, double crossover_propability) {
		Schedule[] offsprings = new Schedule[chromosomes.length];
		for(int i=0; i<elitism_offset && i<chromosomes.length; ++i) offsprings[i] = chromosomes[i];
		for(int i=elitism_offset; i<chromosomes.length; ++i){
			Schedule first = chromosomes[(int)(Math.random()*chromosomes.length)];
			if(Math.random()<=crossover_propability){
				Schedule second = chromosomes[(int)(Math.random()*chromosomes.length)];
				while(first.equals(second)) second = chromosomes[(int)(Math.random()*chromosomes.length)];
				chromosomes[i] = crossover(first, second);
			}
			else{
				chromosomes[i] = first;
			}
		}
		return new Population(offsprings);

//		Schedule[] offsprings = new Schedule[chromosomes.length];
//
//		for(int i = 0; i < static_populationSize; i++)
//		{
//			int parrent1Index = (int)(Math.random()*chromosomes.length);
//			int parrent2Index;
//			do{ parrent2Index = (int)(Math.random()*chromosomes.length);}while (parrent1Index == parrent2Index);
//
//			offsprings[i] = crossover(chromosomes[parrent1Index], chromosomes[parrent2Index]);
//		}
//
//		return new Population(offsprings);

	}

	private Schedule crossover(Schedule first, Schedule second) {
		Schedule crossovered = first.clone();
		int crossover_types = 1;
//				1+(int)(Math.random()*7);
		if(crossover_types%2==1) crossovered.crossoverBySpots(second);
		if((crossover_types>>1)%2==1) crossovered.crossoverByRooms(second);
		if((crossover_types>>2)%2==1) crossovered.crossoverByTeachers(second);

//		Schedule crossovered = new Schedule(new ArrayList<Lesson>(first.lessons.size()));
//		int pivot = (int)(Math.random()*first.lessons.size());
//		for(int i = 0; i < first.lessons.size(); i++)
//		{
//			if(i < pivot) crossovered.lessons.add(first.lessons.get(i));
//			else crossovered.lessons.add(second.lessons.get(i));
//		}
		return crossovered;
	}

	public void doMutation(int elitism_offset, double mutation_propability) {
		for(int i=elitism_offset; i<chromosomes.length; ++i){
			if(Math.random()<=mutation_propability) chromosomes[i]=mutation(chromosomes[i]);
		}
	}

	private Schedule mutation(Schedule chromosome)
	{
		int room = (int)(Math.random()*chromosome.lessons.size());
		int spot = (int)(Math.random()*chromosome.lessons.size());

		int sbjIdR = chromosome.lessons.get(room).subjectId;

		switch ((int)(Math.random()*2)){
			case 0: chromosome.lessons.get(room).classRoomId = (int)(Math.random()*(chromosome.lessons.get(room).isLecture ? static_requirements.subjects[chromosome.lessons.get(room).subjectId].amount_of_students_on_lectures : static_requirements.subjects[chromosome.lessons.get(room).subjectId].amount_of_students_on_seminars)); break;
			case 1: chromosome.lessons.get(spot).classSpotId = (int)(Math.random()*(chromosome.lessons.get(spot).isLecture ? static_requirements.subjects[chromosome.lessons.get(spot).subjectId].amount_of_students_on_lectures : static_requirements.subjects[chromosome.lessons.get(spot).subjectId].amount_of_students_on_seminars)); break;
			default: break;
		}

		return  chromosome;
	}

	public void newGeneration(Population offsprings){
		Arrays.sort(chromosomes);
		Arrays.sort(offsprings.chromosomes);

		Schedule[] new_population = new Schedule[static_populationSize];
		int population_counter = 0;
		int offspring_counter = 0;

		for(int i = 0; i < static_populationSize; ++i){
			if(chromosomes[population_counter].getFitness() < offsprings.chromosomes[offspring_counter].getFitness()){
				new_population[i] = chromosomes[population_counter];
				population_counter++;
			} else{
				new_population[i] = offsprings.chromosomes[offspring_counter];
				offspring_counter++;
			}

		}

		chromosomes = new_population;
	}

}
