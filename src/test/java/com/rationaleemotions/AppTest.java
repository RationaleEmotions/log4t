package com.rationaleemotions;


import org.testng.annotations.Test;

public class AppTest {

  @Test
  public void testMethod1() {
    Log4T.getLogger().info("testMethod1");
  }
  @Test
  public void testMethod2() {
    Log4T.getLogger().info("testMethod1");
  }

  @Test(invocationCount = 2)
  public void tm() {
    Log4T.getLogger().info("testMethod1");
  }
}
