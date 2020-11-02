package main;


import java.io.FileWriter;
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
			ArrayList<Lesson> result = csp.findNextSolution();
			schedule = Schedule.formatSchedule(result);

			String result_str = SchedulePrinter.scheduleToString(schedule);
			result_str+="\n\n\nCorrect: "+(schedule.getFitness()==0);
			write("log.txt", result_str);
		}

	public	static void write(String filename, String string){
		try{
			FileWriter fw = new FileWriter(filename);
			fw.write(string);
			fw.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
}
