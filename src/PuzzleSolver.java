import control_strategies.AStar;
import database.Path;
import io.Input;
import io.Output;

public class PuzzleSolver{
	
	public static void main(String[] args) {
		/**
		 * The entry point for the 8-puzzle program that handles all high-level steps:
		 * 	- Getting input INITIAL_STATE, GOAL_STATE, and HEURISTIC
		 * 	- Running the A* algorithm to solve the 8-puzzle.
		 * 	- Print the result.
		 * 
		 * @param args  the command-line arguments to the program.
		 */
		Input input = Input.getHardCoded();
		//Input input = Input.getFromUser();
		
		if (input.getInitial().canReach(input.getGoal()) ){
			Path path = new AStar().exec(input.getInitial(), input.getGoal(), input.getHeuristic());
			Output.printDone(path);
		}
		else {
			//Not solvable.
			Output.printFailure();
		}
		
	}
}