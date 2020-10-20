package main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SchedulePrinter {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String scheduleToString(Schedule sc){
        return gson.toJson(sc);
    }

    public static String lessonToString(Lesson les){
        return gson.toJson(les);
    }
}
