import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;

public class Main {
		public static void main(String[] args) {
			ScheduleRequirements data = new ScheduleRequirements();
			GeneticAlgorithm ga = new GeneticAlgorithm(data);

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
