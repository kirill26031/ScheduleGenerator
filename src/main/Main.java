package main;


public class Main {
		public static void main(String[] args) {
			ScheduleRequirements data = new ScheduleRequirements();
			GeneticAlgorithm ga = new GeneticAlgorithm(data);
			boolean finished=false;
			while(!finished) finished=ga.makeStep();
		}

}
