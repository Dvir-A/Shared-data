package mmn15_1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;


public class SharedData {
	private int x=0;
	private int y=0;
	private int printCnt=0;
	
	protected boolean moving = false;
	
	private Semaphore semM = new Semaphore(1,true);
	private Semaphore semP = new Semaphore(1,true);
	
	public static final int NUM_OF_PAIRS = 10;
	public static final int MAX_VAL = 100;
	
	private final char question;
	
	public SharedData(int x,int y,final char question) {
		this.x=x;
		this.y=y;
		this.question = question;
	}

	public  SharedData get() {
		if(question == 'c') {
			final SharedData shared;
			try {
				semP.acquire();
				printCnt++;
				if(printCnt==1) {
					if(!semM.tryAcquire()) {
						printCnt--;
						semP.release();
						return get();
					}
				}
				semP.release();
				shared = new SharedData(x, y, 'd');
				semP.acquire();
				printCnt--;
				if(printCnt == 0) {
					semM.release();
				}
				semP.release();
				return shared;
			} catch (InterruptedException e) {
				System.err.println("Interrupted exception occured :"+e.getMessage());
			}
		}
		return (new SharedData(x, y,'d'));
	}
	
	public  void move(int dx,int dy) {
		if(question == 'c') {
			try {
				semM.acquire();	
				semP.acquire();
				x = x+dx;
				y = y + dy;
			} catch (InterruptedException e) {				
				System.err.println("Interrupted exception occured :"+e.getMessage());
			}finally {
				semP.release();
				semM.release();
			}
		}else {
			x = x+dx;
			y = y + dy;
		}
	}
	@Override
	public String toString() {
		return new String("("+x+","+y+")");
	}
	
	
	
	public static void main(String[] args) {
		String seperate = "#################################################################";
		seperate += ("\n"+seperate);
		for(char ch ='a';ch<'c';ch++) {
			SharedData sharedD = new SharedData(1, 2,ch);
			System.out.println(seperate+"\nQuestion :"+ch);
			Mover mover = new Mover(sharedD, ch,0);
			Printer printer = new Printer(sharedD,ch,0);
			mover.start();
			printer.start();
			try {
				mover.join();
				printer.join();
			} catch (InterruptedException e) {
				System.err.println("\nInterrupted exception occurred in question :"+ch);
			}
		}
		
		System.out.println(seperate+"\nQuestion : c");
		final int MOV_PRNT_NUM = 3;
		SharedData sharedD = new SharedData(1, 2,'c');
		ArrayList<Thread> list = new ArrayList<>(3*MOV_PRNT_NUM);
		for(int i=0;i<MOV_PRNT_NUM;i++) {
			list.add(new Printer(sharedD,'c',i));
			list.add(new Mover(sharedD, 'c',i));
			list.add(new Printer(sharedD,'c',3*MOV_PRNT_NUM-i));
		}
		Iterator<Thread> iterator =list.iterator();
		ExecutorService exec = Executors.newFixedThreadPool(3*MOV_PRNT_NUM);
		for (;iterator.hasNext();) {
			final Thread thread = iterator.next();
			exec.execute(new Runnable() {
				@Override
				public void run() {
					thread.start();
				}
			});
		}
		exec.shutdown();
	}
}
