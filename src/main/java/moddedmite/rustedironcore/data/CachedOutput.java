package moddedmite.rustedironcore.data;

import com.google.common.hash.HashCode;
import moddedmite.rustedironcore.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface CachedOutput {
	CachedOutput NO_CACHE = (path, input, hash) -> {
		FileUtil.createDirectoriesSafe(path.getParent());
		Files.write(path, input);
	};

	@SuppressWarnings("UnstableApiUsage")
    void writeIfNeeded(Path path, byte[] input, HashCode hash) throws IOException;
}
