package ee.eee.testwebsock.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class ImageResizer {

	private final ExecutorService executor;

	public ImageResizer() {
		executor = Executors.newFixedThreadPool(32);
	}

	public Map<ImageObject.ImageSize, byte[]> resizeImageOnMap(byte[] image) throws IOException {
		BufferedImage bufferedImage = byteArrayToBufferedImage(image);
		Map<ImageObject.ImageSize, Future<byte[]>> futureMap = new HashMap<>();
		Arrays.stream(ImageObject.imageSizes).forEach(
				size -> futureMap.put(size, executor.submit(() -> resizeImage(bufferedImage, size)))
		);
		Map<ImageObject.ImageSize, byte[]> resultMap = new HashMap<>();
		Arrays.stream(ImageObject.imageSizes).forEach(
				size -> {
					try {
						resultMap.put(size, futureMap.get(size).get(50, TimeUnit.MILLISECONDS));
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						resultMap.put(size, null);
						e.printStackTrace();
					}
				});
		return resultMap;
	}


	public static byte[] bufferedImageToByteArray(BufferedImage bufferedImage) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", outputStream);
		outputStream.flush();
		byte[] imageData = outputStream.toByteArray();
		outputStream.close();
		return imageData;
	}

	public static BufferedImage byteArrayToBufferedImage(byte[] data) throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		inputStream.close();
		return bufferedImage;
	}

	public static byte[] resizeImage(BufferedImage image, ImageObject.ImageSize imageSize) throws IOException {
		BufferedImage resultImage = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = resultImage.createGraphics();
		g2d.drawImage(image, 0, 0, imageSize.width, imageSize.height, null);
		g2d.dispose();
		return bufferedImageToByteArray(resultImage);
	}


}
