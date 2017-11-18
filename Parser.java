import java.io.IOException;

public class Parser {

	private Parser () { }
	public static boolean comp = false;
	public static String compType = "";

	/**
	 * Call Scanner/Tokenizer to generate token stream; Parse token stream;
	 * Build + return parse tree;
	 *
	 * @return          root node of the program parse tree
	 */
	public static PROG getParseTree() throws IOException {
		// Generate Parse Tree;
		PROG tree = new PROG(); 
		tree.parse();
		return tree;
	}
}

class PROG {
	private DECL_SEQ declSeq;
	private STMT_SEQ stmtSeq;

	public void parse() throws IOException {
		Scanner.match("PROGRAM");
		declSeq = new DECL_SEQ();
		declSeq.parse();
		Scanner.match("BEGIN");
		stmtSeq = new STMT_SEQ();
		stmtSeq.parse();
		Scanner.match("END");
		Scanner.match("EOF");
	}

	public DECL_SEQ getDeclSeq(){
		return declSeq;
	}
	public STMT_SEQ getStmtSeq(){
		return stmtSeq;
	}
}

class DECL_SEQ {
	private int altNo = 0;
	private DECL decl;
	private DECL_SEQ declSeq;

	public void parse() throws IOException {
		decl = new DECL();
		decl.parse();
		if(!Scanner.currentToken().equals("BEGIN")){
			altNo = 1;
			declSeq = new DECL_SEQ();
			declSeq.parse();
		}
	}

	public int getAltNo(){
		return altNo;
	}
	public DECL getDecl() {
		return decl;
	}
	public DECL_SEQ getDeclSeq() {
		return declSeq;
	}
}

class DECL {
	private ID_LIST idList;
	public void parse() throws IOException {
		Scanner.match("INT");
		idList = new ID_LIST();
		idList.parse();
		Scanner.match("SEMICOLON");
		String token = Scanner.currentToken();
		if(token.equals("SEMICOLON")){
			System.out.println("ERROR: Invalid ';' token.");
			System.exit(2);
		}
	}

	public ID_LIST getIdList(){
		return idList;
	}
}

class ID_LIST {
	private int altNo = 0;
	private String id;
	private ID_LIST idList;

	public void parse() throws IOException {
		id = Scanner.getID();
		if(Scanner.currentToken().equals("COMMA")){
			altNo = 1;
			Scanner.nextToken();
			String token = Scanner.currentToken();
			if(token.equals("COMMA")){
				System.out.println("ERROR: Invalid ',' token.");
				System.exit(2);
			}
			idList = new ID_LIST();
			idList.parse();
		}
	}

	public int getAltNo(){
		return altNo;
	}
	public String getId() {
		return id;
	}
	public ID_LIST getIdList(){
		return idList;
	}
}

class STMT_SEQ {
	private int altNo = 0;
	private STMT stmt;
	private STMT_SEQ stmtSeq;

	public void parse() throws IOException {
		stmt = new STMT();
		stmt.parse();
		String token = Scanner.currentToken();
		if(!token.equals("END")&&!token.equals("ENDIF")&&!token.equals("ENDWHILE")&&!token.equals("ENDIF")&&!token.equals("ELSE")){
			altNo = 1;
			stmtSeq = new STMT_SEQ();
			stmtSeq.parse();
		}
	}

	public int getAltNo(){
		return altNo;
	}
	public STMT getStmt(){
		return stmt;
	}
	public STMT_SEQ getStmtSeq(){
		return stmtSeq;
	}
}

class STMT {

	private int altNo;
	private ASSIGN s1;
	private IF s2;
	private LOOP s3;
	private IN s4;
	private OUT s5;

	public void parse() throws IOException {
		String token = Scanner.currentToken();
		if (token.contains("ID")) {
			altNo = 1;
			s1 = new ASSIGN(); 
			s1.parse();
		} else if (token.equals("IF")) {
			altNo = 2;
			s2 = new IF(); 
			s2.parse();
		} else if (token.equals("WHILE")) {
			altNo = 3;
			s3 = new LOOP(); 
			s3.parse();
		} else if (token.equals("INPUT")) {
			altNo = 4;
			s4 = new IN(); 
			s4.parse();
		} else if (token.equals("OUTPUT")) {
			altNo = 5;
			s5 = new OUT(); 
			s5.parse();
		} else {
			System.out.println("ERROR: Expected a statement, found "+token);
			System.exit(2);
		}
		Scanner.match("SEMICOLON");
		token = Scanner.currentToken();
		if(token.equals("SEMICOLON")){
			System.out.println("ERROR: Invalid ';' token.");
			System.exit(2);
		}
	}

	public int getAltNo(){ 
		return altNo; 
	}
	public ASSIGN getAssign(){ 
		return s1; 
	}
	public IF getIf(){ 
		return s2; 
	}
	public LOOP getLoop(){ 
		return s3; 
	}
	public IN getIn(){ 
		return s4; 
	}
	public OUT getOut(){ 
		return s5; 
	}
}

class ASSIGN {

	private EXPR expr;
	private String lvalue;

	public void parse() throws IOException {
		lvalue = Scanner.getID();
		Scanner.match("ASSIGN");
		expr = new EXPR(); 
		expr.parse();
	}

	public EXPR getExpr(){ 
		return expr; 
	}
	public String getLvalue(){ 
		return lvalue; 
	}
}

class IF {

	private int altNo = 0;
	private COND cond;
	private STMT_SEQ stmtSeq;
	private STMT_SEQ elseStmtSeq;

	public void parse() throws IOException{
		Scanner.match("IF");
		Scanner.nextToken();
		Scanner.nextToken();
		Scanner.nextToken();
		String token = Scanner.currentToken();
		if(token.equals("AND") || token.equals("OR")){
			Parser.comp = true;
			Parser.compType = token;
		}
		Scanner.prevToken();
		Scanner.prevToken();
		Scanner.prevToken();
		cond = new COND(); 
		cond.parse();

		Scanner.match("THEN");
		stmtSeq = new STMT_SEQ(); 
		stmtSeq.parse();

		token = Scanner.currentToken();
		if (token.equals("ELSE")) {
			altNo = 1;
			Scanner.nextToken();
			elseStmtSeq = new STMT_SEQ(); 
			elseStmtSeq.parse();
		}
		Scanner.match("ENDIF");
	}

	public int getAltNo() { 
		return altNo; 
	}
	public COND getCond() { 
		return cond; 
	}
	public STMT_SEQ getStmtSeq() { 
		return stmtSeq; 
	}
	public STMT_SEQ getElseStmtSeq() { 
		return elseStmtSeq; 
	}
}

class LOOP {

	private STMT_SEQ stmtSeq;
	private COND cond;

	public void parse() throws IOException {
		Scanner.match("WHILE");
		cond = new COND(); 
		cond.parse();
		Scanner.match("BEGIN");
		stmtSeq = new STMT_SEQ();
		stmtSeq.parse();
		Scanner.match("ENDWHILE");
	}

	public STMT_SEQ getStmtSeq() { 
		return stmtSeq; 
	}
	public COND getCond() { 
		return cond; 
	}
}

class IN {

	private ID_LIST idList;

	public void parse() throws IOException {
		Scanner.match("INPUT");
		idList = new ID_LIST(); 
		idList.parse();
	}

	public ID_LIST getIdList() { 
		return idList; 
	}
}

class COND {

	private int altNo;
	private COND neg;
	private COND lhs; //Left hand side = lhs
	private COND rhs; //Right hand side = rhs
	private String op;
	private CMPR cmpr;

	public void parse() throws IOException{
		String token = Scanner.currentToken();

		if (token.equals("NOT")) {
			altNo = 0;
			Scanner.match("NOT");
			Scanner.match("LEFT_PAREN");
			neg = new COND(); 
			neg.parse();
			Scanner.match("RIGHT_PAREN");
		} else if (Parser.comp) {
			Parser.comp=false;
			altNo = 1;
			lhs = new COND(); 
			lhs.parse();
			if (Parser.compType.equals("AND") || Parser.compType.equals("OR")){
				op = Parser.compType;
				Scanner.nextToken();
			} else {
				System.out.println("ERROR: Expected a boolean operator, found " + token);
				System.exit(2);
			}
			rhs = new COND(); 
			rhs.parse();
		} else{
			altNo = 2;
			cmpr = new CMPR(); 
			cmpr.parse();
		}
	}

	public int getAltNo() { 
		return altNo; 
	}
	public CMPR getCmpr() { 
		return cmpr; 
	}
	public COND getNeg() { 
		return neg; 
	}
	public COND getLhs() { 
		return lhs; 
	}
	public COND getRhs() { 
		return rhs; 
	}
	public String getOp() { 
		return op; 
	}
}

class CMPR {

	private CMPR_OP op;
	private EXPR expr1;
	private EXPR expr2;

	public void parse() throws IOException {
		expr1 = new EXPR(); 
		expr1.parse();
		op = new CMPR_OP(); 
		op.parse();
		expr2 = new EXPR(); 
		expr2.parse();
	}

	public CMPR_OP getOp() { 
		return op; 
	}
	public EXPR getExpr1() { 
		return expr1; 
	}
	public EXPR getExpr2() { 
		return expr2; 
	}
}

class CMPR_OP {

	private String op;

	public void parse() throws IOException {
		String token = Scanner.currentToken();
		if (token.equals("EQUALS") || token.equals("LESS_THAN") ||
				token.equals("GREATER_THAN") || token.equals("LESS_EQUAL") ||
				token.equals("GREATER_EQUAL") || token.equals("NOT_EQUAL")) {
			op = token;
		} else {
			System.out.println("ERROR: Expected a comparison operator, found " + token);
			System.exit(2);
		}
		Scanner.nextToken();
	}

	public String getOp() { 
		return op; 
	}
}

class INT_LIST {

	private int altNo = 0;
	private int value;
	private INT_LIST intList;

	public void parse() throws IOException{
		value = Scanner.getConst();
		if(Scanner.currentToken().equals("COMMA")){
			altNo = 1;
			Scanner.nextToken();
			intList = new INT_LIST();
			intList.parse();
		}
	}
	
	public int getAltNo(){
		return altNo;
	}
	public int getValue(){
		return value;
	}
	public INT_LIST getIntList(){
		return intList;
	}
}

// Reused code from Project 1, some modified to accomodate for new things-----

/**
 * Class for the OUT node; Parsing and accessing child node(s) enabled;
 */
class OUT {

	private EXPR expr;
	private ID_LIST idList;
	private int altNo;
	
	//Code used for Project 1, modified to use ID_LIST
	/*public void parse() throws IOException {
		Scanner.match("OUTPUT");
		expr = new EXPR(); expr.parse();
		Scanner.match("SEMICOLON");
		String token = Scanner.currentToken();
		if(token.equals("SEMICOLON")){
			System.out.println("ERROR: Invalid ';' token.");
			System.exit(2);
		}
	}
	*/
	public void parse() throws IOException {
		Scanner.match("OUTPUT");
		String token = Scanner.currentToken();
		idList = new ID_LIST();
		idList.parse();
		//expr = new EXPR();
		//expr.parse();
	}

	public EXPR getExpr() { 
		return expr; 
	}
	public ID_LIST getIdList(){
		return idList;
	}
}

/**
 * Class for the EXPR node; Parsing and accessing child node(s) enabled;
 */
class EXPR {

	private int altNo = 0;  
	private TERM term;
	private EXPR expr;
	private String op;

	public void parse() throws IOException {
		term = new TERM(); 
		term.parse();
		// Continue parsing if arithmetic operator is encountered;
		String token = Scanner.currentToken();
		if (token.equals("PLUS") || token.equals("MINUS")) {
			altNo = 1;
			op = token;
			Scanner.nextToken();
			expr = new EXPR(); expr.parse();
		}
	}

	public int getAltNo() { 
		return altNo; 
	}
	public TERM getTerm() { 
		return term; 
	}
	public EXPR getExpr() { 
		return expr; 
	}
	public String getOp() { 
		return op; 
	}
}

/**
 * Class for the TERM node; Parsing and accessing child node(s) enabled;
 */
class TERM {

	private int altNo = 0;  
	private FACTOR factor;
	private TERM term;

	public void parse() throws IOException {
		factor = new FACTOR(); 
		factor.parse();
		if (Scanner.currentToken().equals("TIMES")) {
			altNo = 1;
			Scanner.nextToken();
			term = new TERM(); 
			term.parse();
		}

	}

	public int getAltNo() { 
		return altNo; 
	}
	public FACTOR getFactor() { 
		return factor; 
	}
	public TERM getTerm() { 
		return term; 
	}
}

/**
 * Class for the FACTOR node; Parsing and accessing child node(s) enabled;
 */
class FACTOR {

	private int altNo;      
	private int value;
	private String id;
	private FACTOR factor;
	private EXPR expr;

	public void parse() throws IOException{
		String token = Scanner.currentToken();
		if (token.contains("CONST")) { // const;
			altNo = 0;
			value = Scanner.getConst();
		} else if (token.contains("ID")) { // id;
			altNo = 1;
			id = Scanner.getID();
		} else if (token.equals("MINUS")) { // -<factor>;
			altNo = 2;
			Scanner.nextToken();
			factor = new FACTOR(); factor.parse();
		} else if (token.equals("LEFT_PAREN")) { // (<expr>);
			altNo = 3;
			Scanner.match("LEFT_PAREN");
			expr = new EXPR(); expr.parse();
			Scanner.match("RIGHT_PAREN");
		} else {
			System.out.println("ERROR: Factor Expected, received " + token);
			System.exit(2);
		}
	}

	public int getAltNo() { return altNo; }
	public int getValue() { return value; }
	public String getId() { return id; }
	public FACTOR getFactor() { return factor; }
	public EXPR getExpr() { return expr; }
}
