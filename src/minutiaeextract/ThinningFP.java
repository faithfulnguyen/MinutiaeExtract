package minutiaeextract;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import static org.bytedeco.javacpp.opencv_core.CV_8U;
import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

/**
 *
 * @author nguyentrungtin
 */
public class ThinningFP {
    private int no_more[] = {0,0,0,0};
    /**
     * @param args the command line arguments
     */
    
    public int next_thin_dir(int thin_dir){
        int next_dir = -1;

        switch(thin_dir)
        {
        case 0 :
            next_dir = 2;
            break;
        case 3 :
            next_dir = 1;
            break;
        case 1 :
            next_dir = 0;
            break;
        case 2 :
            next_dir = 3;
            break;
        }
        return next_dir;
    }

    public void check_neighbors_8simple_up(Mat img, Mat pMark, int y, int x, UByteRawIndexer idx, UByteRawIndexer idxM){
        //cout << "up" << endl;
        int neighbors = idx.get(y-1,x-1) + idx.get(y-1,x+1)
            + idx.get(y,x+1) + idx.get(y+1,x+1) + idx.get(y-1,x)
            + idx.get(y+1,x-1) + idx.get(y,x-1);
        if(neighbors == 1)
            return;

        if( idx.get(y,x+1) == 0 && idx.get(y+1,x+1) == 1 )
            return;
        if( idx.get(y,x-1) == 0 && idx.get(y+1,x-1) == 1 )
            return;
        if( idx.get(y-1,x) == 0 )
            if( idx.get(y,x+1) == 1 || idx.get(y-1,x+1) == 1 )
                if(idx.get(y,x-1) == 1 || idx.get(y-1,x-1) == 1 )
                    return;
        idxM.put(y, x, 1);
    }

    public void check_neighbors_8simple_down(Mat img, Mat pMark, int y, int x, UByteRawIndexer idx, UByteRawIndexer idxM){
        //cout << "down" << endl;
        int neighbors = idx.get(y-1,x-1) + idx.get(y-1,x+1)
            + idx.get(y,x+1) + idx.get(y+1,x+1) + idx.get(y+1,x)
            + idx.get(y+1,x-1) + idx.get(y,x-1);
        if(neighbors == 1)
            return;

        if( idx.get(y,x+1) == 0 && idx.get(y-1,x+1) == 1 )
            return;
        if( idx.get(y,x-1) == 0 && idx.get(y-1,x-1) == 1 )
            return;
        if( idx.get(y+1,x) == 0 )
            if( idx.get(y,x+1) == 1 || idx.get(y+1,x+1) == 1 )
                if(idx.get(y,x-1) ==1 || idx.get(y+1,x-1) == 1 )
                    return;

        idxM.put(y, x, 1);
    }

    public void check_neighbors_8simple_left(Mat img, Mat pMark, int y, int x, UByteRawIndexer idx, UByteRawIndexer idxM){
        //cout << "left" << endl;
        int neighbors = idx.get(y-1,x-1) + idx.get(y-1,x+1)
            + idx.get(y-1,x) + idx.get(y+1,x+1) + idx.get(y+1,x)
            + idx.get(y+1,x-1) + idx.get(y,x-1);
        if(neighbors == 1)
            return;

        if( idx.get(y-1,x) == 0 && idx.get(y-1,x+1) == 1 )
            return;
        if( idx.get(y+1,x) == 0 && idx.get(y+1,x+1) == 1 )
            return;
        if( idx.get(y,x-1) == 0 )
            if( idx.get(y-1,x-1) == 1 || idx.get(y-1,x) == 1 )
                if(idx.get(y+1,x-1) ==1 || idx.get(y+1,x) == 1 )
                    return;

        idxM.put(y,x,1);

    }

    public void check_neighbors_8simple_right(Mat img, Mat pMark, int y, int x, UByteRawIndexer idx, UByteRawIndexer idxM){
        //cout << "right" << endl;
        int neighbors = idx.get(y-1,x-1) + idx.get(y-1,x+1)
            + idx.get(y-1,x) + idx.get(y+1,x+1) + idx.get(y+1,x)
            + idx.get(y+1,x-1) + idx.get(y,x+1);
        if(neighbors == 1)
            return;

        if( idx.get(y-1,x) == 0 && idx.get(y-1,x-1) == 1 )
            return;
        if( idx.get(y+1,x) == 0 && idx.get(y+1,x-1) == 1 )
            return;
        if( idx.get(y,x+1) == 0 )
            if( idx.get(y-1,x) == 1 || idx.get(y-1,x+1) == 1 )
                if(idx.get(y+1,x) ==1 || idx.get(y+1,x+1) == 1 )
                    return;

        idxM.put(y,x,1);
    }

    public void thin(Mat img, int thin_dir, Mat pMark){
        int height = img.rows();
        int width = img.cols();
        int	y, x;
        UByteRawIndexer idx = img.createIndexer();
        UByteRawIndexer idxM = pMark.createIndexer();
        switch(thin_dir)
        {
        case 1 :
            for(y = 1 ; y < height - 1; y ++)
                for(x = 1; x < width - 1; x ++)
                    if(idx.get(y,x) == 1)
                        if(idx.get(y-1,x) == 0)
                            check_neighbors_8simple_down(img, pMark, y, x, idx, idxM);
            break;
        case 0 :
            for(y = 1 ; y < height - 1; y ++)
                for(x = 1; x < width - 1; x ++)
                    if(idx.get(y,x) == 1)
                        if(idx.get(y+1,x) == 0)
                            check_neighbors_8simple_up(img, pMark, y, x, idx, idxM);
            break;
        case 2 :
            for(y = 1 ; y < height - 1; y ++)
                for(x = 1; x < width - 1; x ++)
                    if(idx.get(y,x) == 1)
                        if(idx.get(y,x+1) == 0)
                            check_neighbors_8simple_left(img, pMark, y, x, idx, idxM);
            break;
        case 3 :
            for(y = 1 ; y < height - 1; y ++)
                for(x = 1; x < width - 1; x ++)
                    if(idx.get(y,x) == 1)
                        if(idx.get(y,x-1) == 0)
                            check_neighbors_8simple_right(img, pMark, y, x, idx, idxM);
            break;
        }

        boolean bChange = false;

        for(y = 1; y < height - 1; y ++)
        {
            for(x = 1; x < width - 1; x ++)
            {
                if(idxM.get(y,x) == 1)
                {
                    idx.put(y,x, 0);
                    idxM.put(y,x, 0);
                    bChange = true;
                }
            }
        }
        if(!bChange)
            no_more[thin_dir] = 1;
    }

    public void RidgeThinning(Mat binaryImage){
         // Set ridge as 1, Valley as 0
        UByteRawIndexer idx = binaryImage.createIndexer();
    
        for(int y = 0; y < binaryImage.rows(); y ++){
            for(int x = 0; x < binaryImage.cols(); x ++){
                if(idx.get(y, x) == 255)
                    idx.put(y, x, 1);
                else idx.put(y, x, 0);
            }
        }

        Mat pMark = Mat.zeros(binaryImage.size(), CV_8U).asMat();
        for(int i = 0; i < 4; i++)
            this.no_more[i] = 0;

        int thin_dir = 0;
        while((this.no_more[0] == 0 && this.no_more[1] == 0 && this.no_more[2] == 0 && this.no_more[3] == 0))
        {
            if(no_more[thin_dir] == 0)
                thin(binaryImage, thin_dir, pMark);
            thin_dir = next_thin_dir(thin_dir);
        }

        // Set ridge as 0, Valley as 255
         for(int y = 0; y < binaryImage.rows(); y ++){
            for(int x = 0; x < binaryImage.cols(); x ++){
                if(idx.get(y, x) == 1)
                    idx.put(y, x, 0);
                else idx.put(y, x, 255);
            }
        }
    }    
 
    //end here  
}
