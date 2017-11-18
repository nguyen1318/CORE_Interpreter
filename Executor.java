import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * Executor Class for the Core Interpreter Project;
 */
public class Executor {

	private Executor() { }

	// Used to store variables set
	private static HashMap<String,Integer> VARIABLES = new HashMap<String,Integer>();

	// Used to store the data for inputs
	private static List<Integer> INPUT_DATA = new LinkedList<Integer>();

	/**
	 * Execute program represent by a parse tree using DATA from a file;
	 *
	 * @param parseTree     parse tree representing the program file
	 * @param data          name of file containing the input DATA
	 */
	public static void execute(PROG parseTree, String data) { 

		// Get the input list
		getData(data);

		// Begin execution
		execPROG(parseTree);
	}

	/**
	 * Function to execute a PROG (program)
	 *
	 * @param prog PROG node to execute
	 */
	public static void execPROG (PROG prog){
		execDECLSEQ(prog.getDeclSeq());
		execSTMTSEQ(prog.getStmtSeq());
	}

	/**
	 * Function to execute DECL_SEQ
	 *
	 *@param declSeq DECL_SEQ node to execute
	 */
	private static void execDECLSEQ(DECL_SEQ declSeq){
		execDECL(declSeq.getDecl());
		if(declSeq.getAltNo() == 1){
			execDECLSEQ(declSeq.getDeclSeq());
		}
	}

	/**
	 * Function to execute DECL
	 * 
	 * @param decl DECL node to execute
	 */
	private static void execDECL(DECL decl){
		execIDLIST(decl.getIdList());
	}

	/**
	 * Function to declare all IDs in ID_LIST
	 *
	 * @param idList ID_LIST node to execute
	 */
	private static void execIDLIST (ID_LIST idList){
		String id = idList.getId();
		if(!VARIABLES.containsKey(id)){
			VARIABLES.put(id, null);
		} else {
			System.out.println("Error: VAR "+id+" already created.");
			System.exit(2);
		}

		// See if another ID_LIST needs to be created
		if(idList.getAltNo() == 1){
			execIDLIST(idList.getIdList());
		}
	}

	/**
	 * Function to execute STMT_SEQ
	 *
	 * @param stmtSeq STMT_SEQ node to execute
	 */
	private static void execSTMTSEQ(STMT_SEQ stmtSeq){
		execSTMT(stmtSeq.getStmt());
		if(stmtSeq.getAltNo() == 1){
			execSTMTSEQ(stmtSeq.getStmtSeq());
		}
	}

	/**
	 * Function to execute STMT 
	 *
	 * @param stmt STMT node to execute
	 */
	private static void execSTMT(STMT stmt){
		switch(stmt.getAltNo()){
			case 1:
				execASSIGN(stmt.getAssign());
				break;
			case 2:
				execIF(stmt.getIf());
				break;
			case 3:
				execLOOP(stmt.getLoop());
				break;
			case 4:
				execIN(stmt.getIn());
				break;
			case 5:
				execOUT(stmt.getOut());
				break;
			default:
				break;
		}
	}

	/**
	 * Function to execute ASSIGN
	 *
	 *@param assignStmt ASSIGN node to execute
	 */
	private static void execASSIGN(ASSIGN assignStmt){
		// Get the LEFT value
		String id = assignStmt.getLvalue();
		if(VARIABLES.containsKey(id)){
			VARIABLES.put(id, execEXPR(assignStmt.getExpr()));
		} else {
			System.out.println("Error: VAR id "+id+" not declared.");
			System.exit(2);
		}
	}

	/** 
	 * Function to execute IF-THEN or IF-THEN-ELSE
	 *
	 * @param ifStmt IF node to execute
	 */
	private static void execIF(IF ifStmt){
		if(execCOND(ifStmt.getCond())){
			execSTMTSEQ(ifStmt.getStmtSeq());
		} else {
			execSTMTSEQ(ifStmt.getElseStmtSeq());
		}
	}

	/**
	 * Function to execute WHILE 
	 *
	 * @param loopStmt LOOP node to execute
	 */
	private static void execLOOP(LOOP loopStmt){
		while(execCOND(loopStmt.getCond())){
			execSTMTSEQ(loopStmt.getStmtSeq());
		}
	}

	/**
	 * Function to execute an INPUT
	 *
	 * @param inputStmt IN node to execute
	 */
	private static void execIN(IN inputStmt){

		// Get idList for assignment
		ID_LIST idList = inputStmt.getIdList();
		setVarByInput(idList);
		// Decide if we need the other IDs in the list
		while(idList.getAltNo() == 1){
			idList = idList.getIdList();
			setVarByInput(idList);
		}
	}

	/**
	 * Function to execute an OUTPUT
	 *
	 * @param outputStmt OUT node to execute
	 */
	private static void execOUT(OUT outputStmt){

		// Old code from part 1
		//int result = execEXPR(outputStmt.getExpr());
		//System.out.print(result);

		// Basically the same method as execIN
		ID_LIST idList = outputStmt.getIdList();
		outputVar(idList);
		while(idList.getAltNo() == 1){
			idList = idList.getIdList();
			outputVar(idList);
		}
}

	/**
	 * Function to evaluate a CCOND
	 *
	 * @param cond COND node to evaluate
	 *
	 * @return boolean of the conditional statement
	 */
	private static boolean execCOND(COND cond) {
		boolean result = true;
		switch (cond.getAltNo()){
			case 0: // for a negative condition statement (!)
				result = !execCOND(cond.getNeg());
				break;
			case 1: // for conditionals with AND and OR
				if(cond.getOp().equals("AND")){ // AND statment
					// Get the left hand side and the right hand side of COND
					result=(execCOND(cond.getLhs())&&execCOND(cond.getRhs()));
				} else { // OR operator
					// Again, get left and right hand side
					result=(execCOND(cond.getLhs())||execCOND(cond.getRhs()));
				}
				break;
			case 2: // compare operators
				result = execCMPR(cond.getCmpr());
				break;
			default:
				break;
		}
		return result;
	}

	/**
	 * Function to evaluate CMPR
	 *
	 * @param cmpr CMPR node to evaluate
	 *
	 * @return boolean of the evaluation
	 */
	private static boolean execCMPR(CMPR cmpr){
		boolean result = true;
		CMPR_OP op = cmpr.getOp();
		if(op.getOp().equals("GREATER_THAN")){
			result=(execEXPR(cmpr.getExpr1())>execEXPR(cmpr.getExpr2()));
		}
		
		if(op.getOp().equals("GREATER_EQUAL")){
			result=(execEXPR(cmpr.getExpr1())>=execEXPR(cmpr.getExpr2()));
		}
		
		if(op.getOp().equals("LESS_THAN")){
			result=(execEXPR(cmpr.getExpr1())<execEXPR(cmpr.getExpr2()));
		}
		
		if(op.getOp().equals("LESS_EQUAL")){
			result=(execEXPR(cmpr.getExpr1())<=execEXPR(cmpr.getExpr2()));
		}
		
		if(op.getOp().equals("EQUALS")){
			result=(execEXPR(cmpr.getExpr1())==execEXPR(cmpr.getExpr2()));
		}
		
		if(op.getOp().equals("NOT_EQUAL")){
			result=(execEXPR(cmpr.getExpr1())!=execEXPR(cmpr.getExpr2()));
		}

		return result;

	}

	// Old code reused from Project 1 ---------------------------------------

	/**
	 * Function to evaluate an EXPR node;
	 *
	 * @param expr  EXPR node to evaluate
	 * @return      int value of the expression evaluation
	 */
	private static int execEXPR(EXPR expr) {
		int result = execTERM(expr.getTerm());
		if (expr.getAltNo() == 1) {
			if (expr.getOp().equals("PLUS")) {
				result += execEXPR(expr.getExpr());
			} else {
				result -= execEXPR(expr.getExpr());
			}
		}
		return result;
	}

	/**
	 * Function to evaluate a TERM node;
	 *
	 * @param term  TERM node to evaluate
	 * @return      int value of the term evaluation
	 */
	private static int execTERM(TERM term) {
		int result = execFACTOR(term.getFactor());
		if (term.getAltNo() == 1) {
			result *= execTERM(term.getTerm());
		}
		return result;
	}

	/**
	 * Function to evaluate a FACTOR node;
	 *
	 * @param factor    FACTOR node to evaluate
	 * @return          int value of the factor evaluation
	 */
	private static int execFACTOR(FACTOR factor) {
		int result = 0;
		switch (factor.getAltNo()) {
			case 0: // CONST;
				result = factor.getValue();
				break;
			case 1: // ID;
				result = getValueById(factor.getId());
				break;
			case 2: // -FACTOR;
				result = -1*execFACTOR(factor.getFactor());
				break;
			case 3: // (EXPR);
				result = execEXPR(factor.getExpr());
				break;
			default:
				break;
		}
		return result;
	}

	// Methods to get data from the data file ------------------------

	/**
	 * Get data from INPUT_DATA
	 *
	 * @param data file containing the data
	 */
	private static void getData(String data){

		// Most of the method was taken from stackoverflow
		// Reference: stackoverflow.com
		// To find, search "how to read from a file java"
		// First stackoverflow link
		// Credit: Knubo & gtonic 
		BufferedReader r = null;
		List<String> inputLine = new LinkedList<String>();
		try{
			r = new BufferedReader(new FileReader(new File(data)));
			String line = r.readLine();
			while(line != null){
				inputLine.add(line);
				line = r.readLine();
			} 
			r.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		updateDataList(inputLine);
	}
	
	/**
	 * Update INPUT_DATA list with integers from input file
	 *
	 * @param lines lines of the data file to read from the buffered reader
	 */
	private static void updateDataList(List<String> lines){
		for(String line: lines) {
			int i = 0;
			int length = line.length();
			while(i<length){
				String token = "";
				int n = i+1;
				char c = line.charAt(i);
				if(Character.isDigit(c) || c == '-'){
					while(n < length && Character.isDigit(line.charAt(n))){
						n++;
					}
					token = line.substring(i, n);
				}
				if(token.length() > 0){
					try {
						INPUT_DATA.add(Integer.parseInt(token));
					} catch (NumberFormatException e) {
						System.out.println("Error:"+token+" doesn't match int");
						System.exit(2);
					}
				}
				i = n;
			}
		}
	}

	//Helper Methods... So hard :( ----------------------------------
	
	/**
	 * Used for execIN to set the id field of the idList to int
	 *
	 * @param idList where the current ID to set for input
	 */
	private static void setVarByInput(ID_LIST idList){
		// Check to see if there are more input tokens, if not throw error
		if(INPUT_DATA.size() > 0){
			String currentId = idList.getId();
			if (VARIABLES.containsKey(currentId)){
				VARIABLES.put(currentId, INPUT_DATA.remove(0));
			}else{
				System.out.println("Error:Variable "+currentId+" undeclared.");
				System.exit(2);
			}
		}else{
			System.out.println("Error: No more/lack of input data");
			System.exit(2);
		}
	}

	/**
	 * For execOUT, needed to print value of id  
	 *
	 * @param idList node with current ID field to output
	 */
	private static void outputVar(ID_LIST idList){
		String current = idList.getId();
		if(VARIABLES.containsKey(current)){
			if(VARIABLES.get(current) != null) {
				System.out.println(VARIABLES.get(current));
			} else {
				System.out.println("Error: "+current+" is undefined");
				System.exit(2);
			}
		} else {
			System.out.print("Error: "+current+" undeclared");
			System.exit(2);
		}
	}

	// Reused helper method from last project

	/**
	 * For resolving an ID to a CONST;
	 *
	 * @param id    identifier name to look up in VARIABLES
	 * @return      value associated to identifier name
	 */
	private static int getValueById(String id) {
		int result = 0;
		// Project 1 use x = 1 and y = 2
		//VARIABLES.put("x", 1);
		//VARIABLES.put("y", 2);
		if (VARIABLES.containsKey(id)) {
			if (VARIABLES.get(id) != null){ //|| VARIABLES.get(id).equals("x") ||
					//VARIABLES.get(id).equals("y")) {
				//if(VARIABLES.get(id).equals("x")){
					//result = 1;
				//} else {
					//result = 2;
				//}
				result = VARIABLES.get(id);
			} else {
				System.out.println("ERROR: Variable id " + id + " has not been instantiated");
				System.exit(2); // Failure Case;
			}
		} else {
			System.out.println("ERROR: variable id " + id + " has not been declared");
			System.exit(2); // Failure Case;
		}
		return result;
	}
}
