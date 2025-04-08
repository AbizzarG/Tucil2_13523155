import java.awt.image.BufferedImage;
/**
 * Interface untuk metode pengukuran error
 */
public interface ErrorMeasurement {
    /**
     * Menghitung error untuk blok gambar tertentu
     * 
     * @param image Gambar yang diproses
     * @param x Koordinat X awal blok
     * @param y Koordinat Y awal blok
     * @param width Lebar blok
     * @param height Tinggi blok
     * @return Nilai error (semakin tinggi berarti semakin tidak seragam)
     */
    double calculateError(BufferedImage image, int x, int y, int width, int height);
}