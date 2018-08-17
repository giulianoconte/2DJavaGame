/**
 * Code taken from https://github.com/Satshabad/Simple-Graph-Implementation
 */

package engine.navigation;

import java.util.ArrayList;


public class Edge<V> {

	private V vertex;
	
	private float weight;
	
	public Edge(V vert, float w) {
		vertex = vert;
		weight = w;
	}

	public V getVertex() {
		return vertex;
	}

	public void setVertex(V vertex) {
		this.vertex = vertex;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public String toString(){
		
		return "( "+ vertex + ", " + weight + " )";
	}

}