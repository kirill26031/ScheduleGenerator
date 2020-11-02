package main;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CSPGraph {
	private ArrayList<Vertex<LessonVertex>> vertices = new ArrayList<>();
	private ArrayList<Vertex<LessonVertex>> unused_vertices;
	private LinkedList<GraphState> steps = new LinkedList<>();
	private Vertex<LessonVertex> current = null;

	CSPGraph(ArrayList<Lesson>[] lessons_of_specialities) {
		for (ArrayList<Lesson> lessons_of_speciality : lessons_of_specialities) {
			for (Lesson aLessons_of_speciality : lessons_of_speciality) {
				vertices.add(new Vertex<>(getPossibleValues(aLessons_of_speciality)));
			}
		}
		unused_vertices = (ArrayList<Vertex<LessonVertex>>) vertices.clone();
		for(Vertex<LessonVertex> v : vertices){
			ArrayList<VertexRestriction<LessonVertex>> neighbours = new ArrayList<>();
			for(Vertex<LessonVertex> other_v : vertices){
				if(!v.equals(other_v)){
					if(v.possible_values.get(0).lesson.specialityID == other_v.possible_values.get(0).lesson.specialityID){
						neighbours.add(new VertexRestriction<>(other_v, 0));
					}
					else{
						neighbours.add(new VertexRestriction<>(other_v, 1));
					}
				}
			}
			v.setNeighbours(neighbours);
		}
	}

	private Vertex<LessonVertex> getStartVertex() {
		int max_relations = Integer.MIN_VALUE;
		Vertex<LessonVertex> best_vertex = null;
		for(Vertex<LessonVertex> v : unused_vertices){
			if(v.neighbours.size() > max_relations){
				max_relations = v.neighbours.size();
				best_vertex = v;
			}
		}
		return best_vertex;
	}

	ArrayList<Lesson> findNextSolution(){
		while (steps.size()!=vertices.size()){
//			System.out.println("Length of path: "+steps.size());
			Vertex<LessonVertex> next_vertex = getNextVertex();
			if(next_vertex==null) next_vertex = getStartVertex();
			if(next_vertex==null || next_vertex.possible_values.isEmpty()){
				if(steps.isEmpty()) return null;
//				System.out.println("Go back");
				goBack();
				continue;
			}
			else{
				current = next_vertex;
				steps.add(new GraphState(next_vertex, chooseValue(next_vertex)));
				unused_vertices.remove(next_vertex);
				forwardCheck(steps.getLast());
			}
		}
		return retrieveSolution();
	}

	private void forwardCheck(GraphState last) {
		GraphState current = getNextState(last.current_vertex, last.value);

		for (Map.Entry<Vertex<LessonVertex>, ArrayList<LessonVertex>> pair : current.neighbour_states.entrySet()) {
			pair.getKey().possible_values = pair.getValue();
		}
	}

	private LessonVertex chooseValue(Vertex<LessonVertex> next_vertex) {
		GraphState current_state = new GraphState(next_vertex);
		GraphState best_next_value = null;
		int min = Integer.MAX_VALUE;
		for(LessonVertex value : next_vertex.possible_values){
			GraphState possible_next_state = getNextState(next_vertex, value);
			if(min > sumPossibleValues(current_state.neighbour_states) - sumPossibleValues(possible_next_state.neighbour_states)){
				min = sumPossibleValues(current_state.neighbour_states) - sumPossibleValues(possible_next_state.neighbour_states);
				best_next_value = possible_next_state;
			}
		}
		return best_next_value!=null ? best_next_value.value : null;
	}

	private GraphState getNextState(Vertex<LessonVertex> next_vertex, LessonVertex lessonVertex) {
		HashMap<Vertex<LessonVertex>, ArrayList<LessonVertex>> neighbour_states = new HashMap<>(next_vertex.neighbours.size());
		for(VertexRestriction linked : next_vertex.neighbours){
			ArrayList<LessonVertex> possible_values = (ArrayList)linked.vertex.possible_values.clone();
			Stream<LessonVertex> stream = possible_values.stream();
			if(linked.restriction_type==0){
				possible_values = (ArrayList<LessonVertex>) stream
						.filter(lv -> !(lessonVertex.spot_id == lv.spot_id && lessonVertex.lesson.specialityID == lv.lesson.specialityID))
						.collect(Collectors.toList());
			}
			else if(linked.restriction_type==1){
				possible_values = (ArrayList<LessonVertex>) stream
						.filter(lv -> !(lv.spot_id == lessonVertex.spot_id &&
								(lv.teacher_id == lessonVertex.teacher_id) || lv.room_id == lessonVertex.room_id))
						.collect(Collectors.toList());
			}
			neighbour_states.put(linked.vertex, possible_values);
		}
		return new GraphState(next_vertex, lessonVertex, neighbour_states);
	}

	private int sumPossibleValues(HashMap<Vertex<LessonVertex>, ArrayList<LessonVertex>> neighbour_states) {
		int sum = 0;

		for (Map.Entry<Vertex<LessonVertex>, ArrayList<LessonVertex>> pair : neighbour_states.entrySet()) {
			sum += pair.getValue().size();
		}
		return sum;
	}

	private void goBack() {
		GraphState removed_state = steps.removeLast();
		removed_state.current_vertex.possible_values.remove(removed_state.value);
		unused_vertices.add(removed_state.current_vertex);
		for(VertexRestriction linked : removed_state.current_vertex.neighbours){
			linked.vertex.possible_values = removed_state.neighbour_states.get(removed_state.current_vertex);
		}

	}

	private Vertex<LessonVertex> getNextVertex() {
		if(current==null) return null;
		if(current.neighbours.isEmpty()) return null;
		int min = Integer.MAX_VALUE;
		Vertex<LessonVertex> best_vertex = null;
		for(VertexRestriction linked : current.neighbours){
			if(min > linked.vertex.possible_values.size() && unused_vertices.contains(linked.vertex)){
				min = linked.vertex.possible_values.size();
				best_vertex = linked.vertex;
			}
		}
		return best_vertex;
	}

	private ArrayList<Lesson> retrieveSolution() {
		ArrayList<Lesson> result = new ArrayList<>(vertices.size());
		for(GraphState state : steps){
			state.value.lesson.classSpotId = state.value.spot_id;
			state.value.lesson.teacherId = state.value.teacher_id;
			state.value.lesson.classRoomId = state.value.room_id;
			result.add(state.value.lesson);
		}
		return result;
	}

	private ArrayList<LessonVertex> getPossibleValues(Lesson lesson) {
		ArrayList<LessonVertex> all_values = new ArrayList<>(Main.requirements.spots.size());
		Subject subject = Main.requirements.specialities[lesson.specialityID].subjects[lesson.subjectId];
		for(int i=0; i<Main.requirements.spots.size(); ++i){
			for(int j=0; j<subject.getPossibleTeachers(lesson.isLecture).length; ++j){
				ArrayList<Integer> rooms = Main.requirements.getRooms(subject.getStudentsAmount(lesson.isLecture));
				for (Integer room : rooms) {
					all_values.add(
							new LessonVertex(lesson,
									Main.requirements.spots.get(i),
									subject.getPossibleTeachers(lesson.isLecture)[j],
									room));
				}
			}
		}
		return all_values;
	}

	private class LessonVertex implements Cloneable{
		Lesson lesson;
		int spot_id;
		int teacher_id;
		int room_id;
		LessonVertex(Lesson l, int spot_id, int teacher_id, int room_id){
			this.lesson=l;
			this.spot_id=spot_id;
			this.teacher_id = teacher_id;
			this.room_id = room_id;
		}

		@Override
		public String toString(){
			return "spot: "+spot_id+" , lesson: "+lesson.toString();
		}

		@Override
		public LessonVertex clone(){
			return new LessonVertex(lesson, spot_id, teacher_id, room_id);
		}

//		@Override
//		public boolean equals(Object o){
//			return ((LessonVertex)o).lesson.equals(lesson) &&
//					((LessonVertex)o).spot_id == spot_id &&
//					((LessonVertex)o).teacher_id == teacher_id &&
//					((LessonVertex)o).room_id == room_id;
//		}
	}

	class VertexRestriction<T extends Cloneable> {
		Vertex<T> vertex;
		int restriction_type;

		VertexRestriction(Vertex<T> vertex, int restriction_type){
			this.vertex=vertex;
			this.restriction_type = restriction_type;
		}

		@Override
		public String toString(){
			return "type: "+restriction_type+" , vertex: "+vertex.toString();
		}
	}

	private class GraphState {
		Vertex<LessonVertex> current_vertex;
		HashMap<Vertex<LessonVertex>, ArrayList<LessonVertex>> neighbour_states;
		LessonVertex value = null;

		GraphState(Vertex<LessonVertex> vertex){
			current_vertex = vertex;
			neighbour_states = new HashMap<>(vertex.neighbours.size());
			for(VertexRestriction linked : vertex.neighbours){
				neighbour_states.put(linked.vertex, linked.vertex.possible_values);
			}
		}

		GraphState(
				Vertex<LessonVertex> vertex,
				LessonVertex value
		){
			current_vertex = vertex;
			neighbour_states = new HashMap<>(vertex.neighbours.size());
			for(VertexRestriction linked : vertex.neighbours){
				neighbour_states.put(linked.vertex, linked.vertex.possible_values);
			}
			this.value = value;
		}

		GraphState(
				Vertex<LessonVertex> vertex,
				LessonVertex value,
				HashMap<Vertex<LessonVertex>, ArrayList<LessonVertex>> neighbour_states
		){
			current_vertex = vertex;
			this.neighbour_states = neighbour_states;
			this.value = value;
		}
	}
}

class Vertex<T extends Cloneable>{
	ArrayList<T> possible_values;
	ArrayList<CSPGraph.VertexRestriction<T>> neighbours = null;

	Vertex(ArrayList<T> possible_values){
		this.possible_values = possible_values;
	}

	void setNeighbours(ArrayList<CSPGraph.VertexRestriction<T>> neighbours){
		this.neighbours = neighbours;
	}

	@Override
	public String toString(){
		return  possible_values.get(0).toString()+" , neighbours: "+neighbours.size();
	}
}