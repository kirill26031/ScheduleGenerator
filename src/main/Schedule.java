package main;

import java.util.ArrayList;


public class Schedule implements Cloneable, Comparable{
	ArrayList<Lesson>[] lessons_of_specialities;

	public Schedule(ArrayList<Lesson>[] lessons) {
		this.lessons_of_specialities = lessons;
	}


	public static Schedule getRandomSchedule(ScheduleRequirements requirements) {
		ArrayList<Lesson>[] lessons_of_specialities = new ArrayList[requirements.specialities.length];
		for(int i=0; i<lessons_of_specialities.length; ++i) lessons_of_specialities[i] = new ArrayList<>();
		for(int k=0; k<requirements.specialities.length; ++k){
			for(int i=0; i<requirements.specialities[k].subjects.length; ++i){
				Subject s = requirements.specialities[k].subjects[i];
				int classSpotId;
				int roomSpotId;
				int teacherId;
				for(int j=0; j<s.lectures_amount; ++j){
					classSpotId = getRandomFreeSpot(requirements, lessons_of_specialities, s.id, true);
					roomSpotId = getRandomFreeRoom(requirements, lessons_of_specialities, s.amount_of_students_on_lectures, classSpotId);
					teacherId = getRandomFreeTeacher(lessons_of_specialities[k], s.possible_teachers_for_lectures, classSpotId);
					lessons_of_specialities[k].add(new Lesson(classSpotId, roomSpotId, teacherId, s.id, k, true));
				}
				for(int j=0; j<s.seminars_amount; ++j){
					classSpotId = getRandomFreeSpot(requirements, lessons_of_specialities, s.id, false);
					roomSpotId = getRandomFreeRoom(requirements, lessons_of_specialities, s.amount_of_students_on_seminars, classSpotId);
					teacherId = getRandomFreeTeacher(lessons_of_specialities[k], s.possible_teachers_for_seminars, classSpotId);
					lessons_of_specialities[k].add(new Lesson(classSpotId, roomSpotId, teacherId, s.id, k,false));
				}
			}
		}

		return new Schedule(lessons_of_specialities);
	}

	private static int getRandomFreeTeacher(ArrayList<Lesson> lessons, Integer[] possible_teachers, int classSpotId) {
		ArrayList<Integer> freeTeachers = new ArrayList<Integer>(possible_teachers.length);
		for(int i=0; i<possible_teachers.length; ++i) freeTeachers.add(possible_teachers[i]);
		// now we care only about errors inside speciality
		for(Lesson l : lessons){
			if(l.classSpotId==classSpotId && freeTeachers.contains(l.teacherId)) freeTeachers.remove((Integer)l.teacherId);
		}
		if(freeTeachers.isEmpty()) return possible_teachers[(int)(Math.random()*possible_teachers.length)];
		return freeTeachers.get((int)(Math.random()*freeTeachers.size()));
	}

	private static int getRandomFreeRoom(ScheduleRequirements requirements, ArrayList<Lesson>[] lessons_of_specialities,
										 int amount_of_students, int classSpotId) {
		ArrayList<Integer> freeRooms = requirements.getRooms(amount_of_students);
		for(ArrayList<Lesson> lessons : lessons_of_specialities){
			for(Lesson l : lessons){
				if(l.classSpotId==classSpotId) freeRooms.remove((Integer)l.classRoomId);
			}
		}

		if(freeRooms.isEmpty()) return requirements.getRooms(amount_of_students).get(
				(int)(Math.random()*requirements.getRooms(amount_of_students).size())
		);
		return freeRooms.get((int)(Math.random()*freeRooms.size()));
	}

	private static int getRandomFreeSpot(ScheduleRequirements requirements, ArrayList<Lesson>[] lessons_of_specialities, int subject_id, boolean isLecture) {
		ArrayList<Integer> freeSpots = requirements.getSpots();
		for(ArrayList<Lesson> lessons : lessons_of_specialities){
			for(Lesson l : lessons) {
				if (l.subjectId == subject_id && l.isLecture == isLecture) freeSpots.remove((Integer) l.classSpotId);
			}
		}
		if(freeSpots.isEmpty()) return requirements.getSpots().get(
				(int)(Math.random()*requirements.getSpots().size())
		);
		return freeSpots.get((int)(Math.random()*freeSpots.size()));
	}

	private int fitnessCalculate() {
		int teacher_errors = 0;
		int room_spot_errors = 0;
		int spot_errors = 0;
		int room_size_errors = 0;
//		int students_errors = 0; //students spots errors
		ArrayList<Lesson> all_lessons = new ArrayList<>(lessons_of_specialities.length*lessons_of_specialities[0].size());
		for(ArrayList<Lesson> lessons : lessons_of_specialities){
			all_lessons.addAll(lessons);
		}
		for(int i = 0; i < all_lessons.size() - 1; i++)
		{
			for(int j = i+1; j < all_lessons.size(); j++)
			{
				if(all_lessons.get(i).classSpotId == all_lessons.get(j).classSpotId ){
					if(all_lessons.get(i).specialityID == all_lessons.get(j).specialityID ){
						spot_errors++;
					}
					if(all_lessons.get(i).teacherId == all_lessons.get(j).teacherId) teacher_errors++;
					if(all_lessons.get(i).classRoomId == all_lessons.get(j).classRoomId) room_spot_errors++;
				}
			}
		}
		for(int i=0; i<all_lessons.size(); ++i){
			Subject subject = Population.static_requirements.specialities[
					all_lessons.get(i).specialityID
					].subjects[
							all_lessons.get(i).subjectId
					];
			int required_size = (all_lessons.get(i).isLecture) ?
					subject.amount_of_students_on_lectures :
					subject.amount_of_students_on_seminars;
			if(Population.static_requirements.classes[all_lessons.get(i).classRoomId].size<required_size){
				room_size_errors++;
			}
		}
		return -1*(teacher_errors+room_spot_errors+room_size_errors+spot_errors);
	}

	int getFitness(){return fitnessCalculate();}

	@Override
	protected Schedule clone(){
		ArrayList<Lesson>[] clonned_specialities = new ArrayList[lessons_of_specialities.length];
		for(int i=0; i<lessons_of_specialities.length; ++i) {
			ArrayList<Lesson> lessons = lessons_of_specialities[i];
			ArrayList<Lesson> new_lessons = new ArrayList<>(lessons.size());
			for (Lesson l : lessons) {
				new_lessons.add(l.clone());
			}
			clonned_specialities[i] = new_lessons;
		}
		return new Schedule(clonned_specialities);
	}

	public void crossoverBySpots(Schedule second) {
//		int crossover_point = (int)(Math.random()*(second.lessons_of_specialities.size()-1));
//		int[] firstSpotGenes = new int[lessons_of_specialities.size()];
//		int[] secondSpotGenes = new int[second.lessons_of_specialities.size()];
//		for(int i = 0; i< lessons_of_specialities.size(); ++i) firstSpotGenes[i] = lessons_of_specialities.get(i).classSpotId;
//		for(int i = 0; i<second.lessons_of_specialities.size(); ++i) secondSpotGenes[i] = second.lessons_of_specialities.get(i).classSpotId;
//		int[] crossoveredSpotGenes = crossoverOrder(firstSpotGenes, secondSpotGenes, crossover_point);
//		for(int i = 0; i< lessons_of_specialities.size(); ++i) lessons_of_specialities.get(i).classSpotId = crossoveredSpotGenes[i];
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

	@Override
	public int compareTo(Object o) {
		int compared =  ((Schedule)o).getFitness()-getFitness();
		if(compared==0) {
			return equals(o) ? 0 : 1;
		}
		else return compared;
	}

	@Override
	public boolean equals(Object o){
		for(int i=0; i<lessons_of_specialities.length; ++i){
			for(int j=0; j<lessons_of_specialities[i].size(); ++j){
				if(!lessons_of_specialities[i].get(j).equals(((Schedule)o).lessons_of_specialities[i].get(j))) return false;
			}
		}
		return true;
	}
}

class Lesson implements Cloneable{
	int classSpotId;
	int classRoomId;
	int teacherId;
	int subjectId;
	int specialityID;
	boolean isLecture;

	public Lesson(
			int classSpotId,
			int classRoomId,
			int teacherId,
			int subjectId,
			int specialityID,
			boolean isLecture
	){
		this.classSpotId = classSpotId;
		this.classRoomId = classRoomId;
		this.teacherId = teacherId;
		this.subjectId = subjectId;
		this.specialityID = specialityID;
		this.isLecture = isLecture;
	}

	@Override
	protected Lesson clone(){
		return new Lesson(classSpotId, classRoomId, teacherId, subjectId, specialityID, isLecture);
	}

	@Override
	public boolean equals(Object o){
		Lesson l = (Lesson)o;
		return classSpotId==l.classSpotId &&
				classRoomId==l.classRoomId &&
				teacherId==l.teacherId &&
				subjectId==l.subjectId &&
				specialityID==l.specialityID &&
				isLecture==l.isLecture;
	}
}