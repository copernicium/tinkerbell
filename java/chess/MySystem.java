package chess;
public class MySystem{
	/**
	 * Get the line number of the line where this method is used
	 * @return the line number of where the method is called
	 */
	public static int getLineNumber() {
		return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}

	/**
	 * Get the name of the file this method is used in
	 * @return the name of the file where this method is called from
	 */
	public static String getFileName(){
		return Thread.currentThread().getStackTrace()[2].getFileName();
	}

	/**
	 * Exit the process if the a argument evaluates to flalse
	 * @param a the argument to check
	 * @param fileName the name of the file calling the assert function
	 * @param lineNumber the line number this function is called on
	 */
	public static void myAssert(boolean a,String fileName,int lineNumber){
		if(a) return;
		System.err.println(fileName + ":" + lineNumber + ": Asserting via MyAssert");
		System.exit(1);
	}
	/**
	 * Exits the process and prints a given error message
	 * @param message the message to print to the screen
	 * @param fileName the name of the file calling the assert function
	 * @param lineNumber the line number this function is called on
	 */
	public static void error(String message, String fileName,int lineNumber){
		System.err.println(fileName + ":" + lineNumber + ": " + message);
		System.exit(1);
	}
	/**
	 * Exits the process
	 * @param fileName the name of the file calling the assert function
	 * @param lineNumber the line number this function is called on
	 */
	public static void nyi(String fileName,int lineNumber){
		System.err.println("NYI " + fileName + ":" + lineNumber);
		System.exit(1);
	}
}