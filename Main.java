import java.io.IOException;

public class Main {

	/**
	 * Interpretation := scan tokens => generate parse tree => print program => execute program;
	 *
	 * @param args  command line arguments; args[0] is the program; args[1] is the input data;
	 */
	public static void main (String[] args) throws IOException {
		try {
			Scanner.begin(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Unknown option or incorrect number of arguments");
			System.exit(2);
		}

		// Parser to generate parse tree
		// OUT parseTree = Parser.getParseTree();
		PROG parseTree = Parser.getParseTree();

		// Pretty Printer line below. Uncomment to see
		//Printer.prettyPrint(parseTree);

		try {
			Executor.execute(parseTree, args[1]); 
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Unknown option or incorrect number of arguments");
			System.exit(2);
		}
		System.out.println();
	}
}
