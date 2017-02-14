/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minutiaeextract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_imgcodecs;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GRAY2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.threshold;


/**
 *
 * @author nguyentrungtin
 */
public class Minutiae {

    //para
    private int x;
    private int y;
    //private float orient;
    /**
     * @param args the command line arguments
     */
    
    public Minutiae() {
        this.x = -1;
        this.y = -1;
    }
    
    public Minutiae(Point p) {
        this.x = p.getX();
        this.y = p.getY();
    }
    
    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void set(Point p){
        this.x = p.getX();
        this.y = p.getY();
    }
    
    public void printOutMinutiae(){
        System.out.println(this.x + " " + this.y);
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        Minutiae m = new Minutiae();
        ThinningFP p = new ThinningFP();
        File folder = new File("");
        String fileName = folder.getAbsolutePath() + "/src/DB2/";
        File[] listOfFiles = new File(fileName).listFiles();
        for(int idx = 0; idx <  1; idx++){
            if (listOfFiles[idx].getName().contains(".tif")){
                String name =  listOfFiles[idx].getName();
                opencv_core.Mat img = imread(fileName + "/" + name, opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
                p.RidgeThinning(img);
                m.removeDot(img);

                threshold(img, img, 0, 255, CV_THRESH_OTSU);
                m.pythonCrossingNumber(img, name);
            }
        }
    }
    
    public void removeDot(Mat img){
        int filterSize = 10;
        for(int i = 0; i < img.rows() - filterSize; i++){
            for(int j = 0; j < img.cols() - filterSize; j++){
                Mat block = img.apply(new Rect(i, j, filterSize, filterSize));
                int flag = 0;
                if(opencv_core.sumElems(block.col(0)).get() == 0)
                    flag++;
                if(opencv_core.sumElems(block.col(filterSize - 1)).get() == 0)
                    flag++;
                if(opencv_core.sumElems(block.row(0)).get() == 0)
                    flag++;
                if(opencv_core.sumElems(block.row(filterSize - 1)).get() == 0)
                    flag++;
                if(flag > 3){
                    block = opencv_core.multiplyPut(block, 0);
                }
            }
        }
    }
    
    public List<Minutiae> pythonCrossingNumber(Mat img, String name){
        List<Minutiae> minutia = new ArrayList<>();
        UByteRawIndexer idx = img.createIndexer();
        Mat rgb = new Mat();
        opencv_imgproc.cvtColor(img, rgb, CV_GRAY2RGB);
        for(int i = 0; i < img.rows(); i++){
            for(int j = 0; j < img.cols(); j++){
                if(idx.get(j, i) > 10)
                    idx.put(j, i, 1);
                else idx.put(j, i, 0);
            }
        }

        //
        int crs = 0;
        for(int i = 1; i < img.rows() - 1; i++){
            for(int j = 1; j < img.cols() - 1; j++){
                Minutiae m = new Minutiae();
                crs = crossingNumberAtPixel(idx, j, i);
                if(idx.get(j, i)== 0){
                    if(crs == 3){
                       opencv_imgproc.circle(rgb, new opencv_core.Point(i, j), 2, org.bytedeco.javacpp.helper.opencv_core.AbstractScalar.RED);
                        m.set(i, j);
                        minutia.add(m);
                    }
                    if(crs == 1){
                       opencv_imgproc.circle(rgb, new opencv_core.Point(i, j), 2, org.bytedeco.javacpp.helper.opencv_core.AbstractScalar.CYAN);
                        m.set(i, j);
                        minutia.add(m);
                    }
                    
                }
            }
        }
        for(int i = 0; i < minutia.size(); i++)
            minutia.get(i).printOutMinutiae();
        return minutia;
    }
    
    public int crossingNumberAtPixel(UByteRawIndexer idx, int x, int y){
        int z = 0;
        int [] p = {idx.get(x - 1,y - 1), idx.get(x , y - 1), idx.get(x + 1, y - 1), idx.get(x + 1, y),
                    idx.get(x + 1, y + 1), idx.get(x, y + 1), idx.get(x - 1, y + 1), idx.get(x - 1, y), idx.get(x - 1, y - 1)};

        for(int i = 0; i < 8; i++){
            z += Math.abs(p[i] - p[i+1]);
        }
        z /= 2;
        return z;
    }
    
}
