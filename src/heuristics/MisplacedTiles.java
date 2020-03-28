package heuristics;

import database.State;

public class MisplacedTiles implements Heuristic {
	
	private State goal;
	
	public MisplacedTiles(State goal) {
		/**
		 * Constructs a MisplacedTiles heuristic and sets the goal state.
		 */
		this.goal = goal;
	}

	@Override
	public float eval(State src) {
		/**
		 * Gets the number of non-blank tiles that are out of place.
		 */
		float numMisplaced = 0.0f;
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if (src.getAt(i, j) != '-' && src.getAt(i,j) != this.goal.getAt(i,j)) {
					numMisplaced += 1;
				}
			}
		}
		
		return numMisplaced;
	}
	
	@Override
	public String getSummary() {
		return "Number of non-blank misplaced tiles";
	}
}