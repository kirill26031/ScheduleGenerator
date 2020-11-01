package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;

public class CSPGraph {
	ArrayList<Vertex<LessonVertex>> vertices = new ArrayList<>();

	public CSPGraph(ArrayList<Lesson>[] lessons_of_specialities) {
		for (int i = 0; i < lessons_of_specialities.length; ++i) {
			for (int j = 0; j < lessons_of_specialities[i].size(); ++j) {
				vertices.add(new Vertex<LessonVertex>(getPossibleValues(lessons_of_specialities[i].get(j))));
			}
		}
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

	private ArrayList<LessonVertex> getPossibleValues(Lesson lesson) {
		ArrayList<LessonVertex> all_values = new ArrayList<>(Main.requirements.spots.size());
		for(int i=0; i<Main.requirements.spots.size(); ++i){
			all_values.add(new LessonVertex(lesson, i));
		}
		return all_values;
	}

	private class LessonVertex {
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
	}

	class VertexRestriction<T> {
		Vertex<T> vertex;
		int restriction_type;

		public VertexRestriction(Vertex<T> vertex, int restriction_type){
			this.vertex=vertex;
			this.restriction_type = restriction_type;
		}
	}
}

class Vertex<T>{
	T value=null;
	ArrayList<T> possible_values;
	ArrayList<CSPGraph.VertexRestriction<T>> neighbours = null;

	Vertex(ArrayList<T> possible_values){
		this.possible_values = possible_values;
	}

	void setNeighbours(ArrayList<CSPGraph.VertexRestriction<T>> neighbours){
		this.neighbours = neighbours;
	}

	void setValue(T value){
		this.value=value;
	}

	@Override
	public String toString(){
		return ((value!=null) ? "value: "+value.toString() : possible_values.toString())+" , neighbours: "+neighbours.toString();
	}
}