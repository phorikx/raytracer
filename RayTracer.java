
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

class intersectionPoint{
    threeDVector intersectionLocation;
    double intersectionAngle;
}

class lightSource{
    threeDVector location;
    int brightness = 500;
}

class viewPort{
    threeDVector[] portLocation = new threeDVector[3];
    int[] resolution = new int[2];
    public viewPort(threeDVector firstVector, threeDVector secondVector, threeDVector thirdVector, int width, int height) {
        resolution[0] = width;
        resolution[1] = height;
        portLocation[0] = firstVector;
        portLocation[1] = secondVector;
        portLocation[2] = thirdVector;
    }
}

public class RayTracer {
    public static void main(String[] args) throws StartingPointNotEqualException, NoIntersectionPointFoundException, IOException {
        // We start by creating a scene, which means we have to create a lot of objects.
        scene pictureScene = new scene();        
        int width = 800;
        int height = 600;
        threeDVector sceneViewpoint = new threeDVector(0.0, 0.0, 0.0);

        threeDVector viewportVector1 =  new threeDVector(400.0, 300.0, 50.0);
        threeDVector viewportVector2 =  new threeDVector(-400.0, 300.0, 50.0);
        threeDVector viewportVector3 =  new threeDVector(400.0, -300.0, 50.0);
        viewPort sceneViewport = new viewPort(viewportVector1, viewportVector2, viewportVector3, width, height);

        // Initialize first LightSource
        lightSource firstLightSource = new lightSource();
        threeDVector firstLightSourceLocation = new threeDVector(500.0,500.0,155.0);
        firstLightSource.brightness = 400;
        firstLightSource.location = firstLightSourceLocation;
        

        // Initialize second LightSource;
        lightSource secondLightSource = new lightSource();        
        threeDVector secondLightSourceLocation = new threeDVector(500.0,-100.0,75.0);
        secondLightSource.location = secondLightSourceLocation;
        secondLightSource.brightness = 200;
        
        // Initialize first Sphere;
        Sphere firstSphere = new Sphere();        
        threeDVector firstSphereOrigin = new threeDVector(0.0,0.0,100.0);
        firstSphere.origin = firstSphereOrigin;
        firstSphere.radius = 200.0;

        //Initialize second Sphere
        Sphere secondSphere = new Sphere();
        threeDVector secondSphereOrigin = new threeDVector(100.0,150.0,130.0);
        secondSphere.origin = secondSphereOrigin;
        secondSphere.radius = 50.0;
        
        //Make Scene
        pictureScene.viewPoint = sceneViewpoint;
        pictureScene.viewport = sceneViewport;
        pictureScene.lightSources.add(firstLightSource);
        pictureScene.lightSources.add(secondLightSource);
        pictureScene.objects.add(firstSphere);
        pictureScene.objects.add(secondSphere);
        pictureScene.totalBrightnessFromLightsources = firstLightSource.brightness + secondLightSource.brightness;

        int[][][] pictureArray = pictureScene.renderScene();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++){
                // we colour the picture slightly blue.
                int p = (pictureArray[i][j][3]<<24) | (pictureArray[i][j][0]/2<<16) | (pictureArray[i][j][1]/2<<8) | pictureArray[i][j][2]; //pixel
                img.setRGB(i,j,p);
            }
        }
        //file object
        File f = null;

        try{
            f = new File("C:\\Users\\phori\\Pictures\\output2.png");
            ImageIO.write(img, "png", f);
          }catch(IOException e){
            System.out.println("Error: " + e);
          }
    }    
}
