package mmn15_1;

import java.security.SecureRandom;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Mover class responsible to move the SharedData 
 * 
 * @author Dvir Avikasis
 */
public class Mover extends Thread{
		private SharedData sharedData;
		private final SecureRandom sRand = new SecureRandom();
		private final ArrayBlockingQueue<Vector<Integer>> _pointsArr = new ArrayBlockingQueue<Vector<Integer>>(SharedData.NUM_OF_PAIRS); 
		private final char question;
		private final int MOVER_ID;
		
		/**
		 * create and initialize a new mover for the given SharedData 
		 * for a specific question 
		 * @param sData - the shared data
		 * @param question - a specific question for this Mover.
		 * @param id - Mover id
		 */
		public Mover(SharedData sData,final char question,int id) {
			super("Mover "+id);
			MOVER_ID = id;
			this.question = (question == 'c') ? 'a' : question;
			this.sharedData = sData;
			for (int i = 0; i < SharedData.NUM_OF_PAIRS; i++) {
				Vector<Integer> vec = new Vector<Integer>(2);	
				for(int j =0;j<2;j++) {
					vec.add(sRand.nextInt(SharedData.MAX_VAL));
				}
				_pointsArr.add(vec);
			}
		}
		
		@Override
		public  void run() {
			switch (question) {
			case 'a': // for question c too. 
				while(!_pointsArr.isEmpty()) {
					this.sharedData.move(_pointsArr.peek().get(0), _pointsArr.peek().get(1));
					//synchronized(System.out) {
						System.out.println("\nMover "+MOVER_ID+" >>The point moved by dx = "+_pointsArr.peek().get(0)+" dy = "+_pointsArr.peek().get(1));
					//}
					_pointsArr.poll();
				}
				break;
			case 'b':
				while(!_pointsArr.isEmpty()) {
					synchronized(sharedData) {
						if(!sharedData.moving) {
							continue;
						}
						sharedData.move(_pointsArr.peek().get(0), _pointsArr.peek().get(1));
						sharedData.moving = false;
						sharedData.notifyAll();
					}
					//synchronized(System.out) {
						System.out.println("The point moved by dx = "+_pointsArr.peek().get(0)+" dy = "+_pointsArr.peek().get(1));
					//}
					_pointsArr.poll();
					
				}
				break;
			default:
				break;
			}
		}
		
	
}
