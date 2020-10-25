package main;

import java.util.ArrayList;

public class ScheduleRequirements {
	Teacher[] teachers;
	public Speciality[] specialities;
	ClassRoom[] classes;
	ScheduleDay[] days;
	ArrayList<Integer> spots;
	static int next_teacher_id=0;
	static int next_class_room_id=0;
	static int next_spot_id=0;

	public ScheduleRequirements(){
		teachers = new Teacher[]{
				new Teacher("Michael Arson"),
				new Teacher("Petrenko Volodymyr"),
				new Teacher("Orest Ivanenko"),
				new Teacher("Kozerenko Sergiy Oleksandrovich"),
				new Teacher("Voznyuk Yaroslav Ivanovych"),
				new Teacher("Veretelnyk Roman")
		};
		classes = new ClassRoom[]{
				new ClassRoom("1-100", 17),
				new ClassRoom("1-101", 10),
				new ClassRoom("1-102", 20),
				new ClassRoom("Big room", 100),
				new ClassRoom("1-225", 40),
				new ClassRoom("1-223", 60)
		};
		specialities = new Speciality[]{
				new Speciality("Computer Science 1", new Subject[]{
						new Subject(
								"Algorithms", 1, 4,
								new Integer[]{0},
								new Integer[]{0, 2},
								40, 12, 0),
						new Subject(
								"English", 0, 2,
								new Integer[]{1},
								new Integer[]{1},
								0, 12, 0),
						new Subject(
								"Linear Algebra", 1, 2,
								new Integer[]{1},
								new Integer[]{2,3},
								40, 20, 0)
				}),
				new Speciality("Software Engineering 1", new Subject[]{
						new Subject(
								"Computer Networks", 1, 2,
								new Integer[]{3},
								new Integer[]{3,4},
								40, 12, 1),
						new Subject(
								"English", 0, 2,
								new Integer[]{1},
								new Integer[]{1},
								0, 12, 1),
						new Subject(
								"Linear Algebra", 1, 2,
								new Integer[]{1},
								new Integer[]{2,3},
								40, 20, 1)
				}),
				new Speciality("Applied mathematics 1", new Subject[]{
						new Subject(
								"Computer Networks", 1, 2,
								new Integer[]{3},
								new Integer[]{2, 3},
								40, 12, 2),
						new Subject(
								"English", 0, 2,
								new Integer[]{1},
								new Integer[]{1},
								0, 12, 2),
						new Subject(
								"Ukrainian literature", 1, 3,
								new Integer[]{4},
								new Integer[]{4,5},
								40, 20, 2)
				}),
		};
		days = new ScheduleDay[5];
		int[] classes_spots = new int[2];
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
		return (ArrayList<Integer>) spots.clone();
	}

	public ArrayList<Integer> getRooms(int min_size) {
		ArrayList<Integer> rooms = new ArrayList<Integer>();
		for(int i=0; i<classes.length; ++i) if(classes[i].size>=min_size) rooms.add(i);
		return rooms;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Вимоги до розкладу:");
		for(Speciality speciality : specialities){
			sb.append("\n\nПредмети для спеціальності \"");
			sb.append(speciality.name);
			sb.append("\"");
			for(Subject subject : speciality.subjects){
				sb.append("\n\t");
				sb.append(subject.name);
				sb.append("\n\tКількість лекцій: ");
				sb.append(subject.lectures_amount);
				if(subject.lectures_amount!=0){
					sb.append("\n\tВикладачі кваліфіковані викладати лекції:");
					for(Integer teacher_index : subject.possible_teachers_for_lectures){
						sb.append("\n\t\t");
						sb.append(teachers[teacher_index].fullname);
					}
					sb.append("\n\tКімната має вміщати ");
					sb.append(subject.amount_of_students_on_lectures);
					sb.append(" осіб");
				}
				sb.append("\n\tКількість семінарів: ");
				sb.append(subject.seminars_amount);
				if(subject.seminars_amount!=0){
					sb.append("\n\tВикладачі кваліфіковані викладати семінари:");
					for(Integer teacher_index : subject.possible_teachers_for_seminars){
						sb.append("\n\t\t");
						sb.append(teachers[teacher_index].fullname);
					}
					sb.append("\n\tКімната має вміщати ");
					sb.append(subject.amount_of_students_on_seminars);
					sb.append(" осіб");
				}
			}
		}
		return sb.toString();
	}
}

class Speciality {
	String name;
	Subject[] subjects;

	public Speciality(String name, Subject[] subjects){
		this.name = name;
		this.subjects = subjects;
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
	static int last_speciality_id=-1;
	static int next_subject_id=0;

	public Subject(
			String name,
			int lectures_amount,
			int seminars_amount,
			Integer[] possible_teachers_for_lectures,
			Integer[] possible_teachers_for_seminars,
			int amount_of_students_on_lectures,
			int amount_of_students_on_seminars,
			int speciality_id
	){
		this.lectures_amount = lectures_amount;
		this.seminars_amount = seminars_amount;
		this.possible_teachers_for_lectures = possible_teachers_for_lectures;
		this.possible_teachers_for_seminars = possible_teachers_for_seminars;
		this.name = name;
		this.amount_of_students_on_lectures = amount_of_students_on_lectures;
		this.amount_of_students_on_seminars = amount_of_students_on_seminars;
		if(speciality_id!=last_speciality_id){
			last_speciality_id=speciality_id;
			next_subject_id=0;
		}
		this.id = next_subject_id++;
	}

	public Integer[] getPossibleTeachers(boolean isLecture) {
		return (isLecture) ? possible_teachers_for_lectures : possible_teachers_for_seminars;
	}
}

class ScheduleDay {
	int day;
	ClassSpot[] spots;
	static int next_spot_id=0;
	static String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

	public ScheduleDay(int[] classPositions, int day){
		spots = new ClassSpot[classPositions.length];
		for(int i=0; i<classPositions.length; ++i) spots[i] = new ClassSpot(classPositions[i]);
		this.day = day;
	}

	@Override
	public String toString(){
		return days[day%7];
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