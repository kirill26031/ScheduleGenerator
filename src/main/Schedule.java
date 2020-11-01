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

	private static int[] crossoverOrderArrays(int[] firstSpotGenes, int[] secondSpotGenes, int crossover_point) {
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

	private static int indexOf(int[] crossovered, int spot_second) {
		for(int i=0; i<crossovered.length; ++i) if(crossovered[i]==spot_second) return i;
		return -1;
	}

	@Override
	public int compareTo(Object o) {
		int compared =  ((Schedule)o).getFitness()-getFitness();
		return compared;
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

	public void mutateOrderByRoom(ArrayList<Lesson> lessons_of_speciality) {
		int first_index = (int)(Math.random()*lessons_of_speciality.size());
		int second_index = first_index;
		while(first_index==second_index) second_index = (int)(Math.random()*lessons_of_speciality.size());
		int second_room = lessons_of_speciality.get(second_index).classRoomId;
		lessons_of_speciality.get(second_index).classRoomId=lessons_of_speciality.get(first_index).classRoomId;
		lessons_of_speciality.get(first_index).classRoomId=second_room;
	}

	public void mutateOrderBySpot(ArrayList<Lesson> lessons_of_speciality) {
		int first_index = (int)(Math.random()*lessons_of_speciality.size());
		int second_index = first_index;
		while(first_index==second_index) second_index = (int)(Math.random()*lessons_of_speciality.size());
		int second_spot = lessons_of_speciality.get(second_index).classSpotId;
		lessons_of_speciality.get(second_index).classSpotId=lessons_of_speciality.get(first_index).classSpotId;
		lessons_of_speciality.get(first_index).classSpotId=second_spot;
	}

	public void mutateOrderByTeacher(ArrayList<Lesson> lessons_of_speciality) {
		int first_index = (int)(Math.random()*lessons_of_speciality.size());
		int subject_id = lessons_of_speciality.get(first_index).subjectId;
		ArrayList<Integer> indicies_of_this_subject = new ArrayList<>();
		for(int i=0; i<lessons_of_speciality.size(); ++i){
			if(lessons_of_speciality.get(i).subjectId==subject_id) indicies_of_this_subject.add(i);
		}
		if(indicies_of_this_subject.size()==1) return;
		int second_index = first_index;
		while(first_index==second_index) second_index = (int)(Math.random()*indicies_of_this_subject.size());
		int second_teacher_id = lessons_of_speciality.get(second_index).teacherId;
		lessons_of_speciality.get(second_index).teacherId = lessons_of_speciality.get(first_index).teacherId;
		lessons_of_speciality.get(first_index).teacherId = second_teacher_id;
	}

	public static ArrayList<Lesson> crossoverByTeachers(ArrayList<Lesson> first_lessons, ArrayList<Lesson> second_lessons) {
		return first_lessons;
	}

	public static ArrayList<Lesson> crossoverByRooms(ArrayList<Lesson> first_lessons, ArrayList<Lesson> second_lessons) {
		int crossover_point = (int)(Math.random()*(first_lessons.size()-1));
		int[] firstRoomGenes = new int[first_lessons.size()];
		int[] secondRoomGenes = new int[second_lessons.size()];
		for(int i = 0; i<first_lessons.size(); ++i) firstRoomGenes[i] = first_lessons.get(i).classRoomId;
		for(int i = 0; i<second_lessons.size(); ++i) secondRoomGenes[i] = second_lessons.get(i).classRoomId;
		int[] crossoveredRoomGenes = crossoverOrderArrays(firstRoomGenes, secondRoomGenes, crossover_point);
		ArrayList<Lesson> result = (ArrayList<Lesson>) first_lessons.clone();
		for(int i = 0; i< first_lessons.size(); ++i) result.get(i).classRoomId = crossoveredRoomGenes[i];
		return result;
	}

	public static ArrayList<Lesson> crossoverBySpots(ArrayList<Lesson> first_lessons, ArrayList<Lesson> second_lessons) {
		int crossover_point = (int)(Math.random()*(first_lessons.size()-1));
		int[] firstSpotGenes = new int[first_lessons.size()];
		int[] secondSpotGenes = new int[second_lessons.size()];
		for(int i = 0; i<first_lessons.size(); ++i) firstSpotGenes[i] = first_lessons.get(i).classSpotId;
		for(int i = 0; i<second_lessons.size(); ++i) secondSpotGenes[i] = second_lessons.get(i).classSpotId;
		int[] crossoveredSpotGenes = crossoverOrderArrays(firstSpotGenes, secondSpotGenes, crossover_point);
		ArrayList<Lesson> result = (ArrayList<Lesson>) first_lessons.clone();
		for(int i = 0; i< first_lessons.size(); ++i) result.get(i).classSpotId = crossoveredSpotGenes[i];
		return result;
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
			int subjectId,
			int specialityID,
			boolean isLecture
	){
		this.classSpotId = -1;
		this.classRoomId = -1;
		this.teacherId = -1;
		this.subjectId = subjectId;
		this.specialityID = specialityID;
		this.isLecture = isLecture;
	}

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
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\nНазва: ");
		Subject subject = Population.static_requirements.specialities[specialityID].subjects[subjectId];
		sb.append(subject.name);
		sb.append("\nТип: "+((isLecture) ? "Лекція" : "Семінар"));
//		sb.append("\nВикладач: ");
//		sb.append(Population.static_requirements.teachers[teacherId].fullname);
//		sb.append("\nКімната: ");
//		ClassRoom room = Population.static_requirements.classes[classRoomId];
//		sb.append(room.name);
//		sb.append("\n\tРозмір: "+room.size);
//		sb.append("\n\tНеобхідний розмір: "+
//				((isLecture)?subject.amount_of_students_on_lectures:subject.amount_of_students_on_seminars));
		return sb.toString();
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

	public int getAmountRequired() {
		Subject s = Population.static_requirements.specialities[specialityID].subjects[subjectId];
		return (isLecture) ? s.amount_of_students_on_lectures : s.amount_of_students_on_seminars;
	}
}