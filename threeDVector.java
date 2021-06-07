import java.lang.*;

// Onze vector klasse, die als instance variables 3 doubles heeft die de locatie aangeven. Verder definiëren we methodes die handig zijn voor vectorcalculus.
class threeDVector{
    
    double x;
    double y;
    double z;
    
    public threeDVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return;
    }
    
    // We gebruiken deze methode om een equals te definieren die resistant is tegen afronding door floating point errors, zodat we redelijk zeker weten dat een punt dat we met de ene methode vinden,
    // ook nog voor de andere methode als snijpunt geldt.
    public static boolean almostEquals(double firstDouble, double secondDouble) {
        double closenessParameter =0.0001;
        if (firstDouble >= secondDouble - closenessParameter & firstDouble <= secondDouble + closenessParameter) {
            return true;
        } else{
            return false;
        }        
    }

    public threeDVector add(threeDVector addedVector) {
        threeDVector resultVector = new threeDVector(addedVector.x+this.x,addedVector.y+this.y,addedVector.z+this.z);
        return resultVector;
    }

    public threeDVector multiply(double lambda) {
        threeDVector resultVector = new threeDVector(lambda*this.x, lambda*this.y, lambda*this.z);
        return resultVector;
    }

    public double innerProduct(threeDVector newVector) {
        double result = this.x*newVector.x + this.y*newVector.y + this.z*newVector.z;
        return result;
    }

    public double norm() {
        double result = Math.sqrt(this.innerProduct(this));
        return result;
    }

    public double distance(threeDVector newVector) {
        threeDVector temporaryVector = this.add(newVector.multiply(-1));
        double distance = temporaryVector.norm();
        return distance;
    }

    public threeDVector normalize() {
        threeDVector resultVector = new threeDVector(this.x/this.norm(),this.y/this.norm(),this.z/this.norm());
        return resultVector;
    }
    
    public void printVector() {
        System.out.print("("+String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z) + ")");
    }


    // We gebruiken de AlmostEquals functie met twee doubles hier om gelijkheid voor vectoren te definiëren, die hopelijk resistant is tegen afronding.
    boolean equals(threeDVector otherVector) {
        if(threeDVector.almostEquals(this.x,otherVector.x) && threeDVector.almostEquals(this.y,otherVector.y) && threeDVector.almostEquals(this.z,otherVector.z)) {
            return true;
        } else{
            return false;
        }
    }
}