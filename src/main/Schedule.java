package main;

import java.util.ArrayList;


public class Schedule implements Cloneable{
	ArrayList<Lesson> lessons;
	int fitness = 1;

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
		if(freeTeachers.isEmpty()) return possible_teachers[(int)(Math.random()*possible_teachers.length)];
		return freeTeachers.get((int)(Math.random()*freeTeachers.size()));
	}

	private static int getRandomFreeRoom(ScheduleRequirements requirements, ArrayList<Lesson> lessons,
										 int amount_of_students, int classSpotId) {
		ArrayList<Integer> freeRooms = requirements.getRooms(amount_of_students);
		for(Lesson l : lessons){
			if(l.classSpotId==classSpotId) freeRooms.remove((Integer)l.classRoomId);
		}
		if(freeRooms.isEmpty()) return requirements.getRooms(amount_of_students).get(
				(int)(Math.random()*requirements.getRooms(amount_of_students).size())
		);
		return freeRooms.get((int)(Math.random()*freeRooms.size()));
	}

	private static int getRandomFreeSpot(ScheduleRequirements requirements, ArrayList<Lesson> lessons, int subject_id, boolean isLecture) {
		ArrayList<Integer> freeSpots = (ArrayList<Integer>) requirements.getSpots().clone();
		for(Lesson l : lessons){
			if(l.subjectId==subject_id && l.isLecture==isLecture) freeSpots.remove((Integer)l.classSpotId);
		}
		if(freeSpots.isEmpty()) return requirements.getSpots().get(
				(int)(Math.random()*requirements.getSpots().size())
		);
		return freeSpots.get((int)(Math.random()*freeSpots.size()));
	}

	public int fitnessCalculate() {
		int teacher_errors = 0;
		int spot_errors = 0;
		int room_errors = 0;
		for(Lesson l : lessons){
			// calculate amount of repeats for each error type
		}
		fitness = -1*(teacher_errors+spot_errors+room_errors);
		return fitness;
	}

	int getFitness(){return fitness;}

	@Override
	protected Schedule clone(){
		ArrayList<Lesson> clonned_lessons = new ArrayList<Lesson>(lessons.size());
		for(Lesson l : lessons){
			clonned_lessons.add(l.clone());
		}
		return new Schedule(clonned_lessons);
	}

	public void crossoverBySpots(Schedule second) {
		int crossover_point = (int)(Math.random()*(second.lessons.size()-1));
		int[] firstSpotGenes = new int[lessons.size()];
		int[] secondSpotGenes = new int[second.lessons.size()];
		for(int i=0; i<lessons.size(); ++i) firstSpotGenes[i] = lessons.get(i).classSpotId;
		for(int i=0; i<second.lessons.size(); ++i) secondSpotGenes[i] = second.lessons.get(i).classSpotId;
		int[] crossoveredSpotGenes = crossoverOrder(firstSpotGenes, secondSpotGenes, crossover_point);
		for(int i=0; i<lessons.size(); ++i) lessons.get(i).classSpotId = crossoveredSpotGenes[i];
	}

	private int[] crossoverOrder(int[] firstSpotGenes, int[] secondSpotGenes, int crossover_point) {
		int[] crossovered = new int[firstSpotGenes.length];
		for(int j=0; j<=crossover_point; ++j) crossovered[j]=firstSpotGenes[j];
		int i=crossover_point+1;
		int index;
		for(int spot_second : secondSpotGenes){
			index = indexOf(crossovered, spot_second);
			if(index==-1) crossovered[i++]=spot_second;
		}
		return crossovered;
	}

	private int indexOf(int[] crossovered, int spot_second) {
		for(int i=0; i<crossovered.length; ++i) if(crossovered[i]==spot_second) return i;
		return -1;
	}

	public void crossoverByRooms(Schedule second) {
	}

	public void crossoverByTeachers(Schedule second) {
	}
}

class Lesson implements Cloneable{
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

	@Override
	protected Lesson clone(){
		return new Lesson(classSpotId, classRoomId, teacherId, subjectId, isLecture);
	}
}