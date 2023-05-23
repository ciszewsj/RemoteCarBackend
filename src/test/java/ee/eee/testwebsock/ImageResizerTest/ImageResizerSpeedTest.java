package ee.eee.testwebsock.ImageResizerTest;

import ee.eee.testwebsock.utils.ImageObject;
import ee.eee.testwebsock.utils.ImageResizer;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.imgscalr.Scalr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.resize;

@Slf4j
@SpringBootTest
public class ImageResizerSpeedTest {

	private BufferedImage bufferedImage;
	private final ImageObject.ImageSize imageSize = ImageObject.ImageSize.P720;
	private final int tries = 10;
	private final String outputDir = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath();

	@BeforeEach
	void beforeTest() throws IOException {
		bufferedImage = ImageIO.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("test.jpg")));
	}

	@Test
	public void imageResizerG2DTest() throws IOException {
		int newWidth = imageSize.width;
		int newHeight = imageSize.height;

		BufferedImage resultImage = null;

		long result = 0L;
		for (int i = 0; i < tries; i++) {
			long timeStart = System.currentTimeMillis();
			resultImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = resultImage.createGraphics();
			g2d.drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);
			g2d.dispose();
			result += System.currentTimeMillis() - timeStart;
		}
		File outputFile = new File(outputDir + "result/resultG2D.jpg");
		ImageIO.write(resultImage, "jpg", outputFile);

		log.info("TIME EXECUTED : {} / {} = {}", result, tries, result / tries);
	}

	@Test
	public void imageResizerImageResizeTest() throws IOException {
		int newWidth = imageSize.width;
		int newHeight = imageSize.height;
		long result = 0L;

		BufferedImage resultImage = null;

		for (int i = 0; i < tries; i++) {
			long timeStart = System.currentTimeMillis();
			Image resultingImage = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
			resultImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			resultImage.getGraphics().drawImage(resultingImage, 0, 0, null);
			result += System.currentTimeMillis() - timeStart;
		}
		File outputFile = new File(outputDir + "result/resulImageResize.jpg");
		ImageIO.write(resultImage, "jpg", outputFile);
		log.info("TIME EXECUTED : {} / {} = {}", result, tries, result / tries);

	}

	@Test
	public void imageResizerImgScalarTest() throws IOException {
		int newWidth = imageSize.width;
		int newHeight = imageSize.height;
		long result = 0L;

		BufferedImage resultImage = null;

		for (int i = 0; i < tries; i++) {
			long timeStart = System.currentTimeMillis();
			resultImage = Scalr.resize(bufferedImage, Scalr.Method.SPEED, newWidth, newHeight);
			result += System.currentTimeMillis() - timeStart;
		}
		File outputFile = new File(outputDir + "result/resultImgScalar.jpg");
		ImageIO.write(resultImage, "jpg", outputFile);
		log.info("TIME EXECUTED : {} / {} = {}", result, tries, result / tries);
	}

	@Test
	public void imageResizerThumbnailatorTest() throws IOException {
		int newWidth = imageSize.width;
		int newHeight = imageSize.height;
		long result = 0L;
		BufferedImage resultImage = null;
		for (int i = 0; i < tries; i++) {
			long timeStart = System.currentTimeMillis();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Thumbnails.of(bufferedImage)
					.size(newWidth, newHeight)
					.outputFormat("JPEG")
					.outputQuality(1)
					.toOutputStream(outputStream);
			byte[] data = outputStream.toByteArray();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
			resultImage = ImageIO.read(inputStream);
			result += System.currentTimeMillis() - timeStart;
		}
		File outputFile = new File(outputDir + "result/resultThumbnailator.jpg");
		ImageIO.write(resultImage, "jpg", outputFile);
		log.info("TIME EXECUTED : {} / {} = {}", result, tries, result / tries);
	}

	@Test
	public void imageResizerOpencvTest() throws IOException {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		int newWidth = imageSize.width;
		int newHeight = imageSize.height;
		long result = 0L;
		BufferedImage resultImage = null;
		for (int i = 0; i < tries; i++) {
			long timeStart = System.currentTimeMillis();

			Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
			byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
			mat.put(0, 0, data);

			Mat resizeimage = new Mat();
			Size scaleSize = new Size(newWidth, newHeight);
			resize(mat, resizeimage, scaleSize, 0, 0, INTER_AREA);
			resultImage = matToBufferedImage(resizeimage);
			result += System.currentTimeMillis() - timeStart;
		}
		File outputFile = new File(outputDir + "result/resultOpenCV.jpg");
		ImageIO.write(resultImage, "jpg", outputFile);
		log.info("TIME EXECUTED : {} / {} = {}", result, tries, result / tries);
	}

	private static BufferedImage matToBufferedImage(Mat mat) {
		int width = mat.cols();
		int height = mat.rows();
		int channels = mat.channels();

		byte[] data = new byte[width * height * channels];
		mat.get(0, 0, data);

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] dataBufferArray = dataBuffer.getData();
		System.arraycopy(data, 0, dataBufferArray, 0, data.length);

		return bufferedImage;
	}

	@Test
	public void speedReadImageTest() throws IOException {
		BufferedImage newBufferedImage = null;
		long result = 0L;
		for (int i = 0; i < tries; i++) {
			long timeStart = System.currentTimeMillis();
			byte[] convertedImage = ImageResizer.bufferedImageToByteArray(bufferedImage);
			newBufferedImage = ImageResizer.byteArrayToBufferedImage(convertedImage);
			result += System.currentTimeMillis() - timeStart;
		}
		File outputFile = new File(outputDir + "result/resultTest.jpg");
		ImageIO.write(newBufferedImage, "jpg", outputFile);
		log.info("TIME EXECUTED : {} / {} = {}", result, tries, result / tries);

	}

	@Test
	public void speedFullRescaleTest() throws IOException {
		byte[] convertedImage = ImageResizer.bufferedImageToByteArray(bufferedImage);

		Map<ImageObject.ImageSize, byte[]> imageMap = new HashMap<>();
		long result = 0L;
		for (int i = 0; i < tries; i++) {
			long timeStart = System.currentTimeMillis();
			ImageObject imageObject = new ImageObject(convertedImage);
			Arrays.stream(ImageObject.imageSizes)
					.forEach(size -> imageMap.put(size, imageObject.getImageBySize(size)));
			result += System.currentTimeMillis() - timeStart;
		}
		Arrays.stream(ImageObject.imageSizes).forEach(size -> {
			File outputFile = new File(outputDir + "result/resultTest" + size.toString() + ".jpg");
			try {
				ImageIO.write(ImageResizer.byteArrayToBufferedImage(imageMap.get(size)), "jpg", outputFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		log.info("TIME EXECUTED : {} / {} = {}", result, tries, result / tries);

	}

}
