package src.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;


public class Main {
		public static void main(String[] args) {
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
