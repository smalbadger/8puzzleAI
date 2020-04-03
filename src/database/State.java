package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import operators.Operator;

public class State {
	
	private char[][] board = new char[3][3];
	
	// stores all states found so far.
	final public static HashSet<State> database = new HashSet<>();
	
	public State(char[][] configuration) {
		/**
		 * Creates a State from a 3x3 grid of characters.
		 */
		this.board = configuration;
	}
	
	public int getInvCount() {
		/**
		 * Returns the inversion count of a given state of the 8-puzzle.
		 * The relative evenness of the initial state and the goal state determines 
		 * if a puzzle is solvable.
		 */
		int invCount = 0; // Inversion count. Google 8-puzzle Inversion.
		
		//Squashing the 2D array to a 1D array for easier processing.
		char[][] board2D = this.board;
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
	
	public boolean canReach(State goalState) {
		/**
		 * Tells you if the 8-puzzle has a solution or not for the given initial state and goal state. 
		 */
		int initInversions = this.getInvCount();
		int goalInversions = goalState.getInvCount();
		
		//If the evenness or oddness of the number of inversions between the two states differ, unsolvable.
		return (initInversions % 2) == (goalInversions % 2);
	}
	
	public static State generateRandom() {
		/**
		 * @return a randomly-generated state
		 */
		char[] b = {'1', '2', '3', '4', '5', '6', '7', '8', '-'};
		
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < 9; i++) {
            int change = i + random.nextInt(9 - i);
            char temp = b[i];
            b[i] = b[change];
            b[change] = temp;
        }
		
        char[][] board = {{b[0], b[1], b[2]},{b[3], b[4], b[5]},{b[6], b[7], b[8]}};
		return new State(board);
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
	
	public State shuffle(int times) {
		/**
		 * Shuffles the state a given number of times by performing valid operations.
		 * 
		 * NOTE: With each iteration, a new state object is created.
		 * 
		 * @param times the number of times to shuffle
		 * @return the shuffled state
		 */
		State newState = this;
		Path p = new Path(this);
		Random rand = new Random();
		
		for (int i=0; i<times; i++) {
			ArrayList<Operator> ops = new ArrayList<>(newState.getOperators());
			
			int size = ops.size();
			int item = rand.nextInt(size);
			State s = newState.transform(ops.get(item));
			if (p.contains(s)) {
				for(Object obj : ops) {
				    s = newState.transform((Operator)obj);
				    if (!p.contains(s)) {
				    	newState = s;
				    	p.add(s);
				    	break;
				    }
				}
	    	}
	    	else {
	    		newState = s;
	    		p.add(s);
	    	}
		}
		return newState;
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
	
	public State replaceWithWildCards(ArrayList<Character> chars) {
		/**
		 * @return a new state but all characters given are replaced with '*'
		 */
		char[][] newBoard = new char[3][3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if (chars.contains(this.board[i][j])) {
					newBoard[i][j] = '*';
				}
				else {
					newBoard[i][j] = this.board[i][j];
				}
			}
		}
		
		return new State(newBoard);
	}
	
	@Override
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
}
