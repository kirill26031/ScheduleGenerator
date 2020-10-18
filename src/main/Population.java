import java.util.Arrays;
import java.util.Comparator;

public class Population {
	Schedule[] chromosomes;

	public Population(Schedule[] chromosomes){
		this.chromosomes = chromosomes;
	}

	public boolean selectByFitness(int elitism_offset){
		Arrays.sort(chromosomes, new Comparator<Schedule>() {
			public int compare(Schedule o1, Schedule o2) {
				return o2.fitnessCalculate() - o1.fitnessCalculate();
			}
		});
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
		Schedule[] chromosomes = new Schedule[populationSize];
		for(int i=0; i<chromosomes.length; ++i){
			chromosomes[i] = Schedule.getRandomSchedule(requirements);
		}
		return new Population(chromosomes);
	}

	public void doCrossover(double crossover_propability) {

	}
}
