package main;

import java.util.*;

import static main.SchedulePrinter.scheduleToString;

public class Population {
	Schedule[] chromosomes;
	static ScheduleRequirements static_requirements;
	static int static_populationSize;
	static int tournament_size=5;

	public Population(Schedule[] chromosomes){
		this.chromosomes = chromosomes;
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

	public static boolean isFinished(Population population) {
		return population.chromosomes[0].getFitness()==0;
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

//	public Population doCrossover(int elitism_offset, double crossover_propability) {
//		Schedule[] offsprings = new Schedule[chromosomes.length];
//		for(int i=0; i<elitism_offset && i<chromosomes.length; ++i) offsprings[i] = chromosomes[i];
//		for(int i=elitism_offset; i<chromosomes.length; ++i){
//			Schedule first = chromosomes[(int)(Math.random()*chromosomes.length)].clone();
//			if(Math.random()<=crossover_propability){
//				Schedule second = chromosomes[(int)(Math.random()*chromosomes.length)].clone();
//				while(first.equals(second)) second = chromosomes[(int)(Math.random()*chromosomes.length)];
//				chromosomes[i] = crossover(first, second);
//			}
//			else{
//				chromosomes[i] = first;
//			}
//		}
//		return new Population(offsprings);
//
////		Schedule[] offsprings = new Schedule[chromosomes.length];
////
////		for(int i = 0; i < static_populationSize; i++)
////		{
////			int parrent1Index = (int)(Math.random()*chromosomes.length);
////			int parrent2Index;
////			do{ parrent2Index = (int)(Math.random()*chromosomes.length);}while (parrent1Index == parrent2Index);
////
////			offsprings[i] = crossover(chromosomes[parrent1Index], chromosomes[parrent2Index]);
////		}
////
////		return new Population(offsprings);
//
//	}

	private Schedule crossover(Schedule first, Schedule second) {
//		Schedule crossovered = first.clone();
//		int crossover_types = 1;
//				1+(int)(Math.random()*7);
//		if(crossover_types%2==1) crossovered.crossoverBySpots(second);
//		if((crossover_types>>1)%2==1) crossovered.crossoverByRooms(second);
//		if((crossover_types>>2)%2==1) crossovered.crossoverByTeachers(second);

		boolean[] do_crossover_of_specialities = randomBooleanArray(first.lessons_of_specialities.length);
		Schedule crossovered = new Schedule(new ArrayList[first.lessons_of_specialities.length]);
		for(int k=0; k<crossovered.lessons_of_specialities.length; ++k) {
			crossovered.lessons_of_specialities[k] = new ArrayList<>(first.lessons_of_specialities[k].size());
			if(do_crossover_of_specialities[k]){
				if(first.lessons_of_specialities[k].size()!=second.lessons_of_specialities[k].size()){
					System.out.println("bug size");
				}
				int pivot = (int)(Math.random()*first.lessons_of_specialities[k].size());
				for(int i = 0; i < first.lessons_of_specialities[k].size(); i++)
				{
					if(i < pivot) crossovered.lessons_of_specialities[k].add(first.lessons_of_specialities[k].get(i));
					else crossovered.lessons_of_specialities[k].add(second.lessons_of_specialities[k].get(i));
				}
			}
			else{
				crossovered.lessons_of_specialities[k]=first.lessons_of_specialities[k];
			}
		}
		return crossovered;
	}

	private boolean[] randomBooleanArray(int length) {
		boolean[] res = new boolean[length];
		for(int i=0; i<length; ++i) res[i] = Math.random()<0.5;
		return res;
	}

	public void doMutation(int elitism_offset, double mutation_propability) {
		for(int i=elitism_offset; i<chromosomes.length; ++i){
			if(Math.random()<=mutation_propability) chromosomes[i]=mutation(chromosomes[i]);
		}
	}

	private Schedule mutation(Schedule chromosome)
	{
//		int room = (int)(Math.random()*chromosome.lessons_of_specialities.size());
//		int spot = (int)(Math.random()*chromosome.lessons_of_specialities.size());
//
//		int sbjIdR = chromosome.lessons_of_specialities.get(room).subjectId;
//
//		switch ((int)(Math.random()*2)){
//			case 0: chromosome.lessons_of_specialities.get(room).classRoomId = (int)(Math.random()*(chromosome.lessons_of_specialities.get(room).isLecture ? static_requirements.subjects[chromosome.lessons_of_specialities.get(room).subjectId].amount_of_students_on_lectures : static_requirements.subjects[chromosome.lessons_of_specialities.get(room).subjectId].amount_of_students_on_seminars)); break;
//			case 1: chromosome.lessons_of_specialities.get(spot).classSpotId = (int)(Math.random()*(chromosome.lessons_of_specialities.get(spot).isLecture ? static_requirements.subjects[chromosome.lessons_of_specialities.get(spot).subjectId].amount_of_students_on_lectures : static_requirements.subjects[chromosome.lessons_of_specialities.get(spot).subjectId].amount_of_students_on_seminars)); break;
//			default: break;
//		}

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

	public Population evolve(int elitism_offset, double crossover_propability, double mutation_propability) {
		int[] sizes = {chromosomes[0].lessons_of_specialities[0].size(),
				chromosomes[0].lessons_of_specialities[1].size(),
				chromosomes[0].lessons_of_specialities[2].size()};
		for(int i=0; i<chromosomes.length; ++i){
			if(chromosomes[i].lessons_of_specialities[0].size()!=sizes[0] ||
					chromosomes[i].lessons_of_specialities[1].size()!=sizes[1] ||
					chromosomes[i].lessons_of_specialities[2].size()!=sizes[2]){
				System.out.println("bug");
			}
		}
		Arrays.sort(chromosomes);
//		System.out.println(SchedulePrinter.scheduleToString(chromosomes[0]));
//		System.out.println(chromosomes[0].getFitness());
		Schedule[] new_schedules = new Schedule[chromosomes.length];
		for(int i=0; i<elitism_offset && i<new_schedules.length; ++i){
			new_schedules[i] = chromosomes[i].clone();
		}
		for(int i=elitism_offset; i<new_schedules.length; ++i){
			Schedule first = selectScheduleByTournament();
			if(Math.random()<crossover_propability){
				Schedule second = selectScheduleByTournament();
				new_schedules[i] = crossover(first.clone(), second.clone());
				if(new_schedules[i].lessons_of_specialities[0].size()!=sizes[0] ||
						new_schedules[i].lessons_of_specialities[1].size()!=sizes[1] ||
						new_schedules[i].lessons_of_specialities[2].size()!=sizes[2]){
					System.out.println("bug");
				}
			}
			else{
				new_schedules[i]=first.clone();
				if(new_schedules[i].lessons_of_specialities[0].size()!=sizes[0] ||
						new_schedules[i].lessons_of_specialities[1].size()!=sizes[1] ||
						new_schedules[i].lessons_of_specialities[2].size()!=sizes[2]){
					System.out.println("bug");
				}
			}
		}
		for(int i=0; i<new_schedules.length; ++i){
			if(new_schedules[i].lessons_of_specialities[0].size()!=sizes[0] ||
					new_schedules[i].lessons_of_specialities[1].size()!=sizes[1] ||
					new_schedules[i].lessons_of_specialities[2].size()!=sizes[2]){
				System.out.println("bug");
			}
		}
		return new Population(new_schedules);
	}

	private Schedule selectScheduleByTournament() {
		Schedule best = chromosomes[(int)(Math.random()*chromosomes.length)];
		for(int i=0; i<tournament_size-1; ++i){
			Schedule s = chromosomes[(int)(Math.random()*chromosomes.length)];
			if(best.getFitness()<s.getFitness()){
				best = s;
			}
		}
		return best.clone();
	}

	public double averageFitness() {
		double sum=0;
		for(Schedule s : chromosomes) sum+=s.getFitness();
		return sum/chromosomes.length;
	}

	public int amountOfUnique() {
		TreeSet<Schedule> set = new TreeSet<>();
		for(Schedule s : chromosomes) {
			set.add(s);
		}
		return set.size();
	}
}
