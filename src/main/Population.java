package main;

import java.util.*;

public class Population {
	Schedule[] chromosomes;
	static ScheduleRequirements static_requirements;
	static int static_populationSize;
	static int tournament_size=3;

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

	private Schedule crossoverOrder(Schedule first, Schedule second) {
		boolean[] do_crossover_for_specialities = randomBooleanArray(first.lessons_of_specialities.length);
		Schedule new_schedule = first.clone();
		for(int k=0; k<do_crossover_for_specialities.length; ++k){
			if(do_crossover_for_specialities[k]){
				boolean[] crossover_types = randomBooleanArray(3);
				if(crossover_types[0]) new_schedule.lessons_of_specialities[k] =
						Schedule.crossoverByRooms(first.lessons_of_specialities[k], second.lessons_of_specialities[k]);
				if(crossover_types[1]) new_schedule.lessons_of_specialities[k] =
						Schedule.crossoverBySpots(first.lessons_of_specialities[k], second.lessons_of_specialities[k]);
				if(crossover_types[1]) new_schedule.lessons_of_specialities[k] =
						Schedule.crossoverByTeachers(first.lessons_of_specialities[k], second.lessons_of_specialities[k]);
			}
		}
		return new_schedule;
	}

	private boolean[] randomBooleanArray(int length) {
		boolean[] res = new boolean[length];
		for(int i=0; i<length; ++i) res[i] = Math.random()<0.5;
		return res;
	}

	private Schedule mutation(Schedule chromosome)
	{
		if(Math.random()<0.5){
			boolean[] specialities = randomBooleanArray(chromosome.lessons_of_specialities.length);
			for(int i=0; i<specialities.length; ++i){
				ArrayList<Lesson> lessons = chromosome.lessons_of_specialities[i];
				boolean[] types = randomBooleanArray(3);
				int first_index = (int)(Math.random()*lessons.size());
				Subject s = Population.static_requirements.specialities[i].subjects[lessons.get(first_index).subjectId];
				if(types[0]){
					//spot
					ArrayList<Integer> spots = static_requirements.getSpots();
					int random_spot_id = spots.get((int)(Math.random()*spots.size()));
					lessons.get(first_index).classSpotId=random_spot_id;
				}
				if(types[1]){
					//room
					ArrayList<Integer> rooms = static_requirements.getRooms(lessons.get(first_index).getAmountRequired());
					int random_room_id = rooms.get((int)(Math.random()*rooms.size()));
					lessons.get(first_index).classRoomId=random_room_id;
				}
				if(types[2]){
					//teacher
					Integer[] teachers = s.getPossibleTeachers(lessons.get(first_index).isLecture);
					int random_teacher_id = teachers[(int)(Math.random()*teachers.length)];
					lessons.get(first_index).teacherId=random_teacher_id;
				}
			}
			return chromosome;
		}
		else return mutateOrder(chromosome);
	}

	private Schedule mutateOrder(Schedule chromosome) {
		boolean[] do_mutate_for_specialities = randomBooleanArray(chromosome.lessons_of_specialities.length);
		for(int k=0; k<do_mutate_for_specialities.length; ++k){
			if(do_mutate_for_specialities[k]){
				boolean[] mutation_types = randomBooleanArray(3);
				if(mutation_types[0]) chromosome.mutateOrderByRoom(chromosome.lessons_of_specialities[k]);
				if(mutation_types[1]) chromosome.mutateOrderBySpot(chromosome.lessons_of_specialities[k]);
				if(mutation_types[2]) chromosome.mutateOrderByTeacher(chromosome.lessons_of_specialities[k]);
			}
		}
		return chromosome;
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
//		System.out.println(SchedulePrinter.scheduleToString(chromosomes[0]));
//		System.out.println(chromosomes[0].getFitness());
		Schedule[] new_schedules = new Schedule[chromosomes.length];
		for(int i=elitism_offset; i<new_schedules.length; ++i){
			Schedule first = selectScheduleByTournament();
			if(Math.random()<crossover_propability){
				Schedule second = first;
				while(first==second) second = selectScheduleByTournament();
				new_schedules[i] = crossover(first.clone(), second.clone());
			}
			else{
				new_schedules[i]=first.clone();
			}
			if(Math.random()<mutation_propability){
				new_schedules[i] = mutation(new_schedules[i]);
			}
		}
		Arrays.sort(chromosomes);
		for(int i=0; i<elitism_offset && i<new_schedules.length; ++i){
			new_schedules[i] = chromosomes[i].clone();
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
		TreeSet<Schedule> set = new TreeSet<>(new Comparator<Schedule>() {
			@Override
			public int compare(Schedule o1, Schedule o2) {
				int compared = o1.compareTo(o2);
				if(compared!=0) return compared;
				return o1.equals(o2) ? 0 : 1;
			}
		});
		for(Schedule s : chromosomes) {
			set.add(s);
		}
		return set.size();
	}
}
