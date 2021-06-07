// Een shape heeft een diffusecoefficient, een intersectiefunctie en een maneir om gegeven een punt op de vorm, een loodrechte lijn daarop te krijgen.
interface Shape{
    threeDVector[] intersect(Line line);
    Line getPerpendicularLine(threeDVector toPoint);
    double getDiffuseCoefficient();
}

class Sphere implements Shape {
    double diffuseCoefficient = 0.8;
    threeDVector origin;
    double radius;

    @Override
    public Line getPerpendicularLine(threeDVector toPoint) {
        pointLine perpendicularLine = new pointLine(this.origin, toPoint);
        return perpendicularLine;
    }

    @Override
    public double getDiffuseCoefficient() {
        return this.diffuseCoefficient;
    }
    
    public threeDVector[] intersect(Line line) {
        // We gebruiken inproducten om te berekenen op welke punten een lijn een bol kan doorsnijden. Hiervoor moeten we een kwadratische vergelijking oplossen. Als de vergelijking
        // geen oplossingen heeft, zijn er geen snijpunten. Anders zijn de oplossingen van lambda, de snijpunten van de lijn a+lambda*b met de cirkel.
        double a = line.orientation.norm()*line.orientation.norm();
        double b = 2*line.startingPoint.innerProduct(line.orientation) - 2*line.orientation.innerProduct(this.origin);
        double c = line.startingPoint.innerProduct(line.startingPoint) + this.origin.innerProduct(this.origin) - 2*line.startingPoint.innerProduct(this.origin) - Math.pow(this.radius,2);
       
        double determinant = Math.pow(b,2) - (4*a*c);
        
        if (determinant < 0.0) {
            threeDVector[] intersectionLocations = new threeDVector[0];
            return intersectionLocations;
        } else{
        threeDVector[] intersectionLocations = new threeDVector[2];
        double firstLambda = (-b+Math.sqrt(determinant))/(2*a);
        double secondLambda = (-b-Math.sqrt(determinant))/(2*a);
        intersectionLocations[0] = line.startingPoint.add(line.orientation.multiply(firstLambda));
        intersectionLocations[1] = line.startingPoint.add(line.orientation.multiply(secondLambda));
        return intersectionLocations;
        }
    }
}