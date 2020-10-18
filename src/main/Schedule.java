import java.util.ArrayList;

public class Schedule {
	ArrayList<Lesson> lessons;

	public Schedule(ArrayList<Lesson> lessons) {
		this.lessons = lessons;
	}

	public static Schedule getRandomSchedule(ScheduleRequirements requirements) {
		ArrayList<Lesson> lessons = new ArrayList<Lesson>();
		for(int i=0; i<requirements.subjects.length; ++i){
			Subject s = requirements.subjects[i];
			int classSpotId;
			int roomSpotId;
			int teacherId;
			for(int j=0; j<s.lectures_amount; ++j){
				classSpotId = getRandomFreeSpot(requirements, lessons, s.id, true);
				roomSpotId = getRandomFreeRoom(requirements, lessons, s.amount_of_students_on_lectures, classSpotId);
				teacherId = getRandomFreeTeacher(lessons, s.possible_teachers_for_lectures, classSpotId);
				lessons.add(new Lesson(classSpotId, roomSpotId, teacherId, s.id, true));
			}
			for(int j=0; j<s.seminars_amount; ++j){
				classSpotId = getRandomFreeSpot(requirements, lessons, s.id, false);
				roomSpotId = getRandomFreeRoom(requirements, lessons, s.amount_of_students_on_seminars, classSpotId);
				teacherId = getRandomFreeTeacher(lessons, s.possible_teachers_for_seminars, classSpotId);
				lessons.add(new Lesson(classSpotId, roomSpotId, teacherId, s.id, false));
			}
		}
		return new Schedule(lessons);
	}

	private static int getRandomFreeTeacher(ArrayList<Lesson> lessons, Integer[] possible_teachers, int classSpotId) {
		ArrayList<Integer> freeTeachers = new ArrayList<Integer>(possible_teachers.length);
		for(int i=0; i<possible_teachers.length; ++i) freeTeachers.add(possible_teachers[i]);
		for(Lesson l : lessons){
			if(l.classSpotId==classSpotId && freeTeachers.contains(l.teacherId)) freeTeachers.remove((Integer)l.teacherId);
		}
		if(freeTeachers.isEmpty()) return -1;
		return freeTeachers.get((int)(Math.random()*freeTeachers.size()));
	}

	private static int getRandomFreeRoom(ScheduleRequirements requirements, ArrayList<Lesson> lessons,
										 int amount_of_students, int classSpotId) {
		ArrayList<Integer> freeRooms = requirements.getRooms(amount_of_students);
		for(Lesson l : lessons){
			if(l.classSpotId==classSpotId) freeRooms.remove((Integer)l.classRoomId);
		}
		if(freeRooms.isEmpty()) return -1;
		return freeRooms.get((int)(Math.random()*freeRooms.size()));
	}

	private static int getRandomFreeSpot(ScheduleRequirements requirements, ArrayList<Lesson> lessons, int subject_id, boolean isLecture) {
		ArrayList<Integer> freeSpots = requirements.getSpots();
		for(Lesson l : lessons){
			if(l.subjectId==subject_id && l.isLecture==isLecture) freeSpots.remove((Integer)l.classSpotId);
		}
		if(freeSpots.isEmpty()) return -1;
		return freeSpots.get((int)(Math.random()*freeSpots.size()));
	}

}

class Lesson {
	int classSpotId;
	int classRoomId;
	int teacherId;
	int subjectId;
	boolean isLecture;

	public Lesson(
			int classSpotId,
			int classRoomId,
			int teacherId,
			int subjectId,
			boolean isLecture
	){
		this.classSpotId = classSpotId;
		this.classRoomId = classRoomId;
		this.teacherId = teacherId;
		this.subjectId = subjectId;
		this.isLecture = isLecture;
	}
}