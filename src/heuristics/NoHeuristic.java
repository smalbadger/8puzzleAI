package heuristics;

import database.State;

public class NoHeuristic implements Heuristic {
	
	private State goal;
	
	public NoHeuristic(State goal) {
		/**
		 * Constructs a NoHeuristic heuristic and sets the goal state.
		 */
		this.goal = goal;
	}

	@Override
	public float eval(State src) {
		/**
		 * return 0.0
		 */
		float dist = 0.0f;
		return dist;
	}
	
	@Override
	public String getSummary() {
		return "No heuristic. normal breadth-first search.";
	}
}