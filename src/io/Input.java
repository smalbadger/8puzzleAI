package io;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import database.State;
import heuristics.Heuristic;
import heuristics.NoHeuristic;
import heuristics.CompositeHeuristic;
import heuristics.ManhattanDistance;
import heuristics.MisplacedTiles;
import heuristics.NoHeuristic;

public class Input {
	/**
	 * This class handles all input operations via static methods.
	 * An instance of the Input class holds the initial state, goal state, and heuristic.
	 */
	
	
	// STATIC
	private static Scanner userInput;   // Used to read from console
	
	
	// INSTANCE
	private State initial, goal;		// The initial and goal states for the puzzle
	private Heuristic heuristic;		
	
	private Input(State initial, State goal, Heuristic heuristic) {
		/**
		 * An object carrying all necessary information from the user.
		 * 
		 * @param initial    the initial state to start the puzzle with.
		 * @param goal       the goal state we're trying to get to.
		 * @param heuristic  the heuristic used to solve the 8-puzzle more efficiently.
		 */
		this.initial = initial;
		this.goal = goal;
		this.heuristic = heuristic;
	}
	
	public State getInitial() {
		/**
		 * @return the initial state.
		 */
		return this.initial;
	}
	
	public State getGoal() {
		/**
		 * @return the goal state
		 */
		return this.goal;
	}
	
	public Heuristic getHeuristic() {
		/**
		 * @return the heuristic
		 */
		return this.heuristic;
	}
	
	static public Input getHardCoded() {
		/**
		 * A helper method for testing the program so we don't have to enter
		 * things every time we run.
		 * 
		 * Hardcodes the initial and goal states as well as the heuristic.
		 * 
		 * @return the hard-coded input
		 */
		char [][] initial_config = {
				{'5', '3', '6'},
				{'2', '1', '8'},
				{'4', '7', '-'}
		};
		
		char [][] goal_config = {
				{'1', '2', '3'},
				{'4', '5', '6'},
				{'7', '8', '-'}
		};
		
		State initial = new State(initial_config);
		State goal    = new State(goal_config);
		//Heuristic heuristic = new NoHeuristic(goal);
		//Heuristic heuristic = new MisplacedTiles(goal);
		Heuristic heuristic = new ManhattanDistance(goal);
		//Heuristic heuristic = new CompositeHeuristic(goal);
		
		return new Input(initial, goal, heuristic);
	}
	
	static public Input getFromUser() {
		/**
		 * Read the initial state, goal state, and heuristic from the user
		 * and return an Input object carrying the information.
		 * 
		 * @return the input read in from the user.
		 */
		
		userInput = new Scanner(System.in);
		
		// get initial state
		Output.printQueryForInitialState();
		State initial = Input.readState();
		
		// get goal state
		Output.printQueryForGoalState();
		State goal = Input.readState();
		
		// get heuristic
		HashMap<String, Heuristic> heuristicMapping = new HashMap<>();
		heuristicMapping.put("a", new MisplacedTiles(goal));
		heuristicMapping.put("b", new ManhattanDistance(goal));
		heuristicMapping.put("c", new CompositeHeuristic(goal));
		heuristicMapping.put("d", new NoHeuristic(goal));
		Heuristic heuristic = heuristicMapping.get(Input.readHeuristicOption(heuristicMapping));
		
		userInput.close();
		
		return new Input(initial, goal, heuristic);
	}
	
	static private State readState() {
		/**
		 * Reads a single state from the console.
		 * 
		 * The state must have a all numbers 1-8 and a hyphen, but no other characters.
		 * Error checking is performed to make sure that the puzzle contains all the correct
		 * information. If something was not entered right, the user will be asked to start over.
		 * 
		 * NOTE: all whitespace is ignored.
		 * 
		 * @return the state that was read from the user.
		 */
		HashSet<Character> validNums = new HashSet<>(Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8'));
		
		char[] b = new char[9];	// create empty flattened board.
		int foundNums = 0; // not to exceed 8
		int foundDashes = 0; // not to exceed 1
		boolean done = false;
		
		while (!done) {
			String line = userInput.nextLine();
			
			for (int i=0; i<line.length(); i++) {
				char cur = line.charAt(i);
				
				if (validNums.contains(cur)) {
					foundNums += 1;
					validNums.remove(cur);
				}
				else if (cur == '-' && foundDashes == 0) {
					foundDashes += 1;
				}
				else if (Character.isWhitespace(cur)) {
					continue;
				}
				else {
					Output.printIllegalChar(cur);
					Output.printStartOverState();
					return readState();
				}
				
				b[foundNums + foundDashes - 1] = cur;
				
				if (foundNums == 8 && foundDashes == 1) {
					done = true;
					break;
				}
			}
		}
		
		// convert the board to be 3x3
		char[][] board = {{b[0], b[1], b[2]}, {b[3], b[4], b[5]}, {b[6], b[7], b[8]}};
		return new State(board);
	}
	
	static private String readHeuristicOption(HashMap<String, Heuristic> heuristicMapping) {
		/**
		 * Reads the heuristic option. If an invalid option is chosen, the user will be prompted again.
		 * 
		 * @param heuristicMapping  maps the user options to heuristics ("a" -> MisplacedTiles, etc.)
		 * @return the option of the heuristic chosen (a, b, c, etc.)
		 */
		String read = "";
		
		while (!heuristicMapping.containsKey(read)) {
			Output.printQueryForHeuristic(heuristicMapping);
			read = userInput.nextLine().trim();
			
			if (!heuristicMapping.containsKey(read)) {
				Output.printIllegalHeuristicOption(read);
			}
		}
		
		return read;
	}
	
}
