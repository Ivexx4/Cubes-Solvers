package cs.cube555;

import static cs.cube555.Util.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class Tools {

	private static Path cacheFile(String filename) {
		String configuredDirectory = System.getProperty("pt.cubesolvers.cube555.cacheDir");
		Path directory = configuredDirectory == null || configuredDirectory.isBlank()
				? Path.of(System.getProperty("user.home"), ".cubesolvers", "cube555")
				: Path.of(configuredDirectory);
		return directory.resolve(filename);
	}

	static boolean SaveToFile(String filename, Object obj) {
		try {
			Path file = cacheFile(filename);
			Files.createDirectories(file.getParent());
			try (ObjectOutputStream output = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(file.toFile())))) {
				output.writeObject(obj);
			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	static Object LoadFromFile(String filename) {
		java.io.File cacheFile = cacheFile(filename).toFile();
		if (!cacheFile.isFile()) {
			return null;
		}
		try (ObjectInputStream input = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(cacheFile)))) {
			return input.readObject();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	static Random gen = new Random();

	static CubieCube randomCubieCube(Random gen) {
		CubieCube cc = new CubieCube();
		for (int i = 0; i < 23; i++) {
			swap(cc.xCenter, i, i + gen.nextInt(24 - i));
			swap(cc.tCenter, i, i + gen.nextInt(24 - i));
			swap(cc.wEdge, i, i + gen.nextInt(24 - i));
		}
		int eoSum = 0;
		int eParity = 0;
		for (int i = 0; i < 11; i++) {
			int swap = gen.nextInt(12 - i);
			if (swap != 0) {
				swap(cc.mEdge, i, i + swap);
				eParity ^= 1;
			}
			int flip = gen.nextInt(2);
			cc.mEdge[i] ^= flip;
			eoSum ^= flip;
		}
		cc.mEdge[11] ^= eoSum;
		int cp = 0;
		do {
			cp = gen.nextInt(40320);
		} while (eParity != getParity(cp, 8));
		cc.corner.copy(new CubieCube.CornerCube(cp, gen.nextInt(2187)));
		return cc;
	}

	static CubieCube randomCubieCube() {
		return randomCubieCube(gen);
	}

	public static String randomCube(Random gen) {
		return randomCubieCube(gen).toFacelet();
	}

	public static String randomCube() {
		return randomCube(gen);
	}
}