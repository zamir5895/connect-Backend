package dbp.connect.Review.Exceptions;

public class ReviewNotFoundException extends RuntimeException{
    public ReviewNotFoundException(String mensaje ){
        super(mensaje);
    }
}
