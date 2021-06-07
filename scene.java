import java.util.*;

class scene{
    // A scene consists of object, lightsources, a viewport and a viewpoint. We also include methods to render the scene.
    ArrayList<lightSource> lightSources = new ArrayList<lightSource>();
    viewPort viewport;
    threeDVector viewPoint;
    ArrayList<Shape> objects = new ArrayList<Shape>();
    int totalBrightnessFromLightsources;

    public int[] renderPixel(int widthPixel, int heightPixel) {
        int[] pixelRGBA = new int[4];

        // We make a vector to the current pixel in the Viewport, by adding the vectors which make up the viewport to the origin of the viewport.
        /*
        threeDVector viewPortVectorfirstPart = this.viewport.portLocation[0].multiply(1-(double)widthPixel/((double) this.viewport.resolution[0]) - (double)heightPixel/((double) this.viewport.resolution[1]));
        threeDVector viewPortVectorsecondPart = this.viewport.portLocation[1].multiply((double) widthPixel/(double) this.viewport.resolution[0]);
        threeDVector viewPortVectorthirdPart = this.viewport.portLocation[2].multiply((double) heightPixel/(double) this.viewport.resolution[1]);
        threeDVector viewPortVector = viewPortVectorfirstPart.add(viewPortVectorsecondPart).add(viewPortVectorthirdPart);
        */
        //viewPortVector.printVector();
        //System.out.print("\n");

        // We create a new line from the viewpoint to the pixel in the viewport.
        threeDVector orientationToFirstViewportVector = this.viewport.portLocation[0].add(this.viewPoint.multiply(-1)).normalize().multiply(1-(double)widthPixel/((double) this.viewport.resolution[0]) - (double)heightPixel/((double) this.viewport.resolution[1]));
        threeDVector orientationToSecondViewportVector = this.viewport.portLocation[1].add(this.viewPoint.multiply(-1)).normalize().multiply((double) widthPixel/(double) this.viewport.resolution[0]);
        threeDVector orientationToThirdViewportVector = this.viewport.portLocation[2].add(this.viewPoint.multiply(-1)).normalize().multiply((double) heightPixel/(double) this.viewport.resolution[1]);
        threeDVector viewPortVector = orientationToFirstViewportVector.add(orientationToSecondViewportVector).add(orientationToThirdViewportVector);
        
        parametricLine viewpointLine = new parametricLine(viewPoint, viewPortVector);

        // For the line, we want to look at the possible intersections, the shapes they interact with, and at which point in the line the intersection occurs.
        ArrayList<threeDVector> allIntersections = new ArrayList<threeDVector>();
        ArrayList<Shape> intersectingShapes = new ArrayList<Shape>();
        ArrayList<Double> lambdaList = new ArrayList<Double>();
        ArrayList<Integer> coordinateList = new ArrayList<Integer>();
        double lowestLambda = 1000000000;
        int currentCoordinate = 0;
        for (Shape shape : this.objects) {            
            threeDVector[] intersections = shape.intersect(viewpointLine);
            if (intersections.length != 0) {                
                allIntersections.add(intersections[0]);
                allIntersections.add(intersections[1]);
                intersectingShapes.add(shape);
            }
        }
        // If there are no intersections, we return a black pixel.
        if (allIntersections.size() == 0) {
            for (int i = 0; i < 3; i++) {
                pixelRGBA[i] = 0;
            }
            pixelRGBA[3] = 255;
            //System.out.print(" ");
            return pixelRGBA;
        } else {
            // If there are multiple intersections, we look at which one is closest. We want to store this intersection to track the ray,
            // and the corresponding shape to calculate the shader later.
            for (int i = 0; i < allIntersections.size(); i++) {
                threeDVector intersection = allIntersections.get(i);
                try{
                    lambdaList.add(viewpointLine.getLambda(intersection));
                    coordinateList.add(i);
                } catch(NoIntersectionPointFoundException e) {
                    System.out.print("Some inconsistency has been generated. At finding Lambda");
                }
            }
            for (int i=0; i< lambdaList.size(); i++) {
                double lambda = lambdaList.get(i);
                int shapeCoordinate = coordinateList.get(i);
                if (lambda  > 0 & lambda < lowestLambda) {
                    lowestLambda = lambda;
                    currentCoordinate = shapeCoordinate;
                }
            }
            
            Shape reflectionShape = intersectingShapes.get(currentCoordinate/2);
            threeDVector intersectionPoint = viewpointLine.startingPoint.add(viewpointLine.orientation.multiply(lowestLambda));
            int totalBrightness = 0;

            // For all lightsources, we track rays to the intersecting point. If there is any ray to the intersection point which is uninterrupted, we add the brightness
            // of the lighsource to the total brightness for the pixel, multiplied by a shader function.
            for (lightSource lightSource : this.lightSources) {
                pointLine lightLine = new pointLine(lightSource.location, intersectionPoint);
                ArrayList<threeDVector> lightRayIntersections = new ArrayList<threeDVector>();
                for (Shape shape : this.objects) {
                    threeDVector[] intersections = shape.intersect(lightLine);
                    if (!(intersections.length == 0)) {                
                        lightRayIntersections.add(intersections[0]);
                        lightRayIntersections.add(intersections[1]);
                    }
                }
                double maxLambda = 0;
                try{
                    maxLambda = lightLine.getLambda(intersectionPoint);
                } catch (NoIntersectionPointFoundException e) {
                    System.out.print("Some inconsistency has been generated. At begin of light-step.");
                }
                boolean lightReachesIntersection = true;
                for (threeDVector intersection : lightRayIntersections) {
                    try {
                        if(lightLine.getLambda(intersection) > 0 && lightLine.getLambda(intersection) < maxLambda-1){
                            lightReachesIntersection = false;
                        }
                    } catch(NoIntersectionPointFoundException e) {
                        System.out.print("Some inconsistency has been generated. At end of light-step.");
                    }
                }
                if(lightReachesIntersection) {
                    Line perpendicularLine = reflectionShape.getPerpendicularLine(intersectionPoint);
                    double diffuseCoefficient = reflectionShape.getDiffuseCoefficient();
                    double brightness = diffuseCoefficient*lightSource.brightness*Math.max(0, Math.max(perpendicularLine.orientation.innerProduct(lightLine.orientation.multiply(-1)), perpendicularLine.orientation.innerProduct(lightLine.orientation)));
                    int convertedBrightness = (int) brightness;

                    totalBrightness += convertedBrightness;///(lightSource.location.distance(intersectionPoint));
                }
            }
            //we return the minimnum of the total brightness and 255 for each coordinate in the RGBA vector.
            pixelRGBA[0] = Math.min(totalBrightness,255);
            pixelRGBA[1] = Math.min(totalBrightness,255);
            pixelRGBA[2] = Math.min(totalBrightness,255);
            pixelRGBA[3] = 255;           

        }
        //System.out.print("(" + String.valueOf(pixelRGBA[0]) + ", " + String.valueOf(pixelRGBA[1]) + ", " + String.valueOf(pixelRGBA[2]) + ", " + String.valueOf(pixelRGBA[3]) + " )");
        //System.out.print("1");
        return pixelRGBA;
    }

    // To render the Scene, we just render every pixel and return the results in an array.
    public int[][][] renderScene() {
        int imageWidth = this.viewport.resolution[0];
        int imageHeight  = this.viewport.resolution[1];
        int[][][] pictureArray = new int[imageWidth][imageHeight][4];

        for (int i = 0; i < imageWidth; i += 1) {
            for (int j = 0; j < imageHeight; j+=1) {
                int[] pixelRGBA = renderPixel(i,j);
                pictureArray[i][j] = pixelRGBA;
            }
            //System.out.print("\n");
        }
        return pictureArray;
    }

}