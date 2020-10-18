
public class ScheduleData {
	Teacher[] teachers;
	Subject[] subjects;
	ClassRoom[] classes;

	static int next_teacher_id=0;

	public ScheduleData(){
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
						new Teacher[]{teachers[0]},
						new Teacher[]{teachers[0], teachers[2]},
						40, 12),
				new Subject(
						"English", 0, 2,
						new Teacher[]{teachers[1]},
						new Teacher[]{teachers[1]},
						0, 12),
				new Subject(
						"Linear Algebra", 1, 2,
						new Teacher[]{teachers[3]},
						new Teacher[]{teachers[2], teachers[3]},
						40, 20)
		};
	}

	private class ClassRoom {
		int size;
		String name;

		public ClassRoom(String name, int size){
			this.name = name;
			this.size = size;
		}
	}

	private class Teacher {
		String fullname;
		int id;

		public Teacher(String fullname){
		this.fullname = fullname;
		this.id = ScheduleData.next_teacher_id++;
		}
	}

	private class Subject {
		int lectures_amount;
		int seminars_amount;
		Teacher[] possible_teachers_for_lectures;
		Teacher[] possible_teachers_for_seminars;
		int amount_of_students_on_seminars;
		int amount_of_students_on_lectures;
		String name;

		public Subject(
				String name,
				int lectures_amount,
				int seminars_amount,
				Teacher[] possible_teachers_for_lectures,
				Teacher[] possible_teachers_for_seminars,
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