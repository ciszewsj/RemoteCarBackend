package ee.eee.testwebsock.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImageResizer {

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
