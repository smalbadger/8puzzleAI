package control_strategies;

import database.Path;
import database.State;
import heuristics.Heuristic;

public abstract class ControlStrategy {
	public Path exec(State state, State state2, Heuristic heuristic) {
		return null;
	}
	
	public String getName() {
		return "";
	}
}
