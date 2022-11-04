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
		should_convert_diagram_to_output("cl_analysis.uxf", "png", "analysis.png", "A5D6A58C6222723AF2BFAAB33CAB9C7F");
	}

	@Test
	public void should_convert_analysis_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("cl_analysis.uxf", "svg", "analysis.svg", "C8C3A6F0AD2FD18F2060DE02DC9DEE53");
	}

	@Test
	public void should_convert_analysis_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("cl_analysis.uxf", "analysis.pdf", 2827);
	}

	@Test
	public void should_convert_analysis_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("cl_analysis.uxf", "jpeg", "analysis.jpeg", "869B53DAE13C8A764B744179C507CB73");
	}

	// object

	@Test
	public void should_convert_object_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "png", "object.png", "20AE4938EF494C4A1283F714A3EF0ADB");
	}

	@Test
	public void should_convert_object_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "svg", "object.svg", "C0BEE70459FAA5663FDF023CD2D758EF");
	}

	@Test
	public void should_convert_object_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("cl_object.uxf", "object.pdf", 3481);
	}

	@Test
	public void should_convert_object_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "jpeg", "object.jpeg", "452FC3D2F60D03DD9DA3881CB9340EA9");
	}

	// struct

	@Test
	public void should_convert_struct_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "png", "struct.png", "BEDD82E06A1F58326C5D84E0CA2053DD");
	}

	@Test
	public void should_convert_struct_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "svg", "struct.svg", "6492B7242E03D7424F346A44396B1E25");
	}

	@Test
	public void should_convert_struct_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("kxd_composite_structure.uxf", "struct.pdf", 2930);
	}

	@Test
	public void should_convert_struct_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "jpeg", "struct.jpeg", "CFA12AED8295B66CB6A692CA6538D7ED");
	}

	// state

	@Test
	public void should_convert_state_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "png", "state.png", "DA76399E42E10CDB76246F0B005C0225");
	}

	@Test
	public void should_convert_state_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "svg", "state.svg", "0FDFEF7C7F00912AFF5A7308CA0B1448");
	}

	@Test
	public void should_convert_state_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("sm_complex_state.uxf", "state.pdf", 4613);
	}

	@Test
	public void should_convert_state_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "jpeg", "state.jpeg", "274B9C328F9B13A441A4490D05E84845");
	}

	// seq

	@Test
	public void should_convert_seq_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "png", "seq.png", "43E5FA5C25673F0824480FB1BEC48CA6");
	}

	@Test
	public void should_convert_seq_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "svg", "seq.svg", "2F6E0FCB68E62F338AF7F596C519DC76");
	}

	@Test
	public void should_convert_seq_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("sd_sequence.uxf", "seq.pdf", 7714);
	}

	@Test
	public void should_convert_seq_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "jpeg", "seq.jpeg", "B98DBCF56123F3D5CC3497910F5AC506");
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
		int approx = 10;
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
