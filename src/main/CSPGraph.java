package main;

import java.util.*;
import java.util.function.BiConsumer;

public class CSPGraph {
	ArrayList<Vertex<LessonVertex>> vertices = new ArrayList<>();
	ArrayList<Vertex<LessonVertex>> unused_vertices = null;
	LinkedList<GraphState> steps = new LinkedList();
	Vertex<LessonVertex> current = null;

	public CSPGraph(ArrayList<Lesson>[] lessons_of_specialities) {
		for (int i = 0; i < lessons_of_specialities.length; ++i) {
			for (int j = 0; j < lessons_of_specialities[i].size(); ++j) {
				vertices.add(new Vertex<LessonVertex>(getPossibleValues(lessons_of_specialities[i].get(j))));
			}
		}
		unused_vertices = (ArrayList<Vertex<LessonVertex>>) vertices.clone();
		for(Vertex<LessonVertex> v : vertices){
			ArrayList<VertexRestriction<LessonVertex>> neighbours = new ArrayList<>();
			for(Vertex<LessonVertex> other_v : vertices){
				if(v.possible_values.get(0).lesson.specialityID == other_v.possible_values.get(0).lesson.specialityID &&
					!v.equals(other_v)){
					neighbours.add(new VertexRestriction(other_v, 0));
				}
			}
			v.setNeighbours(neighbours);
		}
	}

	private Vertex<LessonVertex> getStartVertex() {
		int max_relations = unused_vertices.get(0).neighbours.size();
		Vertex<LessonVertex> best_vertex = unused_vertices.get(0);
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
			Vertex<LessonVertex> next_vertex = getNextVertex();
			if(next_vertex==null){
				next_vertex = getStartVertex();
			}
			if(next_vertex.possible_values.isEmpty()){
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
		Iterator<Map.Entry<Vertex<LessonVertex>, ArrayList<LessonVertex>>> it = current.neighbour_states.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Vertex<LessonVertex>, ArrayList<LessonVertex>> pair = it.next();
			pair.getKey().possible_values=pair.getValue();
		}
	}

	private LessonVertex chooseValue(Vertex<LessonVertex> next_vertex) {
		GraphState current_state = new GraphState(next_vertex);
		GraphState best_next_value = getNextState(next_vertex, next_vertex.possible_values.get(0));
		int min = sumPossibleValues(current_state.neighbour_states) - sumPossibleValues(best_next_value.neighbour_states);
		for(LessonVertex value : next_vertex.possible_values){
			GraphState possible_next_state = getNextState(next_vertex, value);
			if(min > sumPossibleValues(current_state.neighbour_states) - sumPossibleValues(possible_next_state.neighbour_states)){
				min = sumPossibleValues(current_state.neighbour_states) - sumPossibleValues(possible_next_state.neighbour_states);
				best_next_value = possible_next_state;
			}
		}
		return best_next_value.value;
	}

	private GraphState getNextState(Vertex<LessonVertex> next_vertex, LessonVertex lessonVertex) {
		HashMap<Vertex<LessonVertex>, ArrayList<LessonVertex>> neighbour_states = new HashMap<>(next_vertex.neighbours.size());
		for(VertexRestriction linked : next_vertex.neighbours){
			if(linked.restriction_type==0){
				ArrayList<LessonVertex> possible_values = (ArrayList<LessonVertex>) linked.vertex.possible_values.clone();
				remove(possible_values, lessonVertex);
				neighbour_states.put(linked.vertex, possible_values);
			}
		}
		return new GraphState(next_vertex, lessonVertex, neighbour_states);
	}

	private boolean remove(ArrayList<LessonVertex> possible_values, LessonVertex lessonVertex) {
		for(int i=0; i<possible_values.size(); ++i){
			if(possible_values.get(i).spot_id==lessonVertex.spot_id) {
				possible_values.remove(i);
				return true;
			}
		}
		return false;
	}

	private int sumPossibleValues(HashMap<Vertex<LessonVertex>, ArrayList<LessonVertex>> neighbour_states) {
		int sum = 0;
		Iterator<Map.Entry<Vertex<LessonVertex>, ArrayList<LessonVertex>>> it = neighbour_states.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Vertex<LessonVertex>, ArrayList<LessonVertex>> pair = it.next();
			sum+=pair.getValue().size();
		}
		return sum;
	}

	private void goBack() {
		GraphState removed_state = steps.removeLast();
		unused_vertices.add(removed_state.current_vertex);
		for(VertexRestriction linked : removed_state.current_vertex.neighbours){
			linked.vertex.possible_values = removed_state.neighbour_states.get(removed_state.current_vertex);
		}
	}

	private Vertex<LessonVertex> getNextVertex() {
		if(current==null) return null;
		if(current.neighbours.isEmpty()) return null;
		int min =1000;
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
			result.add(state.value.lesson);
		}
		return result;
	}

	private ArrayList<LessonVertex> getPossibleValues(Lesson lesson) {
		ArrayList<LessonVertex> all_values = new ArrayList<>(Main.requirements.spots.size());
		for(int i=0; i<Main.requirements.spots.size(); ++i){
			all_values.add(new LessonVertex(lesson, i));
		}
		return all_values;
	}

	private class LessonVertex implements Cloneable{
		Lesson lesson;
		int spot_id;
		public LessonVertex(Lesson l, int spot_id){
			this.lesson=l;
			this.spot_id=spot_id;
		}

		@Override
		public String toString(){
			return "spot: "+spot_id+" , lesson: "+lesson.toString();
		}

		@Override
		public LessonVertex clone(){
			return new LessonVertex(lesson, spot_id);
		}
	}

	class VertexRestriction<T extends Cloneable> {
		Vertex<T> vertex;
		int restriction_type;

		public VertexRestriction(Vertex<T> vertex, int restriction_type){
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

		public GraphState(Vertex<LessonVertex> vertex){
			current_vertex = vertex;
			neighbour_states = new HashMap<>(vertex.neighbours.size());
			for(VertexRestriction linked : vertex.neighbours){
				neighbour_states.put(linked.vertex, linked.vertex.possible_values);
			}
		}

		public GraphState(
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

		public GraphState(
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
		return  possible_values.get(0).toString()+" , neighbours: "+neighbours.toString();
	}
}