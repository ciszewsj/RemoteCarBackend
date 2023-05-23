package ee.eee.testwebsock.utils;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ee.eee.testwebsock.utils.ImageResizer.byteArrayToBufferedImage;

@Data
public class ImageObject {
	public final static ImageSize[] imageSizes = {ImageSize.P144, ImageSize.P240, ImageSize.P360, ImageSize.P480, ImageSize.P720, ImageSize.P1080};
	public final static ImageSize defaultImageSize = ImageSize.P720;

	private Map<ImageSize, byte[]> imageSizeMap;


	public ImageObject(byte[] image) throws IOException {
		imageSizeMap = new HashMap<>();
		BufferedImage bufferedImage = byteArrayToBufferedImage(image);
		Arrays.stream(imageSizes).forEach(size -> {
			try {
				imageSizeMap.put(size, ImageResizer.resizeImage(bufferedImage, size));
			} catch (Exception e) {
				e.printStackTrace();
				imageSizeMap.put(size, null);
			}
		});
	}

	public byte[] getImageBySize(ImageSize size) {
		if (!imageSizeMap.containsKey(size)) {
			throw new IllegalStateException("Could not find element");
		}
		return imageSizeMap.get(size);
	}

	public enum ImageSize {
		P144(256, 144),
		P240(426, 240),
		P360(480, 360),
		P480(640, 480),
		P720(1280, 720),
		P1080(1920, 1080);

		public final int width;
		public final int height;

		ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}

}

