package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Color;

public class ScaleImageProcessingActivity extends BaseImageProcessingActivity{

    @Override
    public int[][] processImageSequential(int[][] image ) {
        return resize(image, (int) Math.floor(image.length * 0.5), (int) Math.floor(image[0].length * 0.5));
    }

   @Override
    public int[][] processImageThreads(int[][] image) {
        //4 threads
        return resizeThreads(image, (int) Math.floor(image.length * 0.5), (int) Math.floor(image[0].length * 0.5), 4);
    }

    public int [][] resize(int [][] image, int w, int h) {
        int origHeight = image[0].length;
        int origWidth = image.length;

        double sr = origWidth / w;
        double sc = origHeight / h;

        int [][] result = new int[w][h];

        double rf, cf;
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
                        + Color.red(image[r + 1][c]) * deltar * (1 - deltac)
                        + Color.red(image[r][c + 1]) * (1 - deltar) * deltac
                        + Color.red(image[r + 1][c + 1]) * deltac * deltar;

                green = Color.green(image[r][c]) * (1 - deltar) * (1 - deltac)
                        + Color.green(image[r + 1][c]) * deltar * (1 - deltac)
                        + Color.green(image[r][c + 1]) * (1 - deltar) * deltac
                        + Color.green(image[r + 1][c + 1]) * deltac * deltar;

                blue = Color.blue(image[r][c]) * (1 - deltar) * (1 - deltac)
                        + Color.blue(image[r + 1][c]) * deltar * (1 - deltac)
                        + Color.blue(image[r][c + 1]) * (1 - deltar) * deltac
                        + Color.blue(image[r + 1][c + 1]) * deltac * deltar;

                result[i][j] = Color.rgb(red, green, blue);
            }
        }
//        System.out.printf("now size %d, %d", result.length, result[0].length);
        return result;
    }

    public int [][] resizeThreads(int [][] image, int w, int h, int threadCount) {
        int[][] result = new int[w][h];
        System.out.println(result.length);
        System.out.println(result[0].length);
        Thread[] threads = new Thread[threadCount];
        int sr = image.length / w;
        int sc = image[0].length / h;

        int fragsPerThread = result.length / threadCount;

        int prevEnd = 0;

        for(int i = 0; i < threadCount; i++) {
            int start = prevEnd;
            int end = start +  fragsPerThread;

            if(i == threadCount - 1) {
                end = result.length;
            }
            prevEnd = end;
            threads[i] = new Thread(new ScalingRunnable(image, start, end, sr, sc, result));
            threads[i].start();
        }

        for(int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private class ScalingRunnable implements Runnable {
        int [][] image;
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;
        int sr;
        int sc;
        int [][] result;

        ScalingRunnable(int [][] image, int xStart, int xEnd, int sr, int sc, int[][] result) {
            this.image = image;
            this.xStart = xStart;
            this.xEnd = xEnd;
//            this.yStart = yStart;
//            this.yEnd = yEnd;
            this.sr = sr;
            this.sc = sc;
            this.result = result;
        }

        @Override
        public void run() {
            double rf = 0, cf = 0;
            for(int i = xStart; i < xEnd; i++) {
                for (int j = 0; j < result[0].length; j++) {
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
                            + Color.red(image[r + 1][c]) * deltar * (1 - deltac)
                            + Color.red(image[r][c + 1]) * (1 - deltar) * deltac
                            + Color.red(image[r + 1][c + 1]) * deltac * deltar;

                    green = Color.green(image[r][c]) * (1 - deltar) * (1 - deltac)
                            + Color.green(image[r + 1][c]) * deltar * (1 - deltac)
                            + Color.green(image[r][c + 1]) * (1 - deltar) * deltac
                            + Color.green(image[r + 1][c + 1]) * deltac * deltar;

                    blue = Color.blue(image[r][c]) * (1 - deltar) * (1 - deltac)
                            + Color.blue(image[r + 1][c]) * deltar * (1 - deltac)
                            + Color.blue(image[r][c + 1]) * (1 - deltar) * deltac
                            + Color.blue(image[r + 1][c + 1]) * deltac * deltar;

                    result[i][j] = Color.rgb(red, green, blue);
                }
            }
        }
    }
}