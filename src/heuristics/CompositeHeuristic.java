package heuristics;

import java.util.ArrayList;
import java.util.Arrays;

import database.State;

public class CompositeHeuristic implements Heuristic {
	
	private State goal;
	
	public CompositeHeuristic(State goal) {
		/**
		 * Constructs a CompositeHeuristic heuristic and sets the goal state.
		 */
		this.goal = goal;
	}

	@Override
	public float eval(State src) {
		/**
		 * Returns the maximum between the misplaced tiles and manhattan distance heuristics.
		 */
		Heuristic misplaced = new MisplacedTiles(goal);
		Heuristic manhattan = new ManhattanDistance(goal);
		return Math.max(misplaced.eval(src), manhattan.eval(src));
	}
	
	@Override
	public String getSummary() {
		return "Max of Misplaced Tiles and Manhattan Distance heuristics";
	}
}
