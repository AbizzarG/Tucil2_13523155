import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Kelas untuk memproses dan memanipulasi gambar
 */
public class ImageProcessor {
    /**
     * Menormalisasi warna dalam blok dengan menggunakan rata-rata RGB
     */
    public static void normalizeBlock(BufferedImage image, QuadTreeNode node) {
        if (node.isLeaf()) {
            // Hanya normalisasi leaf nodes
            Color avgColor = node.getAverageColor();
            int rgbValue = avgColor.getRGB();
            
            // Set semua piksel dalam blok ke warna rata-rata
            for (int i = node.getX(); i < node.getX() + node.getWidth() && i < image.getWidth(); i++) {
                for (int j = node.getY(); j < node.getY() + node.getHeight() && j < image.getHeight(); j++) {
                    image.setRGB(i, j, rgbValue);
                }
            }
        } else {
            // Rekursi untuk semua child nodes
            normalizeBlock(image, node.getNorthWest());
            normalizeBlock(image, node.getNorthEast());
            normalizeBlock(image, node.getSouthWest());
            normalizeBlock(image, node.getSouthEast());
        }
    }
    
    /**
     * Menghitung kedalaman pohon quadtree
     */
    public static int calculateTreeDepth(QuadTreeNode node) {
        if (node == null) {
            return 0;
        }
        
        if (node.isLeaf()) {
            return 1;
        }
        
        // Hitung kedalaman maksimum dari semua anak
        int nwDepth = calculateTreeDepth(node.getNorthWest());
        int neDepth = calculateTreeDepth(node.getNorthEast());
        int swDepth = calculateTreeDepth(node.getSouthWest());
        int seDepth = calculateTreeDepth(node.getSouthEast());
        
        return 1 + Math.max(Math.max(nwDepth, neDepth), Math.max(swDepth, seDepth));
    }
    
    /**
     * Menghitung jumlah node dalam pohon quadtree
     */
    public static int countNodes(QuadTreeNode node) {
        if (node == null) {
            return 0;
        }
        
        if (node.isLeaf()) {
            return 1;
        }
        
        // Hitung jumlah node untuk semua anak dan tambahkan node ini
        return 1 + countNodes(node.getNorthWest()) + 
                   countNodes(node.getNorthEast()) + 
                   countNodes(node.getSouthWest()) + 
                   countNodes(node.getSouthEast());
    }
}