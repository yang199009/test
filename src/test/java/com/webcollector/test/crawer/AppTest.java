package com.webcollector.test.crawer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    public static void main(String[] args) {
    	ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();  
    	  for (int i = 0; i < 10; i++) {  
    	   final int index = i;  
    	   singleThreadExecutor.execute(new Runnable() {  
    	    public void run() {  
    	     try {  
    	      System.out.println(index);  
    	      Thread.sleep(2000);  
    	     } catch (InterruptedException e) {  
    	      e.printStackTrace();  
    	     }  
    	    }  
    	   });  
    	  }
    	  }
}
