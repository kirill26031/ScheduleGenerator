package src.main;

import java.util.ArrayList;

public class ScheduleRequirements {
	Teacher[] teachers;
	public Subject[] subjects;
	ClassRoom[] classes;
	ScheduleDay[] days;
	ArrayList<Integer> spots;
	static int next_teacher_id=0;
	static int next_class_room_id=0;
	static int next_subject_id=0;
	static int next_spot_id=0;

	public ScheduleRequirements(){
		teachers = new Teacher[]{
				new Teacher("Michael Arson"),
				new Teacher("Volodymyr Petrenko"),
				new Teacher("Orest Ivanenko"),
				new Teacher("Kozerenko")
		};
		classes = new ClassRoom[]{
				new ClassRoom("1-100", 17),
				new ClassRoom("1-101", 10),
				new ClassRoom("1-102", 20),
				new ClassRoom("Big room", 100),
				new ClassRoom("1-225", 40),
				new ClassRoom("1-223", 60)
		};
		subjects = new Subject[]{
				new Subject(
						"Algorithms", 1, 4,
						new Integer[]{0},
						new Integer[]{0, 2},
						40, 12),
				new Subject(
						"English", 0, 2,
						new Integer[]{1},
						new Integer[]{1},
						0, 12),
				new Subject(
						"Linear Algebra", 1, 2,
						new Integer[]{1},
						new Integer[]{2,3},
						40, 20)
		};
		days = new ScheduleDay[6];
		int[] classes_spots = new int[1];
		for(int j=0; j<classes_spots.length; ++j){
			classes_spots[j]=j;
		}
		for(int i=0; i<days.length; ++i){
			days[i] = new ScheduleDay(classes_spots, i);
		}
		this.spots = createSpots();
	}

	private ArrayList<Integer> createSpots() {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for(ScheduleDay d : days){
			for(ScheduleDay.ClassSpot spot : d.spots) res.add(spot.id);
		}
		return res;
	}

	public ArrayList<Integer> getSpots() {
		return spots;
	}

	public ArrayList<Integer> getRooms(int min_size) {
		ArrayList<Integer> rooms = new ArrayList<Integer>();
		for(int i=0; i<classes.length; ++i) if(classes[i].size>=min_size) rooms.add(i);
		return rooms;
	}
}

class ClassRoom {
	int size;
	String name;
	int id;

	public ClassRoom(String name, int size){
		this.name = name;
		this.size = size;
		this.id = ScheduleRequirements.next_class_room_id++;
	}
}

class Teacher {
	String fullname;
	int id;

	public Teacher(String fullname){
		this.fullname = fullname;
		this.id = ScheduleRequirements.next_teacher_id++;
	}
}

class Subject {
	int lectures_amount;
	int seminars_amount;
	Integer[] possible_teachers_for_lectures;
	Integer[] possible_teachers_for_seminars;
	int amount_of_students_on_seminars;
	int amount_of_students_on_lectures;
	String name;
	int id;

	public Subject(
			String name,
			int lectures_amount,
			int seminars_amount,
			Integer[] possible_teachers_for_lectures,
			Integer[] possible_teachers_for_seminars,
			int amount_of_students_on_lectures,
			int amount_of_students_on_seminars
	){
		this.lectures_amount = lectures_amount;
		this.seminars_amount = seminars_amount;
		this.possible_teachers_for_lectures = possible_teachers_for_lectures;
		this.possible_teachers_for_seminars = possible_teachers_for_seminars;
		this.name = name;
		this.amount_of_students_on_lectures = amount_of_students_on_lectures;
		this.amount_of_students_on_seminars = amount_of_students_on_seminars;
		this.id = ScheduleRequirements.next_subject_id++;
	}
}

class ScheduleDay {
	int day;
	ClassSpot[] spots;
	static int next_spot_id=0;

	public ScheduleDay(int[] classPositions, int day){
		spots = new ClassSpot[classPositions.length];
		for(int i=0; i<classPositions.length; ++i) spots[i] = new ClassSpot(classPositions[i]);
		this.day = day;
	}

	class ClassSpot {
		int id;
		int position;

		public ClassSpot(int position){
			this.position=position;
			this.id = ScheduleDay.next_spot_id++;
		}
	}
}
/*
*
    в системі мають бути дисципліни, що мають лекції та практичні
    мають бути обмеження, на аудиторії в яких проводяться зайняття (так припустимо в аудиторії, що містить 15 місць не можна провести лекцію)
    практичні зайняття можуть проводити різні викладачі
    один викладач не може проводити одночасно різні пари
    один студент не може одночасно бути на різних парах
    розклад зайнять має відповідати стандарту запровадженому на факультеті (початок та кінець пар, навчальні дні)
*
* */