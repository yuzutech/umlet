package com.baselet.diagram;

import com.baselet.control.config.handler.ConfigHandler;
import com.baselet.control.enums.Program;
import com.baselet.control.enums.RuntimeType;
import com.baselet.control.util.Utils;
import com.baselet.diagram.io.OutputHandler;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import static com.baselet.control.util.Utils.readBuildInfo;
import static org.assertj.core.api.Assertions.assertThat;

public class ConverterTest {

	@Before
	public void before() {
		Utils.BuildInfo buildInfo = readBuildInfo();
		Program.init(buildInfo.version, RuntimeType.BATCH);
		ConfigHandler.loadConfig();
	}

	// analysis

	@Test
	public void should_convert_analysis_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("cl_analysis.uxf", "png", "analysis.png", "4724D859D2CC352C4483967CD650EA3F");
	}

	@Test
	public void should_convert_analysis_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("cl_analysis.uxf", "svg", "analysis.svg", "10BE3BA0254F599B80668DACEB02F948");
	}

	@Test
	public void should_convert_analysis_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("cl_analysis.uxf", "analysis.pdf", 2827);
	}

	@Test
	public void should_convert_analysis_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("cl_analysis.uxf", "jpeg", "analysis.jpeg", "920B05549C6D1D5F6AFB5905335B746A");
	}

	// object

	@Test
	public void should_convert_object_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "png", "object.png", "FDD842A8CD64F8E5151AFEF0EEB8462C");
	}

	@Test
	public void should_convert_object_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "svg", "object.svg", "96A0B9B6A7232AF686A17DC63B31C48E");
	}

	@Test
	public void should_convert_object_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("cl_object.uxf", "object.pdf", 3448);
	}

	@Test
	public void should_convert_object_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "jpeg", "object.jpeg", "EB0A42B76D1B795203B418BD6E114047");
	}

	// struct

	@Test
	public void should_convert_struct_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "png", "struct.png", "EC6D93BFA7676EF04A91842D72C28B7C");
	}

	@Test
	public void should_convert_struct_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "svg", "struct.svg", "46AF25C1382CF0E33D1C89BF0D16EF91");
	}

	@Test
	public void should_convert_struct_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("kxd_composite_structure.uxf", "struct.pdf", 2930);
	}

	@Test
	public void should_convert_struct_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "jpeg", "struct.jpeg", "772A9245EE6C832F1CB1EB7BB3D9C810");
	}

	// state

	@Test
	public void should_convert_state_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "png", "state.png", "D756D51CE76CE8985DC41CED4EADE84E");
	}

	@Test
	public void should_convert_state_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "svg", "state.svg", "9D35554E37E6C23EB275C0DBDEFA87F3");
	}

	@Test
	public void should_convert_state_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("sm_complex_state.uxf", "state.pdf", 4593);
	}

	@Test
	public void should_convert_state_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "jpeg", "state.jpeg", "F5555DB6DF01EA3D03DB3B12F5E8F138");
	}

	// seq

	@Test
	public void should_convert_seq_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "png", "seq.png", "04D2CB59FD5108862A36E5C598B298CE");
	}

	@Test
	public void should_convert_seq_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "svg", "seq.svg", "5955633AE8ABF7F7A348419A69204E8E");
	}

	@Test
	public void should_convert_seq_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("sd_sequence.uxf", "seq.pdf", 6990);
	}

	@Test
	public void should_convert_seq_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "jpeg", "seq.jpeg", "9552FEEE5155167ADC53CB339399F1FB");
	}

	private void should_convert_diagram_to_output(String diagramName, String outputFormat, String outputPath, String expectedHash) throws Exception, IOException, NoSuchAlgorithmException {
		InputStream diagram = Thread.currentThread().getContextClassLoader().getResourceAsStream("./" + diagramName);
		byte[] result = convert(read(diagram), outputFormat);
		// write the file in target to debug
		Files.write(Paths.get("target", outputPath), result);
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(result);
		byte[] digest = md.digest();
		String hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		assertThat(hash).isEqualTo(expectedHash);
	}

	private void should_convert_diagram_to_pdf(String diagramName, String outputPath, int expectedSize) throws Exception, IOException, NoSuchAlgorithmException {
		InputStream diagram = Thread.currentThread().getContextClassLoader().getResourceAsStream("./" + diagramName);
		byte[] result = convert(read(diagram), "pdf");
		// write the file in target to debug
		Files.write(Paths.get("target", outputPath), result);
		int approx = 20;
		assertThat(result.length).isBetween(expectedSize - approx, expectedSize + approx);
	}

	private static String read(InputStream input) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

	private byte[] convert(String source, String format) throws Exception, IOException {
		DiagramHandler handler = DiagramHandler.forExport(source);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputHandler.createToStream(format, baos, handler);
		return baos.toByteArray();
	}
}
