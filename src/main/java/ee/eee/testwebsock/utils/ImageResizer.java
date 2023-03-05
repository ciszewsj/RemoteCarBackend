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

	public byte[] resizeImage(byte[] image, ImageObject.ImageSize imageSize) {
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(image));
			ByteArrayOutputStream newImage = new ByteArrayOutputStream();
			BufferedImage newImg = resizeImage(img, imageSize.width, imageSize.height);
			ImageIO.write(newImg, "jpg", newImage);
			return newImage.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = resizedImage.createGraphics();
		graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		graphics2D.dispose();
		return resizedImage;
	}


}
