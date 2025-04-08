import java.awt.Color;
import java.awt.image.BufferedImage;
/**
 * Implementasi dari metode pengukuran error
 */
public class ErrorMeasurementImpl implements ErrorMeasurement {
    public static final int VARIANCE = 1;
    public static final int MEAN_ABSOLUTE_DEVIATION = 2;
    public static final int MAX_PIXEL_DIFFERENCE = 3;
    public static final int ENTROPY = 4;
    
    private final int method;
    
    public ErrorMeasurementImpl(int method) {
        this.method = method;
    }
    
    @Override
    public double calculateError(BufferedImage image, int x, int y, int width, int height) {
        return switch (method) {
            case VARIANCE -> calculateVariance(image, x, y, width, height);
            case MEAN_ABSOLUTE_DEVIATION -> calculateMAD(image, x, y, width, height);
            case MAX_PIXEL_DIFFERENCE -> calculateMaxPixelDifference(image, x, y, width, height);
            case ENTROPY -> calculateEntropy(image, x, y, width, height);
            default -> calculateVariance(image, x, y, width, height);
        };
    }
    
    /**
     * Menghitung variansi sesuai rumus dalam tugas
     */
    private double calculateVariance(BufferedImage image, int x, int y, int width, int height) {
        // Hitung rata-rata untuk setiap kanal warna
        double[] avgValues = calculateAverages(image, x, y, width, height);
        double avgRed = avgValues[0];
        double avgGreen = avgValues[1];
        double avgBlue = avgValues[2];
        
        double sumRedVariance = 0;
        double sumGreenVariance = 0;
        double sumBlueVariance = 0;
        int pixelCount = 0;
        
        // Hitung sum of squared differences
        for (int i = x; i < x + width && i < image.getWidth(); i++) {
            for (int j = y; j < y + height && j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j));
                
                sumRedVariance += Math.pow(color.getRed() - avgRed, 2);
                sumGreenVariance += Math.pow(color.getGreen() - avgGreen, 2);
                sumBlueVariance += Math.pow(color.getBlue() - avgBlue, 2);
                
                pixelCount++;
            }
        }
        
        // Hitung variansi setiap kanal
        double varianceRed = pixelCount > 0 ? sumRedVariance / pixelCount : 0;
        double varianceGreen = pixelCount > 0 ? sumGreenVariance / pixelCount : 0;
        double varianceBlue = pixelCount > 0 ? sumBlueVariance / pixelCount : 0;
        
        // Rata-rata variansi dari ketiga kanal
        return (varianceRed + varianceGreen + varianceBlue) / 3.0;
    }
    
    /**
     * Menghitung Mean Absolute Deviation sesuai rumus
     */
    private double calculateMAD(BufferedImage image, int x, int y, int width, int height) {
        // Hitung rata-rata untuk setiap kanal warna
        double[] avgValues = calculateAverages(image, x, y, width, height);
        double avgRed = avgValues[0];
        double avgGreen = avgValues[1];
        double avgBlue = avgValues[2];
        
        double sumRedMAD = 0;
        double sumGreenMAD = 0;
        double sumBlueMAD = 0;
        int pixelCount = 0;
        
        // Hitung sum of absolute differences
        for (int i = x; i < x + width && i < image.getWidth(); i++) {
            for (int j = y; j < y + height && j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j));
                
                sumRedMAD += Math.abs(color.getRed() - avgRed);
                sumGreenMAD += Math.abs(color.getGreen() - avgGreen);
                sumBlueMAD += Math.abs(color.getBlue() - avgBlue);
                
                pixelCount++;
            }
        }
        
        // Hitung MAD setiap kanal
        double madRed = pixelCount > 0 ? sumRedMAD / pixelCount : 0;
        double madGreen = pixelCount > 0 ? sumGreenMAD / pixelCount : 0;
        double madBlue = pixelCount > 0 ? sumBlueMAD / pixelCount : 0;
        
        // Rata-rata MAD dari ketiga kanal
        return (madRed + madGreen + madBlue) / 3.0;
    }
    
    /**
     * Menghitung Max Pixel Difference sesuai rumus
     */
    private double calculateMaxPixelDifference(BufferedImage image, int x, int y, int width, int height) {
        int minRed = 255, minGreen = 255, minBlue = 255;
        int maxRed = 0, maxGreen = 0, maxBlue = 0;
        
        // Cari nilai min dan max untuk setiap kanal
        for (int i = x; i < x + width && i < image.getWidth(); i++) {
            for (int j = y; j < y + height && j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j));
                
                minRed = Math.min(minRed, color.getRed());
                minGreen = Math.min(minGreen, color.getGreen());
                minBlue = Math.min(minBlue, color.getBlue());
                
                maxRed = Math.max(maxRed, color.getRed());
                maxGreen = Math.max(maxGreen, color.getGreen());
                maxBlue = Math.max(maxBlue, color.getBlue());
            }
        }
        
        // Hitung selisih max-min setiap kanal
        double diffRed = maxRed - minRed;
        double diffGreen = maxGreen - minGreen;
        double diffBlue = maxBlue - minBlue;
        
        // Rata-rata selisih dari ketiga kanal
        return (diffRed + diffGreen + diffBlue) / 3.0;
    }
    
    /**
     * Menghitung Entropy sesuai rumus
     */
    private double calculateEntropy(BufferedImage image, int x, int y, int width, int height) {
        // Hitung histogram untuk setiap kanal (0-255)
        int[] histRed = new int[256];
        int[] histGreen = new int[256];
        int[] histBlue = new int[256];
        int pixelCount = 0;
        
        // Bangun histogram
        for (int i = x; i < x + width && i < image.getWidth(); i++) {
            for (int j = y; j < y + height && j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j));
                
                histRed[color.getRed()]++;
                histGreen[color.getGreen()]++;
                histBlue[color.getBlue()]++;
                
                pixelCount++;
            }
        }
        
        // Hitung entropy untuk setiap kanal
        double entropyRed = 0;
        double entropyGreen = 0;
        double entropyBlue = 0;
        
        if (pixelCount > 0) {
            for (int i = 0; i < 256; i++) {
                if (histRed[i] > 0) {
                    double probabilityRed = (double) histRed[i] / pixelCount;
                    entropyRed -= probabilityRed * (Math.log(probabilityRed) / Math.log(2));
                }
                
                if (histGreen[i] > 0) {
                    double probabilityGreen = (double) histGreen[i] / pixelCount;
                    entropyGreen -= probabilityGreen * (Math.log(probabilityGreen) / Math.log(2));
                }
                
                if (histBlue[i] > 0) {
                    double probabilityBlue = (double) histBlue[i] / pixelCount;
                    entropyBlue -= probabilityBlue * (Math.log(probabilityBlue) / Math.log(2));
                }
            }
        }
        
        // Rata-rata entropy dari ketiga kanal
        return (entropyRed + entropyGreen + entropyBlue) / 3.0;
    }
    
    /**
     * Helper method untuk menghitung rata-rata RGB
     */
    private double[] calculateAverages(BufferedImage image, int x, int y, int width, int height) {
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        int pixelCount = 0;
        
        for (int i = x; i < x + width && i < image.getWidth(); i++) {
            for (int j = y; j < y + height && j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j));
                sumRed += color.getRed();
                sumGreen += color.getGreen();
                sumBlue += color.getBlue();
                pixelCount++;
            }
        }
        
        double[] averages = new double[3];
        if (pixelCount > 0) {
            averages[0] = (double) sumRed / pixelCount;
            averages[1] = (double) sumGreen / pixelCount;
            averages[2] = (double) sumBlue / pixelCount;
        }
        
        return averages;
    }
}