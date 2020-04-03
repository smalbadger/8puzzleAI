import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.lang.Math;

////////////////////////////////////////////////////////////////////////////////////////////
// CONTROL STRATEGY
////////////////////////////////////////////////////////////////////////////////////////////

class hw03_smalbadger_sfarris{
	
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
		Path path = aStar(input.getInitial(), input.getGoal(), input.getHeuristic());
		Output.printDone(path);
	}
	
	private static Path aStar(State initial, State goal, Heuristic heuristic) {
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
		paths.add(new Path(initial));
		
		// 2.
		while (!paths.isEmpty() && !(paths.get(0).terminalState().equals(goal))) {
			
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
}


////////////////////////////////////////////////////////////////////////////////////////////
// I/O DEVICES
////////////////////////////////////////////////////////////////////////////////////////////

class Input {
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
		//Heuristic heuristic = new MisplacedTiles(goal);
		//Heuristic heuristic = new ManhattanDistance(goal);
		Heuristic heuristic = new CustomHeuristic(goal);
		
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
		heuristicMapping.put("c", new CustomHeuristic(goal));
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

class Output {
	
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
	
	private static void printFailure() {
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


////////////////////////////////////////////////////////////////////////////////////////////
// DATABASE
////////////////////////////////////////////////////////////////////////////////////////////

class State {
	
	private char[][] board = new char[3][3];
	
	// stores all states found so far.
	final static HashSet<State> database = new HashSet<>();
	
	public State(char[][] configuration) {
		/**
		 * Creates a State from a 3x3 grid of characters.
		 */
		this.board = configuration;
	}
	
	public HashSet<Operator> getOperators() {
		/**
		 * Gets a set of all possible operators that can be used on the current state.
		 * 
		 * @return all operators that can be used on this state.
		 */
		HashSet<Operator> ops = new HashSet<Operator>();
		
		HashMap<String, Integer> pos = this.getLocation('-');
		int row = pos.get("row");
		int col = pos.get("column");
		
		if (row > 1) 
			ops.add(Operator.UP);
		if (row < 3)
			ops.add(Operator.DOWN);
		if (col > 1)
			ops.add(Operator.LEFT);
		if (col < 3)
			ops.add(Operator.RIGHT);
		
		return ops;
	}
	
	public Operator getOperator(State result) {
		/**
		 * Gets the operator that would transform the current state into the resultant state.
		 * If no operator transforms the current state into the result state, null is returned.
		 * 
		 * @param result  the state to transform into.
		 * @return the operator used to transform the current state into the result state.
		 */
		for (Operator o : this.getOperators()) {
			if (this.transform(o).equals(result)) {
				return o;
			}
		}
		return null;
	}
	
	public HashMap<String, Integer> getLocation(char search){
		/**
		 * Gets the location of the search character in the state. The location is returned
		 * as a map. The keys in the map are "row" and "column" and the values are between
		 * 1 and 3 (inclusive).
		 * 
		 * @param search The character to search for
		 * @return A map containing the location.
		 */
		HashMap<String, Integer> loc = new HashMap<String, Integer>();
		
		for (int i=1; i<=3; i++) {
			for (int j=1; j<=3; j++) {
				if (this.board[i-1][j-1] == search){
					loc.put("row", i);
					loc.put("column", j);
					return loc;
				}
			}
		}
		return null;
	}
	
	public char getAt(int row, int col) {
		/**
		 * Get the character at the location (row, col)
		 * 
		 * @param row  the index of the row to search at in the range 0-2
		 * @param col  the index of the column to search at in the range 0-2
		 * @return the character at the location (row, col)
		 */
		return this.board[row][col];
	}
	
	public boolean equals(Object obj) {
		/**
		 * Allows us to compare 2 states for equality.
		 * States are equal if they have the same board.
		 * 
		 * @param obj  the state to compare with the current state
		 * @return true if the states are equal and false otherwise.
		 */
		if (obj == this)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false; 
		
		State s = (State) obj;
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if (this.getAt(i, j) != s.getAt(i, j)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override 
	public int hashCode() { 
		/**
		 * Allows us to hash a state.
		 * To get the hashcode, we append all numbers in the state together and 
		 * replace the blank with 0, then parse into an integer.
		 * 
		 * @return the hashed state
		 */
		String result = "";
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				result += Character.toString(this.getAt(i, j));
			}
		}
		result = result.replace('-', '0');
		return Integer.parseInt(result); 
	}
	
	public ArrayList<State> getNeighbors() {
		/**
		 * @return the neighboring states by applying all possible operators.
		 */
		ArrayList<State> unexplored = new ArrayList<State>();
		
		for (Operator o : this.getOperators()) {
			State neighbor = this.transform(o);
			if (neighbor != null) {
				unexplored.add(neighbor);
				State.database.add(neighbor);
			}
		}
		
		return unexplored;
	}
	
	public State transform(Operator o) {
		/**
		 * Apply the operator on this state to get a new state.
		 * If the operator is not valid, return null.
		 * 
		 * @param o  The operator to apply to the current state.
		 * @return the new state after the operator is applied.
		 */
		
		if (!this.getOperators().contains(o))
			return null;
		
		int originalRow = this.getLocation('-').get("row") - 1;
		int originalCol = this.getLocation('-').get("column") - 1;
		int tradeRow = originalRow;
		int tradeCol = originalCol;
		char[][] newBoard = new char[3][3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				newBoard[i][j] = this.board[i][j];
			}
		}
		
		if (o == Operator.UP) {
			tradeRow = originalRow - 1;
		}
		if (o == Operator.DOWN) {
			tradeRow = originalRow + 1;
		}
		if (o == Operator.LEFT) {
			tradeCol = originalCol - 1;
		}
		if (o == Operator.RIGHT) {
			tradeCol = originalCol + 1;
		}

		newBoard[originalRow][originalCol] = newBoard[tradeRow][tradeCol];
		newBoard[tradeRow][tradeCol] = '-';
		State newState = new State(newBoard);
		
		return newState;
	}
	
	public String toString() {
		/**
		 * Convert the state to a string.
		 */
		String str = "";
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				str += String.format("%c ", this.board[i][j]);
			}
			str += "\n";
		}
		return str;
	}
	
	public char[][] getBoard(){
		return this.board;
	}
}


class Path{
	
	private ArrayList<State> states;
	
	public Path(ArrayList<State> states) {
		/**
		 * Build a path using the states given.
		 * Assumption: there is at least one state in the path.
		 * 
		 * @param states  The states in the path.
		 */
		this.states = states;
	}
	
	public Path(State state) {
		/**
		 * Create a zero-length path with a single state.
		 * 
		 * @param state  The only state in the path.
		 */
		this.states = new ArrayList<>();
		this.states.add(state);
	}
	
	public boolean add(State state) {
		/**
		 * Adds the state to the path.
		 * 
		 * If the state already exists in the path, don't add it and return false.
		 * Else, add it and return true.
		 * 
		 * @param state  The state to add to the path. 
		 * @return true if the state was successfully added to the path, false otherwise.
		 */
		if (this.contains(state)) {
			return false;
		}
		this.states.add(state);
		return true;
	}
	
	public boolean contains(State state) {
		/**
		 * Determine if the path is contained in the state.
		 * 
		 * @param state the state that we're checking for in the path.
		 * @return true if the state exists in the path, false otherwise.
		 */
		for (int i=0; i<this.states.size(); i++) {
			if (this.states.get(i).equals(state)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Operator> getOperators(){
		/**
		 * @return all operators to traverse the states in the path.
		 */
		ArrayList<Operator> ops = new ArrayList<>();
		
		State cur = this.states.get(0);
		State next;
		
		for (int i=1; i<this.states.size(); i++) {
			next = this.states.get(i);
			ops.add(cur.getOperator(next));
			cur = next;
		}
		
		return ops;
	}
	
	public int length() {
		/**
		 * @return the number of transitions (number of states minus one)
		 */
		return this.states.size() - 1;
	}
	
	public float estimate(Heuristic heuristic) {
		/**
		 * Estimate the number of moves to get to reach the goal state. This number is calculated as:
		 * 
		 * estimate = (moves so far) + (heuristic estimate from terminal state)
		 * 
		 * NOTE: As we build the correct path, the estimate becomes more accurate and the heuristic
		 *       of the estimate gets smaller.
		 * 
		 * @param heuristic  The heuristic used to estimate the distance to the goal state.
		 */
		return this.getOperators().size() + heuristic.eval(this.terminalState());
	}
	
	public State terminalState() {
		/**
		 * @return the last state in the path.
		 */
		return this.states.get(this.states.size() - 1);
	}
	
	@SuppressWarnings("unchecked")
	public Path copy() {
		/**
		 * @return a copy of this path.
		 */
		return new Path((ArrayList<State>)this.states.clone());
	}
	
	public int compareTo(Path other, Heuristic heuristic) {
		/**
		 * Compare this path to another path based on their heuristic estimates.
		 */
		float est1 = this.estimate(heuristic);
		float est2 = other.estimate(heuristic);
		
		if (est1 > est2) {
			return 1;
		}
		else if (est1 == est2) {
			return 0;
		}
		else {
			return -1;
		}
	}
	
	@Override
	public String toString() {
		/**
		 * Convert the path to a string.
		 */
		String str = "'";
		for (Operator o : this.getOperators())
			str += " -> " + o.toString();
		return str + "'";
	}
}

////////////////////////////////////////////////////////////////////////////////////////////
// OPERATORS
////////////////////////////////////////////////////////////////////////////////////////////

enum Operator {
	/**
	 * All directions that the blank can be moved.
	 */
    UP, DOWN, RIGHT, LEFT; 
}


////////////////////////////////////////////////////////////////////////////////////////////
// HEURISTICS
////////////////////////////////////////////////////////////////////////////////////////////

interface Heuristic {
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

class MisplacedTiles implements Heuristic {
	
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

class ManhattanDistance implements Heuristic {
	
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

class CustomHeuristic implements Heuristic {
	
	private State goal;
	
	public CustomHeuristic(State goal) {
		/**
		 * Constructs a CustomHeuristic heuristic and sets the goal state.
		 */
		this.goal = goal;
	}

	@Override
	public float eval(State src) {
		/**
		 * Take the average of the MisplacedTiles and the ManhattanDistance heuristics.
		 */
		MisplacedTiles h1 = new MisplacedTiles(goal);
		ManhattanDistance h2 = new ManhattanDistance(goal);
		return (h1.eval(src) + h2.eval(src)) / 2;
	}
	
	@Override
	public String getSummary() {
		return "Mean of Misplaced Tiles and Manhattan Distance heuristics";
	}
}

//////////////////////////
// Solvability Checker
//////////////////////////
class Solvability{
	
	static int getInvCount(State state) {
		int invCount = 0; // Inversion count. Google 8-puzzle Inversion.
		
		//Squashing the 2D array to a 1D array for easier processing.
		char[][] board2D = state.getBoard();
		char[] board1D = new char[9];
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				board1D[(i*3)+(j)] = board2D[i][j];
			}
		}
		
		//Compare every element against every subsequent element.
	    for(int i = 0; i < 9-1; i++) {
	    	for(int j = i+1; j < 9; j++) {
	    		//Ignore blanks '-'.
	    		if( (board1D[i] != '-') && (board1D[j] != '-') && (board1D[i] > board1D[j]) ){
	    			invCount++;
	    		}
	    	}
	    }
		
		return invCount; 
	}
	
	static boolean isSolvable(State initState, State goalState) {
		int initInversions = getInvCount(initState);
		int goalInversions = getInvCount(goalState);
		
		//If the evenness or oddness of the number of inversions between the two states differ, unsolvable.
		return (initInversions % 2) == (goalInversions % 2);
	}
}

