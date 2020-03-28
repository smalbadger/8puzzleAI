package heuristics;

import database.State;

public interface Heuristic {
	/**
	 * The interface used by all heuristics
	 */
	default public float eval(State src) {
		/**
		 * Estimates how many moves it would take to move from the src state to the goal state.
		 * This always gives an underestimate to ensure optimality with the A* algorithm.
		 * 
		 * @param src  The state to estimate the number of moves from the goal.
		 * @return an underestimate of the number of moves it would take to reach the goal state from the src state.
		 */
		return .0f;
	}
	default public String getSummary() {
		/**
		 * @return a summary of the heuristic
		 */
		return "No summary given";
	}
}