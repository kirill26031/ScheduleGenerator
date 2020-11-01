package main;

import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;

public class CSPGraph {
//	Lesson[] nodes;
//	ArrayList<Lesson> current_nodes;
	// nodes grouped by specialities
	ArrayList<Lesson>[] grouped_nodes;
	ArrayList<Lesson>[] current_grouped_nodes;
	ArrayList<Pair<Lesson, Lesson>> connections;
	boolean finished = false;
	int max_spot_id;

	public CSPGraph(ArrayList<Lesson>[] lessons_of_specialities) {
		this.grouped_nodes = lessons_of_specialities;
		this.connections = new ArrayList<>();
		this.max_spot_id = Main.requirements.spots.size()-1;
	}

	public ArrayList<Lesson> solve(){
		current_grouped_nodes = new ArrayList[grouped_nodes.length];
		for(int j=0; j<grouped_nodes[0].size(); ++j) {
			grouped_nodes[0].get(j).classSpotId = j;
		}
		for(int i=0; i<current_grouped_nodes.length; ++i){
			current_grouped_nodes[i] = (ArrayList<Lesson>) grouped_nodes[i].clone();
		}

		while(!finished){
//			current_grouped_nodes = nextSolution();

		}

//		for(int i=1; i<current_grouped_nodes.length && !finished; ++i){
//			last_spot_for_lesson[i] = new int[current_grouped_nodes[i].size()];
//			for(int j=0; j<current_grouped_nodes[i].size() && !finished; ++j){
//				for(int k=last_spot_for_lesson[i][j]; k<=max_spot_id; ++k){
//
//				}
//				if(i==current_grouped_nodes.length-1 && j==current_grouped_nodes[i].size()-1){
//					finished=true;
//				}
//			}
//		}
		return collectAll(current_grouped_nodes);
	}

	private ArrayList<Lesson> collectAll(ArrayList<Lesson>[] current_grouped_nodes) {
	return null;
	}


}

class Vertex<T>{
	T value=null;
	ArrayList<T> possible_values;
	ArrayList<Vertex<T>> neighbours = null;

	Vertex(ArrayList<T> possible_values){
		this.possible_values = possible_values;
	}

	void setNeighbours(ArrayList<Vertex<T>> neighbours){
		this.neighbours = neighbours;
	}

	void setValue(T value){
		this.value=value;
	}

}