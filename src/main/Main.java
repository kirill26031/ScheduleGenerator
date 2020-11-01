package main;


import java.util.ArrayList;

public class Main {
	static ScheduleRequirements requirements = new ScheduleRequirements();
		public static void main(String[] args) {
			Schedule schedule = Schedule.getRandomSchedule(requirements);
			ArrayList<Lesson>[] lessons_of_specialities = schedule.lessons_of_specialities;
			for(int i=0; i<lessons_of_specialities.length; ++i){
				for(int j=0; j<lessons_of_specialities[i].size(); ++j){
					lessons_of_specialities[i].get(j).teacherId=-1;
					lessons_of_specialities[i].get(j).classRoomId=-1;
					lessons_of_specialities[i].get(j).classSpotId=-1;
				}
			}
			CSPGraph csp = new CSPGraph(lessons_of_specialities);
//			GeneticAlgorithm ga = new GeneticAlgorithm(data);
//			boolean finished=false;
//			while(!finished) finished=ga.makeStep();
		}

}
