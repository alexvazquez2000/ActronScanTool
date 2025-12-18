import java.util.Hashtable;

public class FrameData {

	private String frameNumber = "??";
	private String frameTime = "??";

	private Hashtable<String, String> frameData = new Hashtable<String, String>();

	public void addData(String key, String value) {
		frameData.put(key, value);
	}

	/**
	 * @return the frameData
	 */
	public Hashtable<String, String> getFrameData() {
		return frameData;
	}

	/**
	 * @param frameData the frameData to set
	 */
	public void setFrameData(Hashtable<String, String> frameData) {
		this.frameData = frameData;
	}

	/**
	 * @return the frameName
	 */
	public String getFrameNumber() {
		return frameNumber;
	}

	public String getFrameTime() {
		return frameTime;
	}

	/**
	 * @param frameName the frameName to set
	 */
	public void setFrameNumber(String frameNumber) {
		this.frameNumber = frameNumber.substring(0, frameNumber.indexOf("Time"));
		this.frameNumber = this.frameNumber.replace("Frame", "").trim();

		if (frameNumber.contains("Time")) {
			this.frameTime = frameNumber.substring(frameNumber.indexOf("Time") + 4).trim();
		}
	}

}
