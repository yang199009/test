package com.webcollector.test.crawer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ThreadTest {
	@Test
	public void cache(){
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
		  for (int i = 0; i < 10; i++) {  
		   final int index = i;  
		   try {  
		    Thread.sleep(index * 1000);  
		   } catch (InterruptedException e) {  
		    e.printStackTrace();  
		   }  
		   cachedThreadPool.execute(new Runnable() {  
		    public void run() {  
		     System.out.println(index);  
		    }  
		   });  
		  }  
	}  
	
	@Test
	public void fixed(){
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);  
		  for (int i = 0; i < 10; i++) {  
		   final int index = i;  
		   fixedThreadPool.execute(new Runnable() {  
		    public void run() {  
		     try {  
		      System.out.println(index);  
		      Thread.sleep(1000);  
		     } catch (InterruptedException e) {  
		      e.printStackTrace();  
		     }  
		    }  
		   });  
		  }  
	}
	
	@Test
	public void scheduled(){
		 ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);  
		  scheduledThreadPool.schedule(new Runnable() {  
		   public void run() {  
		    System.out.println("delay 3 seconds");  
		   }  
		  }, 3, TimeUnit.SECONDS);  
		 }  
}
