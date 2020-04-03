package heuristics;

import database.State;

public class ManhattanDistance implements Heuristic {
	
	private State goal;
	
	public ManhattanDistance(State goal) {
		/**
		 * Constructs a Manhattan Distance heuristic and sets the goal state.
		 */
		this.goal = goal;
	}

	@Override
	public float eval(State src) {
		/**
		 * For each non-blank tile, determine the manhattan distance of where it is in the src and goal states.
		 * Add all manhattan distances together.
		 */
		float dist = 0.0f;
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				char find = src.getAt(i, j);
				
				if (find == '-')
					continue;
				
				int goalRow = goal.getLocation(find).get("row") - 1;
				int goalCol = goal.getLocation(find).get("column") - 1;
				
				dist += Math.abs(i-goalRow) + Math.abs(j-goalCol);
			}
		}
		
		return dist;
	}
	
	@Override
	public String getSummary() {
		return "Manhattan Distance of all non-blank tiles";
	}
}