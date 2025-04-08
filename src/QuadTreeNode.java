import java.awt.Color;
import java.awt.image.BufferedImage;

public class QuadTreeNode {
    // Posisi dan ukuran blok
    private final int x;        // Koordinat sudut kiri atas
    private final int y;        // Koordinat sudut kiri atas
    private final int width; // Lebar dan tinggi blok
    private final int height; // Lebar dan tinggi blok
    
    // Nilai rata-rata RGB untuk blok ini
    private int avgRed;
    private int avgGreen;
    private int avgBlue;
    
    // Child nodes (NW, NE, SW, SE)
    private QuadTreeNode northWest;
    private QuadTreeNode northEast;
    private QuadTreeNode southWest;
    private QuadTreeNode southEast;
    
    // Flag yang menunjukkan apakah node ini adalah leaf (blok yang tidak dibagi lagi)
    private boolean isLeaf;
    
    /**
     * Constructor untuk membuat node baru
     */
    public QuadTreeNode(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isLeaf = true; // Default sebagai leaf node
    }
    
    /**
     * Menghitung nilai rata-rata RGB dari semua piksel dalam blok
     */
    // TODO: Cek lagi ini, kadang masih ada bug kalo imagenya ukuran aneh
    public void calculateAverage(BufferedImage image) {
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        int pixelCount = 0;
        
        // Iterasi melalui semua piksel dalam blok ini
        for (int i = x; i < x + width && i < image.getWidth(); i++) {
            for (int j = y; j < y + height && j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j), true); // true untuk mempertahankan alpha
                sumRed += color.getRed();
                sumGreen += color.getGreen();
                sumBlue += color.getBlue();
                pixelCount++;
            }
        }
        
        // Hitung rata-rata dengan pembulatan yang lebih akurat
        if (pixelCount > 0) {
            // Gunakan Math.round dan casting ke double untuk menghindari masalah pembulatan
            avgRed = (int) Math.round((double)sumRed / pixelCount);
            avgGreen = (int) Math.round((double)sumGreen / pixelCount);
            avgBlue = (int) Math.round((double)sumBlue / pixelCount);
            
            // Pastikan nilai berada dalam rentang valid 0-255
            avgRed = Math.max(0, Math.min(255, avgRed));
            avgGreen = Math.max(0, Math.min(255, avgGreen));
            avgBlue = Math.max(0, Math.min(255, avgBlue));
        }
    }
    
    /**
     * Membagi node menjadi empat anak (children)
     */
    public void split() {
        // Hitung ukuran baru dengan mempertimbangkan piksel terakhir
        int newWidth = (width + 1) / 2;
        int newHeight = (height + 1) / 2;
        
        // Width dan height kuadran timur/selatan mungkin berbeda
        int eastWidth = width - newWidth;
        int southHeight = height - newHeight;
        
        // Buat empat quadran dengan ukuran yang benar
        northWest = new QuadTreeNode(x, y, newWidth, newHeight);
        northEast = new QuadTreeNode(x + newWidth, y, eastWidth, newHeight);
        southWest = new QuadTreeNode(x, y + newHeight, newWidth, southHeight);
        southEast = new QuadTreeNode(x + newWidth, y + newHeight, eastWidth, southHeight);
        
        isLeaf = false;
    }
    
    // Getter dan setter untuk properti
    public boolean isLeaf() {
        return isLeaf;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Color getAverageColor() {
        return new Color(avgRed, avgGreen, avgBlue);
    }
    
    public QuadTreeNode getNorthWest() {
        return northWest;
    }
    
    public QuadTreeNode getNorthEast() {
        return northEast;
    }
    
    public QuadTreeNode getSouthWest() {
        return southWest;
    }
    
    public QuadTreeNode getSouthEast() {
        return southEast;
    }
}