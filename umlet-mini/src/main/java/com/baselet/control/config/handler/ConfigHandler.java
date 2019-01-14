package com.baselet.control.config.handler;

import static com.baselet.control.constants.Constants.exportFormatList;

import java.awt.Point;
import java.io.FileInputStream;
import java.util.Properties;

import com.baselet.control.basics.geom.Dimension;
import com.baselet.control.config.Config;
import com.baselet.control.config.SharedConfig;
import com.baselet.control.enums.Program;
import com.baselet.control.util.Path;

public class ConfigHandler {

	private static final String PROGRAM_VERSION = "program_version";
	private static final String PROPERTIES_PANEL_FONTSIZE = "properties_panel_fontsize";
	private static final String EXPORT_SCALE = "export_scale"; // #510: scales exported images by this factor (default=1, e.g. 2 doubles the amount of pixels)
	private static final String EXPORT_DPI = "export_dpi"; // #510: if set, and the image format supports it (e.g. png), this dpi value is stored in the img metadata (default=null which means no dpi value is stored)
	private static final String DEFAULT_FONTSIZE = "default_fontsize";
	private static final String DEFAULT_FONTFAMILY = "default_fontfamily";
	private static final String SHOW_STICKINGPOLYGON = "show_stickingpolygon";
	private static final String SHOW_GRID = "show_grid";
	private static final String ENABLE_CUSTOM_ELEMENTS = "enable_custom_elements";
	private static final String UI_MANAGER = "ui_manager";
	private static final String PRINT_PADDING = "print_padding";
	private static final String PDF_EXPORT_FONT = "pdf_export_font";
	private static final String PDF_EXPORT_FONT_BOLD = "pdf_export_font_bold";
	private static final String PDF_EXPORT_FONT_ITALIC = "pdf_export_font_italic";
	private static final String PDF_EXPORT_FONT_BOLDITALIC = "pdf_export_font_bolditalic";
	private static final String LAST_EXPORT_FORMAT = "last_export_format";
	private static final String CHECK_FOR_UPDATES = "check_for_updates";
	private static final String SECURE_XML_PROCESSING = "secure_xml_processing";
	private static final String OPEN_FILE_HOME = "open_file_home";
	private static final String SAVE_FILE_HOME = "save_file_home";
	private static final String DEV_MODE = "dev_mode";
	private static final String LAST_USED_PALETTE = "last_used_palette";
	private static final String MAIN_SPLIT_POSITION = "main_split_position";
	private static final String RIGHT_SPLIT_POSITION = "right_split_position";
	private static final String START_MAXIMIZED = "start_maximized";
	private static final String MAIL_SPLIT_POSITION = "mail_split_position";
	private static final String PROGRAM_SIZE = "program_size";
	private static final String PROGRAM_LOCATION = "program_location";

	public static void loadConfig() {

		Properties props = loadProperties();
		if (props.isEmpty()) {
			return;
		}

		Config cfg = Config.getInstance();

		cfg.setProgramVersion(getStringProperty(props, PROGRAM_VERSION, Program.getInstance().getVersion()));
		cfg.setDefaultFontsize(getIntProperty(props, DEFAULT_FONTSIZE, cfg.getDefaultFontsize()));
		cfg.setPropertiesPanelFontsize(getIntProperty(props, PROPERTIES_PANEL_FONTSIZE, cfg.getPropertiesPanelFontsize()));
		cfg.setExportScale(getIntProperty(props, EXPORT_SCALE, cfg.getExportScale()));
		cfg.setExportDpi(getIntProperty(props, EXPORT_DPI, cfg.getExportDpi()));
		cfg.setDefaultFontFamily(getStringProperty(props, DEFAULT_FONTFAMILY, cfg.getDefaultFontFamily()));
		SharedConfig.getInstance().setShow_stickingpolygon(getBoolProperty(props, SHOW_STICKINGPOLYGON, SharedConfig.getInstance().isShow_stickingpolygon()));
		cfg.setShow_grid(getBoolProperty(props, SHOW_GRID, cfg.isShow_grid()));
		cfg.setEnable_custom_elements(getBoolProperty(props, ENABLE_CUSTOM_ELEMENTS, cfg.isEnable_custom_elements()));
		cfg.setUiManager(getStringProperty(props, UI_MANAGER, cfg.getUiManager()));
		cfg.setPrintPadding(getIntProperty(props, PRINT_PADDING, cfg.getPrintPadding()));
		cfg.setPdfExportFont(getStringProperty(props, PDF_EXPORT_FONT, cfg.getPdfExportFont()));
		cfg.setPdfExportFontBold(getStringProperty(props, PDF_EXPORT_FONT_BOLD, cfg.getPdfExportFontBold()));
		cfg.setPdfExportFontItalic(getStringProperty(props, PDF_EXPORT_FONT_ITALIC, cfg.getPdfExportFontItalic()));
		cfg.setPdfExportFontBoldItalic(getStringProperty(props, PDF_EXPORT_FONT_BOLDITALIC, cfg.getPdfExportFontBoldItalic()));
		cfg.setCheckForUpdates(getBoolProperty(props, CHECK_FOR_UPDATES, cfg.isCheckForUpdates()));
		cfg.setSecureXmlProcessing(getBoolProperty(props, SECURE_XML_PROCESSING, cfg.isSecureXmlProcessing()));
		cfg.setOpenFileHome(getStringProperty(props, OPEN_FILE_HOME, cfg.getOpenFileHome()));
		cfg.setSaveFileHome(getStringProperty(props, SAVE_FILE_HOME, cfg.getSaveFileHome()));
		SharedConfig.getInstance().setDev_mode(getBoolProperty(props, DEV_MODE, SharedConfig.getInstance().isDev_mode()));
		cfg.setLastUsedPalette(getStringProperty(props, LAST_USED_PALETTE, cfg.getLastUsedPalette()));
		cfg.setMain_split_position(getIntProperty(props, MAIN_SPLIT_POSITION, cfg.getMain_split_position()));
		cfg.setRight_split_position(getIntProperty(props, RIGHT_SPLIT_POSITION, cfg.getRight_split_position()));
		cfg.setMail_split_position(getIntProperty(props, MAIL_SPLIT_POSITION, cfg.getMail_split_position()));
		cfg.setStart_maximized(getBoolProperty(props, START_MAXIMIZED, cfg.isStart_maximized()));

		String lastExportFormatProp = getStringProperty(props, LAST_EXPORT_FORMAT, cfg.getLastExportFormat());
		if (lastExportFormatProp != null && !lastExportFormatProp.isEmpty() && exportFormatList.contains(lastExportFormatProp.toLowerCase())) {
			cfg.setLastExportFormat(getStringProperty(props, LAST_EXPORT_FORMAT, cfg.getLastExportFormat()));
		}

		// In case of start_maximized=true we don't store any size or location information
		if (!cfg.isStart_maximized()) {
			cfg.setProgram_size(getDimensionProperty(props, PROGRAM_SIZE, cfg.getProgram_size()));
			cfg.setProgram_location(getPointProperty(props, PROGRAM_LOCATION, cfg.getProgram_location()));
		}
	}

	private static Properties loadProperties() {
		Properties result = new Properties();

		if (Path.hasOsConformConfig()) {
			result = loadPropertiesFromFile(Path.osConformConfig());
		}
		else if (Path.hasLegacyConfig()) {
			result = loadPropertiesFromFile(Path.legacyConfig());
		}

		return result;
	}

	private static Properties loadPropertiesFromFile(String filePath) {
		Properties result = new Properties();

		try {
			FileInputStream inputStream = new FileInputStream(filePath);
			try {
				result.load(inputStream);
			} finally {
				inputStream.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private static Integer getIntProperty(Properties props, String key, Integer defaultValue) {
		String result = props.getProperty(key);
		if (result != null) {
			try {
				return Integer.parseInt(result);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	private static boolean getBoolProperty(Properties props, String key, boolean defaultValue) {
		String result = props.getProperty(key);
		if (result != null) {
			return Boolean.parseBoolean(result);
		}
		return defaultValue;
	}

	private static String getStringProperty(Properties props, String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	private static Dimension getDimensionProperty(Properties props, String key, Dimension defaultValue) {
		String result = props.getProperty(key);
		if (result != null) {
			try {
				int x = Integer.parseInt(result.substring(0, result.indexOf(",")));
				int y = Integer.parseInt(result.substring(result.indexOf(",") + 1));
				return new Dimension(x, y);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}

	private static Point getPointProperty(Properties props, String key, Point defaultValue) {
		String result = props.getProperty(key);
		if (result != null) {
			try {
				int x = Integer.parseInt(result.substring(0, result.indexOf(",")));
				int y = Integer.parseInt(result.substring(result.indexOf(",") + 1));
				return new Point(x, y);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return defaultValue;
	}
}
