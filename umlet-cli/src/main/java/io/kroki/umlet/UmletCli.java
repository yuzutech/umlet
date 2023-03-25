package io.kroki.umlet;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class UmletCli {
  public static void main(String[] args) throws Exception {
    String outputFormat = args[0];
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String input = reader.lines().collect(Collectors.joining("\n"));
    System.out.write(UmletConverter.convert(input, outputFormat));
  }
}
