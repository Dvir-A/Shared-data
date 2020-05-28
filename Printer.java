package mmn15_1;

/**
 * Printer class responsible to get the SharedData 
 * 
 * @author Dvir Avikasis
 */
public class Printer extends Thread{
	private int _printingCnt=0;
	private SharedData _sData;
	private char question;
	private final int PRINTER_ID;
	/**
	 * create and initialize a new Printer for the given SharedData 
	 * for a specific question 
	 * @param sData - the shared data
	 * @param question - a specific question for this Printer.
	 * @param id - Printer id
	 */
	public Printer(SharedData sData,final char question,int id) {
		super();
		this.question = (question == 'c') ? 'a' : question;
		this._sData = sData;
		PRINTER_ID = id;
	}

	@Override
	public  void run() {
		switch (question) {
		case 'a': //for question c too.
			while(_printingCnt<SharedData.NUM_OF_PAIRS) {
				printPoint();
			}
			break;
		case 'b':
			while(_printingCnt<=SharedData.NUM_OF_PAIRS) {
				synchronized(_sData) {
					if(_sData.moving) {
						continue;
					}
					printPoint();
					_sData.moving = true;
					_sData.notifyAll();
				}
			}
			break;
		default:
			break;
		};
	}
		
	/**
	 *  print the point from the shared data with the get() function
	 */
	public  void printPoint() {
		_printingCnt++;
		if(question=='b') {
			SharedData shared= _sData.get();;
			synchronized (System.out) {
				System.out.println("\nPrinter "+PRINTER_ID+ " >> "+shared.toString());
			}
			return;
		}
		System.out.println("\nPrinter "+PRINTER_ID+ " >> "+_sData.get().toString());
	}

}
