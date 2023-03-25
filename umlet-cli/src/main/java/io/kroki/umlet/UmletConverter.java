package io.kroki.umlet;

import com.baselet.control.config.handler.ConfigHandler;
import com.baselet.control.enums.Program;
import com.baselet.control.enums.RuntimeType;
import com.baselet.control.util.Utils;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.io.OutputHandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.lang.Exception;

import static com.baselet.control.util.Utils.readBuildInfo;

public class UmletConverter {

  static {
    Program.init("1.0.0", RuntimeType.BATCH);
    ConfigHandler.loadConfig();
  }

  public static byte[] convert(String source, String outputFormat) throws Exception {
    DiagramHandler handler = DiagramHandler.forExport(source);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputHandler.createToStream(outputFormat, baos, handler);
    byte[] result = baos.toByteArray();
    handler.close();
    return result;
  }
}
