package io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import database.Path;
import database.State;
import operators.Operator;
import heuristics.Heuristic;

public class Output {
	
	public static void printQueryForInitialState() {
		/**
		 * Ask the user to enter the initial state.
		 */
		System.out.println("Enter the initial state");
	}
	
	public static void printQueryForGoalState() {
		/**
		 * Ask the user to enter the goal state.
		 */
		System.out.println("Enter the goal state");
	}
	
	public static void printQueryForHeuristic(Map<String, Heuristic> heuristicMapping) {
		/**
		 * Ask the user to chose the heuristic to use.
		 * 
		 * @param heuristicMapping  maps the user options to heuristics ("a" -> MisplacedTiles, etc.)
		 */
		System.out.println("Select the heuristic");
		
		ArrayList<String> keys = new ArrayList<>(heuristicMapping.keySet().size());
		keys.addAll(heuristicMapping.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			System.out.printf("%3s) %s\n", key, heuristicMapping.get(key).getSummary());
		}
	}
	
	public static void printIllegalChar(char illegalChar) {
		/**
		 * Let the user know that an invalid character was read in while getting a state from the user.
		 * 
		 * @param illegalChar  the character that was detected to be illegal.
		 */
		System.out.printf("Illegal Character '%c'\n", illegalChar);
		System.out.println("The character was not a digit in the range 1-8, not a dash, or was a duplicate character.\n");
	}
	
	public static void printStartOverState() {
		/**
		 * Tell the user to re-enter the state starting from the beginning because of an error.
		 */
		System.out.println("Because of the error above, please enter the state again from the beginning.");
	}
	
	public static void printIllegalHeuristicOption(String option) {
		/**
		 * Tell the user that the selected heuristic was not a valid option.
		 * 
		 * @param option  the option of the heuristic to use (a, b, c, etc.)
		 */
		System.out.printf("'%s' is not a valid heuristic option.\n", option);
	}
	
	public static void printDone(Path path) {
		/**
		 * Tell the user that the program is done running. The following cases can happen.
		 * 
		 * - If the path is null, the puzzle was not solved.
		 * - If the path is not null, print out the moves in the path and tell how many states were explored.
		 * 
		 * @param path  the optimal path to solve the puzzle (null if un-solvable)
		 */
		if (path == null) {
			Output.printFailure();
		}
		else {
			Output.printSuccess(path);
		}
		System.out.println();
	}
	
	private static void printSuccess(Path optimalPath) {
		/**
		 * Tell the user all the moves of the solvable-puzzle as well as the number of states explored.
		 * 
		 * @param optimalPath  The path that solves the puzzle in the fewest number of steps.
		 */
		System.out.println("Solution:\n");
		
		for (Operator o : optimalPath.getOperators()) {
			System.out.println("Move blank " + o.toString().toLowerCase());
		}
		System.out.println();
		
		System.out.printf("Given the selected heuristic, the solution required %d moves.\n", optimalPath.getOperators().size());
		System.out.printf("The A* explored %d number of nodes to find this solution.", State.database.size());
	}
	
	public static void printFailure() {
		/**
		 * Tell the user that the puzzle is not solvable.
		 */
		System.out.println("For the above combination of the initial/goal states, there is no solution.");
	}
	
	public static void printPathQueue(ArrayList<Path> paths, Heuristic heuristic) {
		/**
		 * NOTE: This method is used for debugging.
		 * Prints all paths that are in the paths array. The heuristic is used to show estimates for each path.
		 * 
		 * @param paths      All paths currently being considered for expansion.
		 * @param heuristic  The heuristic being used to efficiently solve the puzzle.
		 */
		System.out.println("====================================");
		System.out.println("DEBUG: Paths in queue");
		System.out.println("====================================");
		for (Path p : paths) {
			System.out.printf("HEUR:%6.3f  EST:%6.3f %s\n", heuristic.eval(p.terminalState()), p.estimate(heuristic), p.toString());
		}
		System.out.println();
	}
	
	public static void printChosenPath(Path p) {
		/**
		 * Print the path chosen for expansion.
		 * 
		 * @param p  the path chosen for expansion
		 */
		System.out.println("++++++++++++++++++++++++++++++++++++");
		System.out.println("DEBUG: Chosen Path");
		System.out.println("++++++++++++++++++++++++++++++++++++");
		System.out.println(p.toString());
		System.out.println();
	}
}