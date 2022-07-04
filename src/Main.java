import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    private static String cropImage(BufferedImage srcImg, int x_axis, int y_axis) throws IOException {
        int width = srcImg.getWidth()/x_axis;
        int height = srcImg.getHeight()/y_axis;

        for (int i = 0; i < x_axis; i++){
            for (int j = 0; j < y_axis; j++){
                BufferedImage dest = srcImg.getSubimage(i*width, j*height, width, height);

                File hugeDir = new File("src/results");
                int numberFiles = hugeDir.list().length + 1;
                File pathFile = new File("src/results/" + numberFiles + ".PNG");
                ImageIO.write(dest,"PNG", pathFile);
            }
        }
        return "Image divided succeeded.";
    }

    public static BufferedImage grayScale(BufferedImage srcImg) {
        BufferedImage r = null;
        try {
            BufferedImage image = srcImg;

            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D graphic = result.createGraphics();
            graphic.drawImage(image, 0, 0, Color.WHITE, null);

            for (int i = 0; i < result.getHeight(); i++) {
                for (int j = 0; j < result.getWidth(); j++) {
                    Color c = new Color(result.getRGB(j, i));
                    int red = (int) (c.getRed() * 0.299);
                    int green = (int) (c.getGreen() * 0.587);
                    int blue = (int) (c.getBlue() * 0.114);
                    Color newColor = new Color(
                            red + green + blue,
                            red + green + blue,
                            red + green + blue);
                    result.setRGB(j, i, newColor.getRGB());
                }
            }

            File output = new File("src/grayscale.png");
            ImageIO.write(result, "png", output);
            r = ImageIO.read(output);

        }  catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }

    public static double getBlackIntensity(BufferedImage img){
        int intensity = 0;
        int total = img.getHeight() * img.getWidth() - 20;

        for (int i = 0; i < img.getHeight()-10; i++) {
            for (int j = 0; j < img.getWidth()-10; j++) {
                int p  = img.getRGB(i,j) & 0xFF;
                if(p <= 120){
                    intensity++;
                }
            }
        }
        return ((double)intensity/(double)total) * 100;
    }

    public static int[][] classifyCode(File f){
        int[][] result = new int[2][2];
        try{
            BufferedImage img = ImageIO.read(f);
            BufferedImage gc = grayScale(img);
            int width = img.getWidth()/2;
            int height = img.getHeight()/2;
            int numberFiles = new File("src/prcImg/").list().length + 1;
            new File("src/prcImg/" + numberFiles).mkdir();

            for (int i = 0; i < 2; i++){
                for (int j = 0; j < 2; j++){
                    BufferedImage dest = img.getSubimage(i*width, j*height, width, height);
                    File pathFile = new File("src/prcImg/" + numberFiles + "/" + + i + j + ".png");
                    ImageIO.write(dest,"png", pathFile);
                    if(getBlackIntensity(ImageIO.read(pathFile)) > 15){
                        result[i][j] = 1;
                    }else {
                        result[i][j] = 0;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public static void print2DArray(int[][] arr){
        System.out.println(arr[0][0] + " " + arr[1][0]);
        System.out.println(arr[0][1] + " " + arr[1][1]);
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void main(String[] args) {
        try {
            File dir = new File("src/results");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (int i = 0; i < directoryListing.length; i++){
                    System.out.println(directoryListing[i].getName());
                    print2DArray(classifyCode(directoryListing[i]));
                    System.out.println("------------------------------------------------");
                }
            }
            deleteFolder(new File("src/prcImg"));
            deleteFolder(dir);
            new File("src/prcImg").mkdir();
            new File("src/results").mkdir();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
