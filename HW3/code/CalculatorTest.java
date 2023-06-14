import java.io.*;
import java.util.*;

public class CalculatorTest
{
	static private final Map<Character, Integer> OPERATOR_PRECEDENCE = new HashMap<>() {{
		put(',', 0);
		put('-', 1);
		put('+', 1);
		put('*', 2);
		put('%', 2);
		put('/', 2);
		put('~', 3);
		put('^', 4);
	}};

	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("q") == 0)
					break;
				command(input);
			}
			catch (Exception e)
			{
				System.out.println("ERROR");
			}
		}
	}

	private static void command(String input) throws InvalidFormulaException, IllegalOperationException {
		// Checking if the input has spaces between digits
		if (input.matches(".*\\d\\s+\\d.*")) throw new InvalidFormulaException();

		// Converting the infix expression to postfix expression
		List<String> postfixList = infixToPostfix(input.replaceAll("\\s", ""));
		String postfixString = postfixList.toString().replaceAll("[\\[,\\]]", "");

		// Calculating the result using the postfix expression
		long calResult = calculate(postfixList);
		System.out.println(postfixString);
		System.out.println(calResult);
	}
	// The basic skeleton for conversion is given by ChatGPT
	private static List<String> infixToPostfix(String infixString) throws InvalidFormulaException {
		// Initialize a list to store the postfix expression
		List<String> postfixList = new LinkedList<>();
		// Initialize a stack to store the operators
		Stack<Character> operatorStack = new Stack<>();
		// Variables to keep track of whether the last element was a digit or operator,
		// and if the minus sign is for negation or subtraction
		boolean isAfterDigit = false;
		boolean isAfterOperator = true;
		boolean isMinusNegation = true;

		// Iterate over each character in the infix expression
		for (int i = 0; i < infixString.length(); i++){
			char c = infixString.charAt(i);

			// If the character is a digit, add it to the postfix expression
			if (Character.isDigit(c)) {
				if (isAfterDigit) postfixList.add(postfixList.remove(postfixList.size() - 1) + c);
				else postfixList.add(String.valueOf(c));
				isAfterDigit = true;
				isAfterOperator = false;
				isMinusNegation = false;
			}
			// If the character is an opening parenthesis, push it to the stack
			else if (c == '(') {
				if (!isAfterOperator) throw new InvalidFormulaException();
				operatorStack.push(c);
				isAfterDigit = false;
				isAfterOperator = true;
				isMinusNegation = true;
			}
			// If the character is a closing parenthesis,
			// pop the operators from the stack and add them to the postfix expression
			// until reaching the opening parenthesis
			else if (c == ')') {
				// Count # of operands of the average operator
				// If it is not an average operator, its value is 0
				long averageCount = 0;
				while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
					char operator = operatorStack.pop();
					// If the operator is a comma, the parentheses consist of average operator
					if (operator == ',') {
						averageCount++;
					}
					// In average operator,
					// the other operator should remain only between last comma and the closing parenthesis
					// If not, it is an invalid formula, so raise an error
					else if (averageCount == 0) {
						postfixList.add(String.valueOf(operator));
					} else {
						throw new InvalidFormulaException();
					}
				}
				operatorStack.pop();
				// If averageCount != 0, the parentheses and commas are average operator
				if (averageCount != 0) {
					// # of operand must be 1 + # of commas
					averageCount++;

					// Add # of operands and "avg" to the postfix expression
					postfixList.add(String.valueOf(averageCount));
					postfixList.add("avg");
				}
				isAfterDigit = false;
				isAfterOperator = false;
				isMinusNegation = false;
			}
			// If the character is a comma,
			// every operator between commas, or between an opening parenthesis and a comma
			// should be added to the postfix expression
			else if (c == ',') {
				if (isAfterOperator) throw new InvalidFormulaException();
				while (!operatorStack.isEmpty() && operatorStack.peek() != '(' && operatorStack.peek() != ',') {
					postfixList.add(String.valueOf(operatorStack.pop()));
				}
				operatorStack.push(c);
				isAfterDigit = false;
				isAfterOperator = true;
				isMinusNegation = true;
			// If the character is minus and the flag isMinusNegation is true, push '~' to the stack
			}else if (c == '-' && isMinusNegation){
				isAfterOperator = true;
				operatorStack.push('~');
			} else {
				// If input contains non-digit and non-operator character or
				// operator comes after another operator,raise an error
				if (!OPERATOR_PRECEDENCE.containsKey(c)) throw new InvalidFormulaException();
				if (isAfterOperator) throw new InvalidFormulaException();
				// If the character is a power operator, just push it to the stack,
				// since it has right-to-left associativity
				if (c == '^') {
					operatorStack.push(c);
				}
				// If the character is the other operator,
				// pop operators from the stack while it has lower or equal precedence than the operators,
				// add them to the postfix expression, and push it to the stack
				else {
					while (!operatorStack.isEmpty() && operatorStack.peek() != '(' && operatorStack.peek() != ','
							&& OPERATOR_PRECEDENCE.get(c) <= OPERATOR_PRECEDENCE.get(operatorStack.peek())) {
						postfixList.add(String.valueOf(operatorStack.pop()));
					}
					operatorStack.push(c);
				}
				isAfterDigit = false;
				isAfterOperator = true;
				isMinusNegation = true;
			}
		}
		// After iterating the infix expression, pop all operators and add them to the postfix expression
		while (!operatorStack.isEmpty()) {
			// If parenthesis or comma is remaining, the formula is invalid.
			if (operatorStack.peek() == '(' || operatorStack.peek() == ')' || operatorStack.peek() == ',') {
				throw new InvalidFormulaException();
			}
			postfixList.add(String.valueOf(operatorStack.pop()));
		}
		return postfixList;
	}
	// The basic skeleton for conversion is given by ChatGPT

	private static long calculate(List<String> postfixList) throws InvalidFormulaException, IllegalOperationException {
		// Initialize an empty stack to store the operands
		Stack<Long> numStack = new Stack<>();
		// Iterate over the list of postfix expression
		for (String elem : postfixList) {
			// If the current element is "avg", pop # of operands of the average operator from the stack,
			// and push average of `numOperands` operands popped from the stack
			if (elem.equals("avg")) {
				long res = 0;
				try {
					long numOperands = numStack.pop();
					for (long i = 0; i < numOperands; i++) {
						res += numStack.pop();
					}
					res /= numOperands;
					numStack.push(res);

				}
				// If an operand cannot be popped from the stack, then the formula is invalid
				catch (EmptyStackException e) {
					throw new InvalidFormulaException();
				}
			}
			// If the current element is binary operation, pop two operands and calculate
			else if (elem.matches("[-+*/%^]")){
				// Pop second operand and first operand from the stack
				long operand2 = numStack.pop();
				long operand1 = numStack.pop();
				long res = 0;
				// Perform the operation based on the operator
				switch (elem.charAt(0)) {
					case '+' :
						res = operand1 + operand2;
						break;
					case '-' :
						res = operand1 - operand2;
						break;
					case '*' :
						res = operand1 * operand2;
						break;
					case '/' :
						// Check for divide by zero error
						if (operand2 == 0) throw new IllegalOperationException();
						res = operand1 / operand2;
						break;
					case '%' :
						// Check for divide by zero error
						if (operand2 == 0) throw new IllegalOperationException();
						res = operand1 % operand2;
						break;
					case '^' :
						// Check for negative exponent error
						if (operand1 == 0 && operand2 < 0) throw new IllegalOperationException();
						res = (long) Math.pow(operand1,operand2);
						break;
					default:
						throw new InvalidFormulaException();
				}
				// Push the calculation result to the stack again
				numStack.push(res);
			}
			// If the current element is negation, pop one number, negate it, and push it again
			else if(elem.equals("~")) numStack.push(-numStack.pop());
			// If the current element is not an operator,
			// it should be a number, so push it to the stack
			// If it is not a number, the formula is invalid
			else {
				try {
					numStack.push(Long.parseLong(elem));
				}
				catch (NumberFormatException e){
					throw new InvalidFormulaException();
				}
			}
		}
		// If there are the remaining numbers more than one, the formula is invalid
		if (numStack.size() != 1) {
			throw new InvalidFormulaException();
		}
		return numStack.pop();
	}
}

// This exception is thrown when the input formula is invalid or cannot be evaluated.
// This can happen if there is a syntax error in the input formula,
// such as an operator appearing in an unexpected position, or if the formula is not well-formed,
// such as if there are not enough operands for an operator or vice versa.
class InvalidFormulaException extends Exception {
	InvalidFormulaException(){
		super();
	}
}
// This exception is thrown when the input formula contains an illegal operation.
// An operation is considered illegal if it involves dividing by zero or
// taking the square root of a negative number.
class IllegalOperationException extends Exception {
	IllegalOperationException(){
		super();
	}
}
