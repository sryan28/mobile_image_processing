package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Color;

/**
 * Created by user on 11/21/2015.
 */
public class ScaleImageProcessingActivity extends BaseImageProcessingActivity{

    @Override
    public int[][] processImageSequential(int[][] image ) {
        return resize(image, (int) Math.floor(image.length * 0.5), (int) Math.floor(image[0].length * 0.5));
    }

    public int [][] resize(int [][] image, int w, int h) {
        int origHeight = image[0].length;
        int origWidth = image.length;

        double sr = origWidth / w;
        double sc = origHeight / h;

        int [][] result = new int[w][h];

        double rf = 0, cf = 0;
        for(int i = 0; i < w; i ++) {
            for(int j = 0; j < h; j++) {
                rf = i * sr;
                cf = j * sc;

                int r = (int) Math.floor(rf);
                int c = (int) Math.floor(cf);

                int deltar = (int) Math.floor(rf - r);
                int deltac = (int) Math.floor(cf - c);

                int red = 0;
                int green = 0;
                int blue = 0;

                red = Color.red(image[r][c]) * (1 - deltar) * (1 - deltac)
                        + Color.red(image[r + 1][c] * deltar * (1 - deltac))
                        + Color.red(image[r][c + 1]) * (1 - deltar) * deltac
                        + Color.red(image[r + 1][c + 1]) * deltac * deltar;

                green = Color.green(image[r][c]) * (1 - deltar) * (1 - deltac)
                        + Color.green(image[r + 1][c] * deltar * (1 - deltac))
                        + Color.green(image[r][c + 1]) * (1 - deltar) * deltac
                        + Color.green(image[r + 1][c + 1]) * deltac * deltar;

                blue = Color.blue(image[r][c]) * (1 - deltar) * (1 - deltac)
                        + Color.blue(image[r + 1][c] * deltar * (1 - deltac))
                        + Color.blue(image[r][c + 1]) * (1 - deltar) * deltac
                        + Color.blue(image[r + 1][c + 1]) * deltac * deltar;

                result[i][j] = Color.rgb(red, green, blue);
            }
        }
//        System.out.printf("now size %d, %d", result.length, result[0].length);
        return result;
    }

    public int [][] resizeThreads(int [][] image, int w, int h, int threadCount) {
        Thread[] threads = new Thread[threadCount];
        int sr = image.length / w;
        int sc = image[0].length / h;

        int fragsPerThread = image.length / threadCount;

        for(int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new ScalingRunnable(image, start, end, 0, image[0].length, sr, sc));
            threads[i].start();
        }

        for(int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    private class ScalingRunnable implements Runnable {
        int [][] image;
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;
        int sr;
        int sc;

        ScalingRunnable(int [][] image, int xStart, int xEnd, int yStart, int yEnd, int sr, int sc) {
            this.image = image;
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
            this.sr = sr;
            this.sc = sc;
        }

        @Override
        public void run() {
            double rf = 0, cf = 0;
            for(int i = xStart; i < xEnd; i ++) {
                for (int j = yStart; j < yEnd; j++) {
                    rf = i * sr;
                    cf = j * sc;

                    int r = (int) Math.floor(rf);
                    int c = (int) Math.floor(cf);

                    int deltar = (int) Math.floor(rf - r);
                    int deltac = (int) Math.floor(cf - c);

                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    red = Color.red(image[r][c]) * (1 - deltar) * (1 - deltac)
                            + Color.red(image[r + 1][c] * deltar * (1 - deltac))
                            + Color.red(image[r][c + 1]) * (1 - deltar) * deltac
                            + Color.red(image[r + 1][c + 1]) * deltac * deltar;

                    green = Color.green(image[r][c]) * (1 - deltar) * (1 - deltac)
                            + Color.green(image[r + 1][c] * deltar * (1 - deltac))
                            + Color.green(image[r][c + 1]) * (1 - deltar) * deltac
                            + Color.green(image[r + 1][c + 1]) * deltac * deltar;

                    blue = Color.blue(image[r][c]) * (1 - deltar) * (1 - deltac)
                            + Color.blue(image[r + 1][c] * deltar * (1 - deltac))
                            + Color.blue(image[r][c + 1]) * (1 - deltar) * deltac
                            + Color.blue(image[r + 1][c + 1]) * deltac * deltar;

                    image[i][j] = Color.rgb(red, green, blue);
                }
            }
        }
    }
}

