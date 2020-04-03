import java.util.ArrayList;

import control_strategies.AStar;
import control_strategies.ControlStrategy;
import database.Path;
import database.State;
import heuristics.CompositeHeuristic;
import heuristics.Heuristic;
import heuristics.ManhattanDistance;
import heuristics.MisplacedTiles;
import heuristics.NoHeuristic;

public class HeuristicAnalysis {
	public static void main(String[] args) {
		/**
		 * Generates a performance report for all heuristics by solving a set of
		 * randomly-generated puzzles and solving them using each of the heuristics
		 * and control strategies.
		 * 
		 * @param args  the command-line arguments to the program.
		 */
		
		ControlStrategy cs = new AStar();
		
		System.out.println("===== " + cs.getName() + " =====");
		System.out.println("   #        None   Misplaced   Manhattan   Composite");
		System.out.println("----  ----------  ----------  ----------  ----------");
		
		
		int numSamples = 100;
		for (int numMoves=1; numMoves<20; numMoves++) {
			
			int[] cumulativeDBSizes = {0,0,0,0};
			
			for (int i=0; i<numSamples; i++) {
				State goal = State.generateRandom();
				State initial = goal.shuffle(numMoves);
				
				ArrayList<Path> solutions = new ArrayList<>();
				ArrayList<Heuristic> heuristics = new ArrayList<>();
				heuristics.add(new NoHeuristic(goal));
				heuristics.add(new MisplacedTiles(goal));
				heuristics.add(new ManhattanDistance(goal));
				heuristics.add(new CompositeHeuristic(goal));
				
				
				for (int j=0; j<heuristics.size(); j++) {
					State.database.clear();
					Path solution = cs.exec(initial, goal, heuristics.get(j));
					cumulativeDBSizes[j] += State.database.size();
					solutions.add(solution);
				}
				
				// make sure all solutions are the same for each path.
				for (int k=1; k<heuristics.size(); k++) {
					assert(solutions.get(k).equals(solutions.get(k-1)));
				}
			}
			
			System.out.print(String.format("%4d, ", numMoves));
			//System.out.print("      ");
			for (int i=0; i<cumulativeDBSizes.length; i++) {
				System.out.print(String.format("%10.3f, ", cumulativeDBSizes[i]/(float)(numSamples)));
			}
			System.out.println();
		}
	}
}
