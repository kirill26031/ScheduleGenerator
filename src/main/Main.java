package main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;

import static main.SchedulePrinter.lessonToString;


public class Main {
		public static void main(String[] args) {
			Lesson test = new Lesson(10,2,1,24,true);
			System.out.println(lessonToString(test));
			ScheduleRequirements data = new ScheduleRequirements();
			GeneticAlgorithm ga = new GeneticAlgorithm(data);
			boolean finished=false;
			while(!finished) finished=ga.makeStep();
			System.out.println("here");
		}

		void writeJSON(String filename, Object o){
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.setPrettyPrinting().create();
			try{
				FileWriter fw = new FileWriter(filename);
				fw.write(gson.toJson(o));
				fw.close();
			}
			catch(Exception e){e.printStackTrace();}
		}
}
