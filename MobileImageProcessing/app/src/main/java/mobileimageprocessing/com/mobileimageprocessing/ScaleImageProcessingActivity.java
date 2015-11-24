package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Color;
import android.os.Looper;
import android.os.Handler;

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

    @Override
    public int[][] processImagePipes(int [][] image) {
        //4 threads pipelining
        return resizeLooper(image, (int) Math.floor(image.length * 0.5), (int) Math.floor(image[0].length * 0.5), 4);
    }

    public int [][] resize(int [][] image, int w, int h) {
        int origHeight = image[0].length;
        int origWidth = image.length;

        double sr;
        double sc;

        //For Scaling in both directions
        if(origWidth > w) {
            sr = origWidth / w;
        } else {
            sr = ((origWidth - 1) / w);
        }

        //For scaling in both directions
        if(origHeight > h) {
            sc = origHeight / h;
        } else {
            sc = ((origHeight - 1) / h);
        }

        int [][] result = new int[w][h];

        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Scaling utilizing BILINEAR INTERPOLATION
        double rf,cf;
        for(int i = 0; i < w; i ++) {
            for(int j = 0; j < h; j++) {
                rf = i * sr;
                cf = j * sc;

                int r = (int) Math.floor(rf);
                int c = (int) Math.floor(cf);

                double deltar = (int) Math.floor(rf - (double) r);
                double deltac = (int) Math.floor(cf - (double) c);
//
                if(r + 1 > origWidth) {
                    r = w - 1;
                }
                if(c + 1 > origHeight) {
                    c = h - 1;
                }

                double red, green, blue;

                red = (Color.red(image[r][c]) * (1 - deltar) * (1 - deltac))
                        + (Color.red(image[r + 1][c]) * deltar * (1 - deltac))
                        + (Color.red(image[r][c + 1]) * (1 - deltar) * deltac)
                        + (Color.red(image[r + 1][c + 1]) * deltac * deltar);

                green = (Color.green(image[r][c]) * (1 - deltar) * (1 - deltac))
                        + (Color.green(image[r + 1][c]) * deltar * (1 - deltac))
                        + (Color.green(image[r][c + 1]) * (1 - deltar) * deltac)
                        + (Color.green(image[r + 1][c + 1]) * deltac * deltar);

                blue = (Color.blue(image[r][c]) * (1 - deltar) * (1 - deltac))
                        + (Color.blue(image[r + 1][c]) * deltar * (1 - deltac))
                        + (Color.blue(image[r][c + 1]) * (1 - deltar) * deltac)
                        + (Color.blue(image[r + 1][c + 1]) * deltac * deltar);

                result[i][j] = Color.rgb((int) Math.floor(red), (int) Math.floor(green), (int) Math.floor(blue));
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //End BILINEAR INTERPOLATION

        return result;
    }

    public int [][] resizeThreads(int [][] image, int w, int h, int threadCount) {
        int[][] result = new int[w][h];
        System.out.println(result.length);
        System.out.println(result[0].length);
        Thread[] threads = new Thread[threadCount];
        int sr;
        int sc;

        if(image.length > w) {
            sr = image.length / w;
        } else {
            sr = ((image.length - 1) / w);
        }

        if(image[0].length > h) {
            sc = image[0].length / h;
        } else {
            sc = ((image[0].length - 1) / h);
        }

        int fragsPerThread = result.length / threadCount;

        int prevEnd = 0;

        for(int i = 0; i < threadCount; i++) {
            int start = prevEnd;
            int end = start +  fragsPerThread;

            if(i == threadCount - 1) {
                end = result.length;
            }
            prevEnd = end;
            threads[i] = new Thread(new ScalingRunnable(image, start, end, h, sr, sc, result));
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
        int h;
        int sr;
        int sc;
        int [][] result;

        ScalingRunnable(int [][] image, int xStart, int xEnd, int h, int sr, int sc, int[][] result) {
            this.image = image;
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.h = h;
            this.sr = sr;
            this.sc = sc;
            this.result = result;
        }

        @Override
        public void run() {
            double rf, cf;
            for(int i = xStart; i < xEnd; i++) {
                for (int j = 0; j < h; j++) {
                    rf = i * sr;
                    cf = j * sc;

                    int r = (int) Math.floor(rf);
                    int c = (int) Math.floor(cf);

                    int deltar = (int) Math.floor(rf - r);
                    int deltac = (int) Math.floor(cf - c);

                    if(r + 1 > image.length) {
                        r = image.length - 1;
                    }
                    if(c + 1 > image[0].length) {
                        c = h - 1;
                    }

                    double red, green, blue;

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

                    result[i][j] = Color.rgb((int) Math.floor(red), (int) Math.floor(green), (int) Math.floor(blue));
                }
            }
        }
    }

    public int[][] resizeLooper(int [][] image, int w, int h, int threadNum) {
        int sr;
        int sc;

        if(image.length > w) {
            sr = image.length / w;
        } else {
            sr = ((image.length - 1) / w);
        }

        if(image[0].length > h) {
            sc = image[0].length / h;
        } else {
            sc = ((image[0].length - 1) / h);
        }

        int threadCounter = 0;
        LooperThread[] threads = new LooperThread[threadNum];

        int[][] result = new int[w][h];

        for(int i = 0; i < threadNum; i++) {
            threads[i] = new LooperThread();
            threads[i].start();
        }

        for(int i = 0; i < threadNum; i++) {
            threads[i].waitForLooperSafely();
        }

        for(int i = 0; i < w; i++) {
            threads[threadCounter].addJob(new ScaleLooperRunnable(image, i, h, sr, sc, result));
            threadCounter++;
            threadCounter %= threadNum;
        }

        for(int i = 0; i < threadNum; i++) {
            threads[i].exitLooperSafely();
        }

        for(int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private class LooperThread extends Thread {
        public Handler mHandler;

        @Override
        public void run() {
            Looper.prepare();
            synchronized (this) {
                mHandler = new Handler();
                notifyAll();
            }
            Looper.loop();
        }

        public void addJob(Runnable r) {
            this.mHandler.post(r);
        }

        public void exitLooperSafely() {
            this.mHandler.getLooper().quitSafely();
        }

        public void waitForLooperSafely() {
            synchronized (this) {
                while (mHandler == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class ScaleLooperRunnable implements Runnable {
        int [][] image;
        int i;
        int h;
        int sr;
        int sc;
        int result[][];

        public ScaleLooperRunnable(int[][] image, int i, int h, int sr, int sc, int[][] result) {
            this.image = image;
            this.i = i;
            this.h = h;
            this.sr = sr;
            this. sc = sc;
            this.result = result;
        }

        @Override
        public void run() {
            double rf, cf;
            for (int j = 0; j < h; j++) {
                rf = i * sr;
                cf = j * sc;

                int r = (int) Math.floor(rf);
                int c = (int) Math.floor(cf);

                int deltar = (int) Math.floor(rf - r);
                int deltac = (int) Math.floor(cf - c);

                if(r + 1 > image.length) {
                    r = image.length - 1;
                }
                if(c + 1 > image[0].length) {
                    c = image[0].length - 1;
                }

                double red, green, blue;

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

                result[i][j] = Color.rgb((int) Math.floor(red), (int) Math.floor(green), (int) Math.floor(blue));
            }
        }
    }
}