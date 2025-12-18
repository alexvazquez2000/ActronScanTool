import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ParseTextFile {

	private String fileName;

	private List<String> keys = new ArrayList<>();
	private List<FrameData> frames = new ArrayList<>();

	public ParseTextFile(String fileName) {
		this.fileName = fileName;

	}

	public static void main(String[] args) {
		// String fileName = "1_TPC61AC.tmp.txt";
		// String fileName = "ram_cold_p0420_20220109.txt";
		String fileName = "Ram_2_PassangerSideOX.txt";
		ParseTextFile p = new ParseTextFile(fileName);
		p.parseFile();
	}

	public void parseFile() {

		File file = new File(fileName);

		InputStream gzInStream = null;

		BufferedReader bf = null;
		int i = 0;
		try {
			if (fileName.endsWith(".gz")) {
				gzInStream = new GZIPInputStream(new FileInputStream(file));
				bf = new BufferedReader(new InputStreamReader(gzInStream));
			} else {
				bf = new BufferedReader(new FileReader(file));
			}

//			Recorded Data
//
//			MIL STATUS       OFF
//			ABSLT TPS %      6.2
//			ENG SPEED RPM    775
//			BARO PRS KPA      85
//			CALC LOAD %     42.3
//			OBD2 STAT   JA/EU/CA
//
//			Frame  -5 Time -27.5
//
//
//			MIL STATUS($00)  OFF
//			ABSLT TPS($00)  20.7

			String line;
			int errors = 0;
			FrameData frameData = new FrameData();
			while ((line = bf.readLine()) != null) {
				i++;
				line = line.trim();
				if (line.length() == 0)
					continue;
				if (line.startsWith("Recorded Data"))
					continue;

				if (line.startsWith("Frame")) {
					// System.out.println("FRAME '" + line + "'");
					// Frame -5 Time -27.5
					frameData.setFrameNumber(line);
					frames.add(frameData);
					// System.out.println("-------------------");
					frameData = new FrameData();
				} else {
					line = fixLine(line);

					if (line.indexOf("  ") > -1) {
						// split on the " "
						String key = line.substring(0, line.indexOf("  ")).trim();
						String value = line.substring(line.indexOf("  ")).trim();

						if (value.indexOf(")") > -1) {
							value = value.substring(value.indexOf(")") + 1).trim();
						}
						// if (key.startsWith("O2S")) {
						// System.out.println("LINE '" + line + "' key ='" + key + "' value='" + value
						// +"'" );
						// }
						if (!keys.contains(key)) {
							keys.add(key);
						}
						frameData.addData(key, value);
					} else {
						errors++;
						System.out.println(errors + " - Found line '" + line + "'");
					}
				}
			}

			System.out.println("\n\n------------------");
			System.out.print("Frame   ");
			for (FrameData fData : frames) {
				System.out.print("\t'" + fData.getFrameNumber() + "'");
			}
			System.out.println();
			System.out.print("Time    ");
			for (FrameData fData : frames) {
				System.out.print("\t'" + fData.getFrameTime() + "'");
			}
			System.out.println();

			for (String key : keys) {
				if (!key.startsWith("O2S"))
					continue;
				System.out.print(key + "  ");
				for (FrameData fData : frames) {
					String value = " - ";
					if (fData.getFrameData().get(key) != null) {
						value = "'" + fData.getFrameData().get(key) + "'";
					}
					System.out.print("\t" + value);
				}
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private String fixLine(String line) {
		String fixedLine = line.replaceAll("\\(\\$00\\)", " 00  ");
		fixedLine = fixedLine.replaceAll("\\(\\$01\\)", " 01  ");
		fixedLine = fixedLine.replaceAll("\\(\\$0", " 0N  ");
		fixedLine = fixedLine.replaceAll("\\(\\$", " 0X  ");

		if (fixedLine.indexOf("  ") == -1 && fixedLine.indexOf("%") > -1) {
			fixedLine = fixedLine.replaceAll("%", "%  ");
		} else if (fixedLine.indexOf("  ") == -1 && fixedLine.indexOf(" h:m") > -1) {
			fixedLine = fixedLine.replaceAll("h:m", "h:m  ");
		} else if (fixedLine.indexOf("  ") == -1 && fixedLine.indexOf(" GR/SE") > -1) {
			fixedLine = fixedLine.replaceAll(" GR/SE", " GR/SE  ");
		} else if (fixedLine.indexOf("  ") == -1 && fixedLine.indexOf(" DE") > -1) {
			fixedLine = fixedLine.replaceAll(" DE", " DE  ");
		}

		if (fixedLine.length() > 14 && fixedLine.indexOf("  ") == -1) {
			System.out.println("ERROR '" + line + "'");
			fixedLine = fixedLine.substring(0, 14) + "  " + fixedLine.substring(14);
			System.out.println("FIXED '" + fixedLine + "'\n\n");
		}

		if (fixedLine.startsWith("O2S") && fixedLine.contains(" 00")) {
			fixedLine = fixedLine.replace(" 00", " V");
		}
		return fixedLine;
	}

	/**
	 * @return the keys
	 */
	public List<String> getKeys() {
		return keys;
	}

	/**
	 * @return the frames
	 */
	public List<FrameData> getFrames() {
		return frames;
	}
}