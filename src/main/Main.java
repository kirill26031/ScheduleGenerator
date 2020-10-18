import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;

public class Main {
		public static void main(String[] args) {
			ScheduleData data = new ScheduleData();
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.setPrettyPrinting().create();
			try{
				FileWriter fw = new FileWriter("./schedule_requirements.json");
				fw.write(gson.toJson(data));
				fw.close();
			}
			catch(Exception e){e.printStackTrace();}
		}
}
