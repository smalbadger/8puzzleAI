package database;

import java.util.ArrayList;

import heuristics.Heuristic;
import operators.Operator;

public class Path{
	
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
	
	public State getStateAt(int idx) {
		/**
		 * @return the state at idx
		 */
		if (idx >= this.states.size() || idx < 0) {
			return null;
		}
		
		return this.states.get(idx);
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
	
	@Override
	public boolean equals(Object o) {
		/**
		 * @param o  another path to compare equality with
		 * @return true if all states in the paths are equal and false otherwise.
		 */
		Path other = (Path)o;
		
		if (this.length() != other.length())
			return false;
		
		for (int i=0; i<this.states.size(); i++) {
			if (this.getStateAt(i) != other.getStateAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		/**
		 * @return the hashed path
		 */
		int sum = 0;
		for (State s: this.states) {
			sum += s.hashCode();
		}
		return sum;
	}
}