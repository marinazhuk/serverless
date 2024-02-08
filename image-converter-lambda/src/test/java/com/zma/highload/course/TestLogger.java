package com.zma.highload.course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.Arrays;

public class TestLogger implements LambdaLogger {
  public TestLogger(){}
  public void log(String message){
    System.out.println(message);
  }
  public void log(byte[] message){
    System.out.println(Arrays.toString(message));
  }
}
