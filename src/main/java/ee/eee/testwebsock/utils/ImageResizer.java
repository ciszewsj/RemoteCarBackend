package ee.eee.testwebsock.utils;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImageResizer {

	public ImageResizer() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	}

	public byte[] resizeImage(byte[] image, ImageObject.ImageSize imageSize) { // TODO: FIX
		return image;
	}


}
