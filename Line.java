// Een lijn bestaat uit een startpunt en een richting. We definiëren twee subklasses die een lijn op deze manier kunnen definiëren. 
// Ze nemen allebei een 3d vector als eerste input die een startpunt voorstelt. Als tweede input neemt de ene een ander punt in 3D om een lijn tussen die twee punten te trekken.
// De andere neemt als tweede input een richting, ook in de vorm van een 3D vector. 
// Allebei de lijnen worden vervolgens naar deze representatie omgezet.

class Line {
    threeDVector startingPoint;
    threeDVector orientation;


    // We hebben een StartingPointNotEqualException nodig als we een hoek berekenen, voor het geval dat de hoek niet berekend kan worden.
    public double calculateAngle(Line otherLine) throws StartingPointNotEqualException{
        if (this.startingPoint != otherLine.startingPoint){
            throw new StartingPointNotEqualException("Cannot calculate the angle when the starting points are not equal");
        }
        double angle = Math.acos(this.orientation.innerProduct(otherLine.orientation)/(this.orientation.norm()*otherLine.orientation.norm()));
        //System.out.print(String.valueOf(angle) + "\n");
        return angle;
    }

    // We gebruiken vector calculus om het snijpunt in het XY vlak van twee lijnen te berekenen. Dit kan niet altijd; als de lijnen parallel zijn is er niet noodzakelijkerwijs een snijpunt in dit vlak.
    // Verder is het mogelijk dat één van de lijnen een oriëntatie puur in de Z richting heeft. Deze gevallen gooien allebei een error.
    public threeDVector xyIntersectionVector(Line otherLine) throws ArithmeticException{
        if (this.orientation.equals(otherLine.orientation)){
            throw new ArithmeticException("The lines are parallel");
        }
        
        double numerator = this.orientation.y*otherLine.startingPoint.x + this.orientation.x*this.startingPoint.y - this.orientation.y*this.startingPoint.x - this.orientation.x*otherLine.startingPoint.y;
        double denominator = otherLine.orientation.y*this.orientation.x - this.orientation.y*otherLine.orientation.x;
        double gamma = numerator/denominator;
        double lambda;
        try{
        lambda = (otherLine.startingPoint.y - this.startingPoint.y + gamma*otherLine.orientation.y)/this.orientation.y;
        } catch (ArithmeticException e) {
        lambda = (otherLine.startingPoint.x - this.startingPoint.x + gamma*otherLine.orientation.x)/this.orientation.x;
        }
        threeDVector xyIntersectionVector = this.startingPoint.add(this.orientation.multiply(lambda));
        return xyIntersectionVector;
    }
    
    // We gebruiken de XYintersectie om een methode te definiëren die ons verteld of twee lijnen elkaar snijden.
    public boolean doesIntersect(Line otherLine) throws ArithmeticException{   
        //if (this.startingPoint.add(this.orientation.multiply(lambda)).equals(otherLine.startingPoint.add(otherLine.orientation.multiply(gamma)))) {
        if (this.xyIntersectionVector(otherLine).equals(otherLine.xyIntersectionVector(this))){
            return true;
        } else { 
            return false;
        }
    }

    // gegeven een snijpunt rekent deze functie uit waarmee de orïentatie van een lijn vermenigvuldigd moet worden om vanaf het startpunt bij het snijpunt te komen.
    public double getLambda(threeDVector intersectionPoint) throws NoIntersectionPointFoundException {
        double lambda;
        if (this.orientation.x != 0) {
            lambda = (intersectionPoint.x - this.startingPoint.x)/this.orientation.x;
        } else if (this.orientation.y != 0) {
            lambda = (intersectionPoint.y - this.startingPoint.y)/this.orientation.y;
        } else {
            lambda = (intersectionPoint.z - this.startingPoint.z)/this.orientation.z;
        }
        if (this.startingPoint.add(this.orientation.multiply(lambda)).equals(intersectionPoint)){
            return lambda;
        } else{
            throw new NoIntersectionPointFoundException("Intersection point is not on the line");
        }
    }
    
    // We gebruiken de XYintersectie functie ook om de locatie van een eventueel snijpunt uit te rekenen.
    public intersectionPoint generateIntersectionPoint(Line otherLine) throws NoIntersectionPointFoundException{
        if (!this.doesIntersect(otherLine)){
            throw new NoIntersectionPointFoundException("Cannot generate an exception point when there is no single intersection point.");
        } 
        intersectionPoint intersect = new intersectionPoint();
        intersect.intersectionLocation = this.xyIntersectionVector(otherLine);

        parametricLine currentLineWithNewStartingPoint = new parametricLine(intersect.intersectionLocation, this.orientation);
        parametricLine otherLineWithNewStartingPoint = new parametricLine(intersect.intersectionLocation, otherLine.orientation);

        double angle;

        try {
            angle = currentLineWithNewStartingPoint.calculateAngle(otherLineWithNewStartingPoint);
        } catch (StartingPointNotEqualException e) {
            angle = 0.0;
        }
        intersect.intersectionAngle = angle;
        
        return intersect;
    }
}

// Dit zijn twee classes die Line extenden, die vooral als constructor methodes voor Line objecten gebruikt worden.

class pointLine extends Line {
    public pointLine(threeDVector firstVector, threeDVector secondVector){
        this.startingPoint = firstVector;
        threeDVector directionVector = secondVector.add(firstVector.multiply(-1));
        directionVector = directionVector.normalize();
        this.orientation = directionVector;
    }
}

class parametricLine extends Line{
    public parametricLine(threeDVector startingPoint, threeDVector direction){
        this.startingPoint = startingPoint;
        this.orientation = direction.normalize();
    }
}