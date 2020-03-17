import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.lang.Math;

////////////////////////////////////////////////////////////////////////////////////////////
// CONTROL STRATEGY
////////////////////////////////////////////////////////////////////////////////////////////

class hw03_smalbadger{
	
	public static void main(String[] args) {
		
		char [][] initial_config = new char[3][3];
		char [][] goal_config    = new char[3][3];
		
		State initial = new State(initial_config);
		State goal    = new State(goal_config);
		Heuristic heuristic = new MisplacedTiles(goal);
		
		aStar(initial, goal, heuristic);
	}
	
	private static void aStar(State initial, State goal, Heuristic heuristic) {
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
		 */
		
		// 1.
		ArrayList<Path> paths = new ArrayList<>();
		paths.add(new Path(initial));
		
		// 2.
		while (!paths.isEmpty() && !(paths.get(0).terminalState() == goal)) {
			
			// 2a.
			Path best = paths.remove(0);
			ArrayList<State> expansions = best.terminalState().getNeighbors();
			
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
			Output.printSuccess(paths.get(0));
		}
		else {
			Output.printFailure();
		}
	}
}


////////////////////////////////////////////////////////////////////////////////////////////
// I/O DEVICES
////////////////////////////////////////////////////////////////////////////////////////////

class Input {
	
	private Input() {
		// TODO: Store initial state, goal state, and heuristic.
	}
	
	static public Input read() {
		// TODO: Get input from user, add properties to the input object and return it.
		
		Input input = new Input();
		return input;
	}
}

class Output {
	public static void printSuccess(Path optimalPath) {
		
	}
	
	public static void printFailure() {
		
	}
}


////////////////////////////////////////////////////////////////////////////////////////////
// DATABASE
////////////////////////////////////////////////////////////////////////////////////////////

class State {
	
	private char[][] board = new char[3][3];
	
	// stores the shortest path to each node
	final static HashSet<State> database = new HashSet<>();
	
	public State(char[][] configuration) {
		this.board = configuration;
	}
	
	public HashSet<Operator> getOperators() {
		/**
		 * Gets a set of all possible operators that can be used on the current state.
		 * 
		 * @return A HashSet of Operators
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
		 */
		for (Operator o : Operator.values()) {
			if (this.transform(o) == result) {
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
		 */
		return this.board[row][col];
	}
	
	public boolean equals(Object obj) {
		/**
		 * Allows us to overload the == operator.
		 * 
		 * States are equal if they have the same board.
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
		 * Necessary to override for comparing states with the == operator.
		 * 
		 * To get the hashcode, we append all numbers in the state together and 
		 * replace the blank with 0, then parse into an integer.
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
		 * Gets neighbors that are not already explored and 
		 * in the process, explores the neighbor by adding it 
		 * to the set of all explored states.
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
		 */
		
		if (!this.getOperators().contains(o))
			return null;
		
		int originalRow = this.getLocation('-').get("row");
		int originalCol = this.getLocation('-').get("column");
		int tradeRow = originalRow;
		int tradeCol = originalCol;
		Operator opposite;
		
		char[][] newBoard = this.board.clone();
		
		if (o == Operator.UP) {
			tradeRow = originalRow - 1;
			opposite = Operator.DOWN;
		}
		if (o == Operator.DOWN) {
			tradeRow = originalRow + 1;
			opposite = Operator.UP;
		}
		if (o == Operator.LEFT) {
			tradeCol = originalCol - 1;
			opposite = Operator.RIGHT;
		}
		if (o == Operator.RIGHT) {
			tradeCol = originalCol + 1;
			opposite = Operator.LEFT;
		}
			
		newBoard[originalRow][originalCol] = newBoard[tradeRow][tradeCol];
		State newState = new State(newBoard);
		
		return newState;
	}
}


class Path{
	
	private ArrayList<State> states;
	
	public Path(ArrayList<State> states) {
		/**
		 * Assumption: there is at least one state in the path.
		 */
		this.states = states;
	}
	
	public Path(State state) {
		/**
		 * Create a zero-length path with a single state.
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
		 */
		if (this.contains(state)) {
			return false;
		}
		this.states.add(state);
		return true;
	}
	
	public boolean contains(State state) {
		/**
		 * If the state is in the path, return true.
		 * Else, return false.
		 */
		for (int i=0; i<this.states.size(); i++) {
			if (this.states.get(i) == state) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Operator> getOperators(){
		/**
		 * Returns a list of operators to progress through the 
		 * states in the path.
		 */
		ArrayList<Operator> ops = new ArrayList<>();
		
		State cur = this.states.get(0);
		
		for (int i=1; i<this.states.size(); i++) {
			ops.add(cur.getOperator(this.states.get(i)));
		}
		
		return ops;
	}
	
	public int length() {
		/**
		 * Gets the number of transitions in the path.
		 */
		return this.states.size() - 1;
	}
	
	public float estimate(Heuristic heuristic) {
		return this.length() + heuristic.eval(this.terminalState());
	}
	
	public State terminalState() {
		/**
		 * Gets the last state in the path.
		 */
		return this.states.get(this.states.size() - 1);
	}
	
	public Path copy() {
		return new Path((ArrayList<State>)this.states.clone());
	}
	
	public int compareTo(Path path, Heuristic heuristic) {
		return (int)(this.estimate(heuristic) - path.estimate(heuristic));
	}
}

////////////////////////////////////////////////////////////////////////////////////////////
// OPERATORS
////////////////////////////////////////////////////////////////////////////////////////////

enum Operator {
    UP, DOWN, RIGHT, LEFT; 
}


////////////////////////////////////////////////////////////////////////////////////////////
// HEURISTICS
////////////////////////////////////////////////////////////////////////////////////////////

class Heuristic {
	
	protected final State goal;
	
	public Heuristic(State goal) {
		this.goal = goal;
	}
	
	public float eval(State src) {
		return 0;
	}
}

class MisplacedTiles extends Heuristic {
	
	public MisplacedTiles(State goal) {
		super(goal);
	}

	@Override
	public float eval(State src) {
		float numMisplaced = 0.0f;
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if (src.getAt(i,j) != this.goal.getAt(i,j)) {
					numMisplaced += 1;
				}
			}
		}
		
		return numMisplaced;
	}
}

class ManhattanDistance extends Heuristic {
	
	public ManhattanDistance(State goal) {
		super(goal);
	}

	@Override
	public float eval(State src) {
		float dist = 0.0f;
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				char find = src.getAt(i, j);
				int goalRow = goal.getLocation(find).get("row");
				int goalCol = goal.getLocation(find).get("column");
				
				dist += Math.abs(i-goalRow) + Math.abs(j-goalCol);
			}
		}
		
		return dist;
	}
	
}

class CustomHeuristic extends Heuristic {
	
	public CustomHeuristic(State goal) {
		super(goal);
	}

	@Override
	public float eval(State initial) {
		float val = 0.0f;
		
		// TODO: calculate heuristic value
		
		return val;
	}
	
}