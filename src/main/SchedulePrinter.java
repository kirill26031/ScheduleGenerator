package main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SchedulePrinter {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String scheduleToString(Schedule sc){
        StringBuilder sb = new StringBuilder();
        int maxSpot = 0;
        for (Lesson l : sc.lessons){
            if(l.classSpotId > maxSpot) maxSpot = l.classSpotId;
        }
        sb.append("\n\tSCHEDULE\n");
        int lessonsPerSpot = 0;
        for(int i = 0; i <= maxSpot; ++i){
            sb.append("\nSpot#");
            sb.append(i);
            sb.append(" has this lessons:");
            for (Lesson l : sc.lessons){
                if(l.classSpotId == i) {
                    sb.append("\nLesson#");
                    sb.append(lessonsPerSpot + "\n");
                    sb.append(lessonToString(l));
                    lessonsPerSpot++;
                }
            }
            if(lessonsPerSpot == 0) sb.append("   WINDOW   ");
            lessonsPerSpot = 0;
            sb.append("\n===========================\n");
        }

        return sb.toString();
    }

    public static String lessonToString(Lesson les){
        BtLesson beautifiedLesson = new BtLesson(les.classSpotId,
                les.classRoomId,
                Population.static_requirements.teachers[les.teacherId].fullname,
                Population.static_requirements.subjects[les.subjectId].name,
                (les.isLecture? "Lecture": "Practice"),
                Population.static_requirements.classes[les.classRoomId].size);

        return gson.toJson(beautifiedLesson);
    }

}

class BtLesson {
    private int time = 0;
    private int classroom = 0;
    private String teacherName = "";
    private String subjectName = "";
    private String type = "";
    private int classroomSize = 0;

    public BtLesson(int time, int classroom, String teacherName, String subjectName, String type, int classroomSize) {
        this.time = time;
        this.classroom = classroom;
        this.teacherName = teacherName;
        this.subjectName = subjectName;
        this.type = type;
        this.classroomSize = classroomSize;
    }
}

