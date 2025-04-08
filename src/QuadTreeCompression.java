import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
/**
 * Kelas utama untuk proses kompresi gambar dengan quadtree
 */
public class QuadTreeCompression {
    private BufferedImage originalImage;
    private BufferedImage compressedImage;
    private QuadTreeNode rootNode;
    private ErrorMeasurement errorMeasurement;
    private String originalImagePath;
    
    private int minBlockSize;
    private double threshold;
    private int treeDepth;
    private int nodeCount;
    private long executionTime;
    
    // Konstruktor
    public QuadTreeCompression(String inputImagePath, int errorMethod, double threshold, int minBlockSize) {
        try {
            this.originalImagePath = inputImagePath;
            this.originalImage = ImageIO.read(new File(inputImagePath));
            this.compressedImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                originalImage.getType()
            );
            
            // Salin gambar asli ke gambar terkompresi sebagai permulaan
            for (int x = 0; x < originalImage.getWidth(); x++) {
                for (int y = 0; y < originalImage.getHeight(); y++) {
                    compressedImage.setRGB(x, y, originalImage.getRGB(x, y));
                }
            }
            
            this.errorMeasurement = new ErrorMeasurementImpl(errorMethod);
            this.threshold = threshold;
            this.minBlockSize = minBlockSize;
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }
    
    /**
     * Memulai proses kompresi
     */
    public void compress() {
        long startTime = System.currentTimeMillis();
        
        // Buat node root untuk seluruh gambar
        rootNode = new QuadTreeNode(0, 0, originalImage.getWidth(), originalImage.getHeight());
        
        // Bangun quadtree dengan algoritma divide and conquer
        buildQuadTree(rootNode, originalImage);
        
        // Normalisasi warna untuk setiap blok
        ImageProcessor.normalizeBlock(compressedImage, rootNode);
        
        // Hitung statistik
        treeDepth = ImageProcessor.calculateTreeDepth(rootNode);
        nodeCount = ImageProcessor.countNodes(rootNode);
        
        executionTime = System.currentTimeMillis() - startTime;
    }
    
    /**
     * Algoritma rekursif divide and conquer untuk membangun quadtree
     */
    private void buildQuadTree(QuadTreeNode node, BufferedImage image) {
        // Hitung nilai rata-rata RGB untuk node ini
        node.calculateAverage(image);
        
        // Hitung error untuk node ini
        double error = errorMeasurement.calculateError(
            image, node.getX(), node.getY(), node.getWidth(), node.getHeight()
        );
        
        // Kondisi untuk membagi atau tidak:
        // 1. Error di atas threshold
        // 2. Ukuran blok lebih besar dari minimum block size
        // 3. Ukuran blok setelah dibagi tidak kurang dari minimum block size
        boolean shouldSplit = error > threshold && 
                               node.getWidth() > minBlockSize && 
                               node.getHeight() > minBlockSize && 
                               node.getWidth()/2 >= minBlockSize && 
                               node.getHeight()/2 >= minBlockSize;
        
        if (shouldSplit) {
            // Bagi node menjadi empat
            node.split();
            
            // Rekursif untuk setiap anak node
            buildQuadTree(node.getNorthWest(), image);
            buildQuadTree(node.getNorthEast(), image);
            buildQuadTree(node.getSouthWest(), image);
            buildQuadTree(node.getSouthEast(), image);
        }
        // Jika tidak dibagi, node ini menjadi leaf node dengan warna rata-rata
    }
    
    /**
     * Menyimpan gambar hasil kompresi
     */
    public void saveCompressedImage(String outputPath) {
        try {
            String extension = outputPath.substring(outputPath.lastIndexOf('.') + 1);
            File outputFile = new File(outputPath);
            ImageIO.write(compressedImage, extension, outputFile);
            System.out.println("Compressed image saved to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error saving compressed image: " + e.getMessage());
        }
    }
    
    /**
     * Menghitung dan mencetak statistik kompresi
     */
    public void printStatistics() {
        System.out.println("Waktu Eksekusi: " + executionTime + " ms");
        System.out.println("Kedalaman Pohon: " + treeDepth);
        System.out.println("Jumlah Nodes: " + nodeCount);
        
        // Hitung dan tampilkan persentase kompresi
        File originalFile = new File(originalImagePath);
        long originalSize = originalFile.length();
        long compressedSize = 0; // Ini harus dihitung setelah menyimpan file
        
        File tempOutput = new File("temp_compressed.png");
        try {
            ImageIO.write(compressedImage, "png", tempOutput);
            compressedSize = tempOutput.length();
            tempOutput.delete();
        } catch (IOException e) {
        }
        
        double compressionPercentage = (1.0 - (double)compressedSize / originalSize) * 100;
        
        System.out.println("Ukuran Gambar Asli: " + originalSize + " bytes");
        System.out.println("Ukuran Gambar Terkompresi: " + compressedSize + " bytes");
        System.out.println("Persentase Kompresi: " + String.format("%.2f", compressionPercentage) + "%");
    }
    

/**
 * Method utama untuk menjalankan program
 */
public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    
    System.out.println("Image Compression using Quadtree");
    System.out.println("================================");
    
    // Input dari pengguna
    String inputPath = "";
    while (inputPath.isEmpty()) {
        try {
            System.out.print("Masukkan absolute path untuk input gambar: ");
            inputPath = scanner.nextLine().trim();
            File inputFile = new File(inputPath);
            if (!inputFile.exists() || !inputFile.isFile()) {
                System.out.println("Error: File yang dimasksud tidak ada, tolong coba lagi.");
                inputPath = "";
            }
        } catch (Exception e) {
            System.out.println("Error saat membaca input path. Tolong coba lagi.");
            inputPath = "";
        }
    }
    
    // Input metode error
    int errorMethod = 0;
    while (errorMethod < 1 || errorMethod > 4) {
        try {
            System.out.println("Pilih error measurement method:");
            System.out.println("1. Variance");
            System.out.println("2. Mean Absolute Deviation (MAD)");
            System.out.println("3. Max Pixel Difference");
            System.out.println("4. Entropy");
            System.out.print("Masukkan Pilihanmu (1-4): ");
            
            String input = scanner.nextLine().trim();
            errorMethod = Integer.parseInt(input);
            
            if (errorMethod < 1 || errorMethod > 4) {
                System.out.println("Error: Angka yang dimasukkan harus bernilai antara 1 sampai 4");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Tolong masukkan angka yang valid.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    // Input threshold
    double threshold = -1;
    while (threshold < 0) {
        try {
            System.out.print("Masukkan nilai threshold: ");
            String thresholdStr = scanner.nextLine().trim();
            // Ganti koma dengan titik jika diperlukan
            thresholdStr = thresholdStr.replace(',', '.');
            threshold = Double.parseDouble(thresholdStr);
            
            if (threshold < 0) {
                System.out.println("Error: Nilai Threshold haruslah positif.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Masukkan angka yang valid untuk Threshold.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    // Input minimum block size
    int minBlockSize = 0;
    while (minBlockSize <= 0) {
        try {
            System.out.print("Masukkan minimum block size: ");
            String minBlockSizeStr = scanner.nextLine().trim();
            minBlockSize = Integer.parseInt(minBlockSizeStr);
            
            if (minBlockSize <= 0) {
                System.out.println("Error: Minimum block size harus berupa bilangan bulat positif!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Tolong masukkan bilangan bulat yang valid untuk minimum block size.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    // Input output path
    String outputPath = "";
    while (outputPath.isEmpty()) {
        try {
            System.out.print("Masukkan absolute path untuk output gambar terkompresi: ");
            outputPath = scanner.nextLine().trim();
            
            // Validasi bahwa outputPath memiliki ekstensi valid
            if (!outputPath.matches(".*\\.(jpg|jpeg|png|gif|bmp)$")) {
                System.out.println("Error: Output file harus memiliki extension yang valid(.jpg, .png, etc).");
                outputPath = "";
            } else {
                // Validasi bahwa direktori tujuan ada dan dapat ditulis
                File outputFile = new File(outputPath);
                File parentDir = outputFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    System.out.println("Warning: output directory yang dimaksud tidak tersedia.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error saat membaca output path. Tolong coba lagi.");
            outputPath = "";
        }
    }
    
    try {
        // Tampilkan ringkasan parameter
        System.out.println("\nCompression Parameters:");
        System.out.println("Input Image: " + inputPath);
        System.out.println("Error Method: " + getErrorMethodName(errorMethod));
        System.out.println("Threshold: " + threshold);
        System.out.println("Minimum Block Size: " + minBlockSize);
        System.out.println("Output Image: " + outputPath);
        System.out.println("\nStarting compression...");
        
        // Buat dan jalankan kompresor
        QuadTreeCompression compressor = new QuadTreeCompression(inputPath, errorMethod, threshold, minBlockSize);
        compressor.compress();
        
        // Simpan hasil
        compressor.saveCompressedImage(outputPath);
        
        compressor.printStatistics();
        
    } catch (Exception e) {
        System.out.println("Sebuah erorr terjadi saat proses kompresi: " + e.getMessage());
    } finally {
        scanner.close();
    }
}

/**
 * Helper method untuk mendapatkan nama metode error
 */
private static String getErrorMethodName(int method) {
    return switch (method) {
        case 1 -> "Variance";
        case 2 -> "Mean Absolute Deviation (MAD)";
        case 3 -> "Max Pixel Difference";
        case 4 -> "Entropy";
        default -> "Unknown";
    };
}
}