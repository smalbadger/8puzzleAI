package control_strategies;

import java.util.ArrayList;
import java.util.HashMap;

import database.State;
import database.Path;
import heuristics.Heuristic;
import io.Output;

public class AStar extends ControlStrategy{
	
	public Path exec(State state, State state2, Heuristic heuristic) {
		/**
		 * FROM MODULE 4 (PAGE 13 CLASS NOTES)
		 * 
		 * A* Algorithm
		 * 
		 *	1. Form a one-element queue consisting of a zero-length path that contains 
		 *	   on a root node.
		 *
		 *	2. Until the first path in the queue terminates at the goal node or the
		 *	   queue is empty,
		 *		
		 *		2a. Remove the first path from the queue; create new paths by extending
		 *		    the first path to all the neighbors of the terminal node.
		 *		
		 *		2b. Reject all new paths with loops
		 *
		 *		2c. If two or more paths reach a common node, delete all those paths
		 *		    except the one that reaches the common node with minimum cost.
		 *		
		 *		2d. Sort the entire queue by the sum of the path length and a lower-bound
		 *		    estimate of the cost remaining, with least cost paths in front.
		 *
		 *	3. If the goal node is found, announce success; otherwise, announce failure.
		 *     (we return the path on success - null on failure)
		 *     
		 * @param initial    the state we start with
		 * @param goal       the state we're trying to reach
		 * @param heuristic  the heuristic used to solve the 8-puzzle more efficiently
		 * 
		 * @return the optimal path to solve the puzzle. If no solution exists, null is returned.
		 */
		
		// 1.
		ArrayList<Path> paths = new ArrayList<>();
		paths.add(new Path(state));
		
		// 2.
		while (!paths.isEmpty() && !(paths.get(0).terminalState().equals(state2))) {
			
			// DEBUG
			//Output.printPathQueue(paths, heuristic);
			
			// 2a.
			Path best = paths.remove(0);
			ArrayList<State> expansions = best.terminalState().getNeighbors();
			
			// DEBUG
			//Output.printChosenPath(best);
			
			for (State s : expansions) {
				
				// 2b.
				if (!best.contains(s)) {
					Path expandedPath = best.copy();
					expandedPath.add(s);
					paths.add(expandedPath);
				}
			}
			
			
			// 2c.
			HashMap<State, Path> bestPaths = new HashMap<>();
			for (Path p : paths) {
				State terminal = p.terminalState();
				boolean keyExists = bestPaths.containsKey(terminal);
				if ((keyExists && p.estimate(heuristic) < bestPaths.get(terminal).estimate(heuristic)) || !keyExists) {
					bestPaths.put(terminal, p);
				}
			}
			paths = new ArrayList<>(bestPaths.values());
			
			// 2d.
			paths.sort((a, b) -> a.compareTo(b, heuristic));
		}
		
		// 3
		if (!paths.isEmpty()) {
			return paths.get(0);
		}
		return null;
	}
	
	public String getName() {
		return "A* ALGORITHM";
	}
}
