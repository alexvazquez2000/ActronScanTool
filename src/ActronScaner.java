import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class ActronScaner extends JPanel {

	private static final long serialVersionUID = 643775767566507534L;
	private static JFrame frame;

	private ActronScaner() {

		final CategoryDataset dataset = new DefaultCategoryDataset();

		final FileDialog fileDialog = new FileDialog(frame,
				"Select Actron Data File");
		fileDialog.setMultipleMode(false);
		JButton fileDialogButton = new JButton("File Dialog");
		fileDialogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fileDialog.setVisible(true);
				File files[] = fileDialog.getFiles();
				for (File file : files) {
					System.out.println("File: " + file.getName());
					processFile(file);
				}
			}

			private void processFile(File file) {
				ParseTextFile p = new ParseTextFile(file.getAbsolutePath());
				p.parseFile();

				for (String key : p.getKeys()) {
					if (!key.startsWith("O2S"))
						continue;
					if (!key.contains("V"))
						continue;
					for (FrameData fData : p.getFrames()) {
						if (fData.getFrameData().get(key) != null) {
							double value = Double.valueOf(fData.getFrameData()
									.get(key));
							String column = fData.getFrameTime();
							String series = key;
							((DefaultCategoryDataset) dataset).addValue(value,
									series, column);
						}
					}
				}
			}
		});
		add(fileDialogButton);

		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(1400, 400));
		add(chartPanel);
	}

	/**
	 * Creates a sample chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return The chart.
	 */
	private JFreeChart createChart(final CategoryDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createLineChart(
				"Line Chart Demo 1", // chart title
				"Type", // domain axis label
				"Value", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);
		// legend.setShapeScaleX(1.5);
		// legend.setShapeScaleY(1.5);
		// legend.setDisplaySeriesLines(true);

		chart.setBackgroundPaint(Color.white);

		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.white);

		// customise the range axis...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);

		// ****************************************************************************
		// * JFREECHART DEVELOPER GUIDE *
		// * The JFreeChart Developer Guide, written by David Gilbert, is
		// available *
		// * to purchase from Object Refinery Limited: *
		// * *
		// * http://www.object-refinery.com/jfreechart/guide.html *
		// * *
		// * Sales are used to provide funding for the JFreeChart project -
		// please *
		// * support us so that we can continue developing free software. *
		// ****************************************************************************

		// // customise the renderer...
		// final LineAndShapeRenderer renderer = (LineAndShapeRenderer)
		// plot.getRenderer();
		// // renderer.setDrawShapes(true);
		//
		// renderer.setSeriesStroke(
		// 0, new BasicStroke(
		// 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
		// 1.0f, new float[] {10.0f, 6.0f}, 0.0f
		// )
		// );
		// renderer.setSeriesStroke(
		// 1, new BasicStroke(
		// 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
		// 1.0f, new float[] {6.0f, 6.0f}, 0.0f
		// )
		// );
		// renderer.setSeriesStroke(
		// 2, new BasicStroke(
		// 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
		// 1.0f, new float[] {2.0f, 6.0f}, 0.0f
		// )
		// );
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;
	}

	public static void main(String[] args) {
		frame = new JFrame("Axtron Scan Tool");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new ActronScaner());
		frame.setLocation(20, 20);
		frame.pack();
		frame.setVisible(true);
	}

}
