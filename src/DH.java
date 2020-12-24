public class DH {

    private static class User{
        String name;
        int secretNumber;
        int prime;
        int base;
        double key;
        User(int secretNumber, int prime, int base){
            this.secretNumber=secretNumber;
            this.prime=prime;
            this.base=base;
            key=Math.pow(base, secretNumber)%prime;
        }
        public double getKey(){
            return key;
        }
        public double getSecretKey(double companionKey){
            return Math.pow(companionKey, secretNumber)%prime;
        }
    }
    public static void main(String[] args) {
        User firstUser = new User(9,11,7);
        User secondUser = new User(3,11,7);
        System.out.println("Открытое простое число. p = 11");
        System.out.println("Первообразный корень по модулю р. g = 7");
        System.out.println("Открытый ключ первого: " + firstUser.getKey());
        System.out.println("Открытый ключ второго: " + secondUser.getKey());
        System.out.println("Вычисленные секретные ключи: ");
        System.out.println("У первого " + firstUser.getSecretKey(secondUser.getKey()));
        System.out.println("У второго " + secondUser.getSecretKey(firstUser.getKey()));

    }
}
